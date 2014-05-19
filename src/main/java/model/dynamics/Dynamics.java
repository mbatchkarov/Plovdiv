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

package model.dynamics;

import model.EpiState;
import model.MyVertex;

/**
 * @author reseter
 */
public abstract class Dynamics {

    //common characteristics of all types of network dynamics
    private double transmissionRate;
    private double timeStep;

    // rates at which edge between S-I, S-S and I-I edges break/appear/rewire
    private double SIEdgeBreakingRate, SSEdgeBreakingRate, IIEdgeBreakingRate;
    private double SIEdgeCreationRate, SSEdgeCreationRate, IIEdgeCreationRate;
    private double SIEdgeRewiringRate, SSEdgeRewiringRate, IIEdgeRewiringRate;


    public double getSIEdgeBreakingProb() {
        return 1 - Math.exp(SIEdgeBreakingRate);
    }

    public double getSSEdgeBreakingProb() {
        return 1 - Math.exp(SSEdgeBreakingRate);
    }

    public double getIIEdgeBreakingProb() {
        return 1 - Math.exp(IIEdgeBreakingRate);
    }

    public double getSIEdgeCreationProb() {
        return 1 - Math.exp(SIEdgeCreationRate);
    }

    public double getSSEdgeCreationProb() {
        return 1 - Math.exp(SSEdgeCreationRate);
    }

    public double getIIEdgeCreationProb() {
        return 1 - Math.exp(IIEdgeCreationRate);
    }

    public double getSIEdgeRewiringProb() {
        return 1 - Math.exp(SIEdgeRewiringRate);
    }

    public double getSSEdgeRewiringProb() {
        return 1 - Math.exp(SSEdgeRewiringRate);
    }

    public double getIIEdgeRewiringProb() {
        return 1 - Math.exp(IIEdgeRewiringRate);
    }

    public Dynamics(double transmissionRate, double timeStep) {
        this(transmissionRate, timeStep, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
    }

    public Dynamics(double transmissionRate, double timeStep,
                    double SIEdgeBreakingRate, double SSEdgeBreakingRate, double IIEdgeBreakingRate,
                    double SIEdgeCreationRate, double SSEdgeCreationRate, double IIEdgeCreationRate,
                    double SIEdgeRewiringRate, double SSEdgeRewiringRate, double IIEdgeRewiringRate) {
        this.transmissionRate = transmissionRate;
        this.timeStep = timeStep;
        this.SIEdgeBreakingRate = SIEdgeBreakingRate;
        this.SSEdgeBreakingRate = SSEdgeBreakingRate;
        this.IIEdgeBreakingRate = IIEdgeBreakingRate;
        this.SIEdgeCreationRate = SIEdgeCreationRate;
        this.SSEdgeCreationRate = SSEdgeCreationRate;
        this.IIEdgeCreationRate = IIEdgeCreationRate;
        this.SIEdgeRewiringRate = SIEdgeRewiringRate;
        this.SSEdgeRewiringRate = SSEdgeRewiringRate;
        this.IIEdgeRewiringRate = IIEdgeRewiringRate;
    }

    public abstract double getRecoveryRate();

    public double getTransmissionRate() {
        return transmissionRate;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public abstract EpiState getNextState(MyVertex v);

    public double getRecoveryProb() {
        return 1d - Math.exp(-1 * (getRecoveryRate() * getTimeStep()));
    }

    public double getInfectionProb() {
        return 1d - Math.exp((-1 * getTransmissionRate() * getTimeStep()));
    }

}
