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

import edu.uci.ics.jung.graph.util.Pair;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import model.*;
import org.apache.commons.collections15.Factory;

/**
 * Generates a small world network, starting with a ring lattice with
 * n vertices, 2k connections to adjecent vertices and randomly
 * rewires each edge with probability p
 * References:
 * Watts, D & Strogatz, S - Collective dynamics of small-world networks
 * images explaining the relation between n and k available at
 * http://rumordynamics.awardspace.com/RingLattice.html [12 AUG 2009]
 * simply, each vertex is connected to all neighbours within distance k
 *
 * does not allow loops, but if during rewiring
 * @author mb724
 */
public class SmallWorldGenerator {

    private Factory<MyGraph> gf;
    private Factory<MyVertex> vf;
    private Factory<MyEdge> ef;
    private double p;
    private int n, k;

    public SmallWorldGenerator(Factory<MyGraph> gf, Factory<MyVertex> vf, Factory<MyEdge> ef,
            int n, int k, double p) {
        this.gf = gf;
        this.vf = vf;
        this.ef = ef;
        this.n = n;
        this.k = k;
        this.p = p;
    }

    public MyGraph create() {
        MyGraph g = gf.create();
        for (int i = 0; i < n; i++) {
            g.addVertex(vf.create());
        }

        //get k withing reasonable limits
        k = Math.min(k, n / 2);
        Random r = new Random();

        //create the initial ring lattice
        Object[] v = g.getVertices().toArray();
        for (int i = 0; i < v.length; i++) {//for each vertex
            //connect to 2k adjacent vertices
            for (int j = 1; j <= k; j++) {
                int left = Math.abs((i + n - j) % n); //index of the left neighbour of i, j units away from it
                int right = Math.abs((i + j) % n);//same for the right

                if (!g.isNeighbor(v[i], v[left])) {//make an ordinary left connection
                    g.addEdge(ef.create(), v[i], v[left]);
                }
                if (!g.isNeighbor(v[i], v[right])) {//same for the right connection
                    g.addEdge(ef.create(), v[i], v[right]);
                }
            }
        }



        //rewire
        HashMap<Object, Pair> edgesCopy = new HashMap<Object, Pair>();//edges against their endpoints
        for (Object e : g.getEdges()) {//copy old vertices from the graph
            edgesCopy.put(e, g.getEndpoints(e));
//            g.removeEdge(e);
        }

        for (Object e : edgesCopy.keySet()) {//change the SECOND endpoint of an edge probabilistically
            if (r.nextDouble() < p) {
                //rewire the left connection
                Object first = g.getEndpoints(e).getFirst();
//                Object second = g.getEndpoints(e).getSecond();
                if (g.outDegree(first) < n - 1) {//only rewire if the graph is not already complete
                    int pos = r.nextInt(n);
                    while (g.getNeighbors(first).contains(v[pos]) //if that connection already exists
                            || v[pos].equals(first) //or is a loop
                            || edgesCopy.containsValue(new Pair(first, v[pos]))//or is parallel to another newly rewired connection
                            || edgesCopy.containsValue(new Pair(v[pos], first))) {
                        pos = r.nextInt(n); //make sure we really rewire this edge and this is not a loop
                    }
                    edgesCopy.put(e, new Pair(first, v[pos]));
                    //update the graph
                    Object[] edges = g.getEdges().toArray();
                    for (int i = 0; i < edges.length; i++) {//remove old edges from the graph
                        g.removeEdge(edges[i]);
                    }
                    for (Object edge : edgesCopy.keySet()) {//add new edges to the graph
                        g.addEdge(edge, edgesCopy.get(edge));
                    }
                }
            }
        }


        return g;
    }
}
