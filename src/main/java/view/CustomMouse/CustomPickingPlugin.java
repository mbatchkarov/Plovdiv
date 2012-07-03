
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

import controller.Controller;
import controller.Stats;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

import javax.swing.JComponent;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.VisualizationServer.Paintable;
import edu.uci.ics.jung.visualization.control.*;
import edu.uci.ics.jung.visualization.picking.PickedState;
import model.*;
import view.Display;

/**
 * Modified this plugin to send the selected vertex to FreeRoam's
 * vertex statistics pane when the mouse button is released. Also sends the selected vertex
 * to the Stats to enable distance calculation
 *
 *
 * PickingGraphMousePlugin supports the picking of graph elements
 * with the mouse. MouseButtonOne picks a single vertex
 * or edge, and MouseButtonTwo adds to the set of selected Vertices
 * or EdgeType. If a Vertex is selected and the mouse is dragged while
 * on the selected Vertex, then that Vertex will be repositioned to
 * follow the mouse until the button is released.
 *
 * @author Tom Nelson, modified Miroslav Batchkarov
 */
public class CustomPickingPlugin extends AbstractGraphMousePlugin
    implements MouseListener, MouseMotionListener {

	/**
	 * the picked Vertex, if any
	 */
    protected MyVertex vertex;

    /**
     * the picked Edge, if any
     */
    protected MyEdge edge;

    /**
     * the x distance from the picked vertex center to the mouse point
     */
    protected double offsetx;

    /**
     * the y distance from the picked vertex center to the mouse point
     */
    protected double offsety;

    /**
     * controls whether the Vertices may be moved with the mouse
     */
    protected boolean locked;

    /**
     * additional modifiers for the action of adding to an existing
     * selection
     */
    protected int addToSelectionModifiers;

    /**
     * used to draw a rectangle to contain picked vertices
     */
    protected Rectangle2D rect = new Rectangle2D.Float();

    /**
     * the Paintable for the lens picking rectangle
     */
    protected Paintable lensPaintable;

    /**
     * color for the picking rectangle
     */
    protected Color lensColor = Color.cyan;


//    protected FreeRoam fr;//the window in which this mouse operates
        //used to update the contents of this window on mouse events

    /**
	 * create an instance with default settings
	 */
	public CustomPickingPlugin() {
	    this(InputEvent.BUTTON1_MASK, InputEvent.BUTTON1_MASK | InputEvent.SHIFT_MASK);
	}

	/**
	 * create an instance with overides
	 * @param selectionModifiers for primary selection
	 * @param addToSelectionModifiers for additional selection
	 */
    public CustomPickingPlugin(int selectionModifiers, int addToSelectionModifiers) {
        super(selectionModifiers);
        this.addToSelectionModifiers = addToSelectionModifiers;
        this.lensPaintable = new LensPaintable();
        this.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    }

    /**
     * @return Returns the lensColor.
     */
    public Color getLensColor() {
        return lensColor;
    }

    /**
     * @param lensColor The lensColor to set.
     */
    public void setLensColor(Color lensColor) {
        this.lensColor = lensColor;
    }

    /**
     * a Paintable to draw the rectangle used to pick multiple
     * Vertices
     * @author Tom Nelson
     *
     */
    class LensPaintable implements Paintable {

        public void paint(Graphics g) {
            Color oldColor = g.getColor();
            g.setColor(lensColor);
            ((Graphics2D)g).draw(rect);
            g.setColor(oldColor);
        }

        public boolean useTransform() {
            return false;
        }
    }

	/**
	 * For primary modifiers (default, MouseButton1):
	 * pick a single Vertex or Edge that
     * is under the mouse pointer. If no Vertex or edge is under
     * the pointer, unselect all picked Vertices and edges, and
     * set up to draw a rectangle for multiple selection
     * of contained Vertices.
     * For additional selection (default Shift+MouseButton1):
     * Add to the selection, a single Vertex or Edge that is
     * under the mouse pointer. If a previously picked Vertex
     * or Edge is under the pointer, it is un-picked.
     * If no vertex or Edge is under the pointer, set up
     * to draw a multiple selection rectangle (as above)
     * but do not unpick previously picked elements.
	 *
	 * @param e the event
	 */
    @SuppressWarnings("unchecked")
    public void mousePressed(MouseEvent e) {
        down = e.getPoint();
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        GraphElementAccessor pickSupport = vv.getPickSupport();
        PickedState pickedVertexState = vv.getPickedVertexState();
        PickedState pickedEdgeState = vv.getPickedEdgeState();
        if(pickSupport != null && pickedVertexState != null) {
            Layout layout = vv.getGraphLayout();
            if(e.getModifiers() == modifiers) {
                rect.setFrameFromDiagonal(down,down);
                // p is the screen point for the mouse event
                Point2D ip = e.getPoint();

                vertex = (MyVertex) pickSupport.getVertex(layout, ip.getX(), ip.getY());
                if(vertex != null) {
                    if(pickedVertexState.isPicked(vertex) == false) {
                    	pickedVertexState.clear();
                    	pickedVertexState.pick(vertex, true);
                        
                        
                        //#######  SHOW STATISTICS FOR THIS VERTEX  ##########
                        Stats.setSelectedNode(vertex);
//                        System.out.println("selected node: " + vertex);
                        ((Display)Controller.getActiveWindow()).recalculateStats(vertex);

                    }
                    // layout.getLocation applies the layout transformer so
                    // q is transformed by the layout transformer only
                    Point2D q = (Point2D) layout.transform(vertex);
                    // transform the mouse point to graph coordinate system
                    Point2D gp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, ip);

                    offsetx = (float) (gp.getX()-q.getX());
                    offsety = (float) (gp.getY()-q.getY());
                } else if((edge = (MyEdge) pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
                    pickedEdgeState.clear();
                    pickedEdgeState.pick(edge, true);
                    Stats.setSelectedNode(vertex);
                } else {
                    vv.addPostRenderPaintable(lensPaintable);
                	pickedEdgeState.clear();
                    pickedVertexState.clear();
                    //return stats back to zero
                    Stats.setSelectedNode(vertex);
                    ((Display)Controller.getActiveWindow()).recalculateStats(null);
                }

            } else if(e.getModifiers() == addToSelectionModifiers) {
                vv.addPostRenderPaintable(lensPaintable);
                rect.setFrameFromDiagonal(down,down);
                Point2D ip = e.getPoint();
                vertex = (MyVertex) pickSupport.getVertex(layout, ip.getX(), ip.getY());
                if(vertex != null) {
                    boolean wasThere = pickedVertexState.pick(vertex, !pickedVertexState.isPicked(vertex));
                    if(wasThere) {
                        vertex = null;
                    } else {

                        // layout.getLocation applies the layout transformer so
                        // q is transformed by the layout transformer only
                        Point2D q = (Point2D) layout.transform(vertex);
                        // translate mouse point to graph coord system
                        Point2D gp = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(Layer.LAYOUT, ip);

                        offsetx = (float) (gp.getX()-q.getX());
                        offsety = (float) (gp.getY()-q.getY());
                    }
                } else if((edge = (MyEdge) pickSupport.getEdge(layout, ip.getX(), ip.getY())) != null) {
                    pickedEdgeState.pick(edge, !pickedEdgeState.isPicked(edge));
                }
            }
        }
        if(vertex != null) e.consume();
    }

    /**
	 * If the mouse is dragging a rectangle, pick the
	 * Vertices contained in that rectangle
	 *
	 * clean up settings from mousePressed
	 */
    @SuppressWarnings("unchecked")
    public void mouseReleased(MouseEvent e) {
        VisualizationViewer vv = (VisualizationViewer)e.getSource();
        if(e.getModifiers() == modifiers) {
            if(down != null) {
                Point2D out = e.getPoint();

                if(vertex == null && heyThatsTooClose(down, out, 5) == false) {
                    pickContainedVertices(vv, down, out, true);
                }
            }
        } else if(e.getModifiers() == this.addToSelectionModifiers) {
            if(down != null) {
                Point2D out = e.getPoint();

                if(vertex == null && heyThatsTooClose(down,out,5) == false) {
                    pickContainedVertices(vv, down, out, false);
                }
            }
        }
        down = null;
        vertex = null;
        edge = null;
        rect.setFrame(0,0,0,0);
        vv.removePostRenderPaintable(lensPaintable);
        vv.repaint();
    }

    /**
	 * If the mouse is over a picked vertex, drag all picked
	 * vertices with the mouse.
	 * If the mouse is not over a Vertex, draw the rectangle
	 * to select multiple Vertices
	 *
	 */
    @SuppressWarnings("unchecked")
    public void mouseDragged(MouseEvent e) {
        if(locked == false) {
            VisualizationViewer vv = (VisualizationViewer)e.getSource();
            if(vertex != null) {
                Point p = e.getPoint();
                Point2D graphPoint = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(p);
                Point2D graphDown = vv.getRenderContext().getMultiLayerTransformer().inverseTransform(down);
                Layout layout = vv.getGraphLayout();
                double dx = graphPoint.getX()-graphDown.getX();
                double dy = graphPoint.getY()-graphDown.getY();
                PickedState ps = vv.getPickedVertexState();

                for(Object v : ps.getPicked()) {
                    Point2D vp = (Point2D) layout.transform(v);
                    vp.setLocation(vp.getX()+dx, vp.getY()+dy);
                    layout.setLocation(v, vp);
                }
                down = p;

            } else {
                Point2D out = e.getPoint();
                if(e.getModifiers() == this.addToSelectionModifiers ||
                        e.getModifiers() == modifiers) {
                    rect.setFrameFromDiagonal(down,out);
                }
            }
            if(vertex != null) e.consume();
            vv.repaint();
        }
    }

    /**
     * rejects picking if the rectangle is too small, like
     * if the user meant to select one vertex but moved the
     * mouse slightly
     * @param p
     * @param q
     * @param min
     * @return
     */
    private boolean heyThatsTooClose(Point2D p, Point2D q, double min) {
        return Math.abs(p.getX()-q.getX()) < min &&
                Math.abs(p.getY()-q.getY()) < min;
    }

    /**
     * pick the vertices inside the rectangle created from points
     * 'down' and 'out'
     *
     */
    protected void pickContainedVertices(VisualizationViewer vv, Point2D down, Point2D out, boolean clear) {

        Layout layout = vv.getGraphLayout();
        PickedState pickedVertexState = vv.getPickedVertexState();

        Rectangle2D pickRectangle = new Rectangle2D.Double();
        pickRectangle.setFrameFromDiagonal(down,out);

        if(pickedVertexState != null) {
            if(clear) {
            	pickedVertexState.clear();
            }
            GraphElementAccessor pickSupport = vv.getPickSupport();

            Collection picked = pickSupport.getVertices(layout, pickRectangle);
            for(Object v : picked) {
            	pickedVertexState.pick(v, true);
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
        JComponent c = (JComponent)e.getSource();
        c.setCursor(cursor);
    }

    public void mouseExited(MouseEvent e) {
        JComponent c = (JComponent)e.getSource();
        c.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void mouseMoved(MouseEvent e) {
    }

    /**
     * @return Returns the locked.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * @param locked The locked to set.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
