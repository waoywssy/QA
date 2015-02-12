/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.rowset.CachedRowSet;
import org.json.simple.JSONObject;
import util.SQLEntity;
import util.SQLParser;
import util.database.SQLHelper;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.hanlders.impl.HTMLResultHandler;
import util.database.hanlders.impl.QaJsonResultHandler;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public final class TableChart extends Chart {

    public LinkedHashMap<String, TableColumn> columns = new LinkedHashMap<String, TableColumn>();

    public TableChart(int reportID) {
        super(reportID);
        initChart();
    }

    @Override
    protected void initChart() {
        setReportColumns();
        setReportSubReportConfig();
    }

    @Override
    protected JSONObject generateReport(HashMap<String, Object> params) throws SQLException {
        SqlCommand command = new SqlCommand(this.server, this.database, CommandType.Text, this.sql, params);
        JSONObject jroot = SQLHelper.executeCommand(command, new QaJsonResultHandler(this));
        JSONObject config = getConfig();
        jroot.putAll(config);
        return jroot;
    }

    // <editor-fold defaultstate="collapsed" desc="Set report columns config.">
    private void setReportColumns() {
        try {
            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "EXEC sp_getStandardValues @node=" + this.reportID);
            CachedRowSet resultSet = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
            while (resultSet.next()) {
                TableColumn column = new TableColumn(resultSet.getString("ColumnName"), resultSet.getInt("ColOrder"), resultSet.getBoolean("Show"), resultSet.getObject("MinValue"), resultSet.getObject("MaxValue"), resultSet.getObject("AvgValue"), resultSet.getObject("Fluctuation"), resultSet.getString("SourceColumns"));
                this.columns.put(column.ColumnName, column);
            }
            //RunInfo Table
            if (this.reportID < 0 && (this.columns.size() == 0 || true)) {
                this.columns.put("QaDate", new TableColumn("QaDate", 0, true, null, null, null, null, null));
                this.columns.put("RunDate", new TableColumn("RunDate", 0, true, null, null, null, null, null));
                this.columns.put("RunID", new TableColumn("RunID", 0, true, null, null, null, null, null));
                this.columns.put("JobID", new TableColumn("JobID", 0, true, null, null, null, null, null));
                this.columns.put("DateFinished", new TableColumn("DateFinished", 0, true, null, null, null, null, null));

                if (!this.columns.containsKey("Interval")) {
                    this.columns.put("Interval", new TableColumn("Interval", 0, true, null, null, null, null, null));
                }
                if (!this.columns.containsKey("Success")) {
                    this.columns.put("Success", new TableColumn("Success", 0, true, null, null, null, null, null));
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="get selected columns report.">
    public String getSelectedReport(String data) throws SQLException {

        StringBuilder sb = new StringBuilder();
        String[] args = data.split(",");
        if (args.length == 0) {
            return "";
        }

        String[] columns = Arrays.copyOfRange(args, 1, args.length);
        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT QueryText FROM dbo.Qa_Queries WHERE ID =(SELECT ReferQueryID FROM dbo.Qa_Queries WHERE ID = " + reportID + ")", new HashMap<String, Object>());
        String sql = SQLHelper.executeCommand(command, new UniqueResultHandler<String>());

        sql = sql.replace(":Operation", ">")
                .replaceAll(":RunDate", "DATEADD(DAY,-7,GETDATE())")
                .replaceAll(":RunID", "0");

        SQLEntity entity = SQLParser.parse(sql);
        Matcher matcher = Pattern.compile("[^\\r]*").matcher(entity.toString(columns));
        while (matcher.find()) {
            sb.append("<div>").append(matcher.group()).append("</div>");
        }

        command = new SqlCommand(server, database, CommandType.Text, this.sql, new HashMap<String, Object>());
        sb.append("<br/>").append(SQLHelper.executeCommand(command, new HTMLResultHandler(columns)));
        return sb.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Set report sub report config">
    private void setReportSubReportConfig() {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("ReportID", this.reportID);
            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getReportLinkCharts", params);
            CachedRowSet cachedRowSet = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
            while (cachedRowSet.next()) {
                String linkColumn = cachedRowSet.getString("LinkColumn");
                int subReportID = cachedRowSet.getInt("SubReportID");
                String paramName = cachedRowSet.getString("ParamName");
                String paramValueLink = cachedRowSet.getString("ParamValueLink");
                TableColumn reportColumn = this.columns.get(linkColumn);
                reportColumn.subReportID = subReportID;
                reportColumn.subReportName = cachedRowSet.getString("SubReportName");
                if (paramName != null) {
                    if (reportColumn.subReportParams == null) {
                        reportColumn.subReportParams = new HashMap<String, String>();
                    }
                    reportColumn.subReportParams.put(paramName, paramValueLink);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TableChart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    // </editor-fold>

    @Override
    public JSONObject getConfig() {
        JSONObject config = new JSONObject();
        config.put("reportName", this.reportName);
        config.put("reportType", this.reportType);
        return config;
    }

    @Override
    public JSONObject updateConfig(JSONObject config) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
