/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import dao.ReportDesignDao;
import entity.Chart;
import entity.ChartFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import util.LogHelper;
import util.NumericHelper;
import util.StringHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public class ReportDesignServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String method = request.getParameter("method");
            int reportID = NumericHelper.toInt(request.getParameter("ReportID"));
            if ("executeQuery".equals(method)) {
                String queryText = request.getParameter("queryText");
                String server = request.getParameter("server");
                String database = request.getParameter("database");
                String paramString = request.getParameter("params");
                HashMap<String, Object> params = new HashMap<String, Object>();
                JSONArray json = (JSONArray) StringHelper.stringToJson(paramString);
                for (int i = 0; i < json.size(); i++) {
                    JSONObject jSONObject = (JSONObject) json.get(i);
                    String name = jSONObject.get("Name").toString();
                    String value = jSONObject.get("DefaultValue").toString();
                    String type = jSONObject.get("DataType").toString();
                    params.put(name, StringHelper.stringToObject(value, type));
                }
                queryText = SQLHelper.selectTopSQL(queryText, 30);
                SqlCommand command = new SqlCommand(server, database, CommandType.Text, queryText, params);
                out.print(SQLHelper.executeCommand(command, new JSONResultHandler()));
            } else if ("getReportInfo".equals(method)) {
                int node = Integer.parseInt(request.getParameter("node"));
                out.write(ChartFactory.getChart(node).getQueryInfo().toJSONString());
            } else if ("updateReportType".equals(method)) {
                String type = request.getParameter("type").trim();
                int node = Integer.parseInt(request.getParameter("node"));
                SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, "EXEC sp_updateReportType @node=" + node + ",@type=" + type);
            } else if ("updateQueryParam".equals(method)) {
                JSONObject jSONObject = (JSONObject) new JSONParser().parse(request.getReader());
                String delete = request.getParameter("delete");
                if (delete != null) {
                    jSONObject.put("Delete", true);
                }
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateQueryParam", jSONObject);
                SQLHelper.executeCommand(command, null);
                out.write("{success:true,data:{Name:'" + jSONObject.get("Name").toString() + "'}}");
            } else if ("getQueryParams".equals(method)) {
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT * FROM Qa_Query_Params WHERE ReportID = " + request.getParameter("ReportID"));
                out.write(SQLHelper.executeCommand(command, new JSONResultHandler()).toJSONString());
            } else if ("updateChartConfig".equals(method)) {
                Chart chart = ChartFactory.getChart(reportID);
                BufferedReader reader = request.getReader();
                JSONObject jSONObject = (JSONObject) JSONValue.parse(reader);
                out.write(chart.updateConfig(jSONObject).toJSONString());
            } else if ("getChartConfig".equals(method)) {
                Chart chart = ChartFactory.getChart(reportID);
                out.write(chart.getConfig().toJSONString());
            } else if ("updateColumnValueLink".equals(method)) {
                int subReportID = Integer.parseInt(request.getParameter("SubReportID"));
                String linkColumn = request.getParameter("LinkColumn");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("ReportID", reportID);
                params.put("LinkColumn", linkColumn);
                params.put("SubReportID", subReportID);
                params.put("SubReportName", request.getParameter("SubReportName"));
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateReportLinkChart", params);
                int id = SQLHelper.executeCommand(command, new UniqueResultHandler<Integer>());
                String paramsMapping = request.getParameter("params");
                JSONObject jSONObject = (JSONObject) StringHelper.stringToJson(paramsMapping);
                for (Object key : jSONObject.keySet()) {
                    String sql = "INSERT INTO Qa_ReportLinkChartParams VALUES({0},''{1}'',''{2}'')";
                    sql = MessageFormat.format(sql, id, key, jSONObject.get(key));
                    SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, sql);
                }
                out.write("{success:true}");
            } else if ("getLinkReport".equals(method)) {
                String linkColumn = request.getParameter("LinkColumn");
                out.print(new ReportDesignDao().getLinkReportConfig(reportID, linkColumn));
            } else if ("deleteLinkReport".equals(method)) {
                String linkColumn = request.getParameter("LinkColumn");
                String sql = "EXEC sp_deleteLinkChart " + reportID + ",'" + linkColumn + "'";
                SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, sql);
            } else {
                out.write("Unknow:" + method);
            }
        } catch (Exception ex) {
            out.write(ex.getMessage());
            LogHelper.logInfo(ex);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
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
     * Handles the HTTP <code>POST</code> method.
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
