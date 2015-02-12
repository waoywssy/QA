/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package actions.qa;

import dao.ImportDataDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import util.LogHelper;
import util.NumericHelper;

/**
 *
 * @author Administrator
 */
public class ImportDataServlet extends HttpServlet {

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
            if ("importJobInfo".equals(method)) {
                // 导入job信息
                int jobId = NumericHelper.toInt(request.getParameter("JobID"));
                String server = request.getParameter("server");
                new ImportDataDao().importJobInfo(server, jobId);
                out.print("{success:true,msg:'success'}");
            } else if ("importdata".equals(method)) {
                // 导入数据信息
                String fromServer = request.getParameter("fromServer");
                String toServer = request.getParameter("toServer");
                String toTable = request.getParameter("toTable");
                String sql = request.getParameter("queryText");
                new ImportDataDao().importData(fromServer, toServer, toTable, sql);
                out.print("{success:true,msg:'success'}");
            }
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
            out.print("{success:false,msg:\"" + ex.getMessage() + "\"}");
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
