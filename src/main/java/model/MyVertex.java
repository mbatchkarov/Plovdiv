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
package model;

import java.io.Serializable;

/**
 * @author reseter
 */
public class MyVertex implements Serializable {

    public final static int NODE_TYPE_GENERIC = 0x0;
    public final static int NODE_TYPE_USER = 0x1;
    public final static int NODE_TYPE_MOBILE = 0x2;
    public final static int NODE_TYPE_COMPUTER = 0x3;
    public final static int NODE_TYPE_ACCESS_POINT = 0x4;
    
    public final static int VERTEX_ICON_STYLE_SIMPLE = 0x1;
    public final static int VERTEX_ICON_STYLE_PHOTOREALISTIC = 0x2;

    private int id;
    private EpiState epiState; //current state
    private EpiState nextEpiState; // what will happen to this guy next
    private int nodeType = NODE_TYPE_USER; // An abstract type for the node, used for determining the icon for the vertex.
    private int vertexIconStyle = VERTEX_ICON_STYLE_SIMPLE; // An abstract type for the vertex icon, used to select between the simple and photorealistic icon packs.
    private int numberOfConnections = 0;
    private boolean typeAutodetermined = true;

    public MyVertex(int id) {
        this.id = id;
        epiState = EpiState.SUSCEPTIBLE;
        nextEpiState = epiState;
    }

    public void setEpiState(EpiState epiState) {
        this.epiState = epiState;
        nextEpiState = epiState;
    }

    public void setNextEpiState(EpiState nextEpiState) {
        this.nextEpiState = nextEpiState;
    }

    public void advanceEpiState() {
        this.epiState = nextEpiState;
    }

    public boolean isSusceptible() {
        return this.epiState.equals(EpiState.SUSCEPTIBLE);
    }

    public boolean isInfected() {
        return this.epiState.equals(EpiState.INFECTED);
    }

    public boolean isResistant() {
        return this.epiState.equals(EpiState.RESISTANT);
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    public boolean equals(Object o) {
        if (o instanceof MyVertex) {
            MyVertex v = (MyVertex) o;
            return v.id == this.id;
        }
        return false;
    }

    public int hashCode() {
        return 7 * id;
    }

    public String toString() {
        return ("[" + id + "]=" + this.epiState);
    }

    /**
     * @return the node type.
     */
    public int getNodeType() {
        if (this.isTypeAutodetermined()) {
            int nodeType = MyVertex.NODE_TYPE_MOBILE;
            if (numberOfConnections > 2 && numberOfConnections < 4) {
                nodeType = MyVertex.NODE_TYPE_COMPUTER;
            } else if (numberOfConnections > 4) {
                nodeType = MyVertex.NODE_TYPE_ACCESS_POINT;
            }
            this.setNodeType(nodeType);
        }
        return nodeType;
    }

    /**
     * @param nodeType the type of node to set. Should be one of the static
     *                 values predefined in the MyVertex class.
     */
    public void setNodeType(int nodeType) {
        this.nodeType = nodeType;
    }

    public void increaseNumberOfConnections() {
        numberOfConnections++;
    }

    /**
     * @return the typeAutodetermined
     */
    public boolean isTypeAutodetermined() {
        return typeAutodetermined;
    }

    /**
     * @param typeAutodetermined the typeAutodetermined to set
     */
    public void setTypeAutodetermined(boolean typeAutodetermined) {
        this.typeAutodetermined = typeAutodetermined;
    }

    /**
     * @return the vertexIconStyle
     */
    public int getVertexIconStyle() {
        return vertexIconStyle;
    }

    /**
     * @param vertexIconStyle the vertexIconStyle to set
     */
    public void setVertexIconStyle(int vertexIconStyle) {
        this.vertexIconStyle = vertexIconStyle;
    }
}
