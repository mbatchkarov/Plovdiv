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
package controller.simulation;

import controller.Controller;
import controller.ExtraGraphEvent;
import controller.Stats;
import edu.uci.ics.jung.graph.MyGraph;
import model.MyEdge;
import model.MyVertex;
import model.SimulationDynamics;
import org.apache.commons.collections15.buffer.CircularFifoBuffer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * @author Miroslav Batchkarov
 */
public class Simulator {
    private Random rng;
    private SimModelThread thread;
    private volatile int stepNumber;
    private CircularFifoBuffer<Double> xValues;
    private CircularFifoBuffer<Integer> yValues;
    private boolean doOneStepOnly;
    private final int WINDOW_WIDTH = 50;
    private XYSeries infectedXYSeries;
    private XYPlot xyPlot;
    private MyGraph g;
    private Stats stats;
    private Controller controller;

    public Simulator(MyGraph g, Stats stats, Controller controller) {
        this.g = g;
        this.stats = stats;
        this.controller = controller;
        this.rng = new Random();
        this.doOneStepOnly = false;
        this.infectedXYSeries = new SynchronisedXYSeries("Infected", false, false);
        this.xValues = new CircularFifoBuffer<Double>(WINDOW_WIDTH);
        this.yValues = new CircularFifoBuffer<Integer>(WINDOW_WIDTH);

        this.thread = new SimModelThread("sim-thread");
        this.thread.pause();
        this.thread.start();
    }

    public void resetSimulation() {
        this.stepNumber = 0;
        this.xValues.clear();
        this.yValues.clear();
        this.updateChartUnderlyingData();
        this.updateChartAxisParameters();
    }

    private void doStep(MyGraph<MyVertex, MyEdge> g) {
        LinkedList<SimulationCommand> commands = new LinkedList<SimulationCommand>();
        checkForTopologyChanges(g, commands);
        checkForInfection(g, commands);
        checkForRecovery(g, commands);

        //here the next state of each vertex should be known, updating
        for (SimulationCommand c : commands) {
            c.execute();
        }

        //record changes in number of inf/sus/res nodes
        controller.updateCounts();
        xValues.add(stepNumber * g.getDynamics().getTimeStep());
        yValues.add(g.getNumInfected());
        stats.recalculateAll();
    }

