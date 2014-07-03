package controller;

import edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MyGraph;
import model.MyEdge;
import model.MyVertex;
import model.factories.GraphFactory;
import edu.uci.ics.jung.graph.util.EdgeType;
import org.junit.Before;
import org.junit.Test;
import view.Display;

import static org.junit.Assert.*;

/**
 * Created by miroslavbatchkarov on 21/03/2014.
 */
public class StatsTest {

    private MyGraph<MyVertex, MyEdge> triangle;
    private MyGraph<MyVertex, MyEdge> glass;
    private Stats triangleStats, glassStats;

    @Before
    public void setUp() throws Exception {
        triangle = new GraphFactory().create();

        triangle.addVertex(v(0));
        triangle.addVertex(v(1));
        triangle.addVertex(v(2));

        MyEdge e0 = new MyEdge(0);
        MyEdge e1 = new MyEdge(1);
        MyEdge e2 = new MyEdge(2);
        MyEdge e3 = new MyEdge(3);

        triangle.addEdge(e0, v(0), v(1), EdgeType.UNDIRECTED);
        triangle.addEdge(e1, v(0), v(2), EdgeType.UNDIRECTED);
        triangle.addEdge(e2, v(2), v(1), EdgeType.UNDIRECTED);

        glass = new GraphFactory().create();
        for (MyVertex o : triangle.getVertices()) {
            glass.addVertex(o);
        }
        glass.addVertex(v(3));
        for (MyEdge e : triangle.getEdges()) {
            glass.addEdge(e, triangle.getEndpoints(e));
        }
        glass.addEdge(e3, v(2), v(3));

        triangleStats = new Stats(triangle);
        triangle.addGraphEventListener(triangleStats);
        triangle.addExtraGraphEventListener(triangleStats);
        glassStats = new Stats(glass);
        glass.addGraphEventListener(glassStats);
        glass.addExtraGraphEventListener(glassStats);
    }

    /**
     * Convenience methods to get vertices by ID
     *
     * @param id
     * @return
     */
    private MyVertex v(int id) {
        return new MyVertex(id);
    }

    private MyEdge e(int id) {
        return new MyEdge(id);
    }

    @Test
    public void testGetCC() throws Exception {
        assertEquals(1.0, triangleStats.getCC(), 0.01);// average value
        for (MyVertex v : triangle.getVertices()) {
            assertEquals(1.0, triangleStats.getCC(v), 0.01);// per vertex
        }

        assertTrue(glass.getVertices().contains(v(3)));
        assertEquals(0, glassStats.getCC(v(3)), 1e-2);

        glass.addEdge(e(4), v(1), v(3));

        assertEquals(1, glassStats.getCC(v(0)), 0.01);
        assertEquals(0.6666, glassStats.getCC(v(1)), 0.01);
        assertEquals(0.6666, glassStats.getCC(v(2)), 0.01);
        assertEquals(1, glassStats.getCC(v(3)), 0.01);
    }

    @Test
    public void testGetAPL() throws Exception {
        assertEquals(1, triangleStats.getAPL(), 0.01);
        for (int i = 0; i < triangle.getVertexCount(); i++) {
            assertEquals(1, triangleStats.getAPL(v(i)), 0.01);
        }

        // try the weighted case
        for (MyEdge e : triangle.getEdges()) {
            triangle.setEdgeWeight(e, 3);
        }
        assertEquals(3, triangleStats.getAPL(), 0.01);// average value

        // try a more complex case
        assertEquals(1.333, glassStats.getAPL(v(0)), 0.01);
        assertEquals(1.333, glassStats.getAPL(v(1)), 0.01);
        assertEquals(1, glassStats.getAPL(v(2)), 0.01);
        assertEquals(1.6666, glassStats.getAPL(v(3)), 0.01);

        assertEquals(1.3333, glassStats.getAPL(), 0.01);

        glass.addVertex(v(5));
    }

    @Test
    public void testGetAvgDegree() throws Exception {
        assertEquals(2, triangleStats.getAvgDegree(), 0.01);
        assertEquals(2, glassStats.getAvgDegree(), 0.01);
        glass.addEdge(e(4), v(1), v(3));
        assertEquals(2.5, glassStats.getAvgDegree(), 0.01);
    }

    @Test
    public void testGetDegreDistribution(){

        Stats s = new Stats(triangle);
        assertArrayEquals(s.getDegreeDistribution(),
                          new int[]{0, 0, 3}); // three vertices of degree 2

        s = new Stats(glass);
        assertArrayEquals(s.getDegreeDistribution(),
                          new int[]{0, 1, 2, 1});
    }
}
