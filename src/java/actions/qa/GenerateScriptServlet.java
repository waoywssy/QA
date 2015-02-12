/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import dao.GenerateScriptDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.LogHelper;

/**
 *
 * @author wei.yin
 */
public class GenerateScriptServlet extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String server = request.getParameter("server");
            String database = request.getParameter("database");
            String table = request.getParameter("table");
            String command = request.getParameter("command");
            String node = request.getParameter("node");
            GenerateScriptDao dao = new GenerateScriptDao();
            if ("CREATE".equals(command) || "Table Scripts".equals(command)) {
                out.print(dao.getTableScript(server, database, table));
            } else if ("SELECT TOP".equals(command)) {
                out.print(dao.selectTop(server, database, table));
            } else if ("DROP".equals(command)) {
                out.print("-- DROP TABLE " + table);
            } else if ("INSERT".equals(command)) {
                out.print(dao.insert(server, database, table));
            } else if ("UPDATE".equals(command)) {
                out.print(dao.update(server, database, table));
            } else if ("DELETE".equals(command)) {
                out.print("DELETE FROM " + table + " WHERE ");
            } else if ("TRUNCATE".equals(command)) {
                out.print("-- TRUNCATE TABLE " + table);
            } else if ("INSERT PROC".equals(command)) {
                out.print(dao.insertPROC(server, database, table));
            } else if ("Sync Column".equals(command)) {
                dao.syncColumn(server, database, table, node);
                out.write("success");
            } else if ("Sync Table".equals(command)) {
                dao.syncTable(server, database, table, node, true);
                out.write("success");
            } else if ("Sync SP".equals(command)) {
                dao.syncTable(server, database, table, node, false);
                out.write("success");
            } else if (command.contains("SP Script")) {
                out.write(dao.getSpScript(server, database, table));
            }
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
            out.write(ex.getMessage());
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
