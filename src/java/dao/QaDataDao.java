/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import entity.ScheduledScript;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import javax.sql.rowset.CachedRowSet;
import util.LogHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.hanlders.impl.MapMetaDataHandler;
import util.database.hanlders.impl.MapRowHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class QaDataDao {

    public void qaBot(int botID) throws SQLException {
        //botID = 119; // 10033
        boolean success = false;
        String botNameString = null;
        String jobIDs = null;
        HashMap<String, Object> paramMap = null;
        ResultSet resultSet = null;
        CachedRowSet newFoundRunsResultSet = null;
        String sql = null;
        try {
            //1. ��Bots���ж�Ӧ��bot���ó�״̬1�����checkʱ�����óɵ�ǰ
            sql = "UPDATE dbo.Bots SET QaStatus = 1,LastCheckDate = GETDATE() WHERE ID = " + botID;
            SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), null);
            // DateFinished ��Bot���һ��run��finishedʱ�䣬 ��ΪNULL��֤�����һ�λ�û���н�������Ҫ�ٲ鿴��ǰ״̬
            sql = "SELECT *,CASE WHEN DateFinished IS NULL THEN '>=' ELSE '>' END AS Operation FROM dbo.Bots WHERE ID = " + botID;
            resultSet = SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());

            paramMap = new MapRowHandler().handle(resultSet); // ÿ��botֻ��һ����¼

            if (paramMap == null || paramMap.size() == 0) {
                LogHelper.logInfo("Not found bot info:" + botID);
                return;
            }
            botNameString = paramMap.get("BotName").toString();
            jobIDs = paramMap.get("JobIDs").toString();
            LogHelper.logInfo("Start QA Bot:" + botNameString + " RunDate:" + paramMap.get("RunDate")
                    + " Operation:" + paramMap.get("Operation"));
            //2.��ȡ��Bot��QA�ű�����������һ�ű�һ��query
            List<ScheduledScript> scheduledScripts = loadScheduledScripts(botID);
            //3.Get new run info from jobcenter
            LogHelper.logInfo("Get new run info from jobcenter");
            
            // ��Maj_Runs��Maj_Runs_arch����ȡ��������10�ε����м�¼
            newFoundRunsResultSet = getNewRunsFromJobCentral(paramMap);
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            paramMap.put("QaDate", timestamp);
            if (newFoundRunsResultSet == null || newFoundRunsResultSet.size() == 0) {
                //No new runs
                LogHelper.logInfo("no need to qa:" + botNameString);
                return;
            }
            ScheduledScript majRunsScript = new ScheduledScript();
            majRunsScript.name = "Maj_Runs";
            majRunsScript.joinColumn = "RunID";
            majRunsScript.joinRunsColumn = "RunID";
            
            // ����JobCentral�ϵ�Run������
            saveQaData(majRunsScript, jobIDs, newFoundRunsResultSet, (Timestamp) paramMap.get("QaDate"), true);

            //4.execute statistic scripts
            for (int i = 0; i < scheduledScripts.size(); i++) {
                ScheduledScript script = scheduledScripts.get(i);
                String qaScript = script.script;
                String serverIP = script.server;
                String dataBaseName = script.database;
                LogHelper.logInfo("---" + script.name);
                CachedRowSet queryCachedRowSet = executeQueryWithRetry(serverIP, dataBaseName, qaScript, paramMap);
                // ����QA���ݵ��������ݿ�
                saveQaData(script, jobIDs, queryCachedRowSet, (Timestamp) paramMap.get("QaDate"), true);
            }

            //5.update bot last run info
            paramMap.clear();

            newFoundRunsResultSet.beforeFirst();
            sql = "UPDATE dbo.Bots SET LastQaDate = GETDATE(),QaStatus = 0,Checked = 0,RunDate = :RunDate,RunID = :RunID,DateFinished = :DateFinished,Success = :Success WHERE ID = " + botID;
            paramMap = new MapRowHandler().handle(newFoundRunsResultSet);

            SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, paramMap), null);

            success = true;
        } catch (Exception execption) {
            LogHelper.logInfo(execption);
        } finally {
            if (!success) {
                sql = "UPDATE dbo.Bots SET QaStatus = 0 WHERE ID = " + botID;
                // ����QA������ֹ��QA����
                try {
                    SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), null);
                } catch (Exception ex) {
                    LogHelper.logInfo(ex);
                }
            }
            LogHelper.logInfo("End QA Bot:" + botNameString);
        }
    }

    private CachedRowSet executeQueryWithRetry(String serverName, String databaseName, String sql, HashMap sqlParamMap) throws InterruptedException, SQLException {
        int retries = 0;

        if (sqlParamMap != null) {
            if (sqlParamMap.containsKey("JobIDs")) {
                sql = sql.replaceAll(":JobIDs", sqlParamMap.get("JobIDs").toString());
            }
            if (sqlParamMap.containsKey("Operation")) {
                sql = sql.replaceAll(":Operation", sqlParamMap.get("Operation").toString());
            }
        }

        do {
            try {
                return SQLHelper.executeCommand(new SqlCommand(serverName, databaseName, CommandType.Text, sql, sqlParamMap), new CachedRowSetResultHandler());
            } catch (Exception exception) {
                LogHelper.logInfo(exception.getMessage());
                Thread.sleep(retries * 5 * 1000);
            }
        } while (++retries < 20);
        return null;
    }

    private CachedRowSet getNewRunsFromJobCentral(HashMap<String, Object> sqlParamMap) throws InterruptedException, SQLException {
        String sql = "SELECT TOP 10 A.RunID,A.DateStarted AS RunDate,A.JobID,A.DateFinished,A.Success "
                + " FROM(SELECT RunID,DateStarted,JobID,DateFinished,Success FROM [JobCentral].[dbo].[Maj_Runs] WITH(nolock) "
                + " UNION SELECT RunID,DateStarted,JobID,DateFinished,Success FROM [JobCentral].[dbo].[Maj_Runs_arch] WITH(nolock) )AS A"
                + " WHERE A.JobID IN (" + sqlParamMap.get("JobIDs").toString() + ") "
                + " AND A.DateStarted " + sqlParamMap.get("Operation") + " :RunDate ORDER BY A.RunID DESC";

        return executeQueryWithRetry(SQLHelper.JOB_CENTRAL_IP, "JobCentral", sql, sqlParamMap);
    }

    /**
     * qaResultSet - ���10�ε����н��
     * 
     */
    private void saveQaData(ScheduledScript script, String jobIDs, CachedRowSet qaResultSet, Timestamp time, boolean retry) {
        String insertSql = null;
        String[] arguments = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            if (qaResultSet == null || qaResultSet.size() == 0) {
                LogHelper.logInfo("No data");
            } else {
                connection = SQLHelper.getConnection(SQLHelper.LOCAL_IP, SQLHelper.DB_QA_DATA);
                qaResultSet.beforeFirst();
                insertSql = SQLHelper.getInsertSql(script.name, qaResultSet).replaceFirst("\\)", ",QaDate)");
                insertSql = insertSql.substring(0, insertSql.lastIndexOf(")")) + ",?)";

                arguments = SQLHelper.getPrepareSQLarguments(insertSql);
                preparedStatement = connection.prepareStatement(arguments[0]);

                while (qaResultSet.next()) {
                    SQLHelper.setPrepareSQLarguments(preparedStatement, arguments, qaResultSet);
                    preparedStatement.setTimestamp(arguments.length, time);
                    preparedStatement.addBatch();
                }
                preparedStatement.executeBatch();
            }

            if (script.joinColumn != null && script.joinColumn.length() > 0) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("tableName", script.name);
                params.put("jobIds", jobIDs);
                params.put("qadate", time);
                params.put("column_data", script.joinColumn);
                params.put("column_runs", script.joinRunsColumn);
                params.put("convertType", script.convertType);
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA_DATA, CommandType.StoredProcedure, "sp_markFinish", params);
                SQLHelper.executeCommand(command, null);
            }
        } catch (Exception ex) {
            SQLHelper.closeConnection(connection, null, preparedStatement);
            LogHelper.logInfo("Exception in save data", ex.getMessage());
            if (retry) {
                try {
                    createQaDataTable(qaResultSet, script.name);
                    saveQaData(script, jobIDs, qaResultSet, time, false);
                } catch (Exception e) {
                    LogHelper.logInfo("Exception in create table", e);
                }
            }
        } finally {
            SQLHelper.closeConnection(connection, null, preparedStatement);
        }
    }

    private void createQaDataTable(ResultSet qaResultSet, String tableName) {

        try {
            String script = "IF NOT EXISTS(SELECT * FROM sys.objects WHERE object_id = object_id(''{0}''))\n"
                    + "BEGIN\n  CREATE TABLE  {0} (\n QaDate datetime default(getdate()) {1}\n,Finished bit default(1)\n)\nEND\n"
                    + "ELSE \nBEGIN\n {2}\nEND";
            String addColumnTemplate = "\n  IF NOT EXISTS(SELECT * FROM sys.columns WHERE object_id = object_id(''{0}'') AND Name = ''{1}'')"
                    + "\n    ALTER TABLE {0} \n      ADD {1} {2};";
            LinkedHashMap<String, String> columns = new MapMetaDataHandler().handle(qaResultSet);

            String buildTableString = "";
            String alterTableString = "";
            Set<String> keySet = columns.keySet();
            for (String key : keySet) {
                buildTableString += "\n   ," + key + "    " + columns.get(key);
                alterTableString += MessageFormat.format(addColumnTemplate, tableName, key, columns.get(key));
            }
            script = MessageFormat.format(script, tableName, buildTableString, alterTableString);

            SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA_DATA, CommandType.Text, script, new String[0]), null);
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
        }
    }

    /*
     * ���صȴ�QA��Bots
     */
    public ArrayList<Integer> getScheduleQuenue() {
        ArrayList<Integer> botIDs = new ArrayList<Integer>();
        ResultSet resultSet = null;
        try {
            String sql = "SELECT TOP 5 ID FROM dbo.Bots WHERE [QaStatus] = 2 AND LEN(JobIDs) > 0 ORDER BY Priority ASC";
            resultSet = SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());
            while (resultSet.next()) {
                botIDs.add(resultSet.getInt("ID"));
            }
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
        }
        return botIDs;
    }

    /*
     * ������12/13(������ʱ��)Сʱû��QA����Bot����ȴ�QA����
     */
    public void checkSchedule() {
        try {
            String sql = "UPDATE dbo.Bots SET [QaStatus] = 2 WHERE [QaStatus] = 0 AND (DATEDIFF(HOUR,[LastQaDate],GETDATE())>13) AND [Disabled]=0";
            SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), null);
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
        }
    }

    /*
     * ����AutoQaScripts
     */
    private List<ScheduledScript> loadScheduledScripts(int botId) throws SQLException {
        List<ScheduledScript> list = new ArrayList<ScheduledScript>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("BotID", botId);
        SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_getScheduledScripts", params);
        CachedRowSet rowSet = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
        while (rowSet.next()) {
            ScheduledScript script = new ScheduledScript(
                    rowSet.getInt("ID"),
                    rowSet.getInt("BotID"),
                    rowSet.getInt("QueryID"),
                    rowSet.getString("Name"),
                    rowSet.getString("JoinColumn"),
                    rowSet.getString("JoinRunsColumn"),
                    rowSet.getString("QueryText"),
                    rowSet.getInt("ConvertType"),
                    rowSet.getString("Database"),
                    rowSet.getString("Server"));
            list.add(script);
        }
        return list;
    }
}
