/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.NumericHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class BotScheduleServlet extends HttpServlet {

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
            if ("update".equals(method)) {
                int botID = NumericHelper.toInt(request.getParameter("BotID"));
                String name = request.getParameter("BotName");
                String jobIDs = request.getParameter("JobIDs");
                String sector = request.getParameter("Sector");
                String disable = request.getParameter("Disable");
                HashMap<String, Object> param = new HashMap<String, Object>();
                param.put("BotID", botID);
                param.put("BotName", name);
                param.put("JobIDs", jobIDs);
                param.put("Sector", sector);
                param.put("Disabled", disable != null && disable.length() > 0);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateBot", param);
                SQLHelper.executeCommand(command, null);
                out.print("{'success':true,  'msg':'Success'}");
            } else if ("getSchedules".equals(method)) {
                response.setContentType("application/json; charset=utf-8");
                int id = NumericHelper.toInt(request.getParameter("nodeId"));
                String sql = null;
                if (id == -1) {
                    sql = "SELECT ID,QaStatus FROM dbo.Bots WHERE Disabled = 0";
                } else {
                    sql = "SELECT * FROM dbo.Bots WHERE  Disabled = 0 and (ID = {0} OR {1} = 0) ORDER BY Sector,BotName ASC";
                    sql = MessageFormat.format(sql, id, id);
                }

                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new HashMap<String, Object>());
                out.print(SQLHelper.executeCommand(command, new JSONResultHandler()));
            } else if ("startSchdeule".equals(method)) {
                int botID = Integer.parseInt(request.getParameter("botID"));
                String sql = "UPDATE dbo.Bots SET QaStatus = 2,DateFinished = NULL WHERE QaStatus<>1 AND ID =  " + botID;
                SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), null);
                //new QaDataDao().qaBot(botID);
            }
        } catch (Exception ex) {
            out.printf(ex.getMessage());
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
