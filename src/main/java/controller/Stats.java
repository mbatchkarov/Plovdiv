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

import edu.uci.ics.jung.algorithms.metrics.Metrics;
import edu.uci.ics.jung.algorithms.scoring.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraDistance;
import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Hypergraph;
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import model.MyEdge;
import model.MyVertex;
import org.apache.commons.collections15.Transformer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mb724
 */
public class Stats implements GraphEventListener<MyVertex, MyEdge> {

    private MyVertex selectedNode;
    private Map<MyVertex, Double> ccMap;
    private double avgCC;
    private Transformer<MyVertex, Double> aplMap;
    private double apl;
    private double avgDegree;
    private int[] degreeDistribution;
    private int[] cumulativeDegreeDistribution;
    private double assortativity;
    private double degreeCorrelation;
    private BetweennessCentrality edgeBetweenness;
    private BetweennessCentrality vertexBetweenness;
    private MyGraph<MyVertex, MyEdge> g;


    public Stats(MyGraph<MyVertex, MyEdge> g) {
        this.g = g;
        this.selectedNode = null;
        this.ccMap = new HashMap<MyVertex, Double>();
        this.avgCC = -1d;
//        this.aplMap = aplMap;
        this.apl = -1;
        this.avgDegree = -1;
        this.degreeDistribution = new int[1];
        this.cumulativeDegreeDistribution = new int[1];
        this.assortativity = -1d;
        this.degreeCorrelation = -1d;
        this.edgeBetweenness = null;
        this.vertexBetweenness = null;

        this.recalculateAll();
    }

    // WORKER METHODS
    public void recalculateAll() {
        calculateClusteringCoefficient();
        calculateAveragePathLength();
        calculateAverageDegree();
        calculateDegreeDistribution();
        calculateAssortativity();
        calculateDegreeCorrelation();


    }

    private void calculateDegreeCorrelation() {
        //degree correlation
        // this is just the Pearson product-moment correlation coefficient averaged over each connection
        double degreeA, degreeB;
        double numConnectionsProcessed = 0.0;
        double currentConnectionWeight = 0.0;

        double eq1, eq2, eq3, eq4, eq5;
        eq1 = eq2 = eq3 = eq4 = eq5 = 0.0;

        //for every node
        for (MyVertex v1 : g.getVertices()) {
            //for all outgoing edges
            for (MyEdge e : g.getOutEdges(v1)) {
                //get this connection's weigth
                currentConnectionWeight = e.getWeigth();
                degreeA = getWeightedDegree(v1) - currentConnectionWeight;
                degreeB = getWeightedDegree(g.getOpposite(v1, e)) - currentConnectionWeight;
                eq1 += degreeA;
                eq2 += degreeB;
                eq3 += (degreeA * degreeB);
                eq4 += (degreeA * degreeA);
                eq5 += (degreeB * degreeB);
                numConnectionsProcessed += 1.0;
            }
        }
        double top = eq3 - ((eq1 * eq2) / numConnectionsProcessed);
        double bottom1 = eq4 - ((eq1 * eq1) / numConnectionsProcessed);
        double bottom2 = eq5 - ((eq2 * eq2) / numConnectionsProcessed);
        double bottom = bottom1 * bottom2;
        bottom = Math.sqrt(bottom);
        double correlation;
        if (bottom == 0) {
            correlation = 1.0;
        } else {
            correlation = top / bottom;
        }
        if (g.getVertexCount() > 0 && g.getEdgeCount() > 0) {
            degreeCorrelation = (correlation);
        } else {
            degreeCorrelation = correlation;
        }
    }

    private void calculateAssortativity() {
        //assortativity
        int m = g.getEdgeCount();
        if (m > 0) {
            MyVertex[] vertices = (MyVertex[]) g.getVertices().toArray();
            int[] degrees = new int[vertices.length];
            int[] degrees_sq = new int[vertices.length];
            for (int i = 0; i < degrees.length; i++) {
                degrees[i] = g.degree(vertices[i]);
            }
            for (int i = 0; i < degrees.length; i++) {
                degrees_sq[i] = degrees[i] ^ 2;
            }
            int num1 = 0, num2 = 0, den1 = 0;

            int i, j;
            for (i = 0; i < vertices.length; i++) {
                for (j = i + 1; j < vertices.length; j++) {
                    num1 += degrees[i] * degrees[j];
                    num2 += degrees[i] + degrees[j];
                    den1 += degrees_sq[i] + degrees_sq[j];
                }
            }
            num1 /= m;
            den1 /= 2 * m;
            num2 = (num2 / (2 * m)) ^ 2;
            try {
                assortativity = ((num1 - num2) / (den1 - num2));
            } catch (ArithmeticException ex) {
                assortativity = Double.NaN;
            }
        } else {
            assortativity = Double.NaN;
        }
    }

