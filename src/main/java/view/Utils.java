package view;

import java.text.DecimalFormat;

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
}