    /**
     * Checks if any edge creations, deletions of rewirings should occur at this time step.
     * <p/>
     * NB: Multiple edge rewirings may occur in a single time step. These are executed in
     * order. The process has two independent step- removing the old edge and then
     * adding a new one. However, the second part may fail if the edge we are adding
     * exists already (as we don't want to be adding multiple edges between a pair of
     * vertices). This means sometimes an edge rewiring mey just turn into an edge
     * deletion. This is particularly problematic when the edge rewiring rate is high.
     *
     * @param g        the graph
     * @param commands a list of changes that will be executed later. This methods appends to the
     *                 list
     */
    public void checkForTopologyChanges(MyGraph<MyVertex, MyEdge> g,
                                        LinkedList<SimulationCommand> commands) {
        // check for edge breaking
        SimulationDynamics d = g.getDynamics();
        for (MyEdge e : g.getEdges()) {
            double eventProba = 0d;

            MyVertex v1 = g.getEndpoints(e).getFirst();
            MyVertex v2 = g.getEndpoints(e).getSecond();
            if (v1.isInfected()) {
                if (v2.isSusceptible()) {
                    eventProba = d.getSIEdgeBreakingProb();
                }
                if (v2.isInfected()) {
                    eventProba = d.getIIEdgeBreakingProb();
                }
            }
            if (v1.isSusceptible()) {
                if (v2.isSusceptible()) {
                    eventProba = d.getSSEdgeBreakingProb();
                }
                if (v2.isInfected()) {
                    eventProba = d.getSIEdgeBreakingProb();
                }
            }

            if (rng.nextFloat() < eventProba) {
                commands.add(new EdgeBreakingCommand(g, e));
            }
        }

        // check for edge creation
        for (MyVertex v1 : g.getVertices()) {
            for (MyVertex v2 : g.getVertices()) {
                if (g.isNeighbor(v1, v2) || v1 == v2)
                    continue;

                double eventProba = 0d;
                if (v1.isInfected()) {
                    if (v2.isSusceptible()) {
                        eventProba = d.getSIEdgeCreationProb();
                    }
                    if (v2.isInfected()) {
                        eventProba = d.getIIEdgeCreationProb();
                    }
                }
                if (v1.isSusceptible()) {
                    if (v2.isSusceptible()) {
                        eventProba = d.getSSEdgeCreationProb();
                    }
                    if (v2.isInfected()) {
                        eventProba = d.getSIEdgeCreationProb();
                    }
                }

                if (rng.nextFloat() < eventProba) {
                    commands.add(new EdgeCreationCommand(controller.getEdgeFactory(), g, v1, v2));
                }
            }
        }

        // check for edge rewiring
        for (MyEdge e : g.getEdges()) {
            double eventProba = 0d;

            MyVertex v1 = g.getEndpoints(e).getFirst();
            MyVertex v2 = g.getEndpoints(e).getSecond();
            if (v1.isInfected()) {
                if (v2.isSusceptible()) {
                    eventProba = d.getSIEdgeRewiringProb();
                }
                if (v2.isInfected()) {
                    eventProba = d.getIIEdgeRewiringProb();
                }
            }
            if (v1.isSusceptible()) {
                if (v2.isSusceptible()) {
                    eventProba = d.getSSEdgeRewiringProb();
                }
                if (v2.isInfected()) {
                    eventProba = d.getSIEdgeRewiringProb();
                }
            }

            if (rng.nextFloat() < eventProba) {
                MyVertex newEndpoint = findSusceptibleVertex(g,
                                                             v1.isInfected() ? v2 : v1,
                                                             v1.isInfected() ? v1 : v2);
                if (newEndpoint != null) {
                    commands.add(new EdgeBreakingCommand(g, e));
                    commands.add(new EdgeCreationCommand(controller.getEdgeFactory(),
                                                         g, v1,
                                                         newEndpoint));
                }
            }
        }
    }

    private MyVertex findSusceptibleVertex(MyGraph<MyVertex, MyEdge> g,
                                           MyVertex origin, MyVertex oldEndoint) {
        //break current connection from origin to oldEndpoint and find another susceptible node
        // to connect origin to
        ArrayList<MyVertex> susceptibles = new ArrayList<MyVertex>();
        for (MyVertex v : g.getVertices()) {
            if (v.isSusceptible() && v != origin && v != oldEndoint &&
                !g.isNeighbor(origin, v)) {
                susceptibles.add(v);
            }
        }
        if (susceptibles.size() < 1) {
            // there aren't any susceptibles in the graph
            return null;
        }


        MyVertex[] candidates = new MyVertex[1];
        candidates = susceptibles.toArray(candidates);
        while (true) {
            int i = rng.nextInt(susceptibles.size());
            return candidates[i];
        }
    }

