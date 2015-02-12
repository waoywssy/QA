/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.SQLEntity;
import util.SQLParser;
import util.StringHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.HTMLResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class DrillDownServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String method = request.getParameter("method");
        String sqltext = null;
        try {
            if ("drilldown".equalsIgnoreCase(method)) {
                String params = request.getParameter("params");
                HashMap<String, Object> jSONObject = (HashMap<String, Object>) StringHelper.stringToJson(params);
                int queryID = Integer.parseInt(jSONObject.get("ReferQueryID").toString());
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT * FROM dbo.Qa_Queries WHERE ID=" + queryID);
                HashMap<String, Object> result = SQLHelper.executeCommand(command, new MapRowHandler());
                sqltext = (String) result.get("QueryText");
                SQLEntity entity = SQLParser.parse(sqltext);
                sqltext = entity.getDrillDownSQL(500, true, jSONObject);

                out.write(sqltext);
                command = new SqlCommand((String) result.get("Server"), (String) result.get("Database"), CommandType.Text, sqltext);
                out.write(SQLHelper.executeCommand(command, new HTMLResultHandler()));
            }
        } catch (Exception ex) {
            out.write(ex.getMessage());
            out.write("<br/>");
            out.write(sqltext);
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
