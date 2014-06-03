/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import edu.uci.ics.jung.graph.MyGraph;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author User
 */
public class BackgroundImageController {
    
    private static BackgroundImageController instance;
    private VisualizationViewer.Paintable preRender;
    private String resourceImagePath;
    
    private BackgroundImageController() {
    }
    
    public static BackgroundImageController getInstance() {
        if (instance == null) {
            instance = new BackgroundImageController();
        }
        return instance;
    }
    
    public void setBackgroundImagePath(String backgroundImagePath) {
        resourceImagePath = backgroundImagePath;
    }
    
    public void setGraphBackgroundImage(final VisualizationViewer vv, String resourceImagePath, final double xOffset, final double yOffset) {
        ImageIcon backgroundImage = new ImageIcon(Utils.loadImageFromResources(Logger.getLogger(getClass().getName()), resourceImagePath));
        setGraphBackgroundImage(vv, backgroundImage, xOffset, yOffset);
        backgroundImage.getImage().flush();
        Runtime.getRuntime().gc();
        this.resourceImagePath = resourceImagePath;
    }
    
    public void setGraphBackgroundImage(final VisualizationViewer vv, final ImageIcon background, final double xOffset, final double yOffset) {
        
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
        
        vv.addPreRenderPaintable(preRender);
        
        background.getImage().flush();
        Runtime.getRuntime().gc();
    }
    
    public void removeBackgroundImage(VisualizationViewer vv) {
        if (preRender != null) {
            vv.removePreRenderPaintable(preRender);
            vv.repaint();
            preRender = null;
            resourceImagePath = null;
        }
    }
    
    public void saveBackgroundImage(String savePath) {
        if (resourceImagePath == null) {
            return;
        }
        
        Path destPath = Paths.get(savePath + ".background");
        try {
            if (!resourceImagePath.startsWith("maps")) {
                Path sourcePath = Paths.get(resourceImagePath);
                Files.copy(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            } else {
                InputStream backgroundFileStream = ClassLoader.getSystemResourceAsStream(resourceImagePath);
                Files.copy(backgroundFileStream, destPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException ex) {
            Logger.getLogger(BackgroundImageController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void checkForAndLoadBackgroundImage(VisualizationViewer vv, String loadPath) {        
        String fileName = FilenameUtils.removeExtension(loadPath);
        fileName += ".background";
        Path sourcePath = Paths.get(fileName);
        
        if (Files.exists(sourcePath, LinkOption.NOFOLLOW_LINKS)) {
            setGraphBackgroundImage(vv, new ImageIcon(fileName), 2, 2);
            setBackgroundImagePath(fileName);
        } else {
            removeBackgroundImage(vv);
        }        
    }
    
    public void loadCustomColors(VisualizationViewer vv, MyGraph g) {
        vv.setBackground(new Color(g.getLayoutParameters().getBackgroundColorRgb()));
        vv.getRenderContext().setEdgeDrawPaintTransformer(new ConstantTransformer(new Color(g.getLayoutParameters().getEdgeColorRgb())));
    }
}
