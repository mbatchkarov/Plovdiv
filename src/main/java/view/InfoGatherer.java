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
package view;

import controller.Controller;
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.visualization.layout.PersistentLayout;
import view.generatorSettings.GeneratorSettings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Shows dialog windows and and parses user input to evoke the appropriate
 * methods of the controller with correct data
 *
 * @author Miroslav Batchkarov
 */
public class InfoGatherer {

    private Controller controller;
    private Display d;

    public InfoGatherer(Controller controller, Display d) {
        this.controller = controller;
        this.d = d;
    }

    /**
     * Shows a standard save window and sends the settings to the controller
     *
     * @param parent the window that this dialog was evoked from
     * @param g the graph to save
     */
    public void showSave(Frame parent, MyGraph g, PersistentLayout layout) {
        FileDialog window = new FileDialog(parent, "Save", FileDialog.SAVE);
        window.setSize(500, 500);
        window.setVisible(true);
        try {
            controller.save(window.getDirectory() + window.getFile(), g, layout);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(d, "Error! \n" + e.getMessage());
        }
    }

    /**
     * Shows a standard Load window and sends the settings to the controller
     *
     * @param parent parent the window that this dialog was evoked from
     */
    public void showLoad(Frame parent) {
        FileDialog window = new FileDialog(parent, "Load", FileDialog.LOAD);
        window.setSize(500, 500);
        window.setVisible(true);
        String path = window.getDirectory() + window.getFile();

        if (!path.equals("nullnull")) {//if the user clicks CANCEL path will be set to "nullnull"
            try {
                controller.load(d.vv, path);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(d, "Error! \n" + e.getMessage());
            }
        }
    }

    /**
     * Shows dialog window that collects user input regarding graph generation
     * and evokes the appropriate method of the generator (via the controller)
     *
     * @param parent
     */
    public void showGenerate(Display parent) {
        GeneratorSettings window = new GeneratorSettings(parent);
        window.setVisible(true);
    }
}
