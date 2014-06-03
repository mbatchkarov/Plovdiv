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
 * A singleton graph decorator which is observable and provides a mechanism for
 * adding user data to the graph.
 *
 * @author Miroslav Batchkarov
 */
import controller.ExtraGraphEvent;
import controller.ExtraGraphEventListener;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;

import model.EpiState;
import model.MyEdge;
import model.MyVertex;
import model.SimulationDynamics;

import java.io.Serializable;
import java.util.*;

import model.VertexIcon;

public class MyGraph<V, E> extends ObservableGraph<V, E> implements Serializable, UndirectedGraph<V, E> {

    private List<ExtraGraphEventListener<V, E>> extraListenerList;

    private SimulationDynamics dynamics;
    private int numSusceptible, numInfected, numResistant;
    private int sleepTimeBetweenSteps; // how long to wait before another simulation step is made (ms)

    private LayoutParameters layoutParameters;

    public MyGraph(Graph<V, E> delegate) {
        super(delegate);
        this.dynamics = null;
        extraListenerList = Collections.synchronizedList(
                new LinkedList<ExtraGraphEventListener<V, E>>());
        numInfected = 0;
        numResistant = 0;
        numSusceptible = 0;
        sleepTimeBetweenSteps = 0;
        layoutParameters = new LayoutParameters();
    }

    public void setInstance(MyGraph<V, E> newInstance) {
        delegate = newInstance.delegate;
        updateCounts();
        setLayoutParameters(newInstance.getLayoutParameters());
        fireExtraEvent(new ExtraGraphEvent(delegate, ExtraGraphEvent.GRAPH_REPLACED));
    }
    public void setInstance(Graph<V, E> newInstance) {
        setInstance(new MyGraph<V, E>(newInstance));
    }


    public void addExtraGraphEventListener(ExtraGraphEventListener<V, E> l) {
        extraListenerList.add(l);
    }

    public SimulationDynamics getDynamics() {
        return dynamics;
    }

    public void setDynamics(SimulationDynamics dynamics) {
        this.dynamics = dynamics;
    }

    public int getNumSusceptible() {
        return numSusceptible;
    }

    public int getNumInfected() {
        return numInfected;
    }

    public int getNumResistant() {
        return numResistant;
    }

    public int getSleepTimeBetweenSteps() {
        return sleepTimeBetweenSteps;
    }

    public void setSleepTimeBetweenSteps(int newValue) {
        this.sleepTimeBetweenSteps = newValue;
    }

    public void setEdgeWeight(MyEdge e, double newWeight) {
        if (this.getEdges().contains(e)) {
            e.setWeigth(newWeight);
            fireExtraEvent(new ExtraGraphEvent(this, ExtraGraphEvent.METADATA_CHANGED));
        } else {
            throw new IllegalStateException("Attempted to alter edge that does not belong to this graph");
        }
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

    @Override
    public String toString() {
        return "MyGraph{"
                + "delegate=" + this.delegate
                + ", extraListenerList=" + extraListenerList
                + '}';
    }

    /**
     * Attaches counters of the numbers of infected/susceptible/resistant to all
     * graphs
     */
    public void updateCounts() {
        int ns = 0, ni = 0, nr = 0;
        for (Object xx : delegate.getVertices()) {//count how many nodes are in each state
            MyVertex yy = (MyVertex) xx;
            if (yy.isInfected()) {
                ni++;
            } else if (yy.isResistant()) {
                nr++;
            } else {
                ns++;
            }
        }
        this.numResistant = nr;
        this.numInfected = ni;
        this.numSusceptible = ns;
    }

    /**
     * sets all vertices to susceptible state and gives them a generation index
     * 0 (ie. not yet infected).
     */
    public void setAllSusceptible() {
        Iterator i = delegate.getVertices().iterator();
        MyVertex x;
        while (i.hasNext()) {
            x = (MyVertex) i.next();
            x.setEpiState(EpiState.SUSCEPTIBLE);
        }
        updateCounts();
    }

    /**
     * Analyzes all of the vertices and returns the predominant style of icons
     * used. The simple icons are preferred if both styles are used in equal
     * amount of vertices.
     */
    private int getDominantIconStyle() {
        if (getVertices().size() < 1) {
            return VertexIcon.STYLE_SIMPLE;
        }

        int simpleIconCount = 0;
        int otherIconCount = 0;
        for (Object vertex : getVertices()) {
            int iconStyle = ((MyVertex) vertex).getIcon().getStyle();
            if (iconStyle == VertexIcon.STYLE_SIMPLE) {
                simpleIconCount++;
            } else {
                otherIconCount++;
            }
        }

        if (simpleIconCount >= otherIconCount) {
            return VertexIcon.STYLE_SIMPLE;
        } else {
            return VertexIcon.STYLE_3D;
        }
    }

    /**
     * Analyzes all of the vertices and returns the predominant type of icon
     * used. If there are two types of icons used in equal amount of vertices,
     * the first one (in order of declaration) is selected.
     */
    private int getDominantIconType() {
        if (getVertices().size() < 1) {
            return VertexIcon.TYPE_USER;
        }

        HashMap<Integer, Integer> iconTypeCounts = new HashMap<Integer, Integer>();
        for (Object vertex : getVertices()) {
            int iconType = ((MyVertex) vertex).getIcon().getType();
            Integer iconTypeCount = iconTypeCounts.get(iconType);
            if (iconTypeCount == null) {
                iconTypeCounts.put(iconType, 1);
            } else {
                iconTypeCounts.put(iconType, iconTypeCount + 1);
            }
        }

        Map.Entry<Integer, Integer> maxEntry = null;
        for (Map.Entry<Integer, Integer> entry : iconTypeCounts.entrySet()) {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                maxEntry = entry;
            }
        }

        return maxEntry.getKey();
    }

    public VertexIcon getDominantVertexIcon() {
        if (layoutParameters.getDominantVertexIcon() == null) {
            updateDominantVertexIcon();
        }
        return layoutParameters.getDominantVertexIcon();
    }

    public void updateDominantVertexIcon() {
        layoutParameters.setDominantVertexIcon(new VertexIcon(getDominantIconType(), getDominantIconStyle()));
    }

    /**
     * @return the layoutParameters
     */
    public LayoutParameters getLayoutParameters() {
        return layoutParameters;
    }

    /**
     * @param layoutParameters the layoutParameters to set
     */
    public void setLayoutParameters(LayoutParameters layoutParameters) {
        this.layoutParameters = layoutParameters;
    }
}
