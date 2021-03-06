/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.ics.jung.io;

import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;
import edu.uci.ics.jung.algorithms.util.SettableTransformer;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import model.EpiState;
import model.MyVertex;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.functors.OrPredicate;

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author User
 */
public class CustomPajekNetReader<G extends Graph<V, E>, V, E> {

    protected Factory<V> vertex_factory;
    protected Factory<E> edge_factory;

    /**
     * The map for vertex labels (if any) created by this class.
     */
    protected SettableTransformer<V, String> vertex_labels = new MapSettableTransformer<V, String>(new HashMap<V, String>());

    /**
     * The map for vertex locations (if any) defined by this class.
     */
    protected SettableTransformer<V, Point2D> vertex_locations = new MapSettableTransformer<V, Point2D>(new HashMap<V, Point2D>());

    protected SettableTransformer<E, Number> edge_weights
            = new MapSettableTransformer<E, Number>(new HashMap<E, Number>());

    /**
     * Used to specify whether the most recently read line is a Pajek-specific
     * tag.
     */
    private static final Predicate<String> cc_pred = new StartsWithPredicate("*colors");
    private static final Predicate<String> v_pred = new StartsWithPredicate("*vertices");
    private static final Predicate<String> a_pred = new StartsWithPredicate("*arcs");
    private static final Predicate<String> e_pred = new StartsWithPredicate("*edges");
    private static final Predicate<String> t_pred = new StartsWithPredicate("*");
    private static final Predicate<String> c_pred = OrPredicate.getInstance(a_pred, e_pred);
    protected static final Predicate<String> l_pred = ListTagPred.getInstance();

    /**
     * Creates a PajekNetReader instance with the specified vertex and edge
     * factories.
     *
     * @param vertex_factory the factory to use to create vertex objects
     * @param edge_factory the factory to use to create edge objects
     */
    public CustomPajekNetReader(Factory<V> vertex_factory, Factory<E> edge_factory) {
        this.vertex_factory = vertex_factory;
        this.edge_factory = edge_factory;
    }

    /**
     * Creates a PajekNetReader instance with the specified edge factory, and
     * whose vertex objects correspond to the integer IDs assigned in the file.
     * Note that this requires <code>V</code> to be assignment-compatible with
     * an <code>Integer</code> value.
     *
     * @param edge_factory the factory to use to create edge objects
     */
    public CustomPajekNetReader(Factory<E> edge_factory) {
        this(null, edge_factory);
    }

    /**
     * Returns the graph created by parsing the specified file, as created by
     * the specified factory.
     *
     * @throws IOException
     */
    public G load(String filename, Factory<? extends G> graph_factory) throws IOException {
        return load(new FileReader(filename), graph_factory.create());
    }

    /**
     * Returns the graph created by parsing the specified reader, as created by
     * the specified factory.
     *
     * @throws IOException
     */
    public G load(Reader reader, Factory<? extends G> graph_factory) throws IOException {
        return load(reader, graph_factory.create());
    }

    /**
     * Returns the graph created by parsing the specified file, by populating
     * the specified graph.
     *
     * @throws IOException
     */
    public G load(String filename, G g) throws IOException {
        if (g == null) {
            throw new IllegalArgumentException("Graph provided must be non-null");
        }
        return load(new FileReader(filename), g);
    }

    /**
     * Populates the graph <code>g</code> with the graph represented by the
     * Pajek-format data supplied by <code>reader</code>. Stores edge weights,
     * if any, according to <code>nev</code> (if non-null).
     *
     * <p>
     * Any existing vertices/edges of <code>g</code>, if any, are unaffected.
     *
     * <p>
     * The edge data are filtered according to <code>g</code>'s constraints, if
     * any; thus, if <code>g</code> only accepts directed edges, any undirected
     * edges in the input are ignored.
     *
     * @throws IOException
     */
    public G load(Reader reader, G g) throws IOException {
        BufferedReader br = new BufferedReader(reader);

        // ignore everything until we see '*Colors'
        String curLine = skip(br, cc_pred);

        StringTokenizer st = new StringTokenizer(curLine);
        st.nextToken(); // skip past "*colors";
        String[] colorMetadata = st.nextToken().split(",");
        ((MyGraph) g).getLayoutParameters().setBackgroundColor(Integer.parseInt(colorMetadata[0]));
        ((MyGraph) g).getLayoutParameters().setEdgeColor(Integer.parseInt(colorMetadata[1]));

        // ignore everything until we see '*Vertices'
        curLine = skip(br, v_pred);

        if (curLine == null) // no vertices in the graph; return empty graph
        {
            return g;
        }

        // create appropriate number of vertices
        st = new StringTokenizer(curLine);
        st.nextToken(); // skip past "*vertices";

        String[] vertexMetadata = st.nextToken().split(",");

        int num_vertices = Integer.parseInt(vertexMetadata[0]);
        boolean areVertexIconsAllowed = Boolean.parseBoolean(vertexMetadata[1]);

        ((MyGraph) g).getLayoutParameters().setAllowNodeIcons(areVertexIconsAllowed);

        List<V> id = null;
        if (vertex_factory != null) {
            for (int i = 1; i <= num_vertices; i++) {
                g.addVertex(vertex_factory.create());
            }
            id = new ArrayList<V>(g.getVertices());
        }

        // read vertices until we see any Pajek format tag ('*...')
        curLine = null;
        while (br.ready()) {
            curLine = br.readLine();
            if (curLine == null || t_pred.evaluate(curLine)) {
                break;
            }
            if (curLine == "") // skip blank lines
            {
                continue;
            }

            try {
                readVertex(curLine, id, num_vertices, g);
            } catch (IllegalArgumentException iae) {
                br.close();
                reader.close();
                throw iae;
            }
        }

        // skip over the intermediate stuff (if any) 
        // and read the next arcs/edges section that we find
        curLine = readArcsOrEdges(curLine, br, g, id, edge_factory);

        // ditto
        readArcsOrEdges(curLine, br, g, id, edge_factory);

        br.close();
        reader.close();

        return g;
    }

