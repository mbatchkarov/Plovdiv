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

import controller.ExtraGraphEvent;
import controller.ExtraGraphEventListener;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import model.EpiState;
import model.MyEdge;
import model.MyVertex;
import model.Strings;
import model.dynamics.SISDynamics;

import java.io.Serializable;
import java.util.*;

public class MyGraph<V, E> extends ObservableGraph<V, E> implements Serializable {

    List<ExtraGraphEventListener<V, E>> extraListenerList;


    private HashMap<Object, Object> userData;

    public MyGraph(Graph<V, E> delegate) {
        super(delegate);
        userData = new HashMap<Object, Object>();
        extraListenerList = Collections.synchronizedList(
                new LinkedList<ExtraGraphEventListener<V, E>>());
    }

    public void setInstance(MyGraph newInstance) {
        delegate = newInstance.delegate;
        setAllSusceptible();
        updateCounts();
        fireExtraEvent(new ExtraGraphEvent.GraphReplacedEvent<V, E>(delegate));
    }

    public void addExtraGraphEventListener(ExtraGraphEventListener<V, E> l) {
        extraListenerList.add(l);
    }


    public void fireGraphEvent(GraphEvent evt) {
        for (GraphEventListener<V, E> listener : super.listenerList) {
            listener.handleGraphEvent(evt);
        }
    }

    public void fireExtraEvent(ExtraGraphEvent evt) {
        for (ExtraGraphEventListener<V, E> listener : extraListenerList) {
            listener.handleExtraGraphEvent(evt);
        }
    }

    public List<ExtraGraphEventListener<V, E>> getExtraListenerList() {
        return extraListenerList;
    }

    public List<GraphEventListener<V, E>> getListenerList() {
        return listenerList;
    }

    public void setDefaultSimulationSettings() {
        setUserDatum("dynamics", new SISDynamics(0.1, 0.1, 0.1, 0.1));

        //attach the running time to the graph
        setUserDatum("time", new Integer(100));

        //attach the speed multiplier to the graph
        setUserDatum("speed", 200);
        //make sure the graphs is in a proper state
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

    @Override
    public String toString() {
        return "MyGraph{" +
               "delegate=" + this.delegate +
               ", extraListenerList=" + extraListenerList +
               ", userData=" + userData +
               '}';
    }


    /**
     * Attaches counters of the numbers of infected/susceptible/resistant to all
     * graphs
     */
    public void updateCounts() {
        int ns = 0, ni = 0, nr = 0;
        for (Object xx : delegate.getVertices()) {//count how many nodes are in each state
            MyVertex yy = (MyVertex) xx;
            if (yy.getUserDatum(Strings.state).equals(EpiState.INFECTED)) {
                ni++;
            } else if (yy.getUserDatum(Strings.state).equals(EpiState.RESISTANT)) {
                nr++;
            } else {
                ns++;
            }
        }
        this.setUserDatum(Strings.numInfected, ni);
        this.setUserDatum(Strings.numRes, nr);
        this.setUserDatum(Strings.numSus, ns);
    }

    /**
     * sets all vertices to susceptible state and gives them a generation index
     * 0 (ie. not yet infected).
     */
    public void setAllSusceptible() {
//        g.setUserDatum(Strings.steps, 0);
        Iterator i = delegate.getVertices().iterator();
        MyVertex x;
        while (i.hasNext()) {
            x = (MyVertex) i.next();
            x.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
//            x.setUserDatum(Strings.generation, new Integer(0));
        }
        Iterator j = delegate.getEdges().iterator();
        MyEdge e;
        while (j.hasNext()) {
            e = (MyEdge) j.next();
            e.setUserDatum(Strings.infected, false);
        }
        updateCounts();
    }
}
