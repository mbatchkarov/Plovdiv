/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package view;

import java.awt.event.ItemEvent;
import javax.swing.*;

import model.SimulationDynamics;

/**
 * @author miroslavbatchkarov
 */
public class SimulationParametersPanel extends javax.swing.JPanel {

    private Display d;
    private double[] advancedRates;
    private AdvancedSimulationSettingsFrame advancedSettingsFrame;

    /**
     * Creates new form SimulationParametersPanel
     */
    public SimulationParametersPanel(Display d) {
        this.d = d;
        advancedRates = new double[9];
        advancedSettingsFrame = new AdvancedSimulationSettingsFrame(this);
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        gamaLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        more = new javax.swing.JButton();
        transmissionRate = new javax.swing.JTextField();
        recoveryRate = new javax.swing.JTextField();
        timeStep = new javax.swing.JTextField();
        dynamics = new javax.swing.JComboBox();
        ok = new javax.swing.JButton();

        jLabel1.setText("Dynamics");

        jLabel2.setText("Transmission rate");

        gamaLabel.setText("Recovery rate");

        jLabel4.setText("Time step");

        more.setText("More...");
        more.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moreActionPerformed(evt);
            }
        });

        transmissionRate.setText("2.0");
        transmissionRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                transmissionRateKeyReleased(evt);
            }
        });

        recoveryRate.setText("1.0");
        recoveryRate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                recoveryRateKeyReleased(evt);
            }
        });

        timeStep.setText("0.1");
        timeStep.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                timeStepKeyReleased(evt);
            }
        });

        dynamics.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"SI", "SIS", "SIR"}));
        dynamics.setSelectedIndex(1);
        dynamics.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dynamicsItemStateChanged(evt);
            }
        });
        dynamics.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                dynamicsItemStateChanged(evt);
            }
        });
        dynamics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dynamicsActionPerformed(evt);
            }
        });

        ok.setText("Apply");
        ok.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gamaLabel)
                    .addComponent(jLabel4)
                    .addComponent(more, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(timeStep, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(recoveryRate, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(transmissionRate, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(dynamics, javax.swing.GroupLayout.Alignment.LEADING, 0, 72, Short.MAX_VALUE))
                    .addComponent(ok, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dynamics, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(transmissionRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gamaLabel)
                    .addComponent(recoveryRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(timeStep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(more)
                    .addComponent(ok))
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void dynamicsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_dynamicsItemStateChanged
        JComboBox source = (JComboBox) evt.getItemSelectable();
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (source.getSelectedItem().toString().equals("SI")) {
                recoveryRate.setEnabled(false);
            }
            else{
                recoveryRate.setEnabled(true);
            }
            parseSimulationParameters();
        }
    }//GEN-LAST:event_dynamicsItemStateChanged

    public void storeAdvancedParams(double[] params) {
        this.advancedRates = params;
    }

    private void okActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okActionPerformed
        parseSimulationParameters();
    }//GEN-LAST:event_okActionPerformed

    private void moreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moreActionPerformed
        advancedSettingsFrame.setVisible(true);
    }//GEN-LAST:event_moreActionPerformed

    private void transmissionRateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_transmissionRateKeyReleased
        UIUtils.parseDoubleOrColourComponentOnError((javax.swing.JTextField) evt.getSource());
    }//GEN-LAST:event_transmissionRateKeyReleased

    private void recoveryRateKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_recoveryRateKeyReleased
        UIUtils.parseDoubleOrColourComponentOnError((javax.swing.JTextField) evt.getSource());
    }//GEN-LAST:event_recoveryRateKeyReleased

    private void timeStepKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeStepKeyReleased
        UIUtils.parseDoubleOrColourComponentOnError((javax.swing.JTextField) evt.getSource());
    }//GEN-LAST:event_timeStepKeyReleased

    private void dynamicsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dynamicsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_dynamicsActionPerformed

    public void parseSimulationParameters() {
        //check the current state of the fields
        //parse the contents of the text field that should be active (based on the combos)
        //and attach them to the graph as a Dynamics object
        //attach the dynamics setting to the graph

        try {
            double transmissionRateVal = Double.parseDouble(transmissionRate.getText());
            double recoveryRateVal = Double.parseDouble(recoveryRate.getText());
            double timeStepVal = Double.parseDouble(timeStep.getText());
            SimulationDynamics.DynamicsType type;

            if (dynamics.getSelectedItem().toString().equals("SIR")) {
                type = SimulationDynamics.DynamicsType.SIR;
            } else if (dynamics.getSelectedItem().toString().equals("SIS")) {
                type = SimulationDynamics.DynamicsType.SIS;
            } else {
                type = SimulationDynamics.DynamicsType.SI;
            }

            SimulationDynamics res = new SimulationDynamics(type,
                                                            transmissionRateVal,
                                                            timeStepVal,
                                                            recoveryRateVal,
                                                            advancedRates[0],
                                                            advancedRates[1],
                                                            advancedRates[2],
                                                            advancedRates[3],
                                                            advancedRates[4],
                                                            advancedRates[5],
                                                            advancedRates[6],
                                                            advancedRates[7],
                                                            advancedRates[8]
                                                          );
            d.updateSimulationParameters(res);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid value entered");
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox dynamics;
    private javax.swing.JLabel gamaLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton more;
    private javax.swing.JButton ok;
    private javax.swing.JTextField recoveryRate;
    private javax.swing.JTextField timeStep;
    private javax.swing.JTextField transmissionRate;
    // End of variables declaration//GEN-END:variables
}
