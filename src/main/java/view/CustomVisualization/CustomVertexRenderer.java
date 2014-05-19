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

package view.CustomVisualization;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import model.MyEdge;
import model.MyVertex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * A renderer that will fill vertex shapes with a GradientPaint.
 * Most code lifted from JUNG's GradientVertexRenderer, the modifications include:
 * -the colour of the node changes depending on its specific boolean "trait"
 *
 * @author Tom Nelson, modified Miroslav Batchkarov
 */
public class CustomVertexRenderer implements Renderer.Vertex<MyVertex, MyEdge> {

    PickedState pickedState;
    boolean     cyclic;

    public CustomVertexRenderer(boolean cyclic) {
        this.cyclic = cyclic;
    }

    public CustomVertexRenderer(PickedState pickedState, boolean cyclic) {
        this.pickedState = pickedState;
        this.cyclic = cyclic;
    }

    public void paintVertex(RenderContext rc, Layout layout, MyVertex v) {
        Graph graph = layout.getGraph();
        if (rc.getVertexIncludePredicate().evaluate(Context.<Graph, MyVertex>getInstance(graph, v))) {
            boolean vertexHit = true;
            // get the shape to be rendered
            Shape shape = new Ellipse2D.Float(-14.0f, -14.0f, 30.0f, 30.0f);
                    
            Point2D p = (Point2D) layout.transform(v);
            p = rc.getMultiLayerTransformer().transform(Layer.LAYOUT, p);

            float x = (float) p.getX();
            float y = (float) p.getY();

            // create a transform that translates to the location of
            // the vertex to be rendered
            AffineTransform xform = AffineTransform.getTranslateInstance(x, y);
            // transform the vertex shape with xtransform
            shape = xform.createTransformedShape(shape);

            vertexHit = vertexHit(rc, shape);
            //rc.getViewTransformer().transform(shape).intersects(deviceRectangle);

            if (vertexHit) {
                paintShapeForVertex(rc, v, shape);
            }
        }
    }

    protected boolean vertexHit(RenderContext rc, Shape s) {
        JComponent vv = rc.getScreenDevice();
        Rectangle deviceRectangle = null;
        if (vv != null) {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(
                    0, 0,
                    d.width, d.height);
        }
        return rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(s).intersects(deviceRectangle);
    }

    protected void paintShapeForVertex(RenderContext rc, MyVertex v, Shape shape) {
        GraphicsDecorator g = rc.getGraphicsContext();
        Paint oldPaint = g.getPaint();
        Rectangle r = shape.getBounds();
        float y2 = (float) r.getMaxY();
        if (cyclic) {
            y2 = (float) (r.getMinY() + r.getHeight() / 2);
        }

        Paint fillPaint = null;
        if (pickedState != null && pickedState.isPicked(v)) {
            //if the vertex is picked, paint with universal colour
            fillPaint = new GradientPaint((float) r.getMinX(), (float) r.getMinY(), Color.BLUE,
                                          (float) r.getMinX(), y2, Color.white, cyclic);

        } else {
            //if not picked, paint based on the state
            if (v.isSusceptible()) {
                fillPaint = new GradientPaint((float) r.getMinX(), (float) r.getMinY(), Color.white,
                                              (float) r.getMinX(), y2, Color.orange, cyclic);
            } else if (v.isInfected()) {
                fillPaint = new GradientPaint((float) r.getMinX(), (float) r.getMinY(), Color.white,
                                              (float) r.getMinX(), y2, Color.red, cyclic);
            } else if (v.isResistant()) {
                fillPaint = new GradientPaint((float) r.getMinX(), (float) r.getMinY(), Color.white,
                                              (float) r.getMinX(), y2, Color.green, cyclic);
            } else {//go black to indicate something's wrong
                fillPaint = new GradientPaint((float) r.getMinX(), (float) r.getMinY(), Color.white,
                                              (float) r.getMinX(), y2, Color.BLACK, cyclic);
            }
        }
        if (fillPaint != null) {
            g.setPaint(fillPaint);
            g.fill(shape);
            g.setPaint(oldPaint);
        }
        Paint drawPaint = (Paint) rc.getVertexDrawPaintTransformer().transform(v);
        if (drawPaint != null) {
            g.setPaint(drawPaint);
        }
        Stroke oldStroke = g.getStroke();
        Stroke stroke = (Stroke) rc.getVertexStrokeTransformer().transform(v);
        if (stroke != null) {
            g.setStroke(stroke);
        }
        g.draw(shape);
        g.setPaint(oldPaint);
        g.setStroke(oldStroke);
    }


}