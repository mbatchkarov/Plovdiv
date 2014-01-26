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

package view;

import controller.Controller;
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.ObservableGraph;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import view.generatorSettings.GeneratorSettings;

import java.awt.*;

/**
 * Shows dialog windows and and parses user input to evoke the appropriate
 * methods of the controller with correct data
 *
 * @author reseter
 */
public class InfoGatherer {

    private Controller controller;

    public InfoGatherer(Controller controller) {
        this.controller = controller;
    }

    /**
     * Shows a standard save window and sends the settings to the controller
     *
     * @param parent the window that this dialog was evoked from
     * @param g      the graph to save
     */
    public void showSave(Frame parent, ObservableGraph g) {
        FileDialog window = new FileDialog(parent, "Save", FileDialog.SAVE);
        window.setSize(500, 500);
        window.setVisible(true);
        controller.save(window.getDirectory() + window.getFile(), g);
    }

    /**
     * Shows a standard Load window and sends the settings to the controller
     *
     * @param parent parent the window that this dialog was evoked from
     */
    public void showLoad(Frame parent) {
        FileDialog window = new FileDialog(parent, "Load", FileDialog.LOAD);
        window.setSize(500, 500);
        window.setVisible(true);
        String path = window.getDirectory() + window.getFile();
        if (!path.equals("nullnull")) {//if the user clicks CANCEL path will be set to "nullnull"
            controller.load(path);
        }

    }

    /**
     * Shows dialog window that collects user input regarding graph generation
     * and evokes the appropriate method of the generator (via the controller)
     *
     * @param parent
     */
    public void showGenerate(Display parent) {
//        GenerateGraphSettings window = new GenerateGraphSettings(controller.getInstance());
        GeneratorSettings window = new GeneratorSettings(parent);
        window.setVisible(true);


    }

//
//    private class EpiCurve extends Thread {
//
//        private final ObservableGraph g;
//
//        public EpiCurve(ObservableGraph g) {
//            this.g = g;
//        }
//
//        @Override
//        /**
//         * displays the data
//         */
//        public void run() {
//            ApplicationFrame t = new ApplicationFrame("Miro");
//            t.setDefaultCloseOperation(ApplicationFrame.DISPOSE_ON_CLOSE);
//            t.setVisible(true);
//            int[] x = {1, 2, 3, 4, 5, 6, 7, 8, 9};
//            int[] y = {0, 1, 2, 3, 4, 5, 6, 7, 8};
//
//            synchronized (g) {
//                while (x[0] < 500) {//number of steps of the simulation
//                    //time flows, y mutates
//                    for (int i = 0; i < y.length; i++) {
//                        x[i]++;
//                        y[i] = x[i];
//                    }
//
//                    t.setContentPane(updateData(x, y));
//                    t.pack();
//                    t.validate();
//
//                    try {
//                        Thread.sleep(100); //waiting time of the simulation or yield
//                        yield();
//                    } catch (InterruptedException ex) {
//                        //nothing
//                    }
//                    g.notifyAll();
//                }
//            }
//        }
//
//        /**
//         * prepares the data
//         *
//         * @param x
//         * @param y
//         * @return
//         */
//        private ChartPanel updateData(int[] x, int[] y) {
//            XYSeries series1 = new XYSeries("Half");
//            for (int i = 0; i < x.length; i++) {
//                series1.add(y[i], x[i]);
//            }
//
//            XYSeriesCollection dataset = new XYSeriesCollection();
//            dataset.addSeries(series1);
//
//            // create the chart...
//            JFreeChart chart = ChartFactory.createXYLineChart("Test", "Individuals", "Time steps",
//                    dataset, PlotOrientation.HORIZONTAL, true, true, true);
//
//            //optional customization
//            return new ChartPanel(chart);
//        }
//    }
}
