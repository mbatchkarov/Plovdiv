package view;

import edu.uci.ics.jung.visualization.picking.PickedState;
import model.MyVertex;
import org.apache.commons.collections15.Transformer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A transformer that chooses an icon for each vertex from a predefined set based on the
 * metadata of the vertex (epidemic state and whether the vertex is clicked on)
 * Created by miroslavbatchkarov on 11/03/2014.
 */
public class IconsStore implements Transformer<MyVertex, Icon> {
    //There should be one key for each node type (as defined in the MyVertex class).
    //The value holds the icons for each node state (corresponding to the values in the EpiState enum
    //+ one icon for when a vertex is selected in the editor).
    private final HashMap<Integer, Image[]> iconsMap = new HashMap<Integer, Image[]>();

    private final static int VERTEX_ICON_SELECTED = 0x0;
    private final static int VERTEX_ICON_INFECTED = 0x1;
    private final static int VERTEX_ICON_SUSCEPTIBLE = 0x2;
    private final static int VERTEX_ICON_IMMUNE = 0x3;
    private PickedState pickedState;

    public IconsStore(PickedState pickedState) {
        this.pickedState = pickedState;
        loadIconsMap();
    }

    private void loadIconsMap() {
        loadUserIcons();
        loadMobileIcons();
        loadComputerIcons();
        loadAccessPointIcons();
    }

    private void loadUserIcons() {
        Image[] userIcons = new Image[4];
        userIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_person_default.png");
        userIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_person_infected.png");
        userIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_person_susceptible.png");
        userIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_person_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_USER, userIcons);
    }

    private void loadMobileIcons() {
        Image[] mobileIcons = new Image[4];
        mobileIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_iphone_default.png");
        mobileIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_iphone_infected.png");
        mobileIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_iphone_susceptible.png");
        mobileIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_iphone_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_MOBILE, mobileIcons);
    }

    private void loadComputerIcons() {
        Image[] computerIcons = new Image[4];
        computerIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_notebook_default.png");
        computerIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_notebook_infected.png");
        computerIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_notebook_susceptible.png");
        computerIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_notebook_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_COMPUTER, computerIcons);
    }

    private void loadAccessPointIcons() {
        Image[] accessPointIcons = new Image[4];
        accessPointIcons[VERTEX_ICON_SELECTED] = loadImageFromResources("icons/ic_router_default.png");
        accessPointIcons[VERTEX_ICON_INFECTED] = loadImageFromResources("icons/ic_router_infected.png");
        accessPointIcons[VERTEX_ICON_SUSCEPTIBLE] = loadImageFromResources("icons/ic_router_susceptible.png");
        accessPointIcons[VERTEX_ICON_IMMUNE] = loadImageFromResources("icons/ic_router_immune.png");
        iconsMap.put(MyVertex.NODE_TYPE_ACCESS_POINT, accessPointIcons);
    }

    private Image loadImageFromResources(String imagePath) {
        Image image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemResource(imagePath));
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, ex.toString(), ex);
        }
        return image;
    }

    /**
     * Returns the <code>Icon</code> associated with <code>v</code>.
     */
    @Override
    public Icon transform(MyVertex v) {
        Image image = null;
        if (pickedState != null && pickedState.isPicked(v)) {
            image = iconsMap.get(v.getNodeType())[VERTEX_ICON_SELECTED];
        } else {
            if (v.isSusceptible()) {
                image = iconsMap.get(v.getNodeType())[VERTEX_ICON_SUSCEPTIBLE];
            } else if (v.isInfected()) {
                image = iconsMap.get(v.getNodeType())[VERTEX_ICON_INFECTED];
            } else if (v.isResistant()) {
                image = iconsMap.get(v.getNodeType())[VERTEX_ICON_IMMUNE];
            }
        }
        return new ImageIcon(image);
    }
}
