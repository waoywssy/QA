/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class LogHelper {
    
    private static final Logger logger = Logger.getAnonymousLogger();//.getLogger(LogHelper.class.getName());
    private static StringBuffer message = new StringBuffer();
    
    public static void logInfo(String info) {
        logInfo(Level.INFO, info, null, null);
    }
    
    public static void logInfo(Exception ex) {
        logInfo(null, ex);
    }
    
    public static void logInfo(String subject, String detail) {
        logInfo(Level.INFO, subject, detail, null);
    }
    
    public static void logInfo(String subject, Exception exception) {
        logInfo(Level.INFO, subject, null, exception);
    }
    
    public static void logInfo(Level logLevel, String subject, String detail, Exception exception) {
        String logMessage = null;
        try {
            if (exception == null) {
                if (detail == null) {
                    logMessage = subject;
                } else {
                    logMessage = "Subject:" + subject + " Detail:" + detail;
                }
            } else if (exception instanceof SQLException) {
                SQLException se = (SQLException) exception;
                logMessage = "Subject: " + subject + " Error Code: " + se.getErrorCode()
                        + " Error State: " + se.getSQLState() + " Error Message: " + se.getMessage().replaceAll("(?<=.{100}).*", "");
            } else {
                String message = exception.getMessage();
                if (message != null) {
                    message = message.length() > 100 ? message.substring(0, 99) : message;
                } else {
                    message = exception.toString();
                }
                logMessage = subject + "  " + message;
            }
            logger.log(logLevel, logMessage);
            
            message.append(DateHelper.getCurrentTime()).append("<br/>").append(logMessage).append("<br/>");
            if (message.length() > 2000) {
                message.delete(0, 500);
            }
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Execption in Log Message:" + ex);
            message.append("Exception in logmessage:").append(ex).append("<br/>");
        }
    }
    
    public static String getLogMessage(int start) {
        return message.toString();
    }
    
    private LogHelper() {
    }
}
