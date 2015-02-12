/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author Administrator
 */
public class DateHelper {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static Timestamp parseTimestamp(String dateString) {
        try {
            return Timestamp.valueOf(dateString);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getCurrentTime(){
        return sdf.format(Calendar.getInstance().getTime());
    }
    
    private DateHelper() {
    }
}
