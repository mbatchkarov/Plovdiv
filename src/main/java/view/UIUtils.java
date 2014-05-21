package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by miroslavbatchkarov on 20/05/2014.
 */
public class UIUtils {

    /**
     * Parses the text of the provided text field. If that is not a valid
     * double, the text is highlighted to grab the user's attention
     *
     * @return
     */
    public static double parseDoubleOrColourComponentOnError(JTextField textField) {
        double value = Double.NaN;
        try {
            value = Double.parseDouble(textField.getText());
            textField.setForeground(Color.black);
        } catch (NumberFormatException ex) {
            textField.setForeground(Color.red);
        }
        return value;
    }

}
