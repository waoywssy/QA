/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import entity.ChartFactory;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import util.NumericHelper;
import util.SQLEntity;
import util.SQLParser;
import util.database.SQLHelper;
import util.database.hanlders.impl.HTMLResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public class QueryEditorServlet extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String node = request.getParameter("node");
        String method = request.getParameter("method");
        String queryText = request.getParameter("queryText");
        String server = request.getParameter("server");
        String database = request.getParameter("database");

        try {
            if ("update".equals(method)) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("node", NumericHelper.toInt(node));
                params.put("parentNode", NumericHelper.toInt(request.getParameter("parentNode")));
                params.put("name", request.getParameter("name"));
                params.put("comments", request.getParameter("comments"));
                params.put("queryText", queryText);
                params.put("server", server);
                params.put("database", database);
                params.put("changeServer", request.getParameter("changeServer") != null);
                params.put("isReport", request.getParameter("isReport") != null);
                params.put("startRow", NumericHelper.toInt(request.getParameter("startRow")));
               
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateQuery", params);
                int nodeId = SQLHelper.executeCommand(command, new UniqueResultHandler<Integer>());
                out.print("{success:true,msg:" + nodeId + "}");
                ChartFactory.removeKey(nodeId);
            } else if ("query".equals(method)) {
                queryText = queryText.replaceAll(":RunDate", " DATEADD(DAY,-7,GETDATE())").replaceAll(":RunID", "0").replace(":Operation", ">");
                SqlCommand command = new SqlCommand(server, database, CommandType.Text, queryText, new HashMap());
                out.print(SQLHelper.executeCommand(command, new HTMLResultHandler()));
            } else if ("get".equals(method)) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("node", NumericHelper.toInt(node));
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getQuery", params);
                HashMap<String, Object> results = SQLHelper.executeCommand(command, new MapRowHandler());
                JSONObject object = new JSONObject();
                object.putAll(results);
                out.print(object.toJSONString());
            }
        } catch (Exception ex) {
            out.print("<br/>");
            out.print(ex.getMessage());
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
