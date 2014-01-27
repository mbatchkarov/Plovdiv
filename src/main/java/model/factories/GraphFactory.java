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

package model.factories;

import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import model.MyEdge;
import model.MyVertex;
import org.apache.commons.collections15.Factory;

/**
 * @author mb724
 */
public class GraphFactory implements Factory {

    private MyGraph g;

    public GraphFactory() {
        this.g = null;
    }

    public GraphFactory(MyGraph g) {
        this.g = g;
        for (Object v : g.getVertices()) {
            g.removeVertex(v);
        }

        for (Object v : g.getEdges()) {
            g.removeVertex(v);
        }
    }

    /**
     * Returns the graph currently in memory (not in buffer)
     *
     * @return
     */
    public MyGraph create() {
        if (this.g != null) {
            return g;
        } else {
            return new MyGraph(new OrderedSparseMultigraph<MyVertex, MyEdge>());
        }
    }

}
