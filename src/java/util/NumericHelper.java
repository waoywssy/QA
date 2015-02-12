/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author Administrator
 */
public class NumericHelper {

    public static Integer toInteger(String string) {

        try {
            return Integer.parseInt(string);
        } catch (Exception ex) {
            return null;
        }
    }

    public static int toInt(String string) {
        if (string == null) {
            return 0;
        }
        try {
            return Integer.parseInt(string);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static Float toFloat(String string) {
        try {
            return Float.parseFloat(string);
        } catch (Exception ex) {
            return null;
        }
    }

    private NumericHelper() {
    }
}
