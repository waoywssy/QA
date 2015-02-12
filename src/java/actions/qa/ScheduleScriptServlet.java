/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.LogHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class ScheduleScriptServlet extends HttpServlet {

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
        try {
            if ("get".equals(method)) {
                String botID = request.getParameter("BotID");
                String sql = "SELECT * FROM dbo.ScheduledScripts WHERE BotID= " + botID;
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql);
                out.write(SQLHelper.executeCommand(command, new JSONResultHandler()).toJSONString());
            } else if ("update".equals(method) || "delete".equals(method)) {
                JSONObject json = (JSONObject) new JSONParser().parse(request.getReader());
                if ("delete".equals(method)) {
                    SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, "DELETE FROM dbo.ScheduledScripts WHERE ID = " + json.get("ID").toString());
                } else {
                    SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateScheduledScripts", json);
                    int id = SQLHelper.executeCommand(command, new UniqueResultHandler<Integer>());
                    out.write("{ID:" + id + "}");
                }
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
