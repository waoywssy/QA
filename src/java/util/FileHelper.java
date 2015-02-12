/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class FileHelper {

    private static String _basePath;

    public static void main(String[] args) {
        _basePath = System.getProperty("user.dir") + "\\web\\sql\\";
        System.out.println(getBasePath());
        System.out.println(fileToString(getBasePath() + "新建 文本文档.txt"));
    }

    static {
        _basePath = System.getProperty("user.dir");
        //URL url = ClassLoader.getSystemClassLoader().getResource("/.");
        //request.getSession().getServletContext().getRealPath("") 
        //System.out.println("Path---" + url.getPath());
    }

    public static String fileToString(String fileName) {
        StringBuilder sb = new StringBuilder();

        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);
            String s = br.readLine();
            while (s != null) {
                sb.append(s).append("\n");
                s = br.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                }
            }
            if (fr != null) {
                try {
                    fr.close();
                } catch (Exception e) {
                }
            }
        }
        return sb.toString();
    }

    public static void stringToFile(String path, String content) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(path);
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @return the _basePath
     */
    public static String getBasePath() {
        return _basePath;
    }

    public static String getSQLString(String fileName) {
        return fileToString(_basePath + "\\web\\sql\\" + fileName);
    }

    public static void saveFile(String fileName, byte[] bytes) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileName);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
        }
    }

    private FileHelper() {
    }
}
