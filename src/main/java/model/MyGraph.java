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
package model;

/**
 * Provides a single instance of a Graph, which is then displayed and modified
 * by the program
 *
 * @author mb724
 */
import controller.Controller;
import edu.uci.ics.jung.graph.OrderedSparseMultigraph;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import model.dynamics.SIDynamics;
import model.dynamics.SIRDynamics;
import model.dynamics.SISDynamics;

/**
 *
 * @author mb724
 */
public class MyGraph<V, E> extends OrderedSparseMultigraph<V, E> implements Serializable {

    private static MyGraph INSTANCE;
    private static HashMap userData;

    /**
     * Mutator
     *
     * @param newInstance
     */
    public static void setInstance(MyGraph newInstance) {
        INSTANCE = newInstance;
    }

    public static void setDefaultSimulationSettings() {
        MyGraph.setUserDatum("dynamics",
                new SISDynamics(0.1, 0.1, 0.1, 0.1));

        //attach the running time to the graph
        MyGraph.setUserDatum("time",
                new Integer(100));

        //attach the speed multiplier to the graph
        MyGraph.setUserDatum("speed", 200);
        //make sure the graphs is in a proper state
        Controller.validateNodeStates();
    }

    protected MyGraph() {
        userData = new HashMap<Object, Object>();
    }

    /**
     * returns a new instance, which can be used as a buffer...
     *
     * @return
     */
    public static MyGraph<MyVertex, MyEdge> getNewInstance() {
        return new MyGraph();
    }

    /**
     * returns the signleton instance, which is to be displayed
     *
     * @return
     */
    public static MyGraph<MyVertex, MyEdge> getInstance() {
        if (INSTANCE == null) {
            setInstance(new MyGraph<Integer, Integer>());
        }

        return INSTANCE;
    }

//    public static MyGraph getBuffer() {
//        if (BUFFER == null) {
//            BUFFER = new MyGraph<Integer, Integer>();
//        }
//
//        return BUFFER;
//    }
//
//    public static void flushBuffer() {
//        BUFFER = null; //the getter will create a new one when required
//    }
    public static void flushInstance() {
        INSTANCE = null;
    }

//    /**
//     * copies the contents of the memory to the buffer.
//     * Since all modifications are made to the original, the user can cancel all changes
//     * by clicking cancel (reverting to what's in the buffer)
//     */
//    public static void backupToBuffer() {
//
//        MyGraph.flushBuffer();
//        MyGraph x = MyGraph.getInstance();
//        MyGraph b = MyGraph.getBuffer();
//
//        //copy the vertices from x to b (instance to buffer)
//        Iterator i = x.getVertices().iterator();
//        while (i.hasNext()) {
//            MyVertex v = (MyVertex) i.next();
//            b.addVertex(v);
//        }
//        //copy the edges
//        Iterator it = x.getEdges().iterator();
//        while (it.hasNext()) {
//            MyEdge e = (MyEdge) it.next();
//            b.addEdge(e, INSTANCE.getEndpoints(e));
//        }
//
//    }
//
//    /**
//     * Replace the current graph with a new one, for example when the user loads or generates.
//     * The old graph is not save automatically.
//     */
//    public static void replaceFromBuffer() {
//        setInstance(null);
//        System.gc();
//        setInstance(BUFFER);
////        BUFFER = null;
//    }
    public static void setUserDatum(Object key, Object value) {
        if (key != null && value != null) {
            userData.put(key, value);
        }
    }

    public static Object getUserDatum(Object key) {
        return userData.get(key);
    }
}
