package view;

import edu.uci.ics.jung.visualization.picking.PickedState;
import model.MyVertex;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * A transformer that chooses an icon for each vertex from a predefined set
 * based on the metadata of the vertex (epidemic state and whether the vertex is
 * clicked on) Created by miroslavbatchkarov on 11/03/2014.
 */
public class IconsStore implements Transformer<MyVertex, Icon> {

    //There should be one key for each node type (as defined in the MyVertex class).
    //The value holds the icons for each node state (corresponding to the values in the EpiState enum
    //+ one icon for when a vertex is selected in the editor).
    private final HashMap<Integer, Image[]> simpleIconsMap = new HashMap<Integer, Image[]>();
    private final HashMap<Integer, Image[]> photorealisticIconsMap = new HashMap<Integer, Image[]>();

    private final static int VERTEX_ICON_SELECTED = 0x0;
    private final static int VERTEX_ICON_INFECTED = 0x1;
    private final static int VERTEX_ICON_SUSCEPTIBLE = 0x2;
    private final static int VERTEX_ICON_IMMUNE = 0x3;
    private PickedState pickedState;
    private Logger errorLogger;

    public IconsStore(PickedState pickedState) {
        this.pickedState = pickedState;
        errorLogger = Logger.getLogger(getClass().getName());
        loadIconMaps();
    }

    private void loadIconMaps() {
        loadSimpleIconsMap();
        loadPhotorealisticIconsMap();
    }

    private void loadSimpleIconsMap() {
        loadSimpleUserIcons();
        loadSimpleMobileIcons();
        loadSimpleComputerIcons();
        loadSimpleAccessPointIcons();
    }

    private void loadSimpleUserIcons() {
        Image[] userIcons = new Image[4];
        userIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_person_default.png");
        userIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_person_infected.png");
        userIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_person_susceptible.png");
        userIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_person_immune.png");
        simpleIconsMap.put(MyVertex.NODE_TYPE_USER, userIcons);
    }

    private void loadSimpleMobileIcons() {
        Image[] mobileIcons = new Image[4];
        mobileIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_iphone_default.png");
        mobileIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_iphone_infected.png");
        mobileIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_iphone_susceptible.png");
        mobileIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_iphone_immune.png");
        simpleIconsMap.put(MyVertex.NODE_TYPE_MOBILE, mobileIcons);
    }

    private void loadSimpleComputerIcons() {
        Image[] computerIcons = new Image[4];
        computerIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_notebook_default.png");
        computerIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_notebook_infected.png");
        computerIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_notebook_susceptible.png");
        computerIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_notebook_immune.png");
        simpleIconsMap.put(MyVertex.NODE_TYPE_COMPUTER, computerIcons);
    }

    private void loadSimpleAccessPointIcons() {
        Image[] accessPointIcons = new Image[4];
        accessPointIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_router_default.png");
        accessPointIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_router_infected.png");
        accessPointIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_router_susceptible.png");
        accessPointIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/simple/ic_router_immune.png");
        simpleIconsMap.put(MyVertex.NODE_TYPE_ACCESS_POINT, accessPointIcons);
    }

    private void loadPhotorealisticIconsMap() {
        loadPhotorealisticUserIcons();
        loadPhotorealisticMobileIcons();
        loadPhotorealisticComputerIcons();
        loadPhotorealisticAccessPointIcons();
    }

    private void loadPhotorealisticUserIcons() {
        Image[] userIcons = new Image[4];
        userIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_user.png");
        userIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_user_infected.png");
        userIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_user_susceptible.png");
        userIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_user_immune.png");
        photorealisticIconsMap.put(MyVertex.NODE_TYPE_USER, userIcons);
    }

    private void loadPhotorealisticMobileIcons() {
        Image[] mobileIcons = new Image[4];
        mobileIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_mobile.png");
        mobileIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_mobile_infected.png");
        mobileIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_mobile_susceptible.png");
        mobileIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_mobile_immune.png");
        photorealisticIconsMap.put(MyVertex.NODE_TYPE_MOBILE, mobileIcons);
    }

    private void loadPhotorealisticComputerIcons() {
        Image[] computerIcons = new Image[4];
        computerIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_computer.png");
        computerIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_computer_infected.png");
        computerIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_computer_susceptible.png");
        computerIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_computer_immune.png");
        photorealisticIconsMap.put(MyVertex.NODE_TYPE_COMPUTER, computerIcons);
    }

    private void loadPhotorealisticAccessPointIcons() {
        Image[] accessPointIcons = new Image[4];
        accessPointIcons[VERTEX_ICON_SELECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_access_point.png");
        accessPointIcons[VERTEX_ICON_INFECTED] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_access_point_infected.png");
        accessPointIcons[VERTEX_ICON_SUSCEPTIBLE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_access_point_susceptible.png");
        accessPointIcons[VERTEX_ICON_IMMUNE] = Utils.loadImageFromResources(errorLogger, "icons/photorealistic/ic_access_point_immune.png");
        photorealisticIconsMap.put(MyVertex.NODE_TYPE_ACCESS_POINT, accessPointIcons);
    }

    /**
     * Returns the <code>Icon</code> associated with <code>v</code>.
     */
    @Override
    public Icon transform(MyVertex v) {
        HashMap<Integer, Image[]> iconsPack = photorealisticIconsMap;
        if (v.getVertexIconStyle() == MyVertex.VERTEX_ICON_STYLE_SIMPLE){
            iconsPack = simpleIconsMap;
        }

        Image image = null;
        if (pickedState != null && pickedState.isPicked(v)) {
            image = iconsPack.get(v.getNodeType())[VERTEX_ICON_SELECTED];
        } else {
            if (v.isSusceptible()) {
                image = iconsPack.get(v.getNodeType())[VERTEX_ICON_SUSCEPTIBLE];
            } else if (v.isInfected()) {
                image = iconsPack.get(v.getNodeType())[VERTEX_ICON_INFECTED];
            } else if (v.isResistant()) {
                image = iconsPack.get(v.getNodeType())[VERTEX_ICON_IMMUNE];
            }
        }
        return new ImageIcon(image);
    }
}
