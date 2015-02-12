/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import entity.ChartFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import util.LogHelper;
import util.NumericHelper;
import util.SQLEntity;
import util.SQLParser;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.hanlders.impl.MapMetaDataHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public class StandardValueServlet extends HttpServlet {

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
            if ("getValues".equals(method)) {
                String sql = null;
                SqlCommand command = null;
                int reportId = NumericHelper.toInt(request.getParameter("node"));
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("node", reportId);
                String syncReportColumn = request.getParameter("syncReportColumn");
                if ("true".equals(syncReportColumn)) {
                    SQLEntity sQLEntity = null;
                    if (reportId < 0) {
                        sql = "SELECT TOP 0 * FROM Maj_Runs";
                        command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA_DATA, CommandType.Text, sql);
                    } else {
                        command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getQuery", params);
                        HashMap rowSet = SQLHelper.executeCommand(command, new MapRowHandler());
                        sql = (String) rowSet.get("queryText");
                        sql = sql.replaceAll(":\\w+", "null");
                        command = new SqlCommand((String) rowSet.get("server"), (String) rowSet.get("database"), CommandType.Text,
                                SQLHelper.selectTopSQL(sql, 0), params);
                        sQLEntity = SQLParser.parse(sql);

                        SqlCommand command1 = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT A.ID,A.QueryText FROM dbo.Qa_Queries AS A INNER JOIN dbo.ScheduledScripts AS B ON A.ID = B.QueryID WHERE A.Name = '" + sQLEntity.tableName + "'", new HashMap<String, Object>());
                        HashMap<String, Object> values = SQLHelper.executeCommand(command1, new MapRowHandler());
                        if (values.size() > 0) {

                            String q = (String) values.get("QueryText");
                            sQLEntity = SQLParser.parse(q);

                            HashMap<String, Object> params1 = new HashMap<String, Object>();
                            params1.put("groupClause", sQLEntity.getGroupClause());
                            params1.put("referQueryID", values.get("ID"));
                            params1.put("ID", reportId);
                            params1.put("TotalItemColumn", sQLEntity.getTotalColumn());
                            command1 = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "UPDATE dbo.Qa_Queries SET referQueryID=:referQueryID,groupClause=:groupClause,TotalItemColumn=:TotalItemColumn WHERE ID = :ID", params1);
                            SQLHelper.executeCommand(command1, null);
                        } else {
                            sQLEntity = null;
                        }
                    }

                    LinkedHashMap<String, String> map = SQLHelper.executeCommand(command, new MapMetaDataHandler());

                    int i = 0;

                    String cols = "";
                    for (String key : map.keySet()) {
                        sql = MessageFormat.format("EXEC sp_updateReportColumn @ReportID={0},@ColOrder={1},@ColumnName=''{2}'',@SourceColumns=''{3}''", String.valueOf(reportId), ++i, key,
                                sQLEntity == null ? "" : sQLEntity.getSourceColumns(key));

                        cols += ",'" + key + "'";
                        SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, sql);
                    }
                    if (cols.length() > 0) {
                        //delete the nonexist columns
                        sql = "DELETE FROM dbo.Qa_ReportColumns WHERE ColumnName NOT IN({0}) AND ReportID = " + reportId;
                        sql = MessageFormat.format(sql, cols.substring(1));
                        SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, sql);
                    }
                    ChartFactory.removeKey(reportId);
                }

                command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getStandardValues", params);
                out.write(SQLHelper.executeCommand(command, new JSONResultHandler()).toJSONString());
            } else if ("updateColumnOrder".equals(method)) {
                int reportId = NumericHelper.toInt(request.getParameter("reportId"));
                int id = NumericHelper.toInt(request.getParameter("id"));
                int end = NumericHelper.toInt(request.getParameter("end"));
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("reportId", reportId);
                params.put("id", id);
                params.put("end", end);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateReportColumnOrder", params);
                SQLHelper.executeCommand(command, null);
                ChartFactory.removeKey(reportId);
                out.write("success");
            } else if ("updateValue".equals(method)) {
                Object json = new JSONParser().parse(request.getReader());
                if (json instanceof JSONArray) {
                    JSONArray array = (JSONArray) json;
                    for (int i = 0; i < array.size(); i++) {
                        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateReportColumn", (JSONObject) array.get(i));
                        SQLHelper.executeCommand(command, null);
                    }
                } else {
                    JSONObject j = (JSONObject) json;
//                    j.put("ReportID", (Integer) j.get("ReportID"));
//                    j.put("ID", (Integer) j.get("ID"));
                    SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateReportColumn", j);
                    SQLHelper.executeCommand(command, null);
                    ChartFactory.removeKey(Integer.parseInt(j.get("ReportID").toString()));
                }
                out.write(json.toString());
            } else if ("getStandardValue".equals(method)) {
                int reportID = Integer.parseInt(request.getParameter("ReportID"));
                String columnName = request.getParameter("ColumnName");
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("ReportID", reportID);
                params.put("ColumnName", columnName);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getStandardValue", params);
                out.write(SQLHelper.executeCommand(command, new JSONResultHandler()).toJSONString());
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
