/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import util.FileHelper;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author wei.yin
 */
public class HTMLResultHandler implements ResultHandler<String> {

    private String[] columns;

    public HTMLResultHandler() {
    }

    public HTMLResultHandler(String[] columns) {
        this.columns = columns;
    }

    @Override
    public String handle(ResultSet rs) throws SQLException {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='qaResult' ondblclick=\"Ext.fly(event.target.parentElement).toggleCls('highlight')\" style='word-break: keep-all;'>");
        if (this.columns == null || this.columns.length == 0) {
            sb.append("<tr><th>Num</th>");
            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();
            //Save the exist columns to finalColumns
            for (int i = 1; i <= columnCount; i++) {
                sb.append("<th>").append(rsm.getColumnName(i)).append("</th>");
            }
            sb.append("</tr>");

            int j = 0;
            while (rs.next()) {
                j++;
                sb.append("<tr><td>").append(j).append("</td>");
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    sb.append("<td>");
                    if (value == null) {
                        sb.append("null");
                    } else if (value instanceof byte[]) {
                        byte[] bytes = (byte[]) value;
                        String pictureName = bytes.length + ".jpg";
                        FileHelper.saveFile(System.getProperty("webapp.root") + "QaImages\\" + pictureName, bytes);
                        sb.append("<img src='").append("QaImages/" + pictureName)
                                .append("'>");
                    } else {
                        sb.append(value.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
                    }
                    sb.append("</td>");
                }
                sb.append("</tr>");
            }
        } else {
            sb.append("<tr>");
            for (int i = 0; i < this.columns.length; i++) {
                sb.append("<th>").append(this.columns[i]).append("</th>");
            }
            sb.append("</tr>");

            while (rs.next()) {
                sb.append("<tr>");
                for (int i = 0; i < this.columns.length; i++) {
                    sb.append("<td>").append(rs.getString(this.columns[i])).append("</td>");
                }
                sb.append("</tr>");
            }

        }
        sb.append("</table>");

        return sb.toString();
    }
}
