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
package view.CustomMouse;

import controller.Controller;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingPopupGraphMousePlugin;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import model.EpiState;
import model.MyEdge;
import model.MyVertex;
import model.Strings;
import view.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.util.Set;

/**
 * Allows the user to interact with the program using the mouse
 *
 * @author mb724
 */
public class CustomPopupPlugin extends EditingPopupGraphMousePlugin implements MouseListener, MouseMotionListener {

    protected MyVertex startVertex;
    protected CubicCurve2D rawEdge = new CubicCurve2D.Float();
    protected Shape edgeShape;
    protected Shape rawArrowShape;
    protected Shape arrowShape;
    protected VisualizationServer.Paintable edgePaintable;
    protected VisualizationServer.Paintable arrowPaintable;
    protected EdgeType edgeIsDirected;

    public CustomPopupPlugin() {
        super(Controller.getVertexFactory(), Controller.getEdgeFactory());
//        this(MouseEvent.BUTTON3_MASK);
        rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50,
                1.0f, 0.0f);
        rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
//        edgePaintable = new EdgePaintable();
//        arrowPaintable = new ArrowPaintable();
        this.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    //    public customPopupPlugin(int modifiers) {
//        super(modifiers);
//    }
    @Override
    protected void handlePopup(MouseEvent e) {
        if (e.isPopupTrigger()) {//only show popup on right-click
            JPopupMenu popup1 = new JPopupMenu();
            final VisualizationViewer<MyVertex, MyEdge> vv =
                    (VisualizationViewer<MyVertex, MyEdge>) e.getSource();
            final Layout<MyVertex, MyEdge> layout = vv.getGraphLayout();
            final Graph<MyVertex, MyEdge> graph = layout.getGraph();
            final Point2D ivp = e.getPoint();
            GraphElementAccessor<MyVertex, MyEdge> pickSupport = vv.getPickSupport();

            if (pickSupport != null) {
//            final MyVertex v = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
                final MyVertex vertex = pickSupport.getVertex(layout, ivp.getX(), ivp.getY());
                final MyEdge edge = pickSupport.getEdge(layout, ivp.getX(), ivp.getY());
                final PickedState<MyVertex> pickedVertexState = vv.getPickedVertexState();
                final PickedState<MyEdge> pickedEdgeState = vv.getPickedEdgeState();

                if (vertex != null) {
                    Set<MyVertex> picked = pickedVertexState.getPicked();
                    if (picked.size() > 0) {
                        if (graph instanceof UndirectedGraph == false) {
                            JMenu directedMenu = new JMenu("Create Directed Connection");
                            popup1.add(directedMenu);
                            for (final MyVertex other : picked) {
                                directedMenu.add(new AbstractAction("[" + other.getId() + "," + vertex.getId() + "]") {

                                    public void actionPerformed(ActionEvent e) {
                                        graph.addEdge(Controller.getEdgeFactory().create(),
                                                other, vertex, EdgeType.DIRECTED);
                                        vv.repaint();
                                        Display.recalculateStats(null);
                                    }
                                });
                            }
                        }
                        if (graph instanceof DirectedGraph == false) {
                            JMenu undirectedMenu = new JMenu("Create Undirected Connection");
                            popup1.add(undirectedMenu);
                            for (final MyVertex other : picked) {
                                undirectedMenu.add(new AbstractAction("[" + other.getId() + "," + vertex.getId() + "]") {

                                    public void actionPerformed(ActionEvent e) {
                                        graph.addEdge(Controller.getEdgeFactory().create(),
                                                other, vertex);
                                        vv.repaint();
                                        Display.recalculateStats(null);
                                    }
                                });
                            }
                        }
                    }
                    popup1.add(new AbstractAction("Delete Node") {

                        public void actionPerformed(ActionEvent e) {
//                            pickedVertexState.pick(vertex, false);
                            for (MyVertex v : pickedVertexState.getPicked()) {
                                pickedVertexState.pick(v, false);
                            }
                            graph.removeVertex(vertex);
                            vv.repaint();
                            Display.recalculateStats(null);
                        }
                    });
                    popup1.add(new AbstractAction("Set susceptible") {

                        public void actionPerformed(ActionEvent e) {
                            vertex.setUserDatum("state", EpiState.SUSCEPTIBLE);
                            vv.repaint();
                            Controller.updateCounts();
                            Display.recalculateStats(vertex);

                        }
                    });
                    popup1.add(new AbstractAction("Set infected") {

                        public void actionPerformed(ActionEvent e) {
                            vertex.setUserDatum("state", EpiState.INFECTED);
                            vv.repaint();
                            Controller.updateCounts();
                            Display.recalculateStats(vertex);

                        }
                    });
                    popup1.add(new AbstractAction("Set resistant") {

                        public void actionPerformed(ActionEvent e) {
                            vertex.setUserDatum("state", EpiState.RESISTANT);
                            vv.repaint();
                            Controller.updateCounts();
                            Display.recalculateStats(vertex);
                        }
                    });
                    vv.repaint();

                    popup1.show(vv, e.getX(), e.getY());
                } else if (edge != null) {
                    popup1.add(new AbstractAction("Delete Edge") {

                        public void actionPerformed(ActionEvent e) {
                            pickedEdgeState.pick(edge, false);
                            graph.removeEdge(edge);
                            vv.repaint();
                            Display.recalculateStats(null);
                        }
                    });

                    popup1.add(new AbstractAction("Set Edge Weight") {

                        public void actionPerformed(ActionEvent e) {
                            String s = (String) JOptionPane.showInputDialog(null, "Enter new weight",
                                    "Input", JOptionPane.PLAIN_MESSAGE, null, null,
                                    "" + edge.getWeigth());
                            if ((s != null) && (s.length() > 0)) {
                                try {
                                    edge.setWeigth(Double.parseDouble(s));
                                    vv.repaint();
                                } catch (NumberFormatException ex) {
                                    //input is not a valid double
                                    JOptionPane.showMessageDialog(Controller.getActiveWindow(), "Input is not a valid number!");
                                }
                            }
                            Display.recalculateStats(null);
                        }
                    });
                } else {
                    popup1.add(new AbstractAction("Create Vertex") {

                        public void actionPerformed(ActionEvent e) {
                            final MyVertex newV = Controller.getVertexFactory().create();
                            newV.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
                            graph.addVertex(newV);
                            layout.setLocation(newV, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(ivp));
                            vv.repaint();
                            Display.recalculateStats(null);
                        }
                    });
                }
                popup1.show(vv, e.getX(), e.getY());
                vv.repaint();
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

}
