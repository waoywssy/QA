/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sun.rmi.log.LogHandler;
import util.LogHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class DownloadServlet extends HttpServlet {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/x-msdownload;charset=UTF-8");
        OutputStream out = null;

        try {
            int id = Integer.parseInt(request.getParameter("node"));
            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT Path+FileName FROM Qa_Files WHERE ID = " + id);
            String filePath = SQLHelper.executeCommand(command, new UniqueResultHandler<String>());
            String fileName = filePath.substring(filePath.lastIndexOf("\\") + 1);
            File file = new File(filePath);

//            response.setHeader("content-type", "application/octet-stream");
//            response.setHeader("Content-disposition",
//                    "attachment; filename=" + fileName);
//            response.setContentLength((int) file.length());

            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "No-cache");
            response.setDateHeader("Expires", 0);
            response.addHeader("Content-Length", file.length() + "");

            FileInputStream bis = new FileInputStream(file);
            OutputStream bos = response.getOutputStream();

            InputStream in = new FileInputStream(file);
            out = response.getOutputStream();

            int length = 0;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            out.flush();

        } catch (Exception ex) {
            LogHelper.logInfo(ex);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
