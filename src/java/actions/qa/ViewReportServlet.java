/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import entity.Chart;
import entity.ChartFactory;
import entity.LineChart;
import entity.TableChart;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.CachedRowSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.LogHelper;
import util.StringHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class ViewReportServlet extends HttpServlet {

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

        PrintWriter out = response.getWriter();
        String method = request.getParameter("method");
        response.setContentType("application/json;charset=utf-8"); //json数据
        try {
            if ("view".equals(method)) {

                int reportID = Integer.parseInt(request.getParameter("node"));
                String linkColumn = request.getParameter("linkColumn");

                if (linkColumn == null || linkColumn.trim().length() == 0) {
                    String paramsString = request.getParameter("params");
                    HashMap<String, Object> p = null;
                    if (paramsString != null && paramsString.length() > 0) {
                        p = (HashMap< String, Object>) StringHelper.stringToJson(paramsString);
                    }
                    Chart chart = ChartFactory.getChart(reportID);
                    out.write(chart.viewReport(p).toJSONString());
                } else {
                    //head link report
                    LineChart lineChart = new LineChart(reportID, linkColumn);
                    out.write(lineChart.viewReport(null).toJSONString());
                }
            } else if ("viewDefault".equals(method)) {
                int botID = Integer.parseInt(request.getParameter("node"));
                JSONArray root = new JSONArray();
                JSONObject error = new JSONObject();
                try {
                    TableChart tableChart = new TableChart(-botID); // Run表
                    root.add(tableChart.viewReport(null));
                    SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT id FROM dbo.Nodes WHERE nodeType=15 AND parentId=(SELECT id FROM dbo.Nodes WHERE nodeType=-15 AND refer = " + botID + ")");
                    CachedRowSet rowSet = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
                    while (rowSet.next()) {
                        // 添加所有的Report表
                        Chart reportInfo = ChartFactory.getChart(rowSet.getInt("id"));
                        root.add(reportInfo.viewReport(null));
                    }
                } catch (Exception sQLException) {
                    error.put("error", sQLException.getMessage());
                    root.add(error);
                }
                out.write(root.toJSONString());
            } else if ("markChecked".equals(method)) {
                String botID = request.getParameter("BotID");
                SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, "UPDATE Bots SET Checked = 1 WHERE ID = " + botID);
            } else if ("showBotServer".equals(method)) {
                String botID = request.getParameter("BotID");
                String sql = "SELECT [SERVER]+':'+[DATABASE] FROM dbo.Qa_Queries WHERE ID = (SELECT MAX(QueryID) FROM dbo.ScheduledScripts WHERE BotID = " + botID + ")";
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql);
                String serverInfo = SQLHelper.executeCommand(command, new UniqueResultHandler<String>());
                out.write(serverInfo);
            } else if ("viewSelectedColumnReport".equals(method)) {
                String data = request.getParameter("Data");
                int reportID = Integer.parseInt(data.split(",")[0]);
                TableChart report = new TableChart(reportID);
                out.write(report.getSelectedReport(data));
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
