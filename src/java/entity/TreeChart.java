/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.SQLException;
import java.util.HashMap;
import org.json.simple.JSONObject;
import util.database.SQLHelper;
import util.database.hanlders.impl.JsonTreeResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public class TreeChart extends Chart {

    public String CatID;
    public String CatName;
    public String CatParentID;
    public String CatLevel;
    public String LastFound;
    public int LostPoint;

    public TreeChart(int reportID) {
        super(reportID);
        initChart();
    }

    @Override
    protected void initChart() {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("ReportID", this.reportID);

            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getTreeReportConfig", params);
            HashMap<String, Object> treeConfig = SQLHelper.executeCommand(command, new MapRowHandler());
            this.LostPoint = (Integer) treeConfig.get("LostPoint");
            this.CatID = (String) treeConfig.get("CatID");
            this.CatLevel = (String) treeConfig.get("CatLevel");
            this.CatName = (String) treeConfig.get("CatName");
            this.CatParentID = (String) treeConfig.get("CatParentID");
            this.LastFound = (String) treeConfig.get("LastFound");

        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected JSONObject generateReport(HashMap<String, Object> params) throws SQLException {
        JSONObject jroot = getConfig();
        JsonTreeResultHandler handler = new JsonTreeResultHandler(this.LostPoint, this.CatID, this.CatName, this.CatParentID, this.CatLevel, this.LastFound);
        SqlCommand command = new SqlCommand(this.server, this.database, CommandType.Text, this.sql, params);
        JSONObject resultObject = SQLHelper.executeCommand(command, handler);
        jroot.put("StoreData", resultObject);
        return jroot;
    }

    @Override
    public JSONObject getConfig() {
        JSONObject config = new JSONObject();
        config.put("reportName", this.reportName);
        config.put("reportType", this.reportType);
        config.put("CatName", this.CatName);
        config.put("CatParentID", this.CatParentID);
        config.put("CatLevel", this.CatLevel);
        config.put("CatID", this.CatID);
        config.put("LastFound", this.LastFound);
        config.put("LostPoint", this.LostPoint);
        config.put("LostPoint", this.LostPoint);
        config.put("ReportID", this.reportID);
        return config;
    }

    @Override
    public JSONObject updateConfig(JSONObject config) throws SQLException {
        ChartFactory.removeKey(reportID);
        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateReport_Tree", config);
        SQLHelper.executeCommand(command, null);
        JSONObject jsono = new JSONObject();
        jsono.put("success", true);
        jsono.put("msg", "success");
        return jsono;
    }
}
