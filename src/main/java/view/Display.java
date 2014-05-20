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
 * Display.java
 *
 * Created on 15-Jul-2009, 14:27:03
 */
package view;

import controller.*;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.graph.event.GraphEvent;
import edu.uci.ics.jung.graph.event.GraphEventListener;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.annotations.AnnotationControls;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.layout.PersistentLayoutImpl;
import edu.uci.ics.jung.visualization.renderers.DefaultVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;
import model.MyEdge;
import model.MyVertex;
import model.SimulationDynamics;
import model.Strings;
import org.apache.commons.collections15.functors.ConstantTransformer;
import view.CustomMouse.CustomGraphMouse;
import view.CustomVisualization.CenterLabelPositioner;
import view.CustomVisualization.CustomEdgeLabeller;
import view.CustomVisualization.CustomVertexLabeler;
import view.CustomVisualization.CustomVertexIconShapeTransformer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

import org.apache.commons.collections15.Transformer;
import view.CustomVisualization.CustomVertexRenderer;

import static view.Utils.round;

/**
 * @author mb724
 */
@SuppressWarnings({"MagicNumber", "AssignmentToStaticFieldFromInstanceMethod", "OverlyLongMethod", "MethodOnlyUsedFromInnerClass"})
public class Display extends JFrame implements GraphEventListener<MyVertex, MyEdge>,
        ExtraGraphEventListener<MyVertex, MyEdge> {

    static javax.swing.JToggleButton annotate;
    private javax.swing.JRadioButtonMenuItem circleL;
    private static javax.swing.JCheckBox degDistCumulative;
    private static javax.swing.JCheckBox degDistLogScale;
    private static javax.swing.JPanel degreeDistPanel;
    static javax.swing.JButton doStepToolbarButton;
    private javax.swing.JMenuItem dumpToJpg;
    private javax.swing.JMenuItem toggleSidebar;
    private javax.swing.JRadioButtonMenuItem eBC;
    private javax.swing.JRadioButtonMenuItem eID;
    private javax.swing.JRadioButtonMenuItem eNone;
    private javax.swing.JRadioButtonMenuItem eWeight;
    private static javax.swing.ButtonGroup edgeLabel;
    static javax.swing.JToggleButton edit;
    private javax.swing.JMenuItem fileGenerate1;
    private javax.swing.JMenuItem fileLoad;
    private javax.swing.JMenuItem fileQuit1;
    private javax.swing.JMenuItem fileSave;
    private javax.swing.JRadioButtonMenuItem fr;
    static javax.swing.JLabel globalAPL;
    static javax.swing.JLabel globalAvgDegree;
    static javax.swing.JLabel globalCC;
    static javax.swing.JLabel globalDegreeCorrelation;
    static javax.swing.JLabel globalEdgeCount;
    static javax.swing.JLabel globalVertexCount;
    private javax.swing.JPanel graphStatsPanel;
    private javax.swing.JMenuItem healAllNodesButton;
    private javax.swing.JMenuItem helpAbout;
    private javax.swing.JMenuItem helpHowTo;
    private static javax.swing.JLabel in;
    private javax.swing.JMenuItem infectNodesButton;
    private javax.swing.JRadioButtonMenuItem isom;
    private javax.swing.JButton infectButton;
    private javax.swing.JButton healButton;
    static javax.swing.JLabel avgDegreeLabel;
    private static javax.swing.JLabel localAPLLabel;
    static javax.swing.JLabel aplLabel;
    private static javax.swing.JLabel bcLabel;
    private static javax.swing.JLabel inDegreeLabel;
    private static javax.swing.JLabel outDegreeLabel;
    static javax.swing.JLabel degreeCorrLabel;
    static javax.swing.JLabel ccLabel;
    static javax.swing.JLabel vertexCountLabel;
    static javax.swing.JLabel edgeCountLabel;
    private static javax.swing.JLabel localCCLabel;
    private static javax.swing.JMenu viewMenu;
    private static javax.swing.JMenuBar topMenuBar;
    private javax.swing.JMenuItem doStepItem;
    private javax.swing.JPanel diseaseControlsPanel;
    private javax.swing.JPanel nodeStatisticsPanel;
    private javax.swing.JPanel stepTimePanel;
    private javax.swing.JSeparator simulationMenuSeparator;
    private javax.swing.JRadioButtonMenuItem kk;
    private static javax.swing.JMenu vertexLabelMenu;
    private static javax.swing.JMenu edgeLabelMenu;
    private static javax.swing.JMenu layoutMenu;
    private static javax.swing.ButtonGroup layouts;
    private static javax.swing.JLabel localAPL;
    private static javax.swing.JLabel localBC;
    private static javax.swing.JLabel localCC;
    private static javax.swing.JMenu menuFile;
    private static javax.swing.JMenu menuHelp;
    private static javax.swing.JMenu menuSimulation;
    private static javax.swing.ButtonGroup mouseModeButtonGroup;
    static javax.swing.JToolBar mouseModeToolbar;
    private javax.swing.JMenuItem newDoc;
    private static javax.swing.JLabel out;
    private static javax.swing.JPanel pane;
    static javax.swing.JButton pauseSimToolbarButton;
    static javax.swing.JToggleButton select;
    private javax.swing.JPanel simControlsPanel;
    private SimulationParametersPanel simParamsPanel;
    private javax.swing.JMenuItem simPauseMenuItem;
    private static javax.swing.JSlider speedSlider;
    private javax.swing.JRadioButtonMenuItem spring;
    private static javax.swing.JPanel statsPanel;
    static javax.swing.JToggleButton transform;
    private javax.swing.JRadioButtonMenuItem vBC;
    private javax.swing.JRadioButtonMenuItem vCC;
    private javax.swing.JRadioButtonMenuItem vDEgree;
    private javax.swing.JRadioButtonMenuItem vDist;
    private javax.swing.JRadioButtonMenuItem vID;
    private javax.swing.JRadioButtonMenuItem vNone;
    private static javax.swing.ButtonGroup vertexLabel;
    private javax.swing.JButton showSidebarButton;
    private javax.swing.JButton hideSidebarButton;
    private javax.swing.JPanel simulationControlsPanel;

    private static InfoGatherer gatherer;
    //declared as fields rather than as local variables so that their value can be altered by listeners
    public static VisualizationViewer<MyVertex, MyEdge> vv;
    private static ScalingControl scaler;
    //    EditingModalGraphMouse<MyVertex, MyEdge> graphMouse;
    private static CustomGraphMouse graphMouse;
    static AnnotationControls<MyVertex, MyEdge> annotationControls;
    static JToolBar annotationControlsToolbar;
    private static PersistentLayoutImpl persistentLayout;
    public boolean handlingEvents;
    private Stats stats;
    private Controller controller;
    private Renderer.Vertex defaultRenderer;
    private Transformer defaultVertexIconShapeTransformer;
    private MyGraph g;
    private IconsStore icons;

    private boolean simulationControlsPanelCollapsed = false;
    private boolean diseaseStatsPanelCollapsed = false;
    private boolean graphStatsPanelCollapsed = false;
    private boolean rightSidebarVisible = true;

    /**
     * Creates new form Display
     */
    public Display(Stats stats, Controller cont, MyGraph g) {
        try {
            UIManager.setLookAndFeel(
                    UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        initComponents();
        pack();
        this.stats = stats;
        this.controller = cont;
        this.g = g;
        this.handlingEvents = false;

        gatherer = new InfoGatherer(controller, this);

        //set shortcuts for controlling the simulation speed
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "+Action");
        actionMap.put("+Action", incrementWaitTime);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "-Action");
        actionMap.put("-Action", decrementWaitTime);
        scaler = new CrossoverScalingControl();
        redisplayCompletely();

        vNone.setSelected(true);
        simParamsPanel.parseSimulationParameters();// trigger parsing of default values
        // for transmission params
    }

    private AbstractAction incrementWaitTime = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
            speedSlider.setValue(speedSlider.getValue() + 100);
        }
    };
    private AbstractAction decrementWaitTime = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
            speedSlider.setValue(speedSlider.getValue() - 100);
        }
    };

    /**
     * Recalculates and display the stats of the graph and of the vertex passed
     * in. If it is null, sets all local statistics to "N/A"
     */
    public void updateStatsDisplay() {
        // get selected vertex
        MyVertex selectedVertex = null;
        ArrayList<MyVertex> pickedVertices = new ArrayList(
                this.getVV().getPickedVertexState().getPicked()
        );
        if (pickedVertices.size() == 1) {
            selectedVertex = pickedVertices.get(0);
        }

        //populate information labels across the screen
        globalCC.setText(round(stats.getCC()));
        globalAPL.setText(round(stats.getAPL()));
        globalAvgDegree.setText(round(stats.getAvgDegree()));
        globalDegreeCorrelation.setText(round(stats.getWeightedDegreeCorrelation()));

        globalVertexCount.setText(String.valueOf((int) stats.getVertexCount()));
        globalEdgeCount.setText(String.valueOf((int) stats.getEdgeCount()));

        updateDegreeDistributionChart();

        //information about a certain node
        if (selectedVertex != null) {
            localCC.setText(round(stats.getCC(selectedVertex)));
            localAPL.setText(round(stats.getAPL(selectedVertex)));
            localBC.setText(round(stats.getBetweennessCentrality(selectedVertex)));
            in.setText(round(g.inDegree(selectedVertex)));
            out.setText(round(g.outDegree(selectedVertex)));
            nodeStatisticsPanel.setPreferredSize(new Dimension(290, 60));
        } else {
            localCC.setText("N/A");
            localAPL.setText("N/A");
            localBC.setText("N/A");
            in.setText("N/A");
            out.setText("N/A");
            nodeStatisticsPanel.setPreferredSize(new Dimension(290, 0));
        }

        nodeStatisticsPanel.revalidate();
    }

    private void initDegreeDistributionChart() {
        JPanel degreeChart = stats.buildDegreeDistributionChart(
                degDistCumulative.isSelected(),
                degDistLogScale.isSelected(),
                degreeDistPanel.getSize());

        degreeDistPanel.setLayout(new FlowLayout());
        degreeDistPanel.removeAll();
        degreeDistPanel.add(degreeChart);
    }

    private void updateDegreeDistributionChart() {
        stats.updateDegreeDistributionChartData(degDistCumulative.isSelected(),
                                                degDistLogScale.isSelected());
    }

    public VisualizationViewer<MyVertex, MyEdge> getVV() {
        return vv;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        vertexLabel = new javax.swing.ButtonGroup();
        layouts = new javax.swing.ButtonGroup();
        edgeLabel = new javax.swing.ButtonGroup();
        mouseModeButtonGroup = new javax.swing.ButtonGroup();
        mouseModeToolbar = new javax.swing.JToolBar();
        select = new javax.swing.JToggleButton();
        edit = new javax.swing.JToggleButton();
        transform = new javax.swing.JToggleButton();
        annotate = new javax.swing.JToggleButton();
        pane = new javax.swing.JPanel();
        diseaseControlsPanel = new javax.swing.JPanel();
        statsPanel = new javax.swing.JPanel();
        nodeStatisticsPanel = new javax.swing.JPanel();
        localCC = new javax.swing.JLabel();
        localCCLabel = new javax.swing.JLabel();
        localAPL = new javax.swing.JLabel();
        localAPLLabel = new javax.swing.JLabel();
        bcLabel = new javax.swing.JLabel();
        localBC = new javax.swing.JLabel();
        inDegreeLabel = new javax.swing.JLabel();
        in = new javax.swing.JLabel();
        outDegreeLabel = new javax.swing.JLabel();
        out = new javax.swing.JLabel();
        graphStatsPanel = new javax.swing.JPanel();
        avgDegreeLabel = new javax.swing.JLabel();
        aplLabel = new javax.swing.JLabel();
        degreeCorrLabel = new javax.swing.JLabel();
        ccLabel = new javax.swing.JLabel();
        globalCC = new javax.swing.JLabel();
        globalAPL = new javax.swing.JLabel();
        globalAvgDegree = new javax.swing.JLabel();
        globalDegreeCorrelation = new javax.swing.JLabel();
        vertexCountLabel = new javax.swing.JLabel();
        globalVertexCount = new javax.swing.JLabel();
        globalEdgeCount = new javax.swing.JLabel();
        edgeCountLabel = new javax.swing.JLabel();
        degreeDistPanel = new javax.swing.JPanel();
        degDistLogScale = new javax.swing.JCheckBox();
        degDistCumulative = new javax.swing.JCheckBox();
        simControlsPanel = new javax.swing.JPanel();
        simParamsPanel = new SimulationParametersPanel(this);
        pauseSimToolbarButton = new javax.swing.JButton();
        doStepToolbarButton = new javax.swing.JButton();
        infectButton = new javax.swing.JButton();
        healButton = new javax.swing.JButton();
        stepTimePanel = new javax.swing.JPanel();
        speedSlider = new javax.swing.JSlider();
        topMenuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        newDoc = new javax.swing.JMenuItem();
        fileSave = new javax.swing.JMenuItem();
        fileLoad = new javax.swing.JMenuItem();
        fileGenerate1 = new javax.swing.JMenuItem();
        fileQuit1 = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        dumpToJpg = new javax.swing.JMenuItem();
        toggleSidebar = new javax.swing.JMenuItem();
        vertexLabelMenu = new javax.swing.JMenu();
        vDEgree = new javax.swing.JRadioButtonMenuItem();
        vCC = new javax.swing.JRadioButtonMenuItem();
        vBC = new javax.swing.JRadioButtonMenuItem();
        vDist = new javax.swing.JRadioButtonMenuItem();
        vID = new javax.swing.JRadioButtonMenuItem();
        vNone = new javax.swing.JRadioButtonMenuItem();
        edgeLabelMenu = new javax.swing.JMenu();
        eWeight = new javax.swing.JRadioButtonMenuItem();
        eID = new javax.swing.JRadioButtonMenuItem();
        eBC = new javax.swing.JRadioButtonMenuItem();
        eNone = new javax.swing.JRadioButtonMenuItem();
        layoutMenu = new javax.swing.JMenu();
        kk = new javax.swing.JRadioButtonMenuItem();
        fr = new javax.swing.JRadioButtonMenuItem();
        isom = new javax.swing.JRadioButtonMenuItem();
        spring = new javax.swing.JRadioButtonMenuItem();
        circleL = new javax.swing.JRadioButtonMenuItem();
        menuSimulation = new javax.swing.JMenu();
        simPauseMenuItem = new javax.swing.JMenuItem();
        doStepItem = new javax.swing.JMenuItem();
        simulationMenuSeparator = new javax.swing.JSeparator();
        infectNodesButton = new javax.swing.JMenuItem();
        healAllNodesButton = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        helpHowTo = new javax.swing.JMenuItem();
        helpAbout = new javax.swing.JMenuItem();
        showSidebarButton = new javax.swing.JButton();
        hideSidebarButton = new javax.swing.JButton();
        simulationControlsPanel = new javax.swing.JPanel();

        MouseListener sidebarButtonsListener = new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
                toggleRightSidebar();
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        };

        showSidebarButton.setText("Show sidebar (F1)");
        hideSidebarButton.setText("Hide sidebar (F1)");
        showSidebarButton.addMouseListener(sidebarButtonsListener);
        hideSidebarButton.addMouseListener(sidebarButtonsListener);

        showSidebarButton.setVisible(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Free Roam Mode");
        setMinimumSize(new java.awt.Dimension(1024, 768));
        setPreferredSize(new java.awt.Dimension(1024, 768));

        mouseModeToolbar.setBorder(javax.swing.BorderFactory.createTitledBorder("Mouse mode"));
        mouseModeToolbar.setFloatable(false);

        mouseModeButtonGroup.add(select);
        select.setSelected(true);
        select.setText("Select");
        select.setFocusable(false);
        select.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        select.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        select.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectActionPerformed(evt);
            }
        });
        mouseModeToolbar.add(select);

        mouseModeButtonGroup.add(edit);
        edit.setText("Edit");
        edit.setFocusable(false);
        edit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        edit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });
        mouseModeToolbar.add(edit);

        mouseModeButtonGroup.add(transform);
        transform.setText("Transform");
        transform.setFocusable(false);
        transform.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transform.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transform.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transformActionPerformed(evt);
            }
        });
        mouseModeToolbar.add(transform);

        mouseModeButtonGroup.add(annotate);
        annotate.setText("Annotate");
        annotate.setFocusable(false);
        annotate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        annotate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        annotate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annotateActionPerformed(evt);
            }
        });
        mouseModeToolbar.add(annotate);

        pane.setBorder(javax.swing.BorderFactory.createTitledBorder("Graph"));
        pane.setMinimumSize(new java.awt.Dimension(600, 100));
        pane.setName("pane"); // NOI18N

        javax.swing.GroupLayout paneLayout = new javax.swing.GroupLayout(pane);
        pane.setLayout(paneLayout);
        paneLayout.setHorizontalGroup(
                paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                          .addGap(0, 0, Short.MAX_VALUE)
                                     );
        paneLayout.setVerticalGroup(
                paneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                          .addGap(0, 0, Short.MAX_VALUE)
                                   );

        diseaseControlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Disease parameters"));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(diseaseControlsPanel);
        diseaseControlsPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(simParamsPanel))
                                        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel1Layout.createSequentialGroup()
                                                    .addComponent(simParamsPanel)));

        statsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("- Disease statistics (click to collapse)"));
        statsPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
                if (Utils.isPanelBorderClicked(graphStatsPanel, e)) {
                    Dimension panelDimension = new Dimension(403, 24);
                    String title = "+ Disease statistics (click to expand)";

                    if (!diseaseStatsPanelCollapsed) {
                        diseaseStatsPanelCollapsed = true;
                    } else {
                        diseaseStatsPanelCollapsed = false;
                        panelDimension = new Dimension(403, 160);
                        title = "- Disease statistics (click to collapse)";
                    }

                    statsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(title));
                    statsPanel.setPreferredSize(panelDimension);
                    statsPanel.revalidate();
                }
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        javax.swing.GroupLayout statsPanelLayout = new javax.swing.GroupLayout(statsPanel);
        statsPanel.setLayout(statsPanelLayout);
        statsPanelLayout.setHorizontalGroup(
                statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGap(0, 0, Short.MAX_VALUE)
                                           );
        statsPanelLayout.setVerticalGroup(
                statsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGap(0, 194, Short.MAX_VALUE)
                                         );

        nodeStatisticsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected node statistics"));
        nodeStatisticsPanel.setMinimumSize(new Dimension(290, 0));

        localCC.setText("0.0");

        localCCLabel.setText("CC");

        localAPL.setText("0.0");

        localAPLLabel.setText("APL");

        bcLabel.setText("BC");

        localBC.setText("0.0");

        inDegreeLabel.setText("In degree");

        in.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        in.setText("0.0");

        outDegreeLabel.setText("Out degree");

        out.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        out.setText("0.0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(nodeStatisticsPanel);
        nodeStatisticsPanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addGap(5, 5, 5)
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                           .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                  .addComponent(outDegreeLabel)
                                                                                                  .addGap(14, 14, 14)
                                                                                                  .addComponent(out))
                                                                           .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                  .addComponent(inDegreeLabel)
                                                                                                  .addGap(24, 24, 24)
                                                                                                  .addComponent(in)))
                                                    .addGap(28, 28, 28)
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                           .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                  .addComponent(localCCLabel)
                                                                                                  .addGap(12, 12, 12)
                                                                                                  .addComponent(localCC))
                                                                           .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                  .addComponent(bcLabel)
                                                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                  .addComponent(localBC)))
                                                    .addGap(28, 28, 28)
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                           .addGroup(jPanel2Layout.createSequentialGroup()
                                                                                                  .addComponent(localAPLLabel)
                                                                                                  .addGap(14, 14, 14)
                                                                                                  .addComponent(localAPL)))
                                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                           .addComponent(outDegreeLabel)
                                                                           .addComponent(bcLabel)
                                                                           .addComponent(out)
                                                                           .addComponent(localBC)
                                                                           .addComponent(localAPLLabel)
                                                                           .addComponent(localAPL))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                           .addComponent(inDegreeLabel)
                                                                           .addComponent(localCCLabel)
                                                                           .addComponent(in)
                                                                           .addComponent(localCC)))
                                      );

        graphStatsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("- Graph statistics (click to collapse)"));
        graphStatsPanel.setMaximumSize(new java.awt.Dimension(382, 224));
        graphStatsPanel.setPreferredSize(new java.awt.Dimension(382, 224));
        graphStatsPanel.revalidate();
        graphStatsPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                if (Utils.isPanelBorderClicked(graphStatsPanel, e)) {
                    Dimension panelDimension = new Dimension(382, 24);
                    String title = "+ Graph statistics (click to expand)";

                    if (!graphStatsPanelCollapsed) {
                        graphStatsPanelCollapsed = true;
                    } else {
                        graphStatsPanelCollapsed = false;
                        panelDimension = new Dimension(382, 224);
                        title = "- Graph statistics (click to collapse)";
                    }

                    graphStatsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(title));
                    graphStatsPanel.setPreferredSize(panelDimension);
                    graphStatsPanel.revalidate();
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        avgDegreeLabel.setText("Avg degree ");

        aplLabel.setText("APL ");

        degreeCorrLabel.setText("Degree corr. ");

        ccLabel.setText("CC ");

        globalCC.setText("0");

        globalAPL.setText("0");

        globalAvgDegree.setText("0");

        globalDegreeCorrelation.setText("0");

        vertexCountLabel.setText("Vertex count ");

        globalVertexCount.setText("0");

        globalEdgeCount.setText("0");

        edgeCountLabel.setText("Edge count ");

        degreeDistPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Degree distribution"));
        degreeDistPanel.setMaximumSize(new java.awt.Dimension(349, 117));
        degreeDistPanel.setMinimumSize(new java.awt.Dimension(349, 117));
        degreeDistPanel.setSize(new java.awt.Dimension(349, 0));

        javax.swing.GroupLayout degreeDistPanelLayout = new javax.swing.GroupLayout(degreeDistPanel);
        degreeDistPanel.setLayout(degreeDistPanelLayout);
        degreeDistPanelLayout.setHorizontalGroup(
                degreeDistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                     .addGap(0, 337, Short.MAX_VALUE)
                                                );
        degreeDistPanelLayout.setVerticalGroup(
                degreeDistPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                     .addGap(0, 93, Short.MAX_VALUE)
                                              );

        degDistLogScale.setText("Log scale");
        degDistLogScale.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                degDistLogScaleItemStateChanged(evt);
            }
        });

        degDistCumulative.setText("Cumulative");
        degDistCumulative.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                degDistCumulativeItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout graphStatsPanelLayout = new javax.swing.GroupLayout(graphStatsPanel);
        graphStatsPanel.setLayout(graphStatsPanelLayout);
        graphStatsPanelLayout.setHorizontalGroup(
                graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                     .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                    .addGap(5, 5, 5)
                                                                    .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                   .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                  .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                 .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                                                                                .addComponent(ccLabel)
                                                                                                                                                                                                .addGap(56, 56, 56)
                                                                                                                                                                                                .addComponent(globalCC, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                                                                                                 .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                                                                                .addComponent(avgDegreeLabel)
                                                                                                                                                                                                .addGap(8, 8, 8)
                                                                                                                                                                                                .addComponent(globalAvgDegree)))
                                                                                                                                  .addGap(20, 20, 20)
                                                                                                                                  .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                                                                                 .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                                                                                .addComponent(degreeCorrLabel)
                                                                                                                                                                                                .addGap(8, 8, 8)
                                                                                                                                                                                                .addComponent(globalDegreeCorrelation))
                                                                                                                                                                 .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                                                                                .addComponent(aplLabel)
                                                                                                                                                                                                .addGap(56, 56, 56)
                                                                                                                                                                                                .addComponent(globalAPL)))
                                                                                                                                  .addGap(20, 20, 20)
                                                                                                                                  .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                                                                                                                                               .addComponent(vertexCountLabel)
                                                                                                                                                                                                                                                               .addGap(6, 6, 6)
                                                                                                                                                                                                                                                               .addComponent(globalVertexCount))
                                                                                                                                                                 .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                                                                                .addComponent(edgeCountLabel)
                                                                                                                                                                                                .addGap(16, 16, 16)
                                                                                                                                                                                                .addComponent(globalEdgeCount)))
                                                                                                                                  .addGap(5, 5, 5))
                                                                                                   .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                  .addComponent(degDistLogScale)
                                                                                                                                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                                                  .addComponent(degDistCumulative, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                  .addContainerGap())
                                                                                                   .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                                                                                  .addComponent(degreeDistPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                                                  .addGap(0, 0, Short.MAX_VALUE))))
                                                );
        graphStatsPanelLayout.setVerticalGroup(
                graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                     .addGroup(graphStatsPanelLayout.createSequentialGroup()
                                                                    .addGap(5, 5, 5)
                                                                    .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                   .addComponent(ccLabel)
                                                                                                   .addComponent(aplLabel)
                                                                                                   .addComponent(globalCC)
                                                                                                   .addComponent(globalAPL)
                                                                                                   .addComponent(vertexCountLabel)
                                                                                                   .addComponent(globalVertexCount))
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                                                   .addComponent(avgDegreeLabel)
                                                                                                   .addComponent(degreeCorrLabel)
                                                                                                   .addComponent(globalDegreeCorrelation)
                                                                                                   .addComponent(globalAvgDegree).addComponent(edgeCountLabel)
                                                                                                   .addComponent(globalEdgeCount))
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addComponent(degreeDistPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                    .addGroup(graphStatsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                   .addComponent(degDistCumulative)
                                                                                                   .addComponent(degDistLogScale))
                                                                    .addGap(5, 5, 5))
                                              );

        simControlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Simulation"));

        pauseSimToolbarButton.setMnemonic('R');
        pauseSimToolbarButton.setText("Resume");
        pauseSimToolbarButton.setFocusable(false);
        pauseSimToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pauseSimToolbarButton.setSize(new java.awt.Dimension(93, 29));
        pauseSimToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pauseSimToolbarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseSimToolbarButtonActionPerformed(evt);
            }
        });

        doStepToolbarButton.setMnemonic('S');
        doStepToolbarButton.setText("Do step");
        doStepToolbarButton.setFocusable(false);
        doStepToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        doStepToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        doStepToolbarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doStepToolbarButtonActionPerformed(evt);
            }
        });

        infectButton.setMnemonic('I');
        infectButton.setText("Infect...");
        infectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        healButton.setMnemonic('H');
        healButton.setText("Heal all");
        healButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout simControlsPanelLayout = new javax.swing.GroupLayout(simControlsPanel);
        simControlsPanel.setLayout(simControlsPanelLayout);
        simControlsPanelLayout.setHorizontalGroup(
                simControlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                      .addGroup(simControlsPanelLayout.createSequentialGroup()
                                                                      .addGap(5, 5, 5)
                                                                      .addGroup(simControlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                      .addComponent(healButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                      .addComponent(infectButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                      .addComponent(doStepToolbarButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                      .addComponent(pauseSimToolbarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                      .addGap(5, 5, 5))
                                                 );
        simControlsPanelLayout.setVerticalGroup(
                simControlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                      .addGroup(simControlsPanelLayout.createSequentialGroup()
                                                                      .addComponent(pauseSimToolbarButton)
                                                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(doStepToolbarButton)
                                                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(infectButton)
                                                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                      .addComponent(healButton))
                                               );

        stepTimePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Time between steps, ms"));

        speedSlider.setMajorTickSpacing(500);
        speedSlider.setMaximum(2000);
        speedSlider.setPaintLabels(true);
        speedSlider.setPaintTicks(true);
        speedSlider.setValue(500);
        speedSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                speedSliderStateChanged(evt);
            }
        });
        speedSlider.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                speedSliderKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(stepTimePanel);
        stepTimePanel.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                                                                                .addContainerGap()
                                                                                                .addComponent(speedSlider, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                .addContainerGap())
                                        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                             .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addComponent(speedSlider, 48, 48, 48)
                                      )
                                      );

        simulationControlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("- Controls (click to collapse)"));
        javax.swing.GroupLayout simulationControlsPanelLayout = new javax.swing.GroupLayout(simulationControlsPanel);
        simulationControlsPanel.setLayout(simulationControlsPanelLayout);
        simulationControlsPanelLayout.setHorizontalGroup(simulationControlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                      .addGroup(simulationControlsPanelLayout.createSequentialGroup()
                                                                                                                             .addComponent(simControlsPanel, 128, 128, 128)
                                                                                                                             .addGap(5, 5, 5)
                                                                                                                             .addComponent(diseaseControlsPanel, 240, 240, 240))
                                                                                      .addComponent(stepTimePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        simulationControlsPanelLayout.setVerticalGroup(simulationControlsPanelLayout.createSequentialGroup()
                                                                                    .addGap(5, 5, 5)
                                                                                    .addGroup(simulationControlsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                                                           .addComponent(diseaseControlsPanel, 156, 156, 156)
                                                                                                                           .addComponent(simControlsPanel, 156, 156, 156))
                                                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                    .addComponent(stepTimePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                      );

        simulationControlsPanel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
                if (Utils.isPanelBorderClicked(simulationControlsPanel, e)) {
                    Dimension panelDimension = new Dimension(382, 24);
                    String title = "+ Controls (click to expand)";

                    if (!simulationControlsPanelCollapsed) {
                        simulationControlsPanelCollapsed = true;
                    } else {
                        simulationControlsPanelCollapsed = false;
                        panelDimension = new Dimension(382, 264);
                        title = "- Controls (click to collapse)";
                    }

                    simulationControlsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(title));
                    simulationControlsPanel.setPreferredSize(panelDimension);
                    simulationControlsPanel.revalidate();
                }
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        menuFile.setMnemonic('F');
        menuFile.setText("File");

        newDoc.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N,
                                                                 Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        newDoc.setMnemonic('N');
        newDoc.setText("New graph");
        newDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDocActionPerformed(evt);
            }
        });
        menuFile.add(newDoc);

        fileSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S,
                                                                   Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileSave.setMnemonic('S');
        fileSave.setText("Save...");
        fileSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileSaveActionPerformed(evt);
            }
        });
        menuFile.add(fileSave);

        fileLoad.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O,
                                                                   Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileLoad.setMnemonic('L');
        fileLoad.setText("Load...");
        fileLoad.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileLoadActionPerformed(evt);
            }
        });
        menuFile.add(fileLoad);

        fileGenerate1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G,
                                                                        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileGenerate1.setMnemonic('G');
        fileGenerate1.setText("Generate...");
        fileGenerate1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileGenerate1ActionPerformed(evt);
            }
        });
        menuFile.add(fileGenerate1);

        fileQuit1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q,
                                                                    Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        fileQuit1.setMnemonic('Q');
        fileQuit1.setText("Quit");
        fileQuit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileQuit1ActionPerformed(evt);
            }
        });
        menuFile.add(fileQuit1);

        topMenuBar.add(menuFile);

        viewMenu.setText("View");

        dumpToJpg.setText("Save to .jpg");
        dumpToJpg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dumpToJpgActionPerformed(evt);
            }
        });
        toggleSidebar.setText("Hide sidebar");
        toggleSidebar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        toggleSidebar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleRightSidebar();
            }
        });

        viewMenu.add(toggleSidebar);
        viewMenu.add(dumpToJpg);

        topMenuBar.add(viewMenu);

        vertexLabelMenu.setText("Label vertices with...");

        vertexLabel.add(vDEgree);
        vDEgree.setMnemonic('A');
        vDEgree.setText("Degree");
        vDEgree.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vDEgreeActionPerformed(evt);
            }
        });
        vertexLabelMenu.add(vDEgree);

        vertexLabel.add(vCC);
        vCC.setMnemonic('B');
        vCC.setText("Clustering coefficient");
        vCC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vCCActionPerformed(evt);
            }
        });
        vertexLabelMenu.add(vCC);

        vertexLabel.add(vBC);
        vBC.setMnemonic('C');
        vBC.setText("Betweennesss centrality");
        vBC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vBCActionPerformed(evt);
            }
        });
        vertexLabelMenu.add(vBC);

        vertexLabel.add(vDist);
        vDist.setMnemonic('D');
        vDist.setText("Distance from selected vertex");
        vDist.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vDistActionPerformed(evt);
            }
        });
        vertexLabelMenu.add(vDist);

        vertexLabel.add(vID);
        vID.setMnemonic('E');
        vID.setSelected(true);
        vID.setText("ID");
        vID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vIDActionPerformed(evt);
            }
        });
        vertexLabelMenu.add(vID);

        vertexLabel.add(vNone);
        vNone.setMnemonic('F');
        vNone.setText("Nothing");
        vNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vNoneActionPerformed(evt);
            }
        });
        vertexLabelMenu.add(vNone);

        topMenuBar.add(vertexLabelMenu);

        edgeLabelMenu.setText("Label edges with...");

        edgeLabel.add(eWeight);
        eWeight.setMnemonic('H');
        eWeight.setText("Weight");
        eWeight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eWeightActionPerformed(evt);
            }
        });
        edgeLabelMenu.add(eWeight);

        edgeLabel.add(eID);
        eID.setMnemonic('I');
        eID.setText("ID");
        eID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eIDActionPerformed(evt);
            }
        });
        edgeLabelMenu.add(eID);

        edgeLabel.add(eBC);
        eBC.setMnemonic('J');
        eBC.setText("Centrality");
        eBC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eBCActionPerformed(evt);
            }
        });
        edgeLabelMenu.add(eBC);

        edgeLabel.add(eNone);
        eNone.setMnemonic('K');
        eNone.setSelected(true);
        eNone.setText("Nothing");
        eNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eNoneActionPerformed(evt);
            }
        });
        edgeLabelMenu.add(eNone);

        topMenuBar.add(edgeLabelMenu);

        layoutMenu.setText("Change layout");
        layoutMenu.setToolTipText("Change the way vertices are positioned");

        layouts.add(kk);
        kk.setMnemonic('0');
        kk.setSelected(true);
        kk.setText("KKLayout");
        kk.setToolTipText("Kamada-Kawai algorithm");
        kk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        layoutMenu.add(kk);

        layouts.add(fr);
        fr.setMnemonic('1');
        fr.setText("FRLayout");
        fr.setToolTipText("Fruchterman- Reingold algorithm");
        fr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        layoutMenu.add(fr);

        layouts.add(isom);
        isom.setMnemonic('2');
        isom.setText("ISOMLayout");
        isom.setToolTipText("Self-organizing map algorithm");
        isom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        layoutMenu.add(isom);

        layouts.add(spring);
        spring.setMnemonic('3');
        spring.setText("SpringLayout");
        spring.setToolTipText("A simple force-based algorithm");
        spring.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        layoutMenu.add(spring);

        layouts.add(circleL);
        circleL.setMnemonic('4');
        circleL.setText("CircleLayout");
        circleL.setToolTipText("Arranges vertices in a circle");
        circleL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLayout();
            }
        });
        layoutMenu.add(circleL);

        topMenuBar.add(layoutMenu);

        menuSimulation.setText("Simulation");

        simPauseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, 0));
        simPauseMenuItem.setText("Pause/ Resume");
        simPauseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simPauseMenuItemActionPerformed(evt);
            }
        });
        menuSimulation.add(simPauseMenuItem);

        doStepItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        doStepItem.setText("Do step");
        doStepItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuSimulation.add(doStepItem);
        menuSimulation.add(simulationMenuSeparator);

        infectNodesButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, 0));
        infectNodesButton.setText("Infect nodes...");
        infectNodesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infectButtonActionPerformed(evt);
            }
        });
        menuSimulation.add(infectNodesButton);

        healAllNodesButton.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, 0));
        healAllNodesButton.setText("Heal all nodes");
        healAllNodesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                healAllButtonActionPerformed(evt);
            }
        });
        menuSimulation.add(healAllNodesButton);

        topMenuBar.add(menuSimulation);

        menuHelp.setText("Help");

        helpHowTo.setText("How to");
        helpHowTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpHowToActionPerformed(evt);
            }
        });
        menuHelp.add(helpHowTo);

        helpAbout.setText("About");
        helpAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpAboutActionPerformed(evt);
            }
        });
        menuHelp.add(helpAbout);

        topMenuBar.add(menuHelp);

        setJMenuBar(topMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        SequentialGroup rightSidebarHorizontalGroup = layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                                    .addComponent(simulationControlsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                    .addComponent(statsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                    .addComponent(graphStatsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                                                    .addComponent(hideSidebarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addGap(5, 5, 5);
        ParallelGroup layoutHorizontalGroup = layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(layout.createSequentialGroup()
                                                                    .addGap(5, 5, 5)
                                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                    .addGroup(layout.createSequentialGroup()
                                                                                                    .addComponent(mouseModeToolbar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                    .addGap(5, 5, 5)
                                                                                                    .addComponent(nodeStatisticsPanel, 290, 290, Short.MAX_VALUE)
                                                                                                    .addGap(5, 5, 5)
                                                                                                    .addComponent(showSidebarButton, 403, 403, 403)
                                                                                             ).addComponent(pane, 600, 600, Short.MAX_VALUE))
                                                                    .addGroup(rightSidebarHorizontalGroup));
        layout.setHorizontalGroup(layoutHorizontalGroup);

        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                      .addGroup(layout.createSequentialGroup()
                                      .addContainerGap()
                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                      .addGroup(layout.createSequentialGroup()
                                                                      .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                                      .addComponent(mouseModeToolbar, 60, 60, 60)
                                                                                      .addComponent(nodeStatisticsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                      .addGroup(layout.createSequentialGroup()
                                                                                                      .addComponent(hideSidebarButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                      .addGap(5, 5, 5)
                                                                                                      .addComponent(simulationControlsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                      .addGroup(layout.createSequentialGroup()
                                                                                                      .addComponent(showSidebarButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(statsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addComponent(graphStatsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                      .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                               ).addGroup(layout.createSequentialGroup()
                                                                                .addGap(64, 64, 64)
                                                                                .addComponent(pane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                      .addContainerGap()
                               )
                               );
    }

    private void toggleRightSidebar() {
        rightSidebarVisible = !rightSidebarVisible;
        if (!rightSidebarVisible) {
            toggleSidebar.setText("Show sidebar");
        } else {
            toggleSidebar.setText("Hide sidebar");
        }

        simulationControlsPanel.setVisible(rightSidebarVisible);
        statsPanel.setVisible(rightSidebarVisible);
        graphStatsPanel.setVisible(rightSidebarVisible);
        showSidebarButton.setVisible(!rightSidebarVisible);
        hideSidebarButton.setVisible(rightSidebarVisible);
    }

    public void loadLayout(String path) throws IOException, ClassNotFoundException {
        persistentLayout.restore(path);
    }

    private void dumpToJpgActionPerformed(java.awt.event.ActionEvent evt) {
        FileDialog window = new FileDialog(this, "Save", FileDialog.SAVE);
        window.setSize(500, 500);
        window.setVisible(true);
        String path = window.getDirectory() + window.getFile();
        if (!path.equals("nullnull")) {//if the user clicks CANCEL path will be set to "nullnull"
            if (!path.endsWith(".jpg")) {
                path += ".jpg";
            }
            File f = new File(path);
            IOClass.writeJPEGImage(vv, f);
        }

    }

    private void infectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        InfectDisp d = new InfectDisp(g, controller);
    }

    private void healAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (g != null) {
            controller.setAllSusceptible();
            // fire appropriate event to trigger an update of the Infected graph
            g.fireExtraEvent(new ExtraGraphEvent(g, ExtraGraphEvent.SIM_STEP_COMPLETE));
            vv.repaint();
        } else {
            JOptionPane.showMessageDialog(this, "That makes no sense when the graph is empty!");
        }
    }

    private void helpHowToActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, Strings.help);
    }

    private void helpAboutActionPerformed(java.awt.event.ActionEvent evt) {
        JOptionPane.showMessageDialog(this, Strings.about);
    }

    private void newDocActionPerformed(java.awt.event.ActionEvent evt) {
        this.getVV().getPickedVertexState().clear();
        controller.generateEmptyGraph();
    }

    private void fileSaveActionPerformed(java.awt.event.ActionEvent evt) {
        gatherer.showSave(this, g, this.persistentLayout);
    }

    private void fileLoadActionPerformed(java.awt.event.ActionEvent evt) {
        gatherer.showLoad(this);
    }

    private void fileGenerate1ActionPerformed(java.awt.event.ActionEvent evt) {
        gatherer.showGenerate(this);
    }

    private void fileQuit1ActionPerformed(java.awt.event.ActionEvent evt) {
        System.exit(0);
    }

    private void vDEgreeActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void vCCActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void vBCActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void vDistActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void vIDActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void vNoneActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void eWeightActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void eIDActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void eBCActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void eNoneActionPerformed(java.awt.event.ActionEvent evt) {
        repaint();
    }

    private void pauseSimToolbarButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (checkEmptyPopupError()) {
            String currText = pauseSimToolbarButton.getText().toLowerCase();
            if (currText.trim().equals("pause")) {
                pauseSimToolbarButton.setText("Resume");
                controller.getSimulator().pauseSim();
            } else {
                pauseSimToolbarButton.setText("Pause   ");
                controller.getSimulator().resumeSim();
            }
        }
    }

    public boolean checkEmptyPopupError() {
        if (g.getVertexCount() < 1) {
            JOptionPane.showMessageDialog(this, "That makes no sense when the graph is empty!");
            return false;
        } else {
            return true;
        }
    }

    private void doStepToolbarButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (checkEmptyPopupError()) {
            checkEmptyPopupError();
            controller.getSimulator().resumeSimForOneStep();
        }
    }

    private void simPauseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        pauseSimToolbarButton.doClick();
    }

    private void speedSliderStateChanged(javax.swing.event.ChangeEvent evt) {
        JSlider source = (JSlider) evt.getSource();
        if (!source.getValueIsAdjusting()) {
            simParamsPanel.parseSimulationParameters();
        }
    }

    private void speedSliderKeyPressed(java.awt.event.KeyEvent evt) {
        simParamsPanel.parseSimulationParameters();
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (checkEmptyPopupError()) {
            checkEmptyPopupError();
            doStepToolbarButton.doClick();
        }
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if (checkEmptyPopupError()) {
            checkEmptyPopupError();
            infectNodesButton.doClick();
        }
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {
        if (checkEmptyPopupError()) {
            checkEmptyPopupError();
            healAllNodesButton.doClick();
        }
    }

    private void selectActionPerformed(java.awt.event.ActionEvent evt) {
        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
        annotationControlsToolbar.setVisible(annotate.isSelected());
        nodeStatisticsPanel.setVisible(true);
        mouseModeToolbar.setPreferredSize(new Dimension(240, 60));
    }

    private void editActionPerformed(java.awt.event.ActionEvent evt) {
        graphMouse.setMode(ModalGraphMouse.Mode.EDITING);
        annotationControlsToolbar.setVisible(annotate.isSelected());
        nodeStatisticsPanel.setVisible(true);
        mouseModeToolbar.setPreferredSize(new Dimension(240, 60));
    }

    private void transformActionPerformed(java.awt.event.ActionEvent evt) {
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);
        annotationControlsToolbar.setVisible(annotate.isSelected());
        nodeStatisticsPanel.setVisible(true);
        mouseModeToolbar.setPreferredSize(new Dimension(240, 60));
    }

    private void annotateActionPerformed(java.awt.event.ActionEvent evt) {
        graphMouse.setMode(ModalGraphMouse.Mode.ANNOTATING);
        annotationControlsToolbar.setVisible(annotate.isSelected());
        nodeStatisticsPanel.setVisible(false);
        mouseModeToolbar.setPreferredSize(new Dimension(594, 60));
    }

    private void degDistLogScaleItemStateChanged(java.awt.event.ItemEvent evt) {
        updateDegreeDistributionChart();
    }

    private void degDistCumulativeItemStateChanged(java.awt.event.ItemEvent evt) {
        updateDegreeDistributionChart();
    }

    public JPanel getStatsPanel() {
        return statsPanel;
    }


    /**
     * Initialises the display
     */
    private void redisplayCompletely() {
        //clear all previous content
        pane.removeAll();
        pane.setLayout(new BorderLayout());

        persistentLayout = new PersistentLayoutImpl<MyVertex, MyEdge>(getSelectedGraphLayout(g));

        vv = new VisualizationViewer<MyVertex, MyEdge>(persistentLayout, pane.getSize());
        initDemoMap();

        icons = new IconsStore(vv.getPickedVertexState());
        defaultRenderer = vv.getRenderer().getVertexRenderer();
        defaultVertexIconShapeTransformer = vv.getRenderContext().getVertexShapeTransformer();
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.black));
        vv.getRenderContext().setArrowDrawPaintTransformer(new ConstantTransformer(Color.black));
        vv.getRenderContext().setVertexLabelTransformer(new CustomVertexLabeler(this.stats));
        vv.getRenderContext().setEdgeLabelTransformer(new CustomEdgeLabeller(this.stats));
        vv.getRenderContext().setVertexLabelRenderer(new DefaultVertexLabelRenderer(Color.black));
        vv.getRenderer().getVertexLabelRenderer().setPositioner(new CenterLabelPositioner());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

        //#########  MOUSE  PLUGINS  ###############
        graphMouse = new CustomGraphMouse(this, this.controller, this.stats, vv.getRenderContext(),
                                          controller.getVertexFactory(), controller.getEdgeFactory());
        graphMouse.loadPlugins();
        vv.setGraphMouse(graphMouse);

        annotationControls = new AnnotationControls<MyVertex, MyEdge>(
                graphMouse.getAnnotatingPlugin());
        annotationControlsToolbar = annotationControls.getAnnotationsToolBar();
        annotationControlsToolbar.setFocusable(false);
        annotationControlsToolbar.setFocusTraversalKeysEnabled(false);
        for (int i = 0; i < annotationControlsToolbar.getComponents().length; i++) {
            Component component = annotationControlsToolbar.getComponents()[i];
            component.setFocusTraversalKeysEnabled(false);
            component.setFocusable(false);
        }

        for (int i = 0; i < mouseModeToolbar.getComponents().length; i++) {
            Component comp = mouseModeToolbar.getComponents()[i];
            if (comp instanceof JToolBar) {
                mouseModeToolbar.remove(comp);
            }

        }

        setVertexRenderer();

        mouseModeToolbar.add(annotationControlsToolbar, BorderLayout.SOUTH);
        annotationControlsToolbar.setVisible(annotate.isSelected());
        annotationControlsToolbar.setFloatable(false);

        pane.add(vv, BorderLayout.CENTER);
        pane.setVisible(true);
        redisplayPartially();
        simParamsPanel.parseSimulationParameters();
        //initially display nothing
        initDegreeDistributionChart();
        updateStatsDisplay();

        mouseModeButtonGroup.clearSelection();
        select.doClick();

        controller.getSimulator().createInfectedCountGraph(this.getStatsPanel());
        controller.getSimulator().resetSimulation();
    }

    public void setVertexRenderer() {
        Transformer vertexShapeTransformer = new EllipseVertexShapeTransformer();
        Transformer vertexIconShapeTransformer = defaultVertexIconShapeTransformer;
        Renderer.Vertex vertexRenderer = defaultRenderer;

        if (g.areNodeIconsAllowed()) {
            vertexShapeTransformer = icons;
            vertexIconShapeTransformer = new CustomVertexIconShapeTransformer(new EllipseVertexShapeTransformer(), icons);
        } else {
            vertexRenderer = new CustomVertexRenderer(vv.getPickedVertexState(), false);
        }

        vv.getRenderer().setVertexRenderer(vertexRenderer);
        vv.getRenderContext().setVertexShapeTransformer(vertexIconShapeTransformer);
        vv.getRenderContext().setVertexIconTransformer(vertexShapeTransformer);
        vv.repaint();
    }

    private void initDemoMap() {
        BackgroundImageController.getInstance().setGraphBackgroundImage(vv, "maps/UK_Map.png",
                                                                        2, 2);
        scaler.scale(vv, .5f, vv.getCenter());

        Color backgroundColor = new Color(10, 20, 20);
        Color edgeColor = Color.WHITE;
        g.setBackgroundColor(backgroundColor.getRGB());
        g.setEdgeColor(edgeColor.getRGB());
        vv.setBackground(backgroundColor);
        vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(edgeColor));
    }

    private static void redisplayPartially() {
        pane.validate();
        vv.repaint();
        pane.repaint();
    }

    /**
     * Returns an appropriate layout based on the state of the layout selection
     * buttons
     *
     * @return
     */
    private Layout<MyVertex, MyEdge> getSelectedGraphLayout(Graph g) {
        //ascii code of 0 is 48, 1 is 49, etc, and the menus have been assigned mnemonics from 0-5
        int type = layouts.getSelection().getMnemonic() - 48;
        Layout<MyVertex, MyEdge> l = null;
        switch (type) {
            case 0: {
                l = getControlledKKLayout(g);
                break;
            }
            case 1: {
                l = new FRLayout<MyVertex, MyEdge>(g);
                break;
            }
            case 2: {
                l = new ISOMLayout<MyVertex, MyEdge>(g);
                break;
            }
            case 3: {
                l = new SpringLayout<MyVertex, MyEdge>(g);
                ((SpringLayout) l).setRepulsionRange(10000);
                break;
            }
            case 4: {
                l = new CircleLayout<MyVertex, MyEdge>(g);
                break;
            }
            case 5: {
                l = new BalloonLayout<MyVertex, MyEdge>((Forest<MyVertex, MyEdge>) g);
                break;
            }
            default: {
                l = getControlledKKLayout(g);
            }
        }
        return l;
    }

    // This method exists as a workaround for the 'unsettling graph' issue. It does two things:
    // 1. Limits the maximum number of iterations the Kamada-Kawai vertex positioning algorithm will make
    // before it settles down, based on the number of vertices in our generated graph.
    // By default, the maximum number of iterations done in the step() method is 2000. Even with smaller graphs,
    // sometimes the resulting distance between vertices does not satisfy the algorithm and it continues to displace 
    // them until the iteration limit is reached.
    // 2. Disables the local minimum escape technique used by the positioning algorhithm.
    // This technique promotes a more aggresive approach to the vertex displacement, which in larger graphs
    // could actually result in longer layout adjustment times.
    // 
    private Layout getControlledKKLayout(Graph g) {
        KKLayout kkLayout = new KKLayout<MyVertex, MyEdge>(g);
        kkLayout.setMaxIterations(2 * g.getVertexCount());
        kkLayout.setExchangeVertices(false);
        return kkLayout;
    }

    /**
     * Based on AnimatingAddNodeDemo, should animate layout change
     */
    public void changeLayout() {
        try {
            Layout oldLayout = vv.getGraphLayout();
            PersistentLayoutImpl newLayout = new PersistentLayoutImpl<MyVertex, MyEdge>(getSelectedGraphLayout(g));
            this.persistentLayout = newLayout;
            newLayout.setSize(oldLayout.getSize());
            oldLayout.initialize();
            Relaxer relaxer = new VisRunner((IterativeContext) oldLayout);
            relaxer.stop();
            relaxer.prerelax();
            LayoutTransition<MyVertex, MyEdge> lt = new LayoutTransition<MyVertex, MyEdge>(vv, oldLayout, newLayout);
            Animator animator = new Animator(lt);
            animator.start();
            vv.repaint();
        } catch (Exception ex) {
            System.out.println("Error while changing layout: " + ex.getMessage());

        }

    }

    /**
     * returns the index of the selectedvertex labeling option in the menu
     *
     * @return
     */
    public static int getSelectedVertexLabelingOption() {
        //the return type will begin with one of these options
        //Degree, Clustering, Centrality, Label
        //mnemonics here are set to A,B,C,D, so subtract 65 to get the selected index
        return vertexLabel.getSelection().getMnemonic() - 65;
    }

    public static int getSelectedEdgeLabelingOption() {
        //first one is H, so subtract 72
        return edgeLabel.getSelection().getMnemonic() - 72;
    }

    @Override
    public void handleGraphEvent(GraphEvent<MyVertex, MyEdge> evt) {
        if (this.handlingEvents) {
            //invoked when a vertex/edge is added/deleted
            updateStatsDisplay();
        }
    }

    @Override
    public void handleExtraGraphEvent(ExtraGraphEvent<MyVertex, MyEdge> evt) {
        if (this.handlingEvents) {
            if (evt.type == ExtraGraphEvent.SIM_STEP_COMPLETE) {
                controller.getSimulator().updateChartUnderlyingData();
                controller.getSimulator().updateChartAxisParameters();
                redisplayPartially();
            }
            if (evt.type == ExtraGraphEvent.STATS_CHANGED) {
                updateStatsDisplay();
            }
            if (evt.type == ExtraGraphEvent.GRAPH_REPLACED) {
                vv.getGraphLayout().setGraph(this.g);
                changeLayout();
                controller.getSimulator().resetSimulation();
            }
        }
    }

    public void updateSimulationParameters(SimulationDynamics dynamics){
        g.setSleepTimeBetweenSteps(speedSlider.getValue() + 20);
        //make sure the graphs is in a proper state
        controller.validateNodeStates();
        this.g.setDynamics(dynamics);
    }

    public Controller getController() {
        return controller;
    }
}
