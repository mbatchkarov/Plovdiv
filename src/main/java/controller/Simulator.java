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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.util.Pair;
import model.EpiState;
import model.MyEdge;
import model.MyVertex;
import model.Strings;
import model.dynamics.Dynamics;
import model.dynamics.SISDynamics;
import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/**
 * @author reseter
 */
public class Simulator {
    private Random rng;
    private SimModelThread thread;
    private Double recoveryProb;
    private Double infectionProb;
    private int sleepTime;
    private Dynamics dynamics;
    private volatile int stepNumber;
    private double beta;
    private CircularFifoBuffer<Integer> xValues;
    private CircularFifoBuffer<Integer> yValues;
    private boolean doOneStepOnly;
    private final int WINDOW_WIDTH = 50;
    private MyGraph g;
    private Stats stats;
    private Controller controller;

    public Simulator(MyGraph g, Stats stats, Controller controller) {
        this.g = g;
        this.stats = stats;
        this.controller = controller;
        rng = new Random();
        sleepTime = 200;
        doOneStepOnly = false;

        xValues = new CircularFifoBuffer<Integer>(WINDOW_WIDTH);
        yValues = new CircularFifoBuffer<Integer>(WINDOW_WIDTH);

        thread = new SimModelThread("sim-thread");
        //stuff below needed?
        thread.pause();
        thread.start();
    }

    public void resetSimulation() {
        stepNumber = 0;
        xValues.clear();
        yValues.clear();

    }

    private void doStep(double beta, ObservableGraph<MyVertex, MyEdge> g, Double recProb, Double infProb) {
        Collection<MyEdge> edges = g.getEdges();
        Collection<MyVertex> vertices = g.getVertices();
        HashMap<MyEdge, Pair> edgesToAdd = new HashMap<MyEdge, Pair>();
        for (MyEdge current : edges) { //for all edges check for infection if I-S or S-I
            MyVertex first = g.getEndpoints(current).getFirst();
            MyVertex second = g.getEndpoints(current).getSecond();
            //I-S
            if (first.isInfected() && second.isSusceptible()) {
                this.checkForInfection(second, current, infProb, beta, edgesToAdd);
            }
            //S-I
            if (second.isInfected() && first.isSusceptible()) {
                this.checkForInfection(first, current, infProb, beta, edgesToAdd);
            }
        }

        for (MyVertex v : vertices) {
            //for all infected nodes check for recovery
            if (v.isInfected()) {
                this.checkForRecovery(v, recProb);
            }
        }
        //modify graph structure
        for (MyEdge e : edgesToAdd.keySet()) {
            g.removeEdge(e);
            g.addEdge(e, edgesToAdd.get(e));
        }

        //here the next state of each vertex should be known, updating
        for (MyVertex ssv : vertices) {
            //set the state to be the one in the next-state-collection, just calculated for it
            ssv.advanceEpiState();
        }
        //record changes in number of inf/sus/res nodes
        controller.updateCounts();
        this.stats.recalculateAll();
    }

    /**
     * prepares the data
     *
     * @param x
     * @param y
     * @return
     */
    private ChartPanel getInfectedCountChart(CircularFifoBuffer<Integer> x,
                                             CircularFifoBuffer<Integer> y,
                                             Dimension maxSize) {

        Integer[] xarr = new Integer[x.size()];
        Integer[] yarr = new Integer[y.size()];
        xarr = x.toArray(xarr);
        yarr = y.toArray(yarr);

        XYSeries series1 = new XYSeries("Infected vertices");
        for (int i = 0; i < xarr.length; i++) {
            series1.add(xarr[i], yarr[i]);
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);

        // create the chart
        JFreeChart jfreechart = ChartFactory.createXYLineChart("", "", "Infected",
                                                               dataset, PlotOrientation.VERTICAL, false, false, false);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        NumberAxis yAxis = (NumberAxis) xyPlot.getRangeAxis();
        yAxis.setRange(0, g.getVertexCount());
        yAxis.setTickUnit(new NumberTickUnit(5));

        int maxHeight = (int) maxSize.getHeight() - 34;
        int maxWidth = (int) maxSize.getWidth() - 34;

        return new ChartPanel(jfreechart, maxWidth, maxHeight, 50, 50, maxWidth, maxHeight,
                              true, true, false, false, false, false, true);
    }

    public void pauseSim() {
        thread.pause();
    }

    public void resumeSim() {
        thread.unpause();
    }

    public void resumeSimForOneStep() {
        doOneStepOnly = true;
        thread.unpause();
    }


