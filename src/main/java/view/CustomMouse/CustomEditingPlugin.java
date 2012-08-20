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

package view.CustomMouse;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractGraphMousePlugin;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.util.Animator;
import edu.uci.ics.jung.visualization.util.ArrowFactory;
import model.*;
import org.apache.commons.collections15.Factory;
import view.Display;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * A plugin that can create vertices, undirected edges, and directed edges
 * using mouse gestures. After every modification to the graph the statistics
 * are recalsulated and displayed
 *
 * @author Tom Nelson, modified by Miroslav Batchkarov
 *
 */
public class CustomEditingPlugin extends AbstractGraphMousePlugin implements
        MouseListener, MouseMotionListener {

    protected MyVertex startVertex;
    protected Point2D down;
    protected CubicCurve2D rawEdge = new CubicCurve2D.Float();
    protected Shape edgeShape;
    protected Shape rawArrowShape;
    protected Shape arrowShape;
    protected VisualizationServer.Paintable edgePaintable;
    protected VisualizationServer.Paintable arrowPaintable;
    protected EdgeType edgeIsDirected;
    protected Factory<MyVertex> vertexFactory;
    protected Factory<MyEdge> edgeFactory;

    public CustomEditingPlugin(Factory vertexFactory, Factory edgeFactory) {
        this(MouseEvent.BUTTON1_MASK, vertexFactory, edgeFactory);
    }

    /**
     * create instance and prepare shapes for visual effects
     * @param modifiers
     * @param vertexFactory
     * @param edgeFactory
     */
    public CustomEditingPlugin(int modifiers, Factory vertexFactory, Factory edgeFactory) {
        super(modifiers);
        this.vertexFactory = vertexFactory;
        this.edgeFactory = edgeFactory;
        rawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50,
                1.0f, 0.0f);
        rawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
        edgePaintable = new EdgePaintable();
        arrowPaintable = new ArrowPaintable();
        this.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    /**
     * Overridden to be more flexible, and pass events with
     * key combinations. The default responds to both ButtonOne
     * and ButtonOne+Shift
     */
    @Override
    public boolean checkModifiers(MouseEvent e) {
        return (e.getModifiers() & modifiers) != 0;
    }

    /**
     * If the mouse is pressed in an empty area, create a new vertex there.
     * If the mouse is pressed on an existing vertex, prepare to create
     * an edge from that vertex to another
     * @param e
     */
    @SuppressWarnings("unchecked")
    public void mousePressed(MouseEvent e) {
        if (checkModifiers(e)) {
//        if(SwingUtilities.isLeftMouseButton(e)){//        MyVertex x = new MyVertex(MyGraph.getBuffer().getVertexCount() + 1);

            final VisualizationViewer vv =
                    (VisualizationViewer) e.getSource();
            final Point2D p = e.getPoint();
            GraphElementAccessor pickSupport = vv.getPickSupport();
            final PickedState<MyVertex> pickedVertexState = vv.getPickedVertexState();

            if (pickSupport != null) {
                Graph graph = vv.getModel().getGraphLayout().getGraph();
                // set default edge type
                if (graph instanceof DirectedGraph) {
                    edgeIsDirected = EdgeType.DIRECTED;
                } else {
                    edgeIsDirected = EdgeType.UNDIRECTED;
                }
                //the vertex that was picked
                final MyVertex vertex = (MyVertex) pickSupport.getVertex(
                        vv.getModel().getGraphLayout(), p.getX(), p.getY());
                if (vertex != null) { // get ready to make an edge
                    startVertex = vertex;
                    down = e.getPoint();
                    transformEdgeShape(down, down);
                    vv.addPostRenderPaintable(edgePaintable);
                    if ((e.getModifiers() & MouseEvent.SHIFT_MASK) != 0 && vv.getModel().getGraphLayout().getGraph() instanceof UndirectedGraph == false) {
                        edgeIsDirected = EdgeType.DIRECTED;
                    }
                    if (edgeIsDirected == EdgeType.DIRECTED) {
                        transformArrowShape(down, e.getPoint());
                        vv.addPostRenderPaintable(arrowPaintable);
                    }
                } else { // make a new vertex

                    final MyVertex newVertex = vertexFactory.create();
                    newVertex.setUserDatum(Strings.state, EpiState.SUSCEPTIBLE);
                    Layout layout = vv.getModel().getGraphLayout();
                    graph.addVertex(newVertex);
                    layout.setLocation(newVertex, vv.getRenderContext().
                            getMultiLayerTransformer().inverseTransform(e.getPoint()));
                    //clear selection
                    for (MyVertex v : pickedVertexState.getPicked()) {
                                pickedVertexState.pick(v, false);
                            }
                    Display.recalculateStats(null);//todo replace with notifyListeners()
                }
            }
            vv.repaint();//todo replace with notifyListeners()
            Display.recalculateStats(null);//todo replace with notifyListeners()
        }
    }

    /**
     * If startVertex is non-null, and the mouse is released over an
     * existing vertex, create an undirected edge from startVertex to
     * the vertex under the mouse pointer. If shift was also pressed,
     * create a directed edge instead.
     * Redraw layout with an animation
     */
    @SuppressWarnings("unchecked")
    public void mouseReleased(MouseEvent e) {
        if (checkModifiers(e)) {
            final VisualizationViewer vv =
                    (VisualizationViewer) e.getSource();
            final Point2D p = e.getPoint();
            Layout layout = vv.getModel().getGraphLayout();
            GraphElementAccessor pickSupport = vv.getPickSupport();
            if (pickSupport != null) {
                final MyVertex vertex = (MyVertex) pickSupport.getVertex(layout, p.getX(), p.getY());
                if (vertex != null && startVertex != null) {
                    Graph graph =
                            vv.getGraphLayout().getGraph();
                    if(MyGraph.getInstance().findEdge(startVertex, vertex) == null
                            && !vertex.equals(startVertex)) {//do not allow edges to self
                        graph.addEdge(edgeFactory.create(), startVertex, vertex, edgeIsDirected);
                    }
//                    process(e);
                    vv.repaint();
                    Display.recalculateStats(null);//todo replace with notifyListeners()
                }
            }
            startVertex = null;
            down = null;
            edgeIsDirected = EdgeType.UNDIRECTED;
            vv.removePostRenderPaintable(edgePaintable);
            vv.removePostRenderPaintable(arrowPaintable);
            Display.recalculateStats(null);
        }
    }

    /**
     * If startVertex is non-null, stretch an edge shape between
     * startVertex and the mouse pointer to simulate edge creation
     */
    @SuppressWarnings("unchecked")
    public void mouseDragged(MouseEvent e) {
        if (checkModifiers(e)) {
            if (startVertex != null) {
                transformEdgeShape(down, e.getPoint());
                if (edgeIsDirected == EdgeType.DIRECTED) {
                    transformArrowShape(down, e.getPoint());
                }
            }
            VisualizationViewer vv =
                    (VisualizationViewer) e.getSource();
            vv.repaint();
        }
    }

    /**
     * code lifted from PluggableRenderer to move an edge shape into an
     * arbitrary position
     */
    private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);

        float dx = x2 - x1;
        float dy = y2 - y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        xform.scale(dist / rawEdge.getBounds().getWidth(), 1.0);
        edgeShape = xform.createTransformedShape(rawEdge);
    }

    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);

        float dx = x2 - x1;
        float dy = y2 - y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        arrowShape = xform.createTransformedShape(rawArrowShape);
    }

    /**
     * Based on AnimatingAddNodeDemo, should produce an animation when an edge is added
     * @param e
     */
    public void process(MouseEvent e) {

        final VisualizationViewer vv =
                (VisualizationViewer) e.getSource();

//        vv.getRenderContext().getPickedVertexState().clear();
//        vv.getRenderContext().getPickedEdgeState().clear();
        try {

            Layout layout = vv.getGraphLayout();
            layout.initialize();

            Relaxer relaxer = new VisRunner((IterativeContext) layout);
            relaxer.stop();
            relaxer.prerelax();
            LayoutTransition<MyVertex, MyEdge> lt = //do not change layout
                    new LayoutTransition<MyVertex, MyEdge>(vv, layout, layout);
            Animator animator = new Animator(lt);
            animator.start();
//				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
            vv.repaint();

        } catch (Exception ex) {
            System.out.println(ex);

        }
    }

    /**
     * Used for the edge creation visual effect during mouse drag
     */
    class EdgePaintable implements VisualizationServer.Paintable {

        public void paint(Graphics g) {
            if (edgeShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D) g).draw(edgeShape);
                g.setColor(oldColor);
            }
        }

        public boolean useTransform() {
            return false;
        }
    }

    /**
     * Used for the directed edge creation visual effect during mouse drag
     */
    class ArrowPaintable implements VisualizationServer.Paintable {

        public void paint(Graphics g) {
            if (arrowShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D) g).fill(arrowShape);
                g.setColor(oldColor);
            }
        }

        public boolean useTransform() {
            return false;
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent) e.getSource();
        c.setCursor(cursor);
    }

    public void mouseExited(MouseEvent e) {
        JComponent c = (JComponent) e.getSource();
        c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseMoved(MouseEvent e) {
    }
}