    private void calculateDegreeDistribution() {
        //degree distribution
        int max = getMaxDegree();
        int numBuckets = max + 1;//if no edges exist, make the buckets array at least 1 element wide
        degreeDistribution = new int[numBuckets];
        for (int i = 0; i < numBuckets; i++) {
            degreeDistribution[i] = 0;//initially all have degree 0
        }
        Collection<MyVertex> v = g.getVertices();
        for (MyVertex x : v) {
            degreeDistribution[g.degree(x)]++;
        }
        //cumulative degree distribution
        int sum = 0;
        for (int i = 0; i < degreeDistribution.length; i++) {
            sum += degreeDistribution[i];
        }
        cumulativeDegreeDistribution = new int[degreeDistribution.length];
        cumulativeDegreeDistribution[0] = degreeDistribution[0];
        for (int i = 1; i < cumulativeDegreeDistribution.length; i++) {
            cumulativeDegreeDistribution[i] = cumulativeDegreeDistribution[i - 1] + degreeDistribution[i];

        }
    }

    private void calculateAverageDegree() {
        //average degree
        if (g.getVertexCount() > 0) {
            double res = 0d;
            for (MyVertex v : g.getVertices()) {
                res += g.outDegree(v);
            }
            avgDegree = (res / g.getVertexCount());
        } else {
            avgDegree = 0d;
        }
    }

    private void calculateAveragePathLength() {
        //average path length
        if (g.getVertexCount() > 0) {
            aplMap = DistanceStatistics.averageDistances(g, new DijkstraDistance(g));
            double sum = 0;
            for (Object x : g.getVertices()) {
                sum += getAPL((MyVertex) x);
            }
            BigDecimal total = new BigDecimal(sum);
            BigDecimal res = total.divide(new BigDecimal(g.getVertexCount()), MathContext.DECIMAL32);
            apl = (res.doubleValue());
        } else {
            apl = Double.NaN;
        }
    }

    private void calculateClusteringCoefficient() {
        //clustering coefficient
        ccMap = Metrics.clusteringCoefficients(g);
        if (g.getVertexCount() > 0) {
            double sum = 0;
            for (Object x : g.getVertices()) {
                sum += ccMap.get(x);
            }
            double res = (sum) / (g.getVertexCount());
            avgCC = (res);
        } else {
            avgCC = Double.NaN;
        }
    }

    // accessors
    public int getEdgeCount() {
        return g.getEdgeCount();
    }

    public int getVertexCount() {
        return g.getVertexCount();
    }

    public double getCC() {
        return avgCC;
    }

    public double getCC(MyVertex vertex) {
        double res = ccMap.get(vertex);
        return (res);
    }

    /**
     * average geodesic path length over the entire network
     *
     * @return
     */
    public double getAPL() {
        return apl;
    }

    /**
     * APL for the specified vertex
     *
     * @param vertex
     * @return
     */
    public double getAPL(MyVertex vertex) {
        Double result = aplMap.transform(vertex);
        //aplMap may return NaN if a node is disconnected from the rest of the graph
        if (result.isNaN() || result.isInfinite()) {
            result = 0d;
        }
        return (result);
    }

    public double getAvgDegree() {
        return avgDegree;
    }

    /**
     * Returns a an array representing the distribution of the degrees in the
     * graph position i holds the number of vertices of degree i
     *
     * @return
     */
    public int[] getDegreeDistribution() {
        return degreeDistribution;
    }

    public int[] getCumulativeDegreeDistribution() {
        return cumulativeDegreeDistribution;
    }

    public int getMaxDegree() {
        int max = 0;
        Collection<MyVertex> v = g.getVertices();
        if (v.size() > 0) {
            for (MyVertex x : v) {
                if (g.degree(x) > max) {
                    max = g.degree(x);
                }
            }
            return max;
        } else return 0;

    }

    public int getMinDegree() {
        int min = Integer.MAX_VALUE;
        Collection<MyVertex> v = g.getVertices();
        if (v.size() > 0) {
            for (MyVertex x : v) {
                if (g.degree(x) < min) {
                    min = g.degree(x);
                }
            }
            return min;
        } else return 0;

    }

    /**
     * Calculates the degree-assortativity coefficient of the graph
     *
     * @return
     */
    public double getAssortativity() {
        return assortativity;
    }

    /**
     * Calculates the weigthed degree correlation of the graph
     *
     * @return
     */
    public double getWeightedDegreeCorrelation() {
        return degreeCorrelation;
    }

    /**
     * Calculates the weighted degree of a nodes- that is the sum of the weigths
     * of its outgoing edges
     *
     * @param v
     * @return
     */
    private double getWeightedDegree(MyVertex v) {
        double res = 0;
        for (Object e : g.getOutEdges(v)) {
            res += ((MyEdge) e).getWeigth();
        }
        return res;
    }

