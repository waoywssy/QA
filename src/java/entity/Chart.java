/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.SQLException;
import java.util.HashMap;
import javax.sql.rowset.CachedRowSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.StringHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public abstract class Chart {

    public int reportID;
    public String reportName;
    public String sql;
    public String server;
    public String database;
    public String comments;
    public String reportType;
    public String groupClause;
    public String totalColumnName;
    public int startRow;
    public int referQueryID;
    private QueryParameter[] rParams;

    public Chart(int reportID) {
        this.reportID = reportID;
        init();
    }

    private void init() {
        try {
            if (this.reportID < 0) {
                this.reportName = "Maj_Runs";
                this.sql = "exec sp_getMaj_Runs @BotID = " + Math.abs(reportID);
                this.server = SQLHelper.LOCAL_IP;
                this.database = SQLHelper.DB_QA_DATA;
                this.reportType = "tablechart";
                this.comments = "";
                this.startRow = 1;
                this.referQueryID = -1;
                this.groupClause = null;
                return;
            }
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("node", reportID);
            SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA,
                    CommandType.StoredProcedure, "sp_getQuery", params);
            HashMap<String, Object> cells = SQLHelper.executeCommand(command, new MapRowHandler());

            this.reportName = (String) cells.get("name");
            this.sql = (String) cells.get("queryText");
            this.server = (String) cells.get("server");
            if (this.server.equals("127.0.0.1") || this.server.equals("localhost")) {
                this.server = SQLHelper.LOCAL_IP;
            }
            this.database = (String) cells.get("database");
            this.reportType = (String) cells.get("queryType");
            this.comments = (String) cells.get("comments");
            this.startRow = (Integer) cells.get("startRow");
            this.referQueryID = (Integer) cells.get("ReferQueryID");
            this.groupClause = (String) cells.get("GroupClause");
            this.totalColumnName = (String) cells.get("TotalItemColumn");
            //set report parameters
            command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, 
                    "SELECT * FROM Qa_Query_Params WHERE ReportID = " + reportID);
            CachedRowSet rowSet = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
            rParams = new QueryParameter[rowSet.size()];
            int i = 0;
            while (rowSet.next()) {
                rParams[i] = new QueryParameter();
                rParams[i].name = rowSet.getString("Name");
                rParams[i].value = rowSet.getString("DefaultValue");
                rParams[i].type = rowSet.getString("DataType");
                i++;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    protected abstract void initChart();

    protected abstract JSONObject generateReport(HashMap<String, Object> params) throws SQLException;

    public abstract JSONObject getConfig();

    public abstract JSONObject updateConfig(JSONObject config) throws SQLException;
    
    public JSONObject getQueryInfo() {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("name", this.reportName);
        jSONObject.put("node", this.reportID);
        jSONObject.put("server", this.server);
        jSONObject.put("database", this.database);
        jSONObject.put("queryText", this.sql);
        jSONObject.put("queryType", this.reportType);
        jSONObject.put("startRow", this.startRow);
        return jSONObject;
    }

    public JSONObject viewReport(HashMap<String, Object> params) throws SQLException {
        JSONObject jo = null;
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        for (int i = 0; rParams != null && i < rParams.length; i++) {
            if (params == null || !params.containsKey(rParams[i].name)) {
                JSONArray jSONArray = new JSONArray();
                for (int j = 0; j < rParams.length; j++) {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("Name", rParams[j].name);
                    jSONObject.put("DefaultValue", rParams[j].value);
                    jSONObject.put("DataType", rParams[j].type);
                    jSONObject.put("ReportID", reportID);
                    jSONArray.add(jSONObject);
                }
                jo = new JSONObject();
                jo.put("params", jSONArray);
            }

            params.put(rParams[i].name
                    , StringHelper.stringToObject(params.get(rParams[i].name) == null ? null : params.get(rParams[i].name).toString(), rParams[i].type)
                    );
        }

        return jo == null ? generateReport(params) : jo;
    }
}
