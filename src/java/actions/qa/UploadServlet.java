package actions.qa;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import util.HttpUtility;
import util.NumericHelper;
import util.database.SQLHelper;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;


/**
 *
 * @author wei.yin
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, // 10MB 
maxFileSize = 1024 * 1024 * 10, // 10MB
maxRequestSize = 1024 * 1024 * 10)   // 10MB
public class UploadServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String _webRoot = request.getServletContext().getRealPath("/");
        // Create path components to save the file

        final Part filePart = request.getPart("file");
        final String fileName = getFileName(filePart);
        String name = request.getParameter("name");
        String parentNode = request.getParameter("parentNode");
        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();
        String path = "E:\\BotDocuments\\";
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("name", name);
            params.put("fileName", fileName);
            params.put("parentNode", NumericHelper.toInt(parentNode));
            if ("rptdesign".equals(getFileType(fileName))) {
                path = _webRoot + "reportTemplates\\"; 
                params.put("nodeType", 17);
            }else{
                 params.put("nodeType", 1);
            }
             params.put("path", path);
            File file = new File(path + fileName);
            out = new FileOutputStream(file);
            filecontent = filePart.getInputStream();

            int read = 0;
            final byte[] bytes = new byte[1024];
            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_addQaFiles", params);
            SQLHelper.executeCommand(command, null);
            writer.printf("{success:true,msg:'success'}");
        } catch (Exception fne) {
            writer.println("{success:false,'msg':'" + fne.getMessage() + "'}");
        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getFileName(final Part part) throws UnsupportedEncodingException {
        final String partHeader = part.getHeader("content-disposition");
        int start = partHeader.indexOf("filename=\"") + 10;
        int end = partHeader.indexOf("\"", start);
        String name = HttpUtility.htmlDecode(partHeader.substring(start, end));
        if (name.contains("\\")) {
            name = name.substring(name.lastIndexOf("\\") + 1);
        }
        return name;
    }

    private String getFileType(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
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
