/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uci.ics.jung.graph;

import edu.uci.ics.jung.graph.util.Pair;
import java.awt.Color;
import model.VertexIcon;

/**
 *
 * @author User
 */
public class LayoutParameters {

    private boolean allowNodeIcons = true;
    private VertexIcon predominantVertexIcon = null;

    private Color backgroundColor = new Color(240, 240, 240);
    private Color edgeColor = Color.BLACK;

    private boolean layoutStatic = false;
    private Pair<Integer> dimension = null;
    private int nodeDensity = 100;

    /**
     * @return the allowNodeIcons
     */
    public boolean areNodeIconsAllowed() {
        return allowNodeIcons;
    }

    /**
     * @param allowNodeIcons the allowNodeIcons to set
     */
    public void setAllowNodeIcons(boolean allowNodeIcons) {
        this.allowNodeIcons = allowNodeIcons;
    }

    public VertexIcon getDominantVertexIcon() {
        return predominantVertexIcon;
    }

    public void setDominantVertexIcon(VertexIcon vertexIcon) {
       predominantVertexIcon = vertexIcon;
    }

    public int getBackgroundColorRgb() {
        return backgroundColor.getRGB();
    }

    public void setBackgroundColor(int rgb) {
        backgroundColor = new Color(rgb);
    }

    public int getEdgeColorRgb() {
        return edgeColor.getRGB();
    }

    public void setEdgeColor(int rgb) {
        edgeColor = new Color(rgb);
    }

    /**
     * @return the layoutStatic
     */
    public boolean isLayoutStatic() {
        return layoutStatic;
    }

    /**
     * @param layoutStatic the layoutStatic to set
     */
    public void setLayoutStatic(boolean layoutStatic) {
        this.layoutStatic = layoutStatic;
    }

    /**
     * @return the graphDensity
     */
    public int getNodeDensity() {
        return nodeDensity;
    }

    /**
     * @param nodeDistance the graphDensity to set
     */
    public void setNodeDensity(int nodeDistance) {
        this.nodeDensity = nodeDistance;
    }

    /**
     * @return the dimension
     */
    public Pair<Integer> getDimension() {
        return dimension;
    }

    /**
     * @param dimension the dimension to set
     */
    public void setDimension(Pair<Integer> dimension) {
        this.dimension = dimension;
    }
}
