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

import edu.uci.ics.jung.graph.Graph;
import model.*;
import model.dynamics.SIRDynamics;
import model.factories.EdgeFactory;
import model.factories.GraphFactory;
import model.factories.VertexFactory;
import view.Display;
import view.Exceptions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

/**
 * @author reseter
 */
public class Controller {

    private static EdgeFactory ef;
    private static VertexFactory vf;
    private static GraphFactory gf;
    private static Display activeWindow;
    private static Simulator sim;
    private static Controller INSTANCE;

    private Controller() {
        Graph g = MyGraph.getInstance();//initializes the instance
        MyGraph.setDefaultSimulationSettings();
        sim = new Simulator();
    }

    public static Controller getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Controller();
        }
        return INSTANCE;
    }

    public static void setActiveWindow(Display w) {
        activeWindow = w;
        w.setVisible(true);
    }

    /**
     * @return the activeWindow
     */
    public static Display getActiveWindow() {
        return activeWindow;
    }

    //auto-generated getters
    public static EdgeFactory getEdgeFactory() {
        if (ef == null) {
            ef = new EdgeFactory();
        }
        return ef;
    }

    public static GraphFactory getGraphFactory() {
        if (gf == null) {
            gf = new GraphFactory();
        }
        return gf;
    }

    public static VertexFactory getVertexFactory() {
        if (vf == null) {
            vf = new VertexFactory();
        }
        return vf;
    }

    /**
     * returns a mapping of edges agains their weigths
     *
     * @return
     */
    public static HashMap getEdgeWeigths() {
        HashMap<MyEdge, Double> m = new HashMap<MyEdge, Double>();
        Iterator i = MyGraph.getInstance().getEdges().iterator();
        while (i.hasNext()) {
            MyEdge e = (MyEdge) i.next();
            m.put(e, e.getWeigth());
        }

        return m;
    }

    //----------SIMULATION CONTROLS
    /**
     * sets all vertices to susceptible state and gives them a generation index
     * 0 (ie. not yet infected).
     */
    public static void setAllSusceptible() {
//        g.setUserDatum(Strings.steps, 0);
        MyGraph g = MyGraph.getInstance();
        Iterator i = g.getVertices().iterator();
        MyVertex x;
        while (i.hasNext()) {
            x = (MyVertex) i.next();
            x.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
//            x.setUserDatum(Strings.generation, new Integer(0));
        }
        Iterator j = g.getEdges().iterator();
        MyEdge e;
        while (j.hasNext()) {
            e = (MyEdge) j.next();
            e.setUserDatum(Strings.infected, false);
        }
        updateCounts();
    }

    /**
     * Attaches counters of the numbers of infected/susceptible/resistant to all
     * graphs
     */
    public static void updateCounts() {
        System.out.println("updating counts in thread " + Thread.currentThread().getName());
        MyGraph g = MyGraph.getInstance();
        int ns = 0, ni = 0, nr = 0;
        for (Object xx : g.getVertices()) {//count how many nodes are in each state
            MyVertex yy = (MyVertex) xx;
            if (yy.getUserDatum(Strings.state).equals(EpiState.INFECTED)) {
                ni++;
            } else if (yy.getUserDatum(Strings.state).equals(EpiState.RESISTANT)) {
                nr++;
            } else {
                ns++;
            }
        }

//            System.out.println("graph  " + i);
//            System.out.println("number inf " + ni);
//            System.out.println("number  sus" + ns);
//            System.out.println("number  res" + nr);

        MyGraph.setUserDatum(Strings.numInfected, ni);
        MyGraph.setUserDatum(Strings.numRes, nr);
        MyGraph.setUserDatum(Strings.numSus, ns);
    }

    /**
     * Sets all undefined nodes to susceptible Sets all resistant nodes in a
     * non-SIR epidemic to susceptible
     *
     */
    public static void validateNodeStates() {
        Iterator i = MyGraph.getInstance().getVertices().iterator();
        while (i.hasNext()) {
            MyVertex current = ((MyVertex) i.next());
            if (current.getUserDatum(Strings.state) == null) {
                current.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
            }
            if (current.getUserDatum(Strings.state).equals(EpiState.RESISTANT) && (MyGraph.getUserDatum(Strings.dynamics) instanceof SIRDynamics)) {
                current.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
            }
        }

    }


    //------------SAVE/ LOAD FUNCTIONALITY--------------
    /**
     * Asks the parser to create a graph from file, makes a frame and displays
     * the graph in it
     *
     * @param path
     */
    public static void load(String path) {
        try {
            MyGraph.setInstance(PajekParser.load(path));
            Controller.setAllSusceptible();
//            Simulator.stepNumber =
        } catch (Exception ex) {
            Exceptions.showReadWriteErrorNotification(ex);
        }
        Display.redisplayCompletely();
    }

    public static void save(String path, MyGraph g) {
        try {
            PajekParser.save(path, g);
        } catch (Exception ex) {
            Exceptions.showReadWriteErrorNotification(ex);
        }
    }

    //=========!!!all generated/loaded graphs will appear in a new window!!!=========
    //-----------GENERATION FUNCTIONALITY-------------------
    public static void generateRandom(int a, int b) {
        MyGraph.setInstance(Generator.generateRandom(a, b));
        Controller.setAllSusceptible();
        Display.redisplayCompletely();

    }

    public static void generate4Lattice(int a, int b) {
        MyGraph.setInstance(Generator.generateRectangularLattice(a, b));
        Display.redisplayCompletely();
        Controller.setAllSusceptible();
    }

    public static void generate6Lattice(int a, int b) {
        MyGraph.setInstance(Generator.generateHexagonalLattice(a, b));
        Controller.setAllSusceptible();
        Display.redisplayCompletely();
    }

    public static void generateKleinbergSmallWorld(int m, int n, double c) {
        MyGraph.setInstance(Generator.generateKleinbergSmallWorld(m, n, c));
        Controller.setAllSusceptible();
        Display.redisplayCompletely();
    }

    public static void generateScaleFree(int a, int b, int c) {
        MyGraph.setInstance(Generator.generateScaleFree(a, 1, c));
        Controller.setAllSusceptible();
        Display.redisplayCompletely();
    }

    public static void generateEppsteinPowerLaw(int numVert, int numEdges, int r) {
        MyGraph.setInstance(Generator.generateEppsteinPowerLaw(numVert, numEdges, r));
        Controller.setAllSusceptible();
        Display.redisplayCompletely();
    }

    //------------OPENING NEW DOCUMENTS--------------------
    /**
     * Puts an graph into the collection of graphs, creates a displaying frame
     * for it and starts displaying it.
     *
     * @param g
     */
    /**
     * Convenience pass-through to the display, prevents the simulators from
     * accessing the display directly
     */
    public static void updateDisplay() {
        Display.vv.repaint();
    }

    /**
     * Infects a given number of vertices in the given graph at random. The
     * number is assumed to be <= number of vertices in g
     *
     *
     *

     *
     * @param g
     * @param number
     */
    public static void infectNodes(Graph g, int number) {
        Object[] x = g.getVertices().toArray();
        MyVertex[] v = new MyVertex[x.length];
        for (int i = 0; i < v.length; i++) {
            v[i] = (MyVertex) x[i];
        }
        Random rand = new Random();
        int i = 0;
        while (i < number) {
            int next = rand.nextInt(v.length);
            if (!v[next].getUserDatum(Strings.state).equals(EpiState.INFECTED)) {
                v[next].setUserDatum(Strings.state, EpiState.INFECTED);
                i++; //only increment if infection occured- number of infections guaranteed
            }
        }
        updateCounts();
        updateDisplay();

    }

    public static void main(String[] args) {
        Controller cont = new Controller();  //controller
//        MyGraph g = MyGraph.getNewInstance();
        setAllSusceptible();
	    final Display d = new Display();
	    Controller.setActiveWindow(d);
	    MyGraph.getInstance().addGraphEventListener(d);
        sim.updateInfectedCountGraph();
    }
}