    /**
     * Parses <code>curLine</code> as a reference to a vertex, and optionally
     * assigns label and location information.
     */
    @SuppressWarnings("unchecked")
    private void readVertex(String curLine, List<V> id, int num_vertices, G g) {
        V v;
        String[] parts = null;
        int coord_idx = -1;     // index of first coordinate in parts; -1 indicates no coordinates found
        String index;
        String metadata = null;
        String label = null;
        String[] metadataSections = null;
        // if there are quote marks on this line, split on them; metadata is surrounded by them
        if (curLine.indexOf('"') != -1) {
            String[] initial_split = curLine.trim().split("\"");
            // if there are any quote marks, there should be exactly 2
            if (initial_split.length < 2 || initial_split.length > 3) {
                throw new IllegalArgumentException("Unbalanced (or too many) "
                        + "quote marks in " + curLine);
            }
            index = initial_split[0].trim();
            metadata = initial_split[1].trim();
            metadataSections = metadata.split(",");
            if (initial_split.length == 3) {
                parts = initial_split[2].trim().split("\\s+", -1);
            }
            coord_idx = 0;
        } else // no quote marks, but are there coordinates?
        {
            parts = curLine.trim().split("\\s+", -1);
            index = parts[0];
            switch (parts.length) {
                case 1:         // just the ID; nothing to do, continue
                    break;
                case 2:         // just the ID and metadata
                    label = parts[1];
                    break;
                case 3:         // ID, no metadata, coordinates
                    coord_idx = 1;
                    break;
                default:         // ID, metadata, (x,y) coordinates, maybe some other stuff
                    coord_idx = 2;
                    break;
            }
        }
        int v_id = Integer.parseInt(index) - 1; // go from 1-based to 0-based index
        if (v_id >= num_vertices || v_id < 0) {
            throw new IllegalArgumentException("Vertex number " + v_id
                    + "is not in the range [1," + num_vertices + "]");
        }
        if (id != null) {
            v = id.get(v_id);
        } else {
            v = (V) (new Integer(v_id));
        }

        // Parse the vertex metadata - node state, vertex icon style and type.
        if (metadataSections != null) {
            EpiState state = EpiState.SUSCEPTIBLE;
            if (metadataSections[0].contains("INFECTED")) {
                state = EpiState.INFECTED;
            } else if (metadataSections[0].contains("RESISTANT")) {
                state = EpiState.RESISTANT;
            }

            ((MyVertex) v).setEpiState(state);
            ((MyVertex) v).getIcon().setStyle(Integer.parseInt(metadataSections[1]));
            ((MyVertex) v).getIcon().setType(Integer.parseInt(metadataSections[2]));
        }
        g.getVertices().toArray()[v_id] = v;

        // only attach the label if there's one to attach
        if (label != null && label.length() > 0 && vertex_labels != null) {
            vertex_labels.set(v, label);
        }

        // parse the rest of the line
        if (coord_idx != -1 && parts != null && parts.length >= coord_idx + 2 && vertex_locations != null) {
            double x = Double.parseDouble(parts[coord_idx]);
            double y = Double.parseDouble(parts[coord_idx + 1]);
            vertex_locations.set(v, new Point2D.Double(x, y));
        }
    }

