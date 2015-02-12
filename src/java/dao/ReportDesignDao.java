/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.SQLException;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class ReportDesignDao {

    public JSONObject getLinkReportConfig(int reportID, String linkColumn) throws SQLException {
        JSONObject json = new JSONObject();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("ReportID", reportID);
        params.put("LinkColumn", linkColumn);
        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getReportHeadLineChart", params);
        HashMap<String, Object> results = SQLHelper.executeCommand(command, new MapRowHandler());
        JSONObject object = new JSONObject();
        if (results == null || results.size() == 0) {
            object.put("ReportID", reportID);
            object.put("LinkColumn", linkColumn);
            object.put("TopRows", 30);
        } else {
            object.putAll(results);
        }
        json.put("NameLink", object);

        command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT * FROM Qa_ReportLinkChart WHERE ReportID = :ReportID AND LinkColumn = :LinkColumn", params);
        results = SQLHelper.executeCommand(command, new MapRowHandler());
        object = new JSONObject();
        if (results == null || results.size() == 0) {
            object.put("ReportID", reportID);
            object.put("LinkColumn", linkColumn);
            object.put("SubReportID", null);
            object.put("SubReportName", null);
        } else {
            object.putAll(results);
        }
        json.put("ValueLink", object);
        command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getReportLinkChartParams", params);
        JSONArray queryParams = SQLHelper.executeCommand(command, new JSONResultHandler());
        json.put("ParamsMapping", queryParams);
        return json;
    }

}
