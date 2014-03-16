package view;

import java.awt.Image;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Copyright
 * User: mmb28
 * Date: 20/08/2012
 * Time: 14:58
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
    
    
    public static Image loadImageFromResources(Logger logger, String imagePath){
        Image image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemResource(imagePath));
        } catch (IOException ex) {
           logger.log(Level.SEVERE, ex.toString(), ex);
        }
        return image;
    }

}
