/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author User
 */
public class BackgroundImageController {

    private static BackgroundImageController instance;
    private VisualizationViewer.Paintable preRender;

    private BackgroundImageController() {
    }

    public static BackgroundImageController getInstance() {
        if (instance == null) {
            instance = new BackgroundImageController();
        }
        return instance;
    }

    public void setGraphBackgroundImage(final VisualizationViewer vv, String resourceImagePath, final double xOffset, final double yOffset, Color backgroundColor) {
        ImageIcon backgroundImage = new ImageIcon(Utils.loadImageFromResources(Logger.getLogger(getClass().getName()), resourceImagePath));
        setGraphBackgroundImage(vv, backgroundImage, xOffset, yOffset, backgroundColor);
        backgroundImage.getImage().flush();
        Runtime.getRuntime().gc();
    }

    public void setGraphBackgroundImage(final VisualizationViewer vv, final ImageIcon background, final double xOffset, final double yOffset, Color backgroundColor) {

        removeBackgroundImage(vv);

        preRender = new VisualizationViewer.Paintable() {
            public void paint(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                AffineTransform oldXform = g2d.getTransform();
                AffineTransform lat
                        = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getTransform();
                AffineTransform vat
                        = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getTransform();
                AffineTransform at = new AffineTransform();
                at.concatenate(g2d.getTransform());
                at.concatenate(vat);
                at.concatenate(lat);
                g2d.setTransform(at);
                g.drawImage(background.getImage(), (int) Math.round(-background.getIconWidth() / xOffset), (int) Math.round(-background.getIconHeight() / yOffset),
                        background.getIconWidth(), background.getIconHeight(), vv);
                g2d.setTransform(oldXform);
            }

            public boolean useTransform() {
                return false;
            }
        };

        vv.setBackground(backgroundColor);
        vv.addPreRenderPaintable(preRender);

        background.getImage().flush();
        Runtime.getRuntime().gc();
    }

    public void removeBackgroundImage(VisualizationViewer vv) {
        if (preRender != null) {
            vv.removePreRenderPaintable(preRender);
            preRender = null;
        }
    }
}
