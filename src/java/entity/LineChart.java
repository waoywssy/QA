/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
public final class LineChart extends Chart {

    public String LinkColumn;
    public String Category;
    public String LineColumns;

    public LineChart(int reportID) {
        super(reportID);
        initChart();
    }

    public LineChart(int reportID, String lineColumns) {
        super(reportID);

        //show recent data for head chart
        Pattern p = Pattern.compile("order\\s+by\\s+(?<Category>\\w+)", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(this.sql);
        if (m.find()) {
            this.Category = m.group("Category");
        }
        this.LineColumns = lineColumns;
        this.sql = SQLHelper.selectTopSQL(this.sql, 30, this.Category + "," + this.LineColumns);
        this.reportType = "linechart";
    }

    @Override
    protected void initChart() {
        try {
            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT * FROM dbo.Qa_LineChart WHERE ReportID=" + this.reportID);
            HashMap<String, Object> chartConfig = SQLHelper.executeCommand(command, new MapRowHandler());
            if (chartConfig.size() > 0) {
                this.Category = (String) chartConfig.get("Category");
                this.LineColumns = (String) chartConfig.get("LineColumns");
                this.LinkColumn = (String) chartConfig.get("LinkColumn");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected JSONObject generateReport(HashMap<String, Object> params) throws SQLException {
        SqlCommand command = new SqlCommand(this.server, this.database, CommandType.Text, this.sql, params);
        JSONObject jroot = getConfig();
        JSONArray resultArray = SQLHelper.executeCommand(command, new JSONResultHandler());
        jroot.put("StoreData", resultArray);
        return jroot;
    }

    @Override
    public JSONObject getConfig() {
        JSONObject config = new JSONObject();
        config.put("ReportID", this.reportID);
        config.put("Category", this.Category);
        JSONArray lineColumnArray = new JSONArray();
        String lineColumns[] = this.LineColumns.split(",");
        for (int i = 0; i < lineColumns.length; i++) {
            lineColumnArray.add(lineColumns[i]);
        }
        config.put("LineColumns", lineColumnArray);
        config.put("reportName", this.reportName);
        config.put("reportType", this.reportType);
        return config;
    }

    @Override
    public JSONObject updateConfig(JSONObject config) throws SQLException {
        ChartFactory.removeKey(reportID);
        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateLineChart", config);
        SQLHelper.executeCommand(command, null);
        JSONObject jsono = new JSONObject();
        jsono.put("success", true);
        jsono.put("msg", "success");
        return jsono;
    }
}
