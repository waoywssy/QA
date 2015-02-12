/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.ResultSet;
import javax.sql.rowset.CachedRowSet;
import util.LogHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.statement.CommandType;
import util.database.statement.IteratorSqlParameters;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class ImportDataDao {
    
    public String importJobInfo(String destinationServer, int jobId) {
        String sql = null;
        ResultSet resultSet = null;
        
        try {
            String jobCentralDatabase = "JobCentral";

            //delete local job info
            sql = "DELETE FROM dbo.Maj_Jobs WHERE JobId = " + jobId;
            SQLHelper.executeCommand(new SqlCommand(destinationServer, jobCentralDatabase, CommandType.Text, sql, new String[0]), null);
            //get job info from remote database and insert job info to local database
            sql = "SELECT * FROM dbo.Maj_Jobs WHERE JobId =" + jobId;
            
            SQLHelper.migrateData(SQLHelper.JOB_CENTRAL_IP, jobCentralDatabase, destinationServer, jobCentralDatabase, sql, "Maj_Jobs");
            //update regex           
            sql = "SELECT B.* FROM dbo.Job_Regex AS A INNER JOIN dbo.Regex AS B ON A.Regex_Id = B.Regex_Id WHERE Job_Id = " + jobId;
            
            resultSet = SQLHelper.executeCommand(new SqlCommand(SQLHelper.JOB_CENTRAL_IP, jobCentralDatabase, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());
            sql = " IF EXISTS(SELECT * FROM dbo.Regex WHERE Regex_Desc = :Regex_Desc)"
                    + " BEGIN\nUPDATE [dbo].[Regex]"
                    + " SET [Regex_Type_Id] = :Regex_Type_Id"
                    + " ,[Regex_Content] = :Regex_Content"
                    + "  ,[Regex_File] = :Regex_File"
                    + "  ,[Last_Updated] = :Last_Updated"
                    + "  	  ,[Last_Updated_By] = :Last_Updated_By"
                    + "   WHERE Regex_Desc = :Regex_Desc\nEND\n"
                    + "  ELSE"
                    + "  \nBEGIN\n	INSERT INTO [dbo].[Regex]"
                    + "  			   ([Regex_Desc],[Regex_Type_Id]"
                    + "  			   ,[Regex_Content]"
                    + "  			   ,[Regex_File]"
                    + "  			   ,[Last_Updated]"
                    + "  			   ,[Last_Updated_By])"
                    + "  		 VALUES"
                    + "  			   (:Regex_Desc,:Regex_Type_Id"
                    + "  			   ,:Regex_Content"
                    + "  			   ,:Regex_File"
                    + "  			   ,:Last_Updated"
                    + "  			   ,:Last_Updated_By)\nEND\n";
            IteratorSqlParameters iterator = new IteratorSqlParameters(resultSet);
            SQLHelper.updateMany(new SqlCommand(destinationServer, jobCentralDatabase, CommandType.Text, sql, iterator));

            //delete job and regex relation        
            sql = "DELETE FROM dbo.Job_Regex WHERE Job_Id = " + jobId;
            
            SQLHelper.executeCommand(new SqlCommand(destinationServer, jobCentralDatabase, CommandType.Text, sql, new String[0]), null);
            //build job and regex relation
            sql = "SELECT A.Job_Id,A.Last_Updated,A.Last_Updated_By,A.ServerName,B.Regex_Desc FROM dbo.Job_Regex AS A"
                    + " LEFT JOIN dbo.Regex AS B"
                    + " ON A.Regex_Id = B.Regex_Id"
                    + " WHERE Job_Id = " + jobId;
            
            resultSet = SQLHelper.executeCommand(new SqlCommand(SQLHelper.JOB_CENTRAL_IP, jobCentralDatabase, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());
            sql = "DECLARE @Regex_Id INT;"
                    + " SELECT @Regex_Id = Regex_Id FROM dbo.Regex WHERE Regex_Desc = :Regex_Desc;"
                    + "INSERT INTO [dbo].[Job_Regex]([Job_Id],[Regex_Id] ,[Last_Updated],[Last_Updated_By],[ServerName])"
                    + "     VALUES(:Job_Id,@Regex_Id,:Last_Updated,:Last_Updated_By,:ServerName)";
            
            iterator = new IteratorSqlParameters(resultSet);
            SQLHelper.updateMany(new SqlCommand(destinationServer, jobCentralDatabase, CommandType.Text, sql, iterator));

            //delete requset info
            sql = "DELETE FROM dbo.Config_Request_Parameters"
                    + " FROM dbo.Config_Request_Parameters AS A"
                    + " INNER JOIN dbo.Config_Job_Requests AS B"
                    + " ON A.RequestId = B.id"
                    + " WHERE JobId = " + jobId
                    + " DELETE FROM dbo.Config_Request_Patterns"
                    + " FROM dbo.Config_Request_Patterns AS C"
                    + " INNER JOIN dbo.Config_Job_Requests AS D"
                    + " ON C.RequestId = D.id"
                    + " WHERE JobId = " + jobId
                    + " DELETE FROM dbo.Config_Job_Requests WHERE JobId =" + jobId;
            
            SQLHelper.executeCommand(new SqlCommand(destinationServer, jobCentralDatabase, CommandType.Text, sql, new String[0]), null);
            //get request info
            sql = "SELECT id,JobId,Url,HttpMethod,GetProcedureName,SaveProcedureName,MinimumSleepTime,MaximumSleepTime,'127.0.0.1:8888' AS Proxy,SiteId,[Target],Active,Comments FROM JobCentral.dbo.Config_Job_Requests WHERE JobId = " + jobId;
            SQLHelper.migrateData(SQLHelper.JOB_CENTRAL_IP, jobCentralDatabase, destinationServer, jobCentralDatabase, sql, "Config_Job_Requests");

            //get 
            sql = "SELECT A.* FROM dbo.Config_Request_Parameters AS A"
                    + " INNER JOIN dbo.Config_Job_Requests AS B"
                    + " ON A.RequestId = B.id"
                    + " WHERE JobId = " + jobId;
            SQLHelper.migrateData(SQLHelper.JOB_CENTRAL_IP, jobCentralDatabase, destinationServer, jobCentralDatabase, sql, "Config_Request_Parameters");
            //get 
            sql = "SELECT A.* FROM dbo.Config_Request_Patterns AS A"
                    + " INNER JOIN dbo.Config_Job_Requests AS B"
                    + " ON A.RequestId = B.id"
                    + " WHERE JobId = " + jobId;
            SQLHelper.migrateData(SQLHelper.JOB_CENTRAL_IP, jobCentralDatabase, destinationServer, jobCentralDatabase, sql, "Config_Request_Patterns");
        } catch (Exception ex) {
            LogHelper.logInfo(ex);
            return ex.getMessage();
        }
        return "Success";
    }
    
    public void importData(String fromServer, String toServer, String toTable, String sql) throws Exception {
        CachedRowSet result = SQLHelper.executeCommand(new SqlCommand(fromServer, null, CommandType.Text, sql), new CachedRowSetResultHandler());
        LogHelper.logInfo("load total records:" + result.size());
        String insertSQL = SQLHelper.getInsertSql(toTable, result);
        IteratorSqlParameters iterator = new IteratorSqlParameters(result);
        SQLHelper.updateMany(new SqlCommand(toServer, "", CommandType.Text, insertSQL, iterator));
    }
}