    /**
     * Once a chart has been created from an up-to-date data set,
     * set its range and ticks
     */
    public void updateChartAxisParameters() {
        NumberAxis yAxis = (NumberAxis) xyPlot.getRangeAxis();
        yAxis.setRange(0, g.getVertexCount());
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        final NumberAxis domainAxis = (NumberAxis) xyPlot.getDomainAxis();
//        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

    public void createInfectedCountGraph(JPanel statsPanel) {
        Dimension maxSize = statsPanel.getSize();
        updateChartUnderlyingData();
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(infectedXYSeries);

        // create the chart
        JFreeChart jfreechart = ChartFactory.createXYLineChart("", "", "Infected",
                                                               dataset, PlotOrientation.VERTICAL, false, false, false);
        // save plot object so that we can update its axes if the graph changes
        xyPlot = (XYPlot) jfreechart.getPlot();

        updateChartAxisParameters();

        int maxHeight = (int) (0.6 * maxSize.getHeight()); //save vertical space
        int maxWidth = (int) maxSize.getWidth();

        ChartPanel panel = new ChartPanel(jfreechart, maxWidth, maxHeight, maxWidth, maxHeight,
                                          maxWidth, maxHeight,
                                          true, true, false, false, false, false, true);

        statsPanel.setLayout(new FlowLayout());
        statsPanel.removeAll();
        statsPanel.add(panel);
        statsPanel.validate();
        statsPanel.revalidate();
        panel.repaint();
    }

    /**
     * Updates the data that underlies the chart. This should fire an
     * event and force the chart to update
     */
    public void updateChartUnderlyingData() {
        if (xValues.size() != yValues.size())
            throw new IllegalStateException("X and Y data of chart has different length");

        Double[] xarr = new Double[xValues.size()];
        Integer[] yarr = new Integer[yValues.size()];
        xarr = xValues.toArray(xarr);
        yarr = yValues.toArray(yarr);
        synchronized (infectedXYSeries) {
            infectedXYSeries.clear();
            for (int i = 0; i < xarr.length; i++) {
                infectedXYSeries.add(xarr[i], yarr[i]);
            }
        }
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
     * Checks if a SUSCEPTIBLE node vertex second will get infected at this
     * time step.
     */
    public void checkForInfection(MyGraph<MyVertex, MyEdge> g,
                                  LinkedList<SimulationCommand> commands) {
        for (MyEdge e : g.getEdges()) {
            MyVertex first = g.getEndpoints(e).getFirst();
            MyVertex second = g.getEndpoints(e).getSecond();

            if (first.isInfected() && second.isSusceptible()) {
                if (rng.nextFloat() < g.getDynamics().getInfectionProb())
                //put on the waiting list to be infected at the next time step
                {
                    commands.add(new ChangeSIRStateCommand
                                         (second,
                                          g.getDynamics().getNextState(second)));
                }
            }
            if (second.isInfected() && first.isSusceptible()) {
                if (rng.nextFloat() < g.getDynamics().getInfectionProb()) {
                    commands.add(new ChangeSIRStateCommand(first,
                                                           g.getDynamics().getNextState(first)));
                }
            }


        }


    }

    /**
     * Checks if the given vertex will recover at this step, it is assumed to be
     * infected, so make your own checks
     */
    public void checkForRecovery(MyGraph<MyVertex, MyEdge> g,
                                 LinkedList<SimulationCommand> commands) {
        for (MyVertex vertex : g.getVertices())
            if (vertex.isInfected())
                if (rng.nextFloat() < g.getDynamics().getRecoveryProb()) {
                    //put this vertex on the "waiting list" for recovery
                    commands.add(new ChangeSIRStateCommand(vertex,
                                                           g.getDynamics().getNextState(vertex)));
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
            xValues = new CircularFifoBuffer<Double>(WINDOW_WIDTH);
            yValues = new CircularFifoBuffer<Integer>(WINDOW_WIDTH);

            while (alive) {
                try {
                    sleep(g.getSleepTimeBetweenSteps());
                    synchronized (this) {
                        while (suspended && alive && !doOneStepOnly) {
                            wait();
                        }
                    }
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
        }
    }

    public void doStepWithCurrentSettings() {
        doStep(g);
        stepNumber++;
        g.fireExtraEvent(new ExtraGraphEvent(g, ExtraGraphEvent.SIM_STEP_COMPLETE));
    }

    private class SynchronisedXYSeries extends XYSeries {

        public SynchronisedXYSeries(Comparable key, boolean autoSort, boolean allowDuplicateXValues) {
            super(key, autoSort, allowDuplicateXValues);
        }

        @Override
        public synchronized XYDataItem getDataItem(int index) {
            return super.getDataItem(index);
        }
    }

}
