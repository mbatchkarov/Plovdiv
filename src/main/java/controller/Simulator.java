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

import cern.jet.random.engine.MersenneTwister;
import edu.uci.ics.jung.graph.util.Pair;
import model.*;
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
import view.Display;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/**
 * @author reseter
 */
public class Simulator {
    //    private int speed;
//    private boolean notStopped;
    //    private Controller controller; //replaced with static access
//    private final MyGraph<MyVertex, MyEdge> g; //each simulator can run on only one graph

    private final MersenneTwister mt;
    //    private boolean notPaused;
    private final SimModelThread thread;
    private Double recoveryProb;
    private Double infectionProb;
    private int sleepTime;
    private Dynamics d;
    private int numSteps;
    private volatile int stepNumber;
    private double beta;
    private CircularFifoBuffer<Integer> xValues;
    private CircularFifoBuffer<Integer> yValues;
    private JFrame statsFrame;
    private boolean doOneStepOnly;

    public Simulator() {
//        this.g = g;
        mt = new MersenneTwister();

        sleepTime = 200;
        doOneStepOnly = false;
        thread = new SimModelThread("sim-thread");
        //stuff below needed?
        thread.start();
        thread.pause();
    }

    private void doStep(double beta, MyGraph<MyVertex, MyEdge> g, Double recProb, Double infProb) {
        System.out.println("Doing step " + stepNumber + " in thread " + Thread.currentThread().getName());
        Collection<MyEdge> edges = g.getEdges();
        Collection<MyVertex> vertices = g.getVertices();
        //make sure all nodes have a next state
        for (MyVertex current : vertices) {
            current.setUserDatum(Strings.next, current.getUserDatum("state"));
        }
        HashMap<MyEdge, Pair> edgesToAdd = new HashMap<MyEdge, Pair>();
        for (MyEdge current : edges) { //for all edges check for infection if I-S or S-I
            MyVertex first = g.getEndpoints(current).getFirst();
            MyVertex second = g.getEndpoints(current).getSecond();
            //I-S
            if (first.getUserDatum(Strings.state).equals(EpiState.INFECTED) && second.getUserDatum(Strings.state).equals(EpiState.SUSCEPTIBLE)) {
                this.checkForInfection(second, current, infProb, beta, edgesToAdd);
            }
            //S-I
            if (second.getUserDatum(Strings.state).equals(EpiState.INFECTED) && first.getUserDatum(Strings.state).equals(EpiState.SUSCEPTIBLE)) {
                this.checkForInfection(first, current, infProb, beta, edgesToAdd);
            }
        }

        for (MyVertex current : vertices) {//for all nodes check for recovery
            if (current.getUserDatum(Strings.state).equals(EpiState.INFECTED)) {
                this.checkForRecovery(current, recProb);
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
            ssv.setUserDatum(Strings.state, ssv.getUserDatum(Strings.next));
        }
        //record changes in number of inf/sus/res nodes
        Controller.updateCounts();
    }

    /**
     * prepares the data
     *
     * @param x
     * @param y
     * @return
     */
    private ChartPanel getPanelForDisplay(CircularFifoBuffer<Integer> x, CircularFifoBuffer<Integer> y) {

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
        JFreeChart jfreechart = ChartFactory.createXYLineChart("Infected vertices vs Time steps", "Time steps", "Individuals",
                dataset, PlotOrientation.VERTICAL, true, true, true);
        XYPlot xyPlot = (XYPlot) jfreechart.getPlot();
        NumberAxis domain = (NumberAxis) xyPlot.getRangeAxis();
        domain.setRange(0, MyGraph.getInstance().getVertexCount());
        domain.setTickUnit(new NumberTickUnit(5));

        //optional customization
        return new ChartPanel(jfreechart);
    }

    public void stopSim() {
        thread.die();
    }

    public void pauseSim() {
        thread.pause();
    }

    public void setNumSteps(int n) {
        numSteps = n;
    }

    public void resumeSim() {
        thread.unpause();
    }

    public void resumeSimForOneStep() {
        this.doOneStepOnly = true;
        thread.unpause();
    }

    public void startSim() {
        thread.start();
    }

    /**
     * Given a vertex, finds the number of infected neighbours it has
     *
     * @param v
     * @return
     */
    private int numInfectedNeighbours(MyVertex v) {
        int ans = 0;
        for (Object o : MyGraph.getInstance().getNeighbors(v)) {
            MyVertex current = (MyVertex) o;

            if (current.getUserDatum(Strings.state).equals(EpiState.INFECTED)) {
                ans++;
            }
        }
        return ans;
    }

    /**
     * Checks if the SUSCEPTIBLE node vertex second will get infected at this
     * time step and colour the edge from which the infection came The vertex is
     * assumed vy this method to have been in contact with an infected node
     *
     * @param vertex the vertex
     */
    private void checkForInfection(MyVertex vertex, MyEdge currentEdge, double infProb, double beta,
            HashMap<MyEdge, Pair> edgesToAdd) {
        System.out.println("checking for infection in thread " + Thread.currentThread().getName());
        //compute a random probability
        Double randomProb = Math.abs((double) mt.nextInt() / new Double(Integer.MAX_VALUE));
        //if this chap is unlucky
        if (randomProb < infProb) {

            Double randomProb1 = Math.abs((double) mt.nextInt() / (double) Integer.MAX_VALUE);

            if (randomProb1 < d.getTau() / (d.getTau() + beta)) {//see email 21 DEC 1009, 13:16
                //put in the "waiting list" to be infected
                vertex.setUserDatum(Strings.next, d.getNextState(vertex));
                //record when he got infected
//            vertex.setUserDatum(Strings.generation, (Integer) g.getUserDatum(Strings.steps) + 1);
                //colour the edge through which the disease spread
                currentEdge.setUserDatum(Strings.infected, true);
            } else {
                //break current connection and try to reconnect to another susceptible node
                int numSus = (Integer) MyGraph.getUserDatum(Strings.numSus);
                if (numSus > 0) {
                    ArrayList<MyVertex> sus = new ArrayList<MyVertex>(numSus);
                    for (Object x : MyGraph.getInstance().getVertices()) {
                        MyVertex v = (MyVertex) x;
                        if (v.getUserDatum(Strings.state).equals(EpiState.SUSCEPTIBLE)) {
                            sus.add(v);
                        }
                    }
                    Random r = new Random();
                    boolean done = false;
                    MyVertex[] v = new MyVertex[1];
                    v = sus.toArray(v);
                    int first = sus.indexOf(vertex);
                    while (!done) {
                        int second = r.nextInt(numSus);
                        if (first != second && !MyGraph.getInstance().isNeighbor(v[first], v[second])) {
                            MyEdge e = Controller.getEdgeFactory().create();//new MyEdge(g.getEdgeCount() + 3);   //todo wtf is the +3 for
                            e.setWeigth(1.0);
                            e.setUserDatum(Strings.infected, false);
//                            g.addEdge(e, (MyVertex) v[first], (MyVertex) v[second], EdgeType.UNDIRECTED);
//                            g.removeEdge(currentEdge);
                            //noinspection RedundantCast
                            edgesToAdd.put(currentEdge, new Pair(v[first], v[second]));
                            Controller.updateDisplay();
                            Controller.updateCounts();
//                            System.out.println("Rewiring");
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
//        compute a random number
        System.out.println("checking for recovery in thread " + Thread.currentThread().getName());
        double randomProb = Math.abs((double) mt.nextInt() / (double) Integer.MAX_VALUE);
        if (randomProb < recProb) {
            //put this vertex on the "waiting list" for recovery
            vertex.setUserDatum(Strings.next, d.getNextState(vertex));
            //record when it recovered
//            vertex.setUserDatum(Strings.generation, (Integer) g.getUserDatum(Strings.steps) + 1);
        } else {
        }
    }

    /**
     * A thread to run the simulation in. Based on code from Green Light
     * District: http://sourceforge.net/projects/stoplicht/
     *
     * @author Joep Moritz
     * @author Miroslav Batchkarov
     */
    class SimModelThread extends Thread {

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
         * The time in milliseconds this thread sleeps after a call to doStep()
         */
//        private int sleepTime;
        /**
         * Returns the current sleep time
         */
//        public int getSleepTime() {
//            return sleepTime;
//        }
//
//        /**
//         * Sets the sleep time
//         */
//        public void setSleepTime(int s) {
//            sleepTime = s;
//        }
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
         * Stops the thread. Invoked when the program exitst. This method cannot
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
            statsFrame = new JFrame("Epidemics statistics");
            statsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            statsFrame.setVisible(true);

            xValues = new CircularFifoBuffer<Integer>(100);
            yValues = new CircularFifoBuffer<Integer>(100);

            //main loop
            while (alive) {
                try {
//                    System.out.println("Thread " + Thread.currentThread().getName() + " sleeping");
                    sleep(sleepTime);
                    synchronized (this) {
                        while (suspended && alive && !doOneStepOnly) {
//                            System.out.println("Thread " + Thread.currentThread().getName() + " waiting for resume signal");
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
            //get simulation parameters
            stepNumber = 0;//TODO this must not be set here
//            numSteps = (Integer) MyGraph.getUserDatum(Strings.time);
//            sleepTime = (Integer) MyGraph.getUserDatum(Strings.speed);
            d = (Dynamics) MyGraph.getUserDatum(Strings.dynamics);

            beta = 0;
            if (d instanceof SISDynamics) {
                beta = ((SISDynamics) d).getEdgeBreakingRate();
            }

            //probabilities based on per-link traversal of the graph
            //probability of recovery is constant, and in this case so is the infection probability
            recoveryProb = 1d - Math.exp((-1 * (d.getGama() * d.getDeltaT())));
            infectionProb = 1d - Math.exp((-1 * ((d.getTau() + beta) * d.getDeltaT())));
            System.out.println("tau " + d.getTau());
            System.out.println("deltaT" + d.getDeltaT());
            System.out.println("gama " + d.getGama());
            System.out.println("recovery prob " + recoveryProb);
            System.out.println("inf prob " + infectionProb);
            System.out.println("sleep time " + sleepTime);
            System.out.println("required steps " + numSteps);
//            int numSteps = (int) ((double) time / d.getDeltaT());
        }
    }

    public void doStepWithCurrentSettings() {
        System.out.println("Doing step " + stepNumber + " in thread " + Thread.currentThread().getName());
        doStep(beta, MyGraph.getInstance(), recoveryProb, infectionProb);
        Display.redisplayPartially();
        updateStatisticsDisplay(statsFrame);
    }

    private void updateStatisticsDisplay(JFrame frame) {
        xValues.add(stepNumber++);
        yValues.add((Integer) MyGraph.getUserDatum(Strings.numInfected));
        ChartPanel panel = getPanelForDisplay(xValues, yValues);
        frame.setContentPane(panel);
        frame.pack();
        frame.validate();
        frame.repaint();
        panel.repaint();
    }
}
