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

import edu.uci.ics.jung.graph.util.Pair;
import java.io.Serializable;

/**
 * @author reseter
 */
public class MyVertex implements Serializable {

    private int id;
    private EpiState epiState; //current state
    private EpiState nextEpiState; // what will happen to this guy next
    
    private VertexIcon icon;
    
    private Pair<Integer> latticePosition;
    
    public MyVertex(int id) {
        this.id = id;
        epiState = EpiState.SUSCEPTIBLE;
        nextEpiState = epiState;
        icon = new VertexIcon();
    }

    public void setEpiState(EpiState epiState) {
        this.epiState = epiState;
        nextEpiState = epiState;
    }

    public void setNextEpiState(EpiState nextEpiState) {
        this.nextEpiState = nextEpiState;
    }
    
    public EpiState getEpiState(){
        return epiState;
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
        return (epiState+","+icon.getStyle()+","+icon.getType());
    }

    /**
     * @return the icon
     */
    public VertexIcon getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(VertexIcon icon) {
        this.icon = icon;
    }

    /**
     * @return the latticePosition
     */
    public Pair<Integer> getLatticePosition() {
        return latticePosition;
    }

    /**
     * @param latticePosition the latticePosition to set
     */
    public void setLatticePosition(Pair<Integer> latticePosition) {
        this.latticePosition = latticePosition;
    }
}
