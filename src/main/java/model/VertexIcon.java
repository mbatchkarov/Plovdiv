/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;

/**
 *
 * @author User
 */
public class VertexIcon implements Serializable {

    public final static int TYPE_USER = 0x1;
    public final static int TYPE_MOBILE = 0x2;
    public final static int TYPE_COMPUTER = 0x3;
    public final static int TYPE_ACCESS_POINT = 0x4;

    public final static int STYLE_SIMPLE = 0x1;
    public final static int STYLE_3D = 0x2;

    private int type = TYPE_USER;
    private int style = STYLE_SIMPLE;

    public VertexIcon() {
    }

    public VertexIcon(int type, int style) {
        this.type = type;
        this.style = style;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the style
     */
    public int getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(int style) {
        this.style = style;
    }
}
