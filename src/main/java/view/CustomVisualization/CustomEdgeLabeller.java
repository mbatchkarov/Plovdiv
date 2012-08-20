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

import view.*;
import controller.Stats;
import model.*;
import org.apache.commons.collections15.Transformer;

import static view.Utils.round;

/**
 *
 * @author mb724
 */
public class CustomEdgeLabeller implements Transformer<MyEdge, String> {

    public CustomEdgeLabeller() {
    }

    public String transform(MyEdge e) {
        String label = "";

        try {
            String mode = "" + Display.getSelectedEdgeLabelingOption();
//            System.out.println("Edge labelling option:" + mode);
            if (mode.equals("0")) {
                label += round(e.getWeigth());
            }
            if (mode.equals("1")) {
//                System.out.println("Edge id: " + e.getId());
                label += e.getId();
            }
            if (mode.equals("2")) {
                label += round(Stats.getBC(e));
            }
            if (mode.equals("3")) {
                label = "";
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Error while labelling edge!");
            return label;
        }

        return label;
    }
}
