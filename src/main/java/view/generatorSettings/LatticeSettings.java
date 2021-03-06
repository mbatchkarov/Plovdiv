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
 * latticeSettings.java
 *
 * Created on 20-Jul-2009, 13:50:36
 */
package view.generatorSettings;

import java.awt.event.KeyEvent;

/**
 *
 * @author mb724
 */
public class LatticeSettings extends javax.swing.JPanel {

    private GeneratorSettings parent;

    /**
     * Creates new form latticeSettings
     */
    public LatticeSettings(GeneratorSettings parent) {
        this.parent = parent;
        initComponents();
        setVisible(true);
        validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        n = new javax.swing.JTextField();
        m = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        n1 = new javax.swing.JTextField();

        n.setText("6");
        n.setToolTipText("Height of the lattice");
        n.setInputVerifier(new IntVerifier());
        n.setMinimumSize(new java.awt.Dimension(50, 20));
        n.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                nKeyTyped(evt);
            }
        });

        m.setText("6");
        m.setToolTipText("Width of the lattice");
        m.setInputVerifier(new IntVerifier());

        jLabel3.setText("Height (int)");

        jLabel2.setText("Width (int)");

        jLabel4.setText("Lattice Settings");

        jLabel5.setText("Node density (%)");

        n1.setText("100");
        n1.setToolTipText("<html>When at 100% density, the entire graph will be fitted in the available visualization<br>space at the default zoom level.<br><br>A higher density would mean that the graph will be squeezed, so it takes just some<br>of the available space (good for small graphs).<br><br>A lower density will translate in some of the vertices being positioned outside of the<br>initially visible space (good for large graphs).</html>");
        n1.setInputVerifier(new IntVerifier());
        n1.setMinimumSize(new java.awt.Dimension(50, 20));
        n1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                n1ActionPerformed(evt);
            }
        });
        n1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                n1KeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(n, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(m)
                            .addComponent(n1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)))
                    .addComponent(jLabel4))
                .addGap(100, 100, 100))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(n, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(n1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        n1.getAccessibleContext().setAccessibleDescription("<html>* When at 100% density, the entire graph will be fitted in the available visualization<br>space at the default zoom level.<br>A higher density would mean that the graph will be squeezed, so it takes just some<br>of the available space (good for small graphs).<br>A lower density will translate in some of the vertices being positioned outside of the<br>initially visible space (good for large graphs).</html>");
    }// </editor-fold>//GEN-END:initComponents

    private void nKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nKeyTyped
        listenForKeys(evt);
    }//GEN-LAST:event_nKeyTyped

    private void n1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_n1KeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_n1KeyTyped

    private void n1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_n1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_n1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField m;
    private javax.swing.JTextField n;
    private javax.swing.JTextField n1;
    // End of variables declaration//GEN-END:variables

    private void listenForKeys(KeyEvent evt) {
        if (evt.getKeyChar() == KeyEvent.VK_ENTER) {
            parent.clickOk();
        }
        if (evt.getKeyChar() == KeyEvent.VK_ESCAPE) {
            parent.clickCancel();
        }
    }

    /**
     * @return the m
     */
    public javax.swing.JTextField getM() {
        return m;
    }

    /**
     * @return the n
     */
    public javax.swing.JTextField getN() {
        return n;
    }

    public javax.swing.JTextField getN1() {
        return n1;
    }

}
