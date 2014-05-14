package view;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import model.VertexIcon;

/**
 * Copyright User: mmb28 Date: 20/08/2012 Time: 14:58
 */
public class Utils {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public static String round(double res) {
        try {
            return "" + Double.valueOf(decimalFormat.format(res));
        } catch (NumberFormatException ex) {
            return "N/A";
        }
    }

    public static Image loadImageFromResources(Logger logger, String imagePath) {
        Image image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemResource(imagePath));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
        }
        return image;
    }

    public static boolean isPanelBorderClicked(JPanel panel, MouseEvent e) {
        int w = panel.getWidth();
        int h = panel.getHeight();
        int x = e.getPoint().x;
        int y = e.getPoint().y;
        Insets ins = panel.getInsets();
        boolean inBorder
                = (x < ins.left
                || x > w - ins.right
                || y < ins.top
                || y > h - ins.bottom);
        return inBorder;
    }

    public static int getVertexIconTypeBasedOnDegree(int degree) {
        int vertexIconType = VertexIcon.TYPE_MOBILE;
        if (degree > 2 && degree < 4) {
            vertexIconType = VertexIcon.TYPE_COMPUTER;
        } else if (degree > 4) {
            vertexIconType = VertexIcon.TYPE_ACCESS_POINT;
        }
        return vertexIconType;
    }
}
