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
import view.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.collections15.functors.ConstantTransformer;
import view.BackgroundImageController;
import static view.Display.vv;

/**
 * Allows the user to interact with the program using the mouse
 *
 * @author mb724
 */
public class CustomPopupPlugin extends EditingPopupGraphMousePlugin implements MouseListener, MouseMotionListener {

    private Controller controller;
    protected Display d; // to trigger GUI updates when needed
    protected MyVertex startVertex;
    protected CubicCurve2D rawEdge = new CubicCurve2D.Float();
    protected Shape edgeShape;
    protected Shape rawArrowShape;
    protected Shape arrowShape;
    protected VisualizationServer.Paintable edgePaintable;
    protected VisualizationServer.Paintable arrowPaintable;
    protected EdgeType edgeIsDirected;

    public CustomPopupPlugin(Controller controller, Display d) {
        super(controller.getVertexFactory(), controller.getEdgeFactory());
        this.controller = controller;
        this.d = d;
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
            final VisualizationViewer<MyVertex, MyEdge> vv
                    = (VisualizationViewer<MyVertex, MyEdge>) e.getSource();
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
                                        graph.addEdge(controller.getEdgeFactory().create(),
                                                other, vertex, EdgeType.DIRECTED);
                                        vv.repaint();
                                        CustomPopupPlugin.this.d.updateStatsDisplay();
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
                                        graph.addEdge(controller.getEdgeFactory().create(),
                                                other, vertex);
                                        vv.repaint();
                                        CustomPopupPlugin.this.d.updateStatsDisplay();
                                    }
                                });
                            }
                        }
                    }
                    JMenu vertexIconMenu = new JMenu("Change Icon");
                    popup1.add(vertexIconMenu);

                    JMenu nodeTypeMenuSimple = new JMenu("Simple");
                    JMenu nodeTypeMenuRealistic = new JMenu("3D");
                    vertexIconMenu.add(nodeTypeMenuSimple);
                    vertexIconMenu.add(nodeTypeMenuRealistic);

                    nodeTypeMenuSimple.add(new AbstractAction("User") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_USER, MyVertex.VERTEX_ICON_STYLE_SIMPLE);
                        }
                    });
                    nodeTypeMenuSimple.add(new AbstractAction("Mobile") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_MOBILE, MyVertex.VERTEX_ICON_STYLE_SIMPLE);
                        }
                    });
                    nodeTypeMenuSimple.add(new AbstractAction("Computer") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_COMPUTER, MyVertex.VERTEX_ICON_STYLE_SIMPLE);
                        }
                    });
                    nodeTypeMenuSimple.add(new AbstractAction("Access Point") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_ACCESS_POINT, MyVertex.VERTEX_ICON_STYLE_SIMPLE);
                        }
                    });

                    nodeTypeMenuRealistic.add(new AbstractAction("User") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_USER, MyVertex.VERTEX_ICON_STYLE_PHOTOREALISTIC);
                        }
                    });
                    nodeTypeMenuRealistic.add(new AbstractAction("Mobile") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_MOBILE, MyVertex.VERTEX_ICON_STYLE_PHOTOREALISTIC);
                        }
                    });
                    nodeTypeMenuRealistic.add(new AbstractAction("Computer") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_COMPUTER, MyVertex.VERTEX_ICON_STYLE_PHOTOREALISTIC);
                        }
                    });
                    nodeTypeMenuRealistic.add(new AbstractAction("Access Point") {
                        public void actionPerformed(ActionEvent e) {
                            changeNodeType(vv, vertex, MyVertex.NODE_TYPE_ACCESS_POINT, MyVertex.VERTEX_ICON_STYLE_PHOTOREALISTIC);
                        }
                    });

                    JMenu nodeStateMenu = new JMenu("Change State");
                    popup1.add(nodeStateMenu);

                    nodeStateMenu.add(new AbstractAction("Susceptible") {

                        public void actionPerformed(ActionEvent e) {
                            vertex.setEpiState(EpiState.SUSCEPTIBLE);
                            vv.repaint();
                            controller.updateCounts();
                            CustomPopupPlugin.this.d.updateStatsDisplay();

                        }
                    });
                    nodeStateMenu.add(new AbstractAction("Infected") {

                        public void actionPerformed(ActionEvent e) {
                            vertex.setEpiState(EpiState.INFECTED);
                            vv.repaint();
                            controller.updateCounts();
                            CustomPopupPlugin.this.d.updateStatsDisplay();

                        }
                    });
                    nodeStateMenu.add(new AbstractAction("Resistant") {

                        public void actionPerformed(ActionEvent e) {
                            vertex.setEpiState(EpiState.RESISTANT);
                            vv.repaint();
                            controller.updateCounts();
                            CustomPopupPlugin.this.d.updateStatsDisplay();
                        }
                    });
                    vv.repaint();

                    popup1.add(new AbstractAction("Delete Node") {

                        public void actionPerformed(ActionEvent e) {
//                            pickedVertexState.pick(vertex, false);
                            for (MyVertex v : pickedVertexState.getPicked()) {
                                pickedVertexState.pick(v, false);
                            }
                            graph.removeVertex(vertex);
                            vv.repaint();
                            CustomPopupPlugin.this.d.updateStatsDisplay();
                        }
                    });

                    popup1.show(vv, e.getX(), e.getY());
                } else if (edge != null) {
                    popup1.add(new AbstractAction("Delete Edge") {

                        public void actionPerformed(ActionEvent e) {
                            pickedEdgeState.pick(edge, false);
                            graph.removeEdge(edge);
                            vv.repaint();
                            CustomPopupPlugin.this.d.updateStatsDisplay();
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
                                    JOptionPane.showMessageDialog(controller.getGui(), "Input is not a valid number!");
                                }
                            }
                            CustomPopupPlugin.this.d.updateStatsDisplay();
                        }
                    });
                } else {
                    final JMenu backgroundImageMenu = new JMenu("Background Image");
                    final JMenu setColorsMenu = new JMenu("Change Color");
                    popup1.add(backgroundImageMenu);
                    popup1.add(setColorsMenu);

                    backgroundImageMenu.add(new AbstractAction("<NONE>") {

                        public void actionPerformed(ActionEvent e) {
                            BackgroundImageController.getInstance().removeBackgroundImage(vv);
                            vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.black));
                            vv.setBackground(new Color(240, 240, 240));
                        }
                    });
                    backgroundImageMenu.add(new AbstractAction("<UK Map>") {

                        public void actionPerformed(ActionEvent e) {
                            BackgroundImageController.getInstance().setGraphBackgroundImage(vv, "maps/UK_Map.png",
                                    1.75, 1.7, new Color(10, 20, 20));
                            vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.white));
                        }
                    });
                    backgroundImageMenu.add(new AbstractAction("<World Map>") {

                        public void actionPerformed(ActionEvent e) {
                            BackgroundImageController.getInstance().setGraphBackgroundImage(vv, "maps/World_Map.jpg",
                                    2.05, 6.5, new Color(10, 10, 50));
                            vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(Color.white));
                        }
                    });
                    backgroundImageMenu.add(new AbstractAction("From File...") {

                        public void actionPerformed(ActionEvent e) {
                            final JFileChooser fc = new JFileChooser();
                            FileNameExtensionFilter imagesFilter = new FileNameExtensionFilter("Images (*.jpg, *.jpeg; *.png)", new String[]{"jpg", "jpeg", "png"});
                            fc.setFileFilter(imagesFilter);

                            int returnVal = fc.showOpenDialog(backgroundImageMenu.getMenuComponent(3));
                            File file;
                            if (returnVal == JFileChooser.APPROVE_OPTION) {
                                file = fc.getSelectedFile();
                                ImageIcon selectedBackgroundImage = new ImageIcon(file.getPath());

                                BackgroundImageController.getInstance().setGraphBackgroundImage(vv, selectedBackgroundImage, 2, 2, new Color(240, 240, 240));
                            }
                        }
                    });

                    setColorsMenu.add(new AbstractAction("Background") {

                        public void actionPerformed(ActionEvent e) {
                            final JColorChooser colorChooser = new JColorChooser();
                            colorChooser.getSelectionModel().addChangeListener(new ChangeListener() {

                                public void stateChanged(ChangeEvent e) {
                                    vv.setBackground(colorChooser.getColor());
                                }
                            });
                            final Color initialBackgroundColor = vv.getBackground();

                            ActionListener okListener = new ActionListener() {

                                public void actionPerformed(ActionEvent e) {

                                }
                            };
                            ActionListener cancelListener = new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    vv.setBackground(initialBackgroundColor);
                                }
                            };
                            JDialog colorSelectionDialog = JColorChooser.createDialog(setColorsMenu.getMenuComponent(0), "Select background color:", true, colorChooser, okListener, cancelListener);
                            colorSelectionDialog.setVisible(true);
                        }
                    });
                    setColorsMenu.add(new AbstractAction("Edges") {

                        public void actionPerformed(ActionEvent e) {
                            final JColorChooser colorChooser = new JColorChooser();

                            ActionListener okListener = new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(colorChooser.getColor()));
                                    vv.repaint();
                                }
                            };
                            ActionListener cancelListener = new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                }
                            };
                            JDialog colorSelectionDialog = JColorChooser.createDialog(setColorsMenu.getMenuComponent(0), "Select edge color:", true, colorChooser, okListener, cancelListener);
                            colorSelectionDialog.setVisible(true);
                        }
                    });

                    popup1.add(new AbstractAction("Create Node") {

                        public void actionPerformed(ActionEvent e) {
                            final MyVertex newV = controller.getVertexFactory().create();
                            newV.setEpiState(EpiState.SUSCEPTIBLE);
                            graph.addVertex(newV);
                            layout.setLocation(newV, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(ivp));
                            vv.repaint();
                            CustomPopupPlugin.this.d.updateStatsDisplay();
                        }
                    });
                }
                popup1.show(vv, e.getX(), e.getY());
                vv.repaint();
            }
        }
    }

    private void changeNodeType(VisualizationViewer vv, MyVertex v, int nodeType, int vertexIconStyle) {
        v.setVertexIconStyle(vertexIconStyle);
        v.setNodeType(nodeType);
        v.setTypeAutodetermined(false);
        vv.repaint();
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

}
