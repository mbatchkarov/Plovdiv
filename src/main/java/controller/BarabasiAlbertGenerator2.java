/*
 * Copyright (c) 2009, Miroslav Batchkarov, University of Sussex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of the University of Sussex nor the names of its
 *    contributors may be used to endorse or promote products  derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.generators.EvolvingGraphGenerator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MultiGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;


/**
 * <p>Simple evolving scale-free random graph generator. At each time
 * step, a new vertex is created and is connected to existing vertices
 * according to the principle of "preferential attachment", whereby
 * vertices with higher degree have a higher probability of being
 * selected for attachment.</p>
 *
 * <p>At a given timestep, the probability <code>p</code> of creating an edge
 * between an existing vertex <code>v</code> and the newly added vertex is
 * <pre>
 * p = (degree(v) + 1) / (|E| + |V|);
 * </pre>
 *
 * <p>where <code>|E|</code> and <code>|V|</code> are, respectively, the number
 * of edges and vertices currently in the network (counting neither the new
 * vertex nor the other edges that are being attached to it).</p>
 *
 * <p>Note that the formula specified in the original paper
 * (cited below) was
 * <pre>
 * p = degree(v) / |E|
 * </pre>
 * </p>
 *
 * <p>However, this would have meant that the probability of attachment for any existing
 * isolated vertex would be 0.  This version uses Lagrangian smoothing to give
 * each existing vertex a positive attachment probability.</p>
 *
 * <p>The graph created may be either directed or undirected (controlled by a constructor
 * parameter); the default is undirected.
 * If the graph is specified to be directed, then the edges added will be directed
 * from the newly added vertex u to the existing vertex v, with probability proportional to the
 * indegree of v (number of edges directed towards v).  If the graph is specified to be undirected,
 * then the (undirected) edges added will connect u to v, with probability proportional to the
 * degree of v.</p>
 *
 * <p>The <code>parallel</code> constructor parameter specifies whether parallel edges
 * may be created.</p>
 *
 * @see "A.-L. Barabasi and R. Albert, Emergence of scaling in random networks, Science 286, 1999."
 * @author Scott White
 * @author Joshua O'Madadhain
 * @author Tom Nelson - adapted to jung2
 */
public class BarabasiAlbertGenerator2<V,E> implements EvolvingGraphGenerator<V,E> {
    private Graph<V, E> mGraph = null;
    private int mNumEdgesToAttachPerStep;
    private int mElapsedTimeSteps;
    private Random mRandom;
    protected List<V> vertex_index;
    protected int init_vertices;
    protected Map<V,Integer> index_vertex;
//    protected Factory<Graph<V,E>> graphFactory;
    protected Factory<V> vertexFactory;
    protected Factory<E> edgeFactory;

    /**
     * Constructs a new instance of the generator.
     * Connects new vertex to pre-existing vertices at each time step
     * @param seed  random number seed
     */
    public BarabasiAlbertGenerator2(Graph<V,E> graph,
    		Factory<V> vertexFactory, Factory<E> edgeFactory,
    		int seed, Set<V> seedVertices)
    {
        
        mNumEdgesToAttachPerStep = 1;
        mRandom = new Random(seed);
        this.mGraph = graph;
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
//        this.init_vertices = init_vertices;
        this.init_vertices = graph.getVertexCount();
        initialize(seedVertices);
    }


    /**
     * Constructs a new instance of the generator, whose output will be an undirected graph,
     * and which will use the current time as a seed for the random number generation.
     * new vertex to pre-existing vertices at each time step
     */
    public BarabasiAlbertGenerator2(Graph<V,E> graph,
    		Factory<V> vertexFactory, Factory<E> edgeFactory,
    		 Set<V> seedVertices) {
        this(graph, vertexFactory, edgeFactory, (int) System.currentTimeMillis(), seedVertices);
    }

    private void initialize(Set<V> seedVertices) {
        
//    	mGraph = graphFactory.create(); // no need to create, it is passed in

        vertex_index = new ArrayList<V>(2*init_vertices);
        index_vertex = new HashMap<V, Integer>(2*init_vertices);

        Object[] vertices = mGraph.getVertices().toArray();
        for (int i = 0; i < init_vertices; i++) {
//            V v = vertexFactory.create(); // no need to create, just copy
//            mGraph.addVertex(v);
            V v = (V) vertices[i];
            vertex_index.add(v);
            index_vertex.put(v, i);
            seedVertices.add(v);
        }

        mElapsedTimeSteps = 0;
    }

    private void createRandomEdge(Collection<V> preexistingNodes,
    		V newVertex, Set<Pair<V>> added_pairs) {
        V attach_point;
        boolean created_edge = false;
        Pair<V> endpoints;
        do {
            attach_point = vertex_index.get(mRandom.nextInt(vertex_index.size()));

            endpoints = new Pair<V>(newVertex, attach_point);

            // if parallel edges are not allowed, skip attach_point if <newVertex, attach_point>
            // already exists; note that because of the way edges are added, we only need to check
            // the list of candidate edges for duplicates.
            if (!(mGraph instanceof MultiGraph))
            {
            	if (added_pairs.contains(endpoints))
            		continue;
            	if (mGraph.getDefaultEdgeType() == EdgeType.UNDIRECTED &&
            		added_pairs.contains(new Pair<V>(attach_point, newVertex)))
            		continue;
            }

            double degree = mGraph.inDegree(attach_point);

            // subtract 1 from numVertices because we don't want to count newVertex
            // (which has already been added to the graph, but not to vertex_index)
            double attach_prob = (degree + 1) / (mGraph.getEdgeCount() + mGraph.getVertexCount() - 1);
            if (attach_prob >= mRandom.nextDouble())
                created_edge = true;
        }
        while (!created_edge);

        added_pairs.add(endpoints);

        if (mGraph.getDefaultEdgeType() == EdgeType.UNDIRECTED) {
        	added_pairs.add(new Pair<V>(attach_point, newVertex));
        }
    }

    public void evolveGraph(int numTimeSteps) {

        for (int i = 0; i < numTimeSteps; i++) {
            evolveGraph();
            mElapsedTimeSteps++;
        }
    }

    private void evolveGraph() {
        Collection<V> preexistingNodes = mGraph.getVertices();
        V newVertex = vertexFactory.create();

        mGraph.addVertex(newVertex);

        // generate and store the new edges; don't add them to the graph
        // yet because we don't want to bias the degree calculations
        // (all new edges in a timestep should be added in parallel)
        Set<Pair<V>> added_pairs = new HashSet<Pair<V>>(mNumEdgesToAttachPerStep*3);

        for (int i = 0; i < mNumEdgesToAttachPerStep; i++)
        	createRandomEdge(preexistingNodes, newVertex, added_pairs);

        for (Pair<V> pair : added_pairs)
        {
        	V v1 = pair.getFirst();
        	V v2 = pair.getSecond();
        	if (mGraph.getDefaultEdgeType() != EdgeType.UNDIRECTED ||
        			!mGraph.isNeighbor(v1, v2))
        		mGraph.addEdge(edgeFactory.create(), pair);
        }
        // now that we're done attaching edges to this new vertex,
        // add it to the index
        vertex_index.add(newVertex);
        index_vertex.put(newVertex, new Integer(vertex_index.size() - 1));
    }

    public int numIterations() {
        return mElapsedTimeSteps;
    }

    public Graph<V, E> create() {
        return mGraph;
    }
}