    @SuppressWarnings("unchecked")
    private String readArcsOrEdges(String curLine, BufferedReader br, Graph<V, E> g, List<V> id, Factory<E> edge_factory)
            throws IOException {
        String nextLine = curLine;

        // in case we're not there yet (i.e., format tag isn't arcs or edges)
        if (!c_pred.evaluate(curLine)) {
            nextLine = skip(br, c_pred);
        }

        boolean reading_arcs = false;
        boolean reading_edges = false;
        EdgeType directedness = null;
        if (a_pred.evaluate(nextLine)) {
            if (g instanceof UndirectedGraph) {
                throw new IllegalArgumentException("Supplied undirected-only graph cannot be populated with directed edges");
            } else {
                reading_arcs = true;
                directedness = EdgeType.DIRECTED;
            }
        }
        if (e_pred.evaluate(nextLine)) {
            if (g instanceof DirectedGraph) {
                throw new IllegalArgumentException("Supplied directed-only graph cannot be populated with undirected edges");
            } else {
                reading_edges = true;
            }
            directedness = EdgeType.UNDIRECTED;
        }

        if (!(reading_arcs || reading_edges)) {
            return nextLine;
        }

        boolean is_list = l_pred.evaluate(nextLine);

        while (br.ready()) {
            nextLine = br.readLine();
            if (nextLine == null || t_pred.evaluate(nextLine)) {
                break;
            }
            if (curLine == "") // skip blank lines
            {
                continue;
            }

            StringTokenizer st = new StringTokenizer(nextLine.trim());

            int vid1 = Integer.parseInt(st.nextToken()) - 1;
            V v1;
            if (id != null) {
                v1 = id.get(vid1);
            } else {
                v1 = (V) new Integer(vid1);
            }

            if (is_list) // one source, multiple destinations
            {
                do {
                    createAddEdge(st, v1, directedness, g, id, edge_factory);
                } while (st.hasMoreTokens());
            } else // one source, one destination, at most one weight
            {
                E e = createAddEdge(st, v1, directedness, g, id, edge_factory);
                // get the edge weight if we care
                if (edge_weights != null && st.hasMoreTokens()) {
                    edge_weights.set(e, new Float(st.nextToken()));
                }
            }
        }
        return nextLine;
    }

    @SuppressWarnings("unchecked")
    protected E createAddEdge(StringTokenizer st, V v1,
            EdgeType directed, Graph<V, E> g, List<V> id, Factory<E> edge_factory) {
        int vid2 = Integer.parseInt(st.nextToken()) - 1;
        V v2;
        if (id != null) {
            v2 = id.get(vid2);
        } else {
            v2 = (V) new Integer(vid2);
        }
        E e = edge_factory.create();

        // don't error-check this: let the graph implementation do whatever it's going to do 
        // (add the edge, replace the existing edge, throw an exception--depends on the graph implementation)
        g.addEdge(e, v1, v2, directed);
        return e;
    }

    /**
     * Returns the first line read from <code>br</code> for which <code>p</code>
     * returns <code>true</code>, or <code>null</code> if there is no such line.
     *
     * @throws IOException
     */
    protected String skip(BufferedReader br, Predicate<String> p) throws IOException {
        while (br.ready()) {
            String curLine = br.readLine();
            if (curLine == null) {
                break;
            }
            curLine = curLine.trim();
            if (p.evaluate(curLine)) {
                return curLine;
            }
        }
        return null;
    }

    /**
     * A Predicate which evaluates to <code>true</code> if the argument starts
     * with the constructor-specified String.
     *
     * @author Joshua O'Madadhain
     */
    protected static class StartsWithPredicate implements Predicate<String> {

        private String tag;

        protected StartsWithPredicate(String s) {
            this.tag = s;
        }

        public boolean evaluate(String str) {
            return (str != null && str.toLowerCase().startsWith(tag));
        }
    }

    /**
     * A Predicate which evaluates to <code>true</code> if the argument ends
     * with the string "list".
     *
     * @author Joshua O'Madadhain
     */
    protected static class ListTagPred implements Predicate<String> {

        protected static ListTagPred instance;

        protected ListTagPred() {
        }

        protected static ListTagPred getInstance() {
            if (instance == null) {
                instance = new ListTagPred();
            }
            return instance;
        }

        public boolean evaluate(String s) {
            return (s != null && s.toLowerCase().endsWith("list"));
        }
    }

    /**
     * @return the vertexLocationTransformer
     */
    public SettableTransformer<V, Point2D> getVertexLocationTransformer() {
        return vertex_locations;
    }

    /**
     * Provides a transformer which will be used to write out the vertex
     * locations.
     */
    public void setVertexLocationTransformer(SettableTransformer<V, Point2D> vertex_locations) {
        this.vertex_locations = vertex_locations;
    }

    /**
     * Returns a transformer from vertices to their labels.
     */
    public SettableTransformer<V, String> getVertexLabeller() {
        return vertex_labels;
    }

    /**
     * Provides a transformer which will be used to write out the vertex labels.
     */
    public void setVertexLabeller(SettableTransformer<V, String> vertex_labels) {
        this.vertex_labels = vertex_labels;
    }

    /**
     * Returns a transformer from edges to their weights.
     */
    public SettableTransformer<E, Number> getEdgeWeightTransformer() {
        return edge_weights;
    }

    /**
     * Provides a transformer which will be used to write out edge weights.
     */
    public void setEdgeWeightTransformer(SettableTransformer<E, Number> edge_weights) {
        this.edge_weights = edge_weights;
    }

}
