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
package edu.uci.ics.jung.graph;

/**
 * A singleton graph decorator which is observable and provides a mechanism for adding user data to the graph.
 * @author Miroslav Batchkarov
 */

import controller.Controller;
import controller.ExtraGraphEvent;
import controller.ExtraGraphEventListener;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import model.MyEdge;
import model.MyVertex;
import model.dynamics.SISDynamics;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MyGraph<V, E> extends ObservableGraph<V, E> implements Serializable {

    private ObservableGraph<V, E> INSTANCE;
    List<ExtraGraphEventListener<V, E>> extraListenerList;


    private HashMap<Object, Object> userData;

    public MyGraph(Graph<V, E> delegate) {
        super(delegate);
        userData = new HashMap<Object, Object>();
        extraListenerList = Collections.synchronizedList(
                new LinkedList<ExtraGraphEventListener<V, E>>());
    }

    public void addExtraGraphEventListener(ExtraGraphEventListener<V, E> l) {
        extraListenerList.add(l);
    }

    /**
     * Removes {@code l} as a listener to this graph.
     */
    public void removeExtraGraphEventListener(GraphEventListener<V, E> l) {
        extraListenerList.remove(l);
    }

    /**
     * returns an empty graph instance (with no edges or vertices)
     *
     * @return
     */
    public ObservableGraph getNewInstance() {
        setInstance(new MyGraph(new OrderedSparseMultigraph<MyVertex, MyEdge>()));
        return getInstance();
    }

    public void setInstance(ObservableGraph newInstance) {
        INSTANCE = newInstance;
        fireExtraEvent(new ExtraGraphEvent.GraphReplacedEvent<V, E>(INSTANCE));
    }

    public void fireEvent(GraphEvent evt) {
        for (GraphEventListener<V, E> listener : super.listenerList) {
            listener.handleGraphEvent(evt);
        }
    }

    public void fireExtraEvent(ExtraGraphEvent evt) {
        for (ExtraGraphEventListener<V, E> listener : extraListenerList) {
            listener.handleExtraGraphEvent(evt);
        }
    }

    public void setDefaultSimulationSettings() {
        setUserDatum("dynamics",
                new SISDynamics(0.1, 0.1, 0.1, 0.1));

        //attach the running time to the graph
        setUserDatum("time", new Integer(100));

        //attach the speed multiplier to the graph
        setUserDatum("speed", 200);
        //make sure the graphs is in a proper state
    }


    /**
     * returns the signleton instance, which is to be displayed
     *
     * @return
     */
    public ObservableGraph<V, E> getInstance() {
        if (INSTANCE == null) {
            setInstance(getNewInstance());
        }

        return INSTANCE;
    }

    public void flushInstance() {
        INSTANCE = null;
    }

    public void setUserDatum(Object key, Object value) {
        if (key != null && value != null) {
            userData.put(key, value);
        }
    }

    public Object getUserDatum(Object key) {
        return userData.get(key);
    }

    public Object getUserDatum(Object key, Object defaultValue) {
        if (userData.containsKey(key)) {
            return userData.get(key);
        } else {
            return defaultValue;
        }
    }
}
