/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.hanlders.impl.ListResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class TreeNodeServlet extends HttpServlet {

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
        String node = request.getParameter("node");
        String parentNode = request.getParameter("parentNode");
        String nodeName = request.getParameter("name");
        try {
            if ("getNode".equals(method)) {
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("parentId", Integer.parseInt(request.getParameter("node")));

                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getNodes", sqlParamMap);
                out.print(SQLHelper.executeCommand(command, new JSONResultHandler()));
            } else if ("getQaResultNode".equals(method)) {
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("parentId", Integer.parseInt(request.getParameter("node")));

                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getQaResultNode", sqlParamMap);
                out.print(SQLHelper.executeCommand(command, new JSONResultHandler()));
            } else if ("addFolder".equals(method)) {
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("node", Integer.parseInt(node));
                sqlParamMap.put("nodeName", nodeName);
                String sql = "INSERT INTO dbo.Nodes(parentId,name,nodeType,leaf,show)VALUES(:node,:nodeName,2,0,1)";
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, sqlParamMap);
                SQLHelper.executeCommand(command, null);
                out.print("success");
            } else if ("delete".equals(method)) {
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("node", Integer.parseInt(node));
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_deleteNode", sqlParamMap);
                sqlParamMap = SQLHelper.executeCommand(command, new MapRowHandler());
                out.print(sqlParamMap.get("msg"));
            } else if ("viewBirtReport".equals(method)) {
                //request.getRequestDispatcher("/frameset?__report=test.rptdesign&sample=my+parameter").forward(request, response);
                response.sendRedirect("/frameset?__report=test.rptdesign&sample=my+parameter");
            } else if ("bookMark".equals(method)) {
                int refer = Integer.parseInt(request.getParameter("refer"));
                int type = Integer.parseInt(request.getParameter("nodeType"));
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("refer", refer);
                sqlParamMap.put("nodeType", type);
                sqlParamMap.put("name", nodeName);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_addBookmark", sqlParamMap);
                SQLHelper.executeCommand(command, null);
            } else if ("updateNote".equals(method)) {
                int id = Integer.parseInt(request.getParameter("ID"));
                String noteContent = request.getParameter("NoteContent");
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("ID", id);
                sqlParamMap.put("NoteContent", noteContent);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateNote", sqlParamMap);
                SQLHelper.executeCommand(command, null);
                out.write("{success:true}");
            } else if ("getNote".equals(method)) {
                int id = Integer.parseInt(request.getParameter("ID"));
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("Id", id);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getNotes", sqlParamMap);
                List<String> list = SQLHelper.executeCommand(command, new ListResultHandler<String>(String.class));
                String noteContentString = "";
                for (String str : list) {
                    noteContentString += str + "<br/>";
                }
                JSONObject json = new JSONObject();
                json.put("ID", id);
                json.put("NoteContent", noteContentString);
                out.print(json.toJSONString());
            } else if ("showNote".equals(method)) {
                int id = Integer.parseInt(request.getParameter("ID"));
                HashMap<String, Object> sqlParamMap = new HashMap<String, Object>();
                sqlParamMap.put("Id", id);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getNotes", sqlParamMap);
                List<String> list = SQLHelper.executeCommand(command, new ListResultHandler<String>(String.class));
                String noteContentString = "";
                for (String str : list) {
                    noteContentString += str + "<br/>";
                }
                out.print(noteContentString);
            }
        } catch (Exception ex) {
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
