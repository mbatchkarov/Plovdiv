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

import controller.simulation.Simulator;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.layout.PersistentLayout;
import model.EpiState;
import model.MyEdge;
import model.MyVertex;
import model.SimulationDynamics;
import model.factories.EdgeFactory;
import model.factories.GraphFactory;
import model.factories.VertexFactory;
import view.Display;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import view.BackgroundImageController;

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

    public Controller(Stats stats, MyGraph g) {
        this.g = g;
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
    public void updateCounts() {
        this.g.updateCounts();
    }

    public void setAllSusceptible() {
        this.g.setAllSusceptible();
        sim.resetSimulation();
    }

    /**
     * Sets all undefined nodes to susceptible. Sets all resistant nodes in a
     * non-SIR epidemic to susceptible
     */
    public void validateNodeStates() {
        Iterator i = this.g.getVertices().iterator();
        while (i.hasNext()) {
            MyVertex current = ((MyVertex) i.next());
            if (current.isResistant()
                    && !(this.g.getDynamics().getType() == SimulationDynamics.DynamicsType.SIR)) {
                current.setEpiState(EpiState.SUSCEPTIBLE);
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
    public void load(VisualizationViewer vv, String path) throws IOException {
        MyGraph g = PajekParser.load(path, getGraphFactory(),
                getVertexFactory().reset(),
                getEdgeFactory().reset());
        this.g.setInstance(g);
        gui.setVertexRenderer();
        IOClass.loadLayout(gui, path);
        BackgroundImageController.getInstance().checkForAndLoadBackgroundImage(vv, path);
        BackgroundImageController.getInstance().loadCustomColors(vv, g);
    }

    public void save(String path, MyGraph g, PersistentLayout layout) throws IOException {
        if (path != "nullnull") {
            //if the user pressed cancel, nullnull will be passed to this method
            PajekParser.save(path + ".graph", g);
            layout.persist(path + ".layout");
            BackgroundImageController.getInstance().saveBackgroundImage(path);
        }
    }

    //=========!!!all generated/loaded graphs will appear in a new window!!!=========
    //-----------GENERATION FUNCTIONALITY-------------------
    public void generateRandom(int a, int b, boolean autodetermineIconType) {
        this.g.setInstance(Generator.generateRandom(a, b, this, autodetermineIconType));
        gui.setVertexRenderer();
    }

    public void generate4Lattice(int a, int b, int nodeDensity, boolean autodetermineIconType) {
        MyGraph newGraph = Generator.generateRectangularLattice(a, b, this, autodetermineIconType);
        newGraph.getLayoutParameters().setNodeDensity(nodeDensity);
        this.g.setInstance(newGraph);
        gui.setVertexRenderer();
    }

    public void generate6Lattice(int a, int b, int nodeDensity, boolean autodetermineIconType) {
        MyGraph newGraph = Generator.generateHexagonalLattice(a, b, this, autodetermineIconType);
        newGraph.getLayoutParameters().setNodeDensity(nodeDensity);
        this.g.setInstance(newGraph);
        gui.setVertexRenderer();
    }

    public void generateKleinbergSmallWorld(int m, int n, double c, boolean autodetermineIconType) {
        this.g.setInstance(Generator.generateKleinbergSmallWorld(m, n, c, this, autodetermineIconType));
        gui.setVertexRenderer();
    }

    public void generateScaleFree(int a, int b, int c, boolean autodetermineIconType) {
        this.g.setInstance(Generator.generateScaleFree(a, 1, c, this, autodetermineIconType));
        gui.setVertexRenderer();
    }

    public void generateEppsteinPowerLaw(int numVert, int numEdges, int r, boolean autodetermineIconType) {
        this.g.setInstance(Generator.generateEppsteinPowerLaw(numVert, numEdges, r, this, autodetermineIconType));
        gui.setVertexRenderer();
    }

    public void generateEmptyGraph() {
        getEdgeFactory().reset();
        getVertexFactory().reset();
        this.g.setInstance(getGraphFactory().create());
        gui.setVertexRenderer();
    }

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
            if (!v[next].isInfected()) {
                v[next].setEpiState(EpiState.INFECTED);
                i++; //only increment if infection occurred- number of infections guaranteed
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

        final Display d = new Display(stats, cont, g); // display uses stats
        cont.setGui(d); // so that controller can trigger updates

        g.addGraphEventListener(d); // so that gui will update when graph changes
        g.addExtraGraphEventListener(d); // so that gui will update when graph changes

        g.addExtraGraphEventListener(stats); // so that stats are recomputed when graph changes
        g.addGraphEventListener(stats); // so that stats will update on graph events

        //display a graph
        d.handlingEvents = true;
        cont.generateScaleFree(20, 1, 1, true);
        d.initDemoLayout();
    }

}