    /**
     * Checks if the SUSCEPTIBLE node vertex second will get infected at this
     * simSleepTime step and colour the edge from which the infection came The
     * vertex is assumed vy this method to have been in contact with an infected
     * node
     *
     * @param vertex the vertex
     */
    private void checkForInfection(MyVertex vertex, MyEdge currentEdge, double infProb, double beta,
                                   HashMap<MyEdge, Pair> edgesToAdd) {
//        System.out.println("checking for infection in thread " + Thread.currentThread().getName());
        //compute a random probability
        Double randomProb = Math.abs((double) rng.nextInt() / new Double(Integer.MAX_VALUE));
        //if this chap is unlucky
        if (randomProb < infProb) {
            Double randomProb1 = Math.abs((double) rng.nextInt() / (double) Integer.MAX_VALUE);

            if (randomProb1 < dynamics.getTau() / (dynamics.getTau() + beta)) {
                //see email 21 DEC 2009, 13:16
                //put in the "waiting list" to be infected
                vertex.setNextEpiState(dynamics.getNextState(vertex));
            } else {
                //break current connection and try to reconnect to another susceptible node
                int numSus = (Integer) g.getUserDatum(Strings.numSus);
                if (numSus > 0) {
                    ArrayList<MyVertex> sus = new ArrayList<MyVertex>(numSus);
                    for (Object x : g.getVertices()) {
                        MyVertex v = (MyVertex) x;
                        if (v.isSusceptible()) {
                            sus.add(v);
                        }
                    }
                    boolean done = false;
                    MyVertex[] v = new MyVertex[1];
                    v = sus.toArray(v);
                    int first = sus.indexOf(vertex);
                    while (!done) {
                        int second = rng.nextInt(numSus);
                        if (first != second && !g.isNeighbor(v[first], v[second])) {
                            MyEdge e = controller.getEdgeFactory().create();
                            e.setWeigth(1.0);
                            e.setUserDatum(Strings.infected, false);
                            edgesToAdd.put(currentEdge, new Pair(v[first], v[second]));
                            controller.updateDisplay();
                            controller.updateCounts();
                            done = true;
                        }
                    }
                }
            }
        }

    }

    /**
     * Checks if the given vertex will recover at this step, it is assumed to be
     * infected, so make your own checks
     *
     * @param recProb
     */
    private void checkForRecovery(MyVertex vertex, double recProb) {
        double randomProb = Math.abs((double) rng.nextInt() / (double) Integer.MAX_VALUE);
        if (randomProb < recProb) {
            //put this vertex on the "waiting list" for recovery
            vertex.setNextEpiState(dynamics.getNextState(vertex));
        }
    }

    public void updateInfectedCountGraph(JPanel statsPanel) {
        xValues.add(stepNumber);
        yValues.add((Integer) g.getUserDatum(Strings.numInfected, 0));
        Dimension maxChartSize = statsPanel.getSize();
        ChartPanel panel = getInfectedCountChart(xValues, yValues, maxChartSize);
        statsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        statsPanel.removeAll();
        statsPanel.add(panel);
        statsPanel.validate();
        statsPanel.revalidate();
        panel.repaint();
    }

    /**
     * A thread to run the simulation in. Based on code from Green Light
     * District: http://sourceforge.net/projects/stoplicht/
     *
     * @author Joep Moritz
     * @author Miroslav Batchkarov
     */
    class SimModelThread extends Thread {

        private final int WINDOW_WIDTH = 50;

        /**
         * Is the thread suspended?
         */
        private volatile boolean suspended;
        /**
         * Is the thread alive? If this is set to false, the thread will die
         * gracefully
         */
        private volatile boolean alive;

        /**
         * Starts the thread.
         */
        public SimModelThread(String name) {
            super(name);
            alive = true;
            suspended = true;
        }

        /**
         * Suspends the thread.
         */
        public synchronized void pause() {
            suspended = true;
        }

        /**
         * Resumes the thread.
         */
        public synchronized void unpause() {
            suspended = false;
            notify();
        }

        /**
         * Stops the thread. Invoked when the program exits. This method cannot
         * be named stop().
         */
        public synchronized void die() {
            alive = false;
            interrupt();
        }


        /**
         * Returns true if the thread is not suspended and not dead
         */
        public boolean isRunning() {
            return !suspended && alive;
        }

        /**
         * Invokes Model.doStep() and sleeps for sleepTime milliseconds
         */
        public void run() {
            xValues = new CircularFifoBuffer<Integer>(WINDOW_WIDTH);
            yValues = new CircularFifoBuffer<Integer>(WINDOW_WIDTH);

            while (alive) {
                try {
                    sleep(sleepTime);
                    synchronized (this) {
                        while (suspended && alive && !doOneStepOnly) {
                            wait();
                        }
                    }
                    readSimSettingsFromGraph();
                    doStepWithCurrentSettings();
                    //make sure
                    if (doOneStepOnly) {
                        doOneStepOnly = false;
                        pause();
                    }

                } catch (InterruptedException e) {
                    System.err.println("Interrupted");
                    e.printStackTrace();
                }
            }
            System.out.println("Alive: " + isAlive());
            System.out.println("Running: " + isRunning());
            System.out.println("Step no " + stepNumber);

        }

        private void readSimSettingsFromGraph() {
            dynamics = (Dynamics) g.getUserDatum(Strings.dynamics);

            beta = 0;
            if (dynamics instanceof SISDynamics) {
                beta = ((SISDynamics) dynamics).getEdgeBreakingRate();
            }
            sleepTime = (Integer) g.getUserDatum(Strings.simSleepTime);

            //probabilities based on per-link traversal of the graph
            //probability of recovery is constant, and in this case so is the infection probability
            recoveryProb = 1d - Math.exp((-1 * (dynamics.getGama() * dynamics.getDeltaT())));
            infectionProb = 1d - Math.exp((-1 * ((dynamics.getTau() + beta) * dynamics.getDeltaT())));
        }
    }

    public void doStepWithCurrentSettings() {
        doStep(beta, g, recoveryProb, infectionProb);
        g.fireExtraEvent(new ExtraGraphEvent.SimStepCompleteEvent(g));//todo fire event at the
        stepNumber++;
    }

    private void updateStatisticsDisplay(JPanel statsPanel) {
        xValues.add(stepNumber);
        yValues.add((Integer) g.getUserDatum(Strings.numInfected));
        ChartPanel panel = getInfectedCountChart(xValues, yValues, statsPanel.getSize());
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        statsPanel.removeAll();
        statsPanel.add(panel);
        statsPanel.validate();
        statsPanel.revalidate();
        panel.repaint();
    }
}
