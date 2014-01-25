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

package view.CustomMouse;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.ItemSelectable;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.plaf.basic.BasicIconFactory;

import controller.Stats;
import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.visualization.MultiLayerTransformer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.annotations.AnnotatingGraphMousePlugin;
import edu.uci.ics.jung.visualization.control.*;
import view.Display;


/**
 * Copied EditingModalGraphMouse and replaced the popupEditingPlugin type from
 * EditingPopupGraphMousePlugin to my own customized customPopupPlugin
 * @author mb724
 */
public class CustomGraphMouse extends AbstractModalGraphMouse
	implements ModalGraphMouse, ItemSelectable {

    protected Display d;
    private Stats stats;
    protected Factory vertexFactory;
	protected Factory edgeFactory;
	protected CustomEditingPlugin editingPlugin;
	protected LabelEditingGraphMousePlugin labelEditingPlugin;
	protected CustomPopupPlugin popupEditingPlugin;
	protected AnnotatingGraphMousePlugin annotatingPlugin;
	protected MultiLayerTransformer basicTransformer;
	protected RenderContext rc;
        
//        protected FreeRoam fr;//the window in which this mouse operates
        //used to update the contents of this window on mouse events

	/**
	 * create an instance with default values
	 *
	 */
	public CustomGraphMouse(Display d, Stats stats, RenderContext rc,
			Factory vertexFactory, Factory edgeFactory) {
		this(rc, vertexFactory, edgeFactory, 1.1f, 1/1.1f);
        this.d = d;
        this.stats = stats;
    }

	/**
	 * create an instance with passed values
	 * @param in override value for scale in
	 * @param out override value for scale out
	 */
	public CustomGraphMouse(RenderContext rc,
			Factory vertexFactory, Factory edgeFactory, float in, float out) {
		super(in,out);
		this.vertexFactory = vertexFactory;
		this.edgeFactory = edgeFactory;
		this.rc = rc;
		this.basicTransformer = rc.getMultiLayerTransformer();
		loadPlugins();
		setModeKeyListener(new ModeKeyAdapter(this));
	}

	/**
	 * create the plugins, and load the plugins for TRANSFORMING mode
	 *
	 */
	@Override
    protected void loadPlugins() {
		pickingPlugin = new CustomPickingPlugin(this.stats, this.d);
		animatedPickingPlugin = new AnimatedPickingGraphMousePlugin();
		translatingPlugin = new TranslatingGraphMousePlugin(InputEvent.BUTTON1_MASK);
		scalingPlugin = new ScalingGraphMousePlugin(new CrossoverScalingControl(), 0, in, out);
		rotatingPlugin = new RotatingGraphMousePlugin();
		shearingPlugin = new ShearingGraphMousePlugin();
		editingPlugin = new CustomEditingPlugin(vertexFactory, edgeFactory);
		labelEditingPlugin = new LabelEditingGraphMousePlugin();
		annotatingPlugin = new AnnotatingGraphMousePlugin(rc);
		popupEditingPlugin = new CustomPopupPlugin(this.d);
		add(scalingPlugin);
		setMode(Mode.EDITING);
	}

	/**
	 * setter for the Mode.
	 */
	@Override
    public void setMode(Mode mode) {
		if(this.mode != mode) {
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED,
					this.mode, ItemEvent.DESELECTED));
			this.mode = mode;
			if(mode == Mode.TRANSFORMING) {
				setTransformingMode();
			} else if(mode == Mode.PICKING) {
				setPickingMode();
			} else if(mode == Mode.EDITING) {
				setEditingMode();
			} else if(mode == Mode.ANNOTATING) {
				setAnnotatingMode();
			}
			if(modeBox != null) {
				modeBox.setSelectedItem(mode);
			}
			fireItemStateChanged(new ItemEvent(this, ItemEvent.ITEM_STATE_CHANGED, mode, ItemEvent.SELECTED));
		}
	}

	@Override
    protected void setPickingMode() {
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(editingPlugin);
		remove(annotatingPlugin);
		add(pickingPlugin);
		add(animatedPickingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	@Override
    protected void setTransformingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(editingPlugin);
		remove(annotatingPlugin);
		add(translatingPlugin);
		add(rotatingPlugin);
		add(shearingPlugin);
		add(labelEditingPlugin);
		add(popupEditingPlugin);
	}

	protected void setEditingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(annotatingPlugin);
		add(editingPlugin);
		add(popupEditingPlugin);
	}

	protected void setAnnotatingMode() {
		remove(pickingPlugin);
		remove(animatedPickingPlugin);
		remove(translatingPlugin);
		remove(rotatingPlugin);
		remove(shearingPlugin);
		remove(labelEditingPlugin);
		remove(editingPlugin);
		remove(popupEditingPlugin);
		add(annotatingPlugin);
	}


	/**
	 * @return the modeBox.
	 */
	@Override
    public JComboBox getModeComboBox() {
		if(modeBox == null) {
			modeBox = new JComboBox(new Mode[]{Mode.TRANSFORMING, Mode.PICKING, Mode.EDITING, Mode.ANNOTATING});
			modeBox.addItemListener(getModeListener());
		}
		modeBox.setSelectedItem(mode);
		return modeBox;
	}

	/**
	 * create (if necessary) and return a menu that will change
	 * the mode
	 * @return the menu
	 */
	@Override
    public JMenu getModeMenu() {
		if(modeMenu == null) {
			modeMenu = new JMenu();// {
			Icon icon = BasicIconFactory.getMenuArrowIcon();
			modeMenu.setIcon(BasicIconFactory.getMenuArrowIcon());
			modeMenu.setPreferredSize(new Dimension(icon.getIconWidth()+10,
					icon.getIconHeight()+10));

			final JRadioButtonMenuItem transformingButton =
				new JRadioButtonMenuItem(Mode.TRANSFORMING.toString());
			transformingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.TRANSFORMING);
					}
				}});

			final JRadioButtonMenuItem pickingButton =
				new JRadioButtonMenuItem(Mode.PICKING.toString());
			pickingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.PICKING);
					}
				}});

			final JRadioButtonMenuItem editingButton =
				new JRadioButtonMenuItem(Mode.EDITING.toString());
			editingButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						setMode(Mode.EDITING);
					}
				}});

			ButtonGroup radio = new ButtonGroup();
			radio.add(transformingButton);
			radio.add(pickingButton);
			radio.add(editingButton);
			transformingButton.setSelected(true);
			modeMenu.add(transformingButton);
			modeMenu.add(pickingButton);
			modeMenu.add(editingButton);
			modeMenu.setToolTipText("Menu for setting Mouse Mode");
			addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange() == ItemEvent.SELECTED) {
						if(e.getItem() == Mode.TRANSFORMING) {
							transformingButton.setSelected(true);
						} else if(e.getItem() == Mode.PICKING) {
							pickingButton.setSelected(true);
						} else if(e.getItem() == Mode.EDITING) {
							editingButton.setSelected(true);
						}
					}
				}});
		}
		return modeMenu;
	}

    public static class ModeKeyAdapter extends KeyAdapter {
    	private char t = 't';
    	private char p = 'p';
    	private char e = 'e';
    	private char a = 'a';
    	protected ModalGraphMouse graphMouse;

    	public ModeKeyAdapter(ModalGraphMouse graphMouse) {
			this.graphMouse = graphMouse;
		}

		public ModeKeyAdapter(char t, char p, char e, char a, ModalGraphMouse graphMouse) {
			this.t = t;
			this.p = p;
			this.e = e;
			this.a = a;
			this.graphMouse = graphMouse;
		}

		@Override
        public void keyTyped(KeyEvent event) {
			char keyChar = event.getKeyChar();
			if(keyChar == t) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				graphMouse.setMode(Mode.TRANSFORMING);
			} else if(keyChar == p) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				graphMouse.setMode(Mode.PICKING);
			} else if(keyChar == e) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.EDITING);
			} else if(keyChar == a) {
				((Component)event.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
				graphMouse.setMode(Mode.ANNOTATING);
			}
		}
    }

	/**
	 * @return the annotatingPlugin
	 */
	public AnnotatingGraphMousePlugin getAnnotatingPlugin() {
		return annotatingPlugin;
	}

	/**
	 * @return the editingPlugin
	 */
	public CustomEditingPlugin getEditingPlugin() {
		return editingPlugin;
	}

	/**
	 * @return the labelEditingPlugin
	 */
	public LabelEditingGraphMousePlugin getLabelEditingPlugin() {
		return labelEditingPlugin;
	}

	/**
	 * @return the popupEditingPlugin
	 */
	public EditingPopupGraphMousePlugin getPopupEditingPlugin() {
		return popupEditingPlugin;
	}
}

