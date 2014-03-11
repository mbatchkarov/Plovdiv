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
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.transform.shape.GraphicsDecorator;
import model.MyEdge;
import model.MyVertex;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * A renderer that will fill vertex shapes with a GradientPaint. Most code
 * lifted from JUNG's GradientVertexRenderer, the modifications include: -the
 * colour of the node changes depending on its specific boolean "trait"
 *
 * @author Tom Nelson, modified Miroslav Batchkarov
 */
public class CustomVertexRenderer implements Renderer.Vertex<MyVertex, MyEdge> {

    private final static int VERTEX_ICON_SELECTED = 0x0;
    private final static int VERTEX_ICON_INFECTED = 0x1;
    private final static int VERTEX_ICON_SUSCEPTIBLE = 0x2;
    private final static int VERTEX_ICON_IMMUNE = 0x3;

    //There should be one key for each node type (as defined in the MyVertex class).
    //The value holds the icons for each node state (corresponding to the values in the EpiState enum
    //+ one icon for when a vertex is selected in the editor).
    private final HashMap<Integer, Image[]> iconsMap = new HashMap<Integer, Image[]>();

    PickedState pickedState;
    boolean cyclic;

    public CustomVertexRenderer(boolean cyclic) {
        this.cyclic = cyclic;
        loadIconsMap();
    }

    public CustomVertexRenderer(PickedState pickedState, boolean cyclic) {
        this.pickedState = pickedState;
        this.cyclic = cyclic;
        loadIconsMap();
    }

    private void loadIconsMap() {
        loadUserIcons();
        loadMobileIcons();
        loadComputerIcons();
        loadAccessPointIcons();
    }

    private void loadUserIcons() {
        Image[] userIcons = new Image[4];
        userIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_user.png");
        userIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_user_infected.png");
        userIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_user_susceptible.png");
        userIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_user_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_USER, userIcons);
    }

    private void loadMobileIcons() {
        Image[] mobileIcons = new Image[4];
        mobileIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_mobile.png");
        mobileIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_mobile_infected.png");
        mobileIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_mobile_susceptible.png");
        mobileIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_mobile_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_MOBILE, mobileIcons);
    }

    private void loadComputerIcons() {
        Image[] computerIcons = new Image[4];
        computerIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_computer.png");
        computerIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_computer_infected.png");
        computerIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_computer_susceptible.png");
        computerIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_computer_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_COMPUTER, computerIcons);
    }

    private void loadAccessPointIcons() {
        Image[] accessPointIcons = new Image[4];
        accessPointIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_access_point.png");
        accessPointIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_access_point_infected.png");
        accessPointIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_access_point_susceptible.png");
        accessPointIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_access_point_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_ACCESS_POINT, accessPointIcons);
    }

    private Image loadImageFromResources(String imagePath) {
        Image image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemResource(imagePath));
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return image;
    }

    public void paintVertex(RenderContext rc, Layout layout, MyVertex v) {
        Graph graph = layout.getGraph();
        if (rc.getVertexIncludePredicate().evaluate(Context.<Graph, MyVertex>getInstance(graph, v))) {
            boolean vertexHit = true;
            // get the shape to be rendered
            int numberOfConnections = v.getNumberOfConnections();

            if (v.isTypeAutodetermined()) {
                int nodeType = MyVertex.NODE_TYPE_MOBILE;
                if (numberOfConnections > 2 && numberOfConnections < 4) {
                    nodeType = MyVertex.NODE_TYPE_COMPUTER;
                } else if (numberOfConnections > 4) {
                    nodeType = MyVertex.NODE_TYPE_ACCESS_POINT;
                }
                v.setNodeType(nodeType);
            }

            Shape shape = (Shape) rc.getVertexShapeTransformer().transform(v);

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
                paintIconForVertex(rc, v, shape);
            }
        }
    }

    protected boolean vertexHit(RenderContext rc, Shape s) {
        JComponent vv = rc.getScreenDevice();
        Rectangle deviceRectangle = null;
        if (vv != null) {
            Dimension d = vv.getSize();
            deviceRectangle = new Rectangle(0, 0, d.width, d.height);
        }
        return rc.getMultiLayerTransformer().getTransformer(Layer.VIEW).transform(s).intersects(deviceRectangle);
    }

    private void paintIconForVertex(RenderContext rc, MyVertex v, Shape shape) {
        GraphicsDecorator g = rc.getGraphicsContext();
        Rectangle r = shape.getBounds();

        Image icon = null;
        if (pickedState != null && pickedState.isPicked(v)) {
            icon = iconsMap.get(v.getNodeType())[VERTEX_ICON_SELECTED];
        } else {
            if (v.isSusceptible()) {
                icon = iconsMap.get(v.getNodeType())[VERTEX_ICON_SUSCEPTIBLE];
            } else if (v.isInfected()) {
                icon = iconsMap.get(v.getNodeType())[VERTEX_ICON_INFECTED];
            } else if (v.isResistant()) {
                icon = iconsMap.get(v.getNodeType())[VERTEX_ICON_IMMUNE];
            }
        }

        g.setColor(Color.WHITE);
        g.fill(shape);
        g.drawImage(icon, (int) (r.getMinX() + (r.getMaxX() - r.getMinX()) / 2) - icon.getWidth(null) / 2, (int) r.getMinY() - icon.getHeight(null), null);
    }
}
