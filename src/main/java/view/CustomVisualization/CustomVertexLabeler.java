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
package view.CustomVisualization;

import controller.Stats;
import edu.uci.ics.jung.graph.MyGraph;
import model.MyVertex;
import org.apache.commons.collections15.Transformer;
import view.Display;

import static view.Utils.round;

/**
 *
 * @author mb724
 */
public class CustomVertexLabeler implements Transformer<MyVertex, String> {

    private Stats stats;

    public CustomVertexLabeler(Stats stats) {
        this.stats = stats;
    }

    public String transform(MyVertex v) {
        String label = "";

        try {
            //Degree, Clustering, Centrality, Label, Distance from selected vertex
            String mode = "" + Display.getSelectedVertexLabelingOption();

            if (mode.startsWith("0")) {
                label += stats.getGraph().degree(v);
            }
            if (mode.startsWith("1")) {
                label += round(stats.getCC(v));
            }
            if (mode.startsWith("2")) {
                label += round(stats.getBetweennessCentrality(v));
            }
            
            if (mode.startsWith("4")) {//distance from selected node
                String d = stats.getDistFromSelectedTo(v);
                if (d == null) {
                    label += "unreachable";
                } else {
                    label += d;
                }
            }
            if (mode.startsWith("5")) {

                label += v.getId();

            }
            if (mode.startsWith("6")) {
                label = "";
            }
        } catch (IllegalArgumentException e) {
            return label;
        }

        return label;
    }
}
