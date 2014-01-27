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
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import model.EpiState;
import model.MyEdge;
import model.MyVertex;
import model.Strings;
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

    private EdgeFactory ef;
    private VertexFactory vf;
    private GraphFactory gf;
    private Display gui;
    private Simulator sim;

    private MyGraph g;

    private Controller(Stats stats, MyGraph g) {
        this.g = g;
        g.setDefaultSimulationSettings();
        sim = new Simulator(g, stats, this);
        validateNodeStates();
    }


    public void setGui(Display w) {
        gui = w;
        w.setVisible(true);
    }

    /**
     * @return the gui
     */
    public Display getGui() {
        return gui;
    }

    //auto-generated getters
    public EdgeFactory getEdgeFactory() {
        if (ef == null) {
            ef = new EdgeFactory();
        }
        return ef;
    }

    public GraphFactory getGraphFactory() {
        if (gf == null) {
            gf = new GraphFactory();
        }
        return new GraphFactory(getGraph());
    }

    public VertexFactory getVertexFactory() {
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
    public HashMap getEdgeWeigths() {
        HashMap<MyEdge, Double> m = new HashMap<MyEdge, Double>();
        Iterator i = this.g.getEdges().iterator();
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
    public void setAllSusceptible(MyGraph g) {
//        g.setUserDatum(Strings.steps, 0);
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
    public void updateCounts() {
        ObservableGraph g = this.g;
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
        this.g.setUserDatum(Strings.numInfected, ni);
        this.g.setUserDatum(Strings.numRes, nr);
        this.g.setUserDatum(Strings.numSus, ns);
    }

    /**
     * Sets all undefined nodes to susceptible Sets all resistant nodes in a
     * non-SIR epidemic to susceptible
     */
    public void validateNodeStates() {
        Iterator i = this.g.getVertices().iterator();
        while (i.hasNext()) {
            MyVertex current = ((MyVertex) i.next());
            if (current.getUserDatum(Strings.state) == null) {
                current.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
            }
            if (current.getUserDatum(Strings.state).equals(EpiState.RESISTANT) &&
                (this.g.getUserDatum(Strings.dynamics) instanceof SIRDynamics)) {
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
    public void load(String path) {
        try {
            g.setInstance(PajekParser.load(path, getGraphFactory(),
                                           getVertexFactory(), getEdgeFactory()));
            setAllSusceptible(g);
        } catch (Exception ex) {
            Exceptions.showReadWriteErrorNotification(ex);
        }
        gui.redisplayCompletely();
    }

    public void save(String path, ObservableGraph g) {
        try {
            PajekParser.save(path, g);
        } catch (Exception ex) {
            Exceptions.showReadWriteErrorNotification(ex);
        }
    }

    //=========!!!all generated/loaded graphs will appear in a new window!!!=========
    //-----------GENERATION FUNCTIONALITY-------------------
    public void generateRandom(int a, int b) {
        this.g.setInstance(Generator.generateRandom(a, b, this));
        gui.redisplayCompletely();
    }

    public void generate4Lattice(int a, int b) {
        this.g.setInstance(Generator.generateRectangularLattice(a, b, this));
        gui.redisplayCompletely();

    }

    public void generate6Lattice(int a, int b) {
        this.g.setInstance(Generator.generateHexagonalLattice(a, b, this));
        gui.redisplayCompletely();
    }

    public void generateKleinbergSmallWorld(int m, int n, double c) {
        this.g.setInstance(Generator.generateKleinbergSmallWorld(m, n, c, this));
        gui.redisplayCompletely();
    }

    public void generateScaleFree(int a, int b, int c) {
        this.g.setInstance(Generator.generateScaleFree(a, 1, c, this));
        gui.redisplayCompletely();
    }

    public void generateEppsteinPowerLaw(int numVert, int numEdges, int r) {
        this.g.setInstance(Generator.generateEppsteinPowerLaw(numVert, numEdges, r, this));
        gui.redisplayCompletely();
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
    public void updateDisplay() {
        Display.vv.repaint();
    }

    /**
     * Infects a given number of vertices in the given graph at random. The
     * number is assumed to be <= number of vertices in g
     *
     * @param g
     * @param number
     */
    public void infectNodes(Graph g, int number) {
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


    public Simulator getSimulator() {
        return sim;
    }

    public MyGraph getGraph() {
        return g;
    }

    public static void main(String[] args) {
        MyGraph g = new GraphFactory().create();


        Stats stats = new Stats(g);
        Controller cont = new Controller(stats, g);  //controller
        cont.setAllSusceptible(g);

        final Display d = new Display(stats, cont, g); // display uses stats
        cont.setGui(d); // so that controller can trigger updates

        g.addGraphEventListener(d); // so that gui will update when graph changes
        g.addExtraGraphEventListener(d); // so that gui will update when graph changes

        g.addExtraGraphEventListener(stats); // so that stats are recomputed when graph changes
        g.addGraphEventListener(stats); // so that stats will update on graph events

        //display a graph
        cont.generateScaleFree(20, 1, 1);
        d.handlingEvents = true;
    }

}