    /**
     * computes the betweenness centrality of the given vertex
     *
     * @param vertex
     * @return
     */
    public double getBetweennessCentrality(MyVertex vertex) {
        BetweennessCentrality bc = new BetweennessCentrality(g, new Transformer<MyEdge, Double>() {
            public Double transform(MyEdge arg0) {
                return arg0.getWeigth();
            }
        });
        int n = g.getVertexCount();
        return ((2 * bc.getVertexScore(vertex)) / ((n - 1) * (n - 2)));
    }

    public double getBetweennessCentrality(MyEdge edge) {
        BetweennessCentrality bc = new BetweennessCentrality(g, new Transformer<MyEdge, Double>() {
            public Double transform(MyEdge arg0) {
                return arg0.getWeigth();
            }
        });
        int n = g.getVertexCount();
        return ((2 * bc.getEdgeScore(edge)) / ((n - 1) * (n - 2)));
    }

    /**
     * Returns the distance from the currently selected node (if any) to the
     * node v
     *
     * @param target
     * @return
     */
    public String getDistFromSelectedTo(MyVertex target) {
        if (selectedNode != null && target != null) {
            DijkstraDistance d = new DijkstraDistance(g);
            Double dist = (Double) d.getDistance(getSelectedNode(), target);
            if (dist != null) {
                return (dist).toString();
            } else {
                return "unreachable";
            }
        } else {
            return "";
        }
    }

    /**
     * @return the selectedNode
     */
    public MyVertex getSelectedNode() {
        return selectedNode;
    }

    /**
     * @param aSelectedNode the selectedNode to set
     */
    public void setSelectedNode(MyVertex aSelectedNode) {
        selectedNode = aSelectedNode;
    }

    /**
     * Number of edges present/ number of possible edges
     *
     * @return
     */
    public double getDensity() {
        if (g.getVertexCount() > 1) {
            return ((2 * g.getEdgeCount()) / (g.getVertexCount() * (g.getVertexCount() - 1)));
        } else {
            return Double.NaN;
        }
    }

    /**
     * Returns a pair, containing the min and max edge weight of the network on
     * the screen
     *
     * @return
     */
    public double[] getExtremaOfEdgeWeights() {
        double[] res = new double[2];
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        for (Object x : g.getEdges()) {
            MyEdge e = (MyEdge) x;
            if (e.getWeigth() > max) {
                max = e.getWeigth();
            }
            if (e.getWeigth() < min) {
                min = e.getWeigth();
            }
        }
        res[0] = (min);
        res[1] = (max);
        return res;
    }

    /**
     * Returns a plot of the degree distribution of the graph
     *
     * @param cumulative
     * @param logy
     * @param maxSize
     * @return
     */
    public ChartPanel getDegreeDistributionChart(boolean cumulative, boolean logy, Dimension maxSize) {
        final JFreeChart chart = ChartFactory.createXYLineChart(
                "", // chart title
                "", // domain axis label
                "", // range axis label
                prepareData(cumulative, logy), // data
                PlotOrientation.VERTICAL,
                false, // include legend
                true,
                false);

        XYPlot plot = chart.getXYPlot();

        plot.setRenderer(0, new XYSplineRenderer(1));

        ValueAxis rangeAxis = null;
        if (logy) {
            rangeAxis = new LogarithmicAxis("Log(Count)");
        } else {
            rangeAxis = new NumberAxis("Count");
            // todo the labels of this axis are out of whack
        }
        plot.setRangeAxis(rangeAxis);

        final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(getMinDegree(), getMaxDegree());
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        int maxHeight = (int) maxSize.getHeight() - 24;
        int maxWidth = (int) maxSize.getWidth() - 12;

        return new ChartPanel(chart, maxWidth, maxHeight, 50, 50, maxWidth, maxHeight,
                true, true, false, false, false, false, true);
    }

    /**
     * Creates a XYSeriesCollection for plotting out of the degree distribution of the
     * currrent graph.
     *
     * @param cumulative
     * @param logy
     * @return
     */
    private XYSeriesCollection prepareData(boolean cumulative, boolean logy) {
        final XYSeries s1 = new XYSeries("Degree");
        int[] buckets;
        if (!cumulative) {
            buckets = getDegreeDistribution();
        } else {
            buckets = getCumulativeDegreeDistribution();
        }
        for (int i = 0; i < buckets.length; i++) {
            if (buckets[i] > 0) {
                final double yValue = logy ? Math.log(buckets[i]) : buckets[i];
                if (yValue > 0)
                    s1.add(i, yValue); //0s not ok for log plotting
            }
        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s1);
        return dataset;
    }

    public MyGraph<MyVertex, MyEdge> getGraph() {
        return g;
    }

    // HANDLE GRAPH EVENTS APPROPRIATELY (AND EFFICIENTLY)
    @Override
    public void handleGraphEvent(GraphEvent<MyVertex, MyEdge> evt) {

    }

}
