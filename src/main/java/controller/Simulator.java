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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import javax.swing.JFrame;
import model.EpiState;
import model.MyEdge;
import model.MyGraph;
import model.MyVertex;
import model.Strings;
import model.dynamics.Dynamics;
import model.dynamics.SISDynamics;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import view.Exceptions;

/**
 *
 * @author reseter
 */
public class Simulator extends Thread {

//    private int speed;
    private boolean notStopped;
//    private Controller controller; //replaced with static access
    private final MyGraph g; //each simulator can run on only one graph
    private MersenneTwister mt;
    Dynamics d;
    private boolean notPaused;
    //dynamic graph structure requires changes to be made after iteration is complete
    private HashMap<MyEdge, Pair> edgesToAdd; //edge -> new endpoints

    public Simulator(MyGraph g) {
        this.g = g;
        notStopped = true;
        notPaused = true;
        mt = new MersenneTwister();
    }

    /**
     * Runs the simulation on the selected graph
     * @param g the graph to run on
     */
    @Override
    public void run() {

        int i = 0;
        int time = (Integer) g.getUserDatum(Strings.time);
        int speedMultiplier = (Integer) g.getUserDatum(Strings.speed);
        d = (Dynamics) g.getUserDatum(Strings.dynamics);

        double beta = 0; //edge breaking rate, only applies for SIS dynamics
        if (d instanceof SISDynamics) {
            beta = ((SISDynamics) d).getEdgeBreakingRate();
        }

        Collection<MyEdge> edges = g.getEdges();
        Collection<MyVertex> vertices = g.getVertices();

        //probabilities based on per-link traversal of the graph
        //probability of recovery is constant, and in this case so is the infection probability
        Double recProb = 1d - Math.pow(Math.E, (-1 * (d.getGama() * d.getDeltaT())));
        Double infProb = 1d - Math.pow(Math.E, (-1 * ((d.getTau() + beta) * d.getDeltaT())));
        System.out.println("tau " + d.getTau());
        System.out.println("deltaT" + d.getDeltaT());
        System.out.println("gama " + d.getGama());
        System.out.println("recovery prob " + recProb);
        System.out.println("inf prob " + infProb);
        int runTime = (int) ((double) time / d.getDeltaT());


        // ~~~~~~~~~~~~ STATISTICAL DATA, FOR DISPLAY ONLY
        //it many steps are to be executed, only plot a fraction of the data (every recordGap-th steps)
        int recordGap = Math.max(runTime / 100, 1); //lower limit 1
        recordGap = Math.min(recordGap, 100); //upper limit 10'000
        int samplePoints = 0; //how many samples have been taken so far
        JFrame frame = new JFrame("Epidemics statistics");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
        int[] x = new int[runTime / recordGap];
        int[] y = new int[runTime / recordGap];
        //END OF STATISTICAL DATA


        System.out.println("Record gap " + recordGap);
        System.out.println("Run time " + runTime);
        while (notStopped) {//boolean flag to enable interruption
//            if (notPaused) {
//                while (i < runTime) {
            while (i < runTime && notStopped) {

//                synchronized (g) {
                //make sure all nodes have a next state
                for (MyVertex current : vertices) {
                    current.setUserDatum(Strings.next, current.getUserDatum("state"));
                }
                edgesToAdd = new HashMap<MyEdge, Pair>();
                for (MyEdge current : edges) { //for all edges check for infection if I-S or S-I
                    MyVertex first = (MyVertex) g.getEndpoints(current).getFirst();
                    MyVertex second = (MyVertex) g.getEndpoints(current).getSecond();
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
                for(MyEdge e:edgesToAdd.keySet()){
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

                // ~~~~~~~~~~~~ UPDATE STATS WINDOW ~~~~~~~~~~~~~~
                //time flows, y mutates
                if (i % recordGap == 0) {//only record some sample points
                    x[samplePoints] = i;
                    y[samplePoints] = (Integer) g.getUserDatum(Strings.numInf);
                    samplePoints++;
                }

                frame.setContentPane(updateData(x, y));
                frame.pack();
                frame.validate();
                // ~~~~~~~~~~~~ END UPDATE STATS WINDOW~~~~~~~~~~~~~~

                Controller.updateDisplay();
                try {
                    //control simulation speed by waiting
                    Thread.sleep(speedMultiplier);
                    //give the display thread time to update
                    yield();
                } catch (InterruptedException ex) {
                    Exceptions.showUninterruptedNotification(ex);
                }
                //increment the number of steps have been performed on the graph
//                    g.setUserDatum(Strings.steps, ((Integer) g.getUserDatum(Strings.steps) + 1));

                i++;
//                    g.notifyAll();//tell the display its ok to update now
//                }
//                    }
//                }
//            } else {// if paused, wait
//                synchronized (g) {
//                    try {
//                        g.wait();
//                    } catch (InterruptedException e) {
//                        //do nothing
//                        } finally {
//                        g.notifyAll();
//                    }
//                }
            }

//            frame.dispose();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            Controller.stopSim();
            System.gc();

        }
    }

    /**
     * prepares the data
     * @param x
     * @param y
     * @return
     */
    private ChartPanel updateData(int[] x, int[] y) {
        XYSeries series1 = new XYSeries("Infected vertices");
        for (int i = 0; i < x.length; i++) {
            series1.add(x[i], y[i]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series1);

        // create the chart...
        JFreeChart chart = ChartFactory.createXYLineChart("Infected vertices vs Time steps", "Time steps", "Individuals",
                dataset, PlotOrientation.VERTICAL, true, true, true);

        //optional customization
        //...
        ChartPanel chartPanel = new ChartPanel(chart);
        return chartPanel;
    }

    public void stopSim() {
        notStopped = false;
    }

    public void pauseSim() {
        notPaused = false;
    }

    public void restart() {
        notPaused = true;
    }

    /**
     * Given a vertex, finds the number of infected neighbours it has
     * @param v
     * @return
     */
    private int numInfectedNeighbours(MyVertex v) {
        int ans = 0;
        Iterator it = g.getNeighbors(v).iterator();
        while (it.hasNext()) {
            MyVertex current = (MyVertex) it.next();

            if (current.getUserDatum(Strings.state).equals(EpiState.INFECTED)) {
                ans++;
            }
        }
        return ans;
    }

    /**
     * Checks if the SUSCEPTIBLE node vertex second will get infected at this time step
     * and colour the edge from which the infection came
     * The vertex is assumed vy this method to have been in contact with an infected node
     * @param vertex the vertex
     */
    private void checkForInfection(MyVertex vertex, MyEdge currentEdge, double infProb, double beta,
            HashMap<MyEdge, Pair> edgesToAdd) {
        //compute a random probability
        Double randomProb = Math.abs(new Double(mt.nextInt()) / new Double(Integer.MAX_VALUE));
        //if this chap is unlucky
        if (randomProb < infProb) {

            Double randomProb1 = Math.abs(new Double(mt.nextInt()) / new Double(Integer.MAX_VALUE));

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
                    Object[] v = sus.toArray();
                    int first = sus.indexOf(vertex);
                    while (!done) {
                        int second = r.nextInt(numSus);
                        if (first != second && !g.isNeighbor((MyVertex) v[first], (MyVertex) v[second])) {
                            MyEdge e = new MyEdge(g.getEdgeCount()+3);
                            e.setWeigth(1.0);
                            e.setUserDatum(Strings.infected, false);
//                            g.addEdge(e, (MyVertex) v[first], (MyVertex) v[second], EdgeType.UNDIRECTED);
//                            g.removeEdge(currentEdge);
                            edgesToAdd.put(currentEdge, new Pair((MyVertex) v[first], (MyVertex) v[second]));
                            Controller.updateDisplay();
                            Controller.updateCounts();
                            System.out.println("Rewiring");
                            done = true;
                        }
                    }
                }
            }
        }

    }

    /**
     * Checks if the given vertex will recover at this step,
     * it is assumed to be infected, so make your own checks
     * @param first
     * @param recProb
     */
    private void checkForRecovery(MyVertex vertex, double recProb) {
//        compute a random number
        double randomProb = Math.abs(new Double(mt.nextInt()) / new Double(Integer.MAX_VALUE));
        if (randomProb < recProb) {
            //put this vertex on the "waiting list" for recovery
            vertex.setUserDatum(Strings.next, d.getNextState(vertex));
            //record when it recovered
//            vertex.setUserDatum(Strings.generation, (Integer) g.getUserDatum(Strings.steps) + 1);
        } else {
        }
    }
}
