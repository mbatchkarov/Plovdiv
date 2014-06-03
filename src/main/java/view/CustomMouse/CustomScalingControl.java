/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.CustomMouse;

import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import java.awt.geom.Point2D;

/**
 *
 * @author User
 */
public class CustomScalingControl extends CrossoverScalingControl {

    public final static float DEFAULT_ZOOM_LEVEL = .5f;
    public final static float MIN_ZOOM_LEVEL = .05f;
    public final static float MAX_ZOOM_LEVEL = 3f;

    private final static int ZOOM_MODE_WINDOWS = 0x1;
    private final static int ZOOM_MODE_MACOS = 0x2;

    private int zoomMode = ZOOM_MODE_MACOS;

    private float currentZoom = 1;

    @Override
    public void scale(VisualizationServer<?, ?> vv, float amount, Point2D at) {
        if (zoomMode == ZOOM_MODE_WINDOWS) {
            if (amount < 1) {
                amount = 1.11f;
            } else {
                amount = 0.9090909f;
            }
        }
        boolean canZoomOut = (amount < 1) && ((currentZoom * amount) > MIN_ZOOM_LEVEL);
        boolean canZoomIn = (amount > 1) && ((currentZoom * amount) < MAX_ZOOM_LEVEL);
        if (canZoomOut || canZoomIn) {
            currentZoom *= amount;
            super.scale(vv, amount, at);
        }
    }

    public void resetStoredZoomLevel() {
       currentZoom = 1;
    }

    public void toggleZoomMode() {
        if (zoomMode == ZOOM_MODE_MACOS){
            zoomMode = ZOOM_MODE_WINDOWS;
        } else {
            zoomMode = ZOOM_MODE_MACOS;
        }
    }   
}
