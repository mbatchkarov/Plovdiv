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

/*
 * GeneratorSettings.java
 *
 * Created on 20-Jul-2009, 13:10:12
 */
package view.generatorSettings;

import controller.Controller;
import view.Display;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * @author mb724
 */
public class GeneratorSettings extends javax.swing.JFrame {

    private final Controller controller;
    private Display parent;

    /**
     * Creates new form GeneratorSettings
     *
     * @param parent
     */
    public GeneratorSettings(Display parent) {
        this.parent = parent;
        initComponents();
        setVisible(true);
        pane.setLayout(new FlowLayout());
        type.setSelectedIndex(0);
        this.controller = parent.getController();
    }

    /**
     * Programatically sets the selected index of the combo box
     *
     * @param n
     */
    public void setIndex(int n) {
        type.setSelectedIndex(n);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        type = new javax.swing.JComboBox();
        cancel = new javax.swing.JButton();
        ok = new javax.swing.JButton();
        pane = new javax.swing.JPanel();
        help = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Generate network");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        jLabel1.setText("Graph type:");

        type.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Rectangular Lattice", "Hexagonal Lattice", "Scale-free", "Small-world", "Random"}));
        type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeActionPerformed(evt);
            }
        });

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        ok.setText("OK");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        pane.setMinimumSize(new java.awt.Dimension(300, 300));

        javax.swing.GroupLayout paneLayout = new javax.swing.GroupLayout(pane);
        pane.setLayout(paneLayout);
        paneLayout.setHorizontalGroup(
                paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 399, Short.MAX_VALUE)
        );
        paneLayout.setVerticalGroup(
                paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 186, Short.MAX_VALUE)
        );

        help.setText("Instructions:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(type, 0, 399, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addContainerGap())
                                        .addComponent(help, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel1)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(cancel)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 283, Short.MAX_VALUE)
                                                                .addComponent(ok)))
                                                .addContainerGap())))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(help, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pane, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(ok)
                                        .addComponent(cancel))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelActionPerformed

    private void typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeActionPerformed
        int index = type.getSelectedIndex();
        pane.removeAll();
        if (index == 0) {
            pane.add(new LatticeSettings(this));
        }
        if (index == 1) {
            pane.add(new LatticeSettings(this));
        }
        if (index == 2) {
            pane.add(new BASettings(this));
        }
        if (index == 3) {
            pane.add(new SmallWorldSettings(this));
        }
        help.setText("Parameters:");
        if (index == 4) {
            pane.add(new RandomSettings(this));
            help.setText("<html>Parameters:<br>* Generates a new random graph with the specified number of nodes<br>and edges.</html>");
        }
        pane.setVisible(true);
        pane.validate();
        pane.repaint();
        this.pack();
        this.repaint();
        this.validate();
    }//GEN-LAST:event_typeActionPerformed

    private void listenForKeys(KeyEvent evt) {
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            ok.doClick();
        }
        if (evt.getKeyChar() == KeyEvent.VK_ESCAPE) {
            cancel.doClick();
        }
    }

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        try {
            switch (type.getSelectedIndex()) {
                case 0: {
                    controller.generate4Lattice(
                            Integer.parseInt(((LatticeSettings) pane.getComponent(0)).getM().getText()),
                            Integer.parseInt(((LatticeSettings) pane.getComponent(0)).getN().getText()));
                    break;
                }
                case 1: {
                    controller.generate6Lattice(
                            Integer.parseInt(((LatticeSettings) pane.getComponent(0)).getM().getText()),
                            Integer.parseInt(((LatticeSettings) pane.getComponent(0)).getN().getText()));
                    break;
                }
                case 2: {
                    controller.generateScaleFree(
                            Integer.parseInt(((BASettings) pane.getComponent(0)).getS().getText()), 1, 1);
                    break;
                }
                case 3: {
                    controller.generateKleinbergSmallWorld(
                            Integer.parseInt(((SmallWorldSettings) pane.getComponent(0)).getM().getText()),
                            Integer.parseInt(((SmallWorldSettings) pane.getComponent(0)).getN().getText()),
                            Double.parseDouble(((SmallWorldSettings) pane.getComponent(0)).getE().getText()));
                    break;
                }
                case 4: {
                    controller.generateRandom(
                            Integer.parseInt(((RandomSettings) pane.getComponent(0)).getV().getText()),
                            Integer.parseInt(((RandomSettings) pane.getComponent(0)).getE().getText()));
                    break;
                }
            }
//            Display.redisplayCompletely();
            dispose();
        } catch (IllegalArgumentException e) {
            //do nothing until the input verifier says so
        }
    }//GEN-LAST:event_okActionPerformed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        listenForKeys(evt);
    }//GEN-LAST:event_formKeyTyped

    /**
     * Used by listeners in the classes, which provided the contents of this.pane
     */
    protected void clickOk() {
        this.ok.doClick();
    }

    protected void clickCancel() {
        this.cancel.doClick();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel;
    private javax.swing.JLabel help;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton ok;
    private javax.swing.JPanel pane;
    private javax.swing.JComboBox type;
    // End of variables declaration//GEN-END:variables
}
