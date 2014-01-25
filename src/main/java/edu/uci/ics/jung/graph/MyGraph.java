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
import edu.uci.ics.jung.graph.event.GraphEventListener;
import model.MyEdge;
import model.MyVertex;
import model.dynamics.SISDynamics;

import java.io.Serializable;
import java.util.HashMap;

public class MyGraph<V, E> extends ObservableGraph<V, E> implements Serializable {

	private static ObservableGraph<MyVertex, MyEdge> INSTANCE;
	private static HashMap<Object, Object> userData;


	public MyGraph(Graph<V, E> delegate) {
		super(delegate);
		userData = new HashMap<Object, Object>();
	}

	/**
	 * returns an empty graph instance (with no edges or vertices)
	 *
	 * @return
	 */
	public static ObservableGraph getNewInstance() {
		setInstance(new MyGraph(new OrderedSparseMultigraph<MyVertex, MyEdge>()));
		return getInstance();
	}

	public static void setInstance(ObservableGraph newInstance) {
		ObservableGraph OLD_INSTANCE = INSTANCE;
		INSTANCE = newInstance;
		Controller.setAllSusceptible();
	}

	public static void fireNullEvent() {
		INSTANCE.fireGraphEvent(null);//indicate things have changed
	}

    public static void fireInfectionEvent(){
//        for(InfectionEventListener<V,E> listener : listenerList) {
//            listener.handleGraphEvent(evt);
//        }
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


	/**
	 * returns the signleton instance, which is to be displayed
	 *
	 * @return
	 */
	public static ObservableGraph<MyVertex, MyEdge> getInstance() {
		if (INSTANCE == null) {
			setInstance(getNewInstance());
		}

		return INSTANCE;
	}

	public static void flushInstance() {
		INSTANCE = null;
	}

	public static void setUserDatum(Object key, Object value) {
		if (key != null && value != null) {
			userData.put(key, value);
		}
	}

	public static Object getUserDatum(Object key) {
		return userData.get(key);
	}

	public static Object getUserDatum(Object key, Object defaultValue) {
		if (userData.containsKey(key)) {
			return userData.get(key);
		} else {
			return defaultValue;
		}
	}
}
