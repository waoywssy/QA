/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.database.SQLHelper;
import util.database.hanlders.impl.JSONResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public final class PivotChart extends Chart {

    public String xAxis;
    public String xAxisValues;
    public String yAxis;
    public String countColumn;

    public PivotChart(int reportID) {
        super(reportID);
        initChart();
    }

    @Override
    protected void initChart() {
        try {
            SqlCommand command = new SqlCommand(this.server, SQLHelper.DB_QA, CommandType.Text, "SELECT * FROM [dbo].[Qa_PivotChart] WHERE ReportID = " + String.valueOf(reportID));
            HashMap<String, Object> pivotConfig = SQLHelper.executeCommand(command, new MapRowHandler());
            this.xAxis = (String) pivotConfig.get("xAxis");
            this.xAxisValues = (String) pivotConfig.get("xAxisValues");
            this.yAxis = (String) pivotConfig.get("yAxis");
            this.countColumn = (String) pivotConfig.get("CountColumn");
        } catch (SQLException ex) {
            Logger.getLogger(PivotChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected JSONObject generateReport(HashMap<String, Object> params) throws SQLException {
        JSONObject jroot = getConfig();
        SqlCommand command = new SqlCommand(this.server, this.database, CommandType.Text, this.sql, params);
        JSONArray queryResult = SQLHelper.executeCommand(command, new JSONResultHandler());
        jroot.put("values", queryResult);
        return jroot;
    }

    @Override
    public JSONObject getConfig() {
        JSONObject config = new JSONObject();
        config.put("ReportID", reportID);
        config.put("xAxis", xAxis);
        config.put("xAxisValues", xAxisValues);
        config.put("yAxis", yAxis);
        config.put("CountColumn", countColumn);
        config.put("reportName", this.reportName);
        config.put("reportType", this.reportType);
        return config;
    }

    @Override
    public JSONObject updateConfig(JSONObject config) throws SQLException {
        ChartFactory.removeKey(reportID);
        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateReportPivotChart", config);
        SQLHelper.executeCommand(command, null);
        JSONObject jsono = new JSONObject();
        jsono.put("success", true);
        jsono.put("msg", "success");
        return jsono;
    }
}
