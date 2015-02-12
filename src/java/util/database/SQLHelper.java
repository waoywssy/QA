package util.database;

import filter.InitConfigFilter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.rowset.CachedRowSet;
import util.LogHelper;
import util.database.hanlders.ResultHandler;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.statement.CommandType;
import util.database.statement.IteratorSqlParameters;
import util.database.statement.ParameterDirection;
import util.database.statement.SqlCommand;
import util.database.statement.SqlParameter;

/**
 *
 * @author Administrator
 */
public class SQLHelper {

    //<editor-fold defaultstate="collapsed" desc="Fields">
    private static Pattern sqlParamPattern = Pattern.compile(":([a-zA-Z]\\w+)");
   
    private static Pattern firstSelectPattern = Pattern.compile("\\bselect\\s+(top\\s+\\d+)?", Pattern.CASE_INSENSITIVE);
    private static Pattern firstSelectFromPattern = Pattern.compile("\\bselect\\s+.*?from", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
    private static HashMap<String, MiniConnectionPoolManager> poolManagers;
    private static HashMap<String, List<SqlParameter>> spsParametersMap;
    public static String DB_QA = "mj_auto_qa";
    public static final String DB_QA_DATA = "Qa_DataStatistic1";
    public static final String JOB_CENTRAL_IP ="10.8.253.41"; //"10.8.253.41";//"10.15.0.47";//"10.8.253.41";//10.10.0.40
    public static final String JOB_CENTRAL_TEST_IP ="10.10.0.41"; 
    public static String LOCAL_IP = "127.0.0.1";
    public static String DRIVER = DataSourceFactory.DRIVER_MSSQL;
    private final static int maxConnections = 15;
    private final static int timeout = 5;//60 * 30;
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Main">
    public static void main(String[] args) throws Exception {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        SqlCommand command = null;
        String sql = null;
        try {
            HashMap<String, Object> parmas = new HashMap<String, Object>();
            sql = "SELECT CatID AS id,ParentCatID as parentId,CatName as name,LastFound as lastfound,CatLevel as level FROM RetailListing.dbo.RetailListing_JCPenneyPrice_Categories_Static"
                    + " WHERE LastFound = (SELECT MAX(LastFound)FROM RetailListing.dbo.RetailListing_JCPenneyPrice_Categories_Static)"
                    + " ORDER BY CatLevel ASC";

            //sql="select top 5 * from dbo.bots";
            //addConnectionPoolManager("w.rdc.sae.sina.com.cn", "test", 3307, "yw1ymyozjo", "hywimxiijxihj4xkl1wy4520jk0yzy20xx1mizhy");
            //resultSet = selectTop1000Rows("w.rdc.sae.sina.com.cn", "test", sql, null);
            //connection = DataSourceFactory.createDataSourceMySQL("localhost", "world", 3306, "root", "root").getPooledConnection().getConnection();
            //addConnectionPoolManager("localhost", "world", 3306, "root", "root");
//            statement = connection.createStatement();
//            resultSet = statement.executeQuery(sql);

            //migrateData("localhost", "world", "localhost", "world", sql, "city1");
            //command = new SqlCommand("localhost", "world", CommandType.Text, sql, parmas);
            command = new SqlCommand(LOCAL_IP, DB_QA, CommandType.Text, sql, parmas);
            //resultSet = executeCommand(command, new CachedRowSetResultHandler());
            // print(executeCommand(command, new JSONResultHandler()));
            //sql = "insert into city1(Name,CountryCode) values(:Name,:CountryCode)";
            //command = new SqlCommand("localhost", "world", CommandType.Text, sql, new IteratorSqlParameters(resultSet));
            //updateMany(command);
            //printResultSet(resultSet);
            //print(executeCommand(command, new JsonTreeResultHandler()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SQLHelper.closeConnection(connection);
        }
    }
//</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Static">
    static {
        spsParametersMap = new HashMap<String, List<SqlParameter>>();
        poolManagers = new HashMap<String, MiniConnectionPoolManager>();

        if (DRIVER.equals(DataSourceFactory.DRIVER_MSSQL)) {
            addConnectionPoolManager(LOCAL_IP, DB_QA, 0, "sa", "P@ssw0rd");
        } else if (DRIVER.equals(DataSourceFactory.DRIVER_MYSQL)) {
            addConnectionPoolManager(LOCAL_IP, DB_QA, 0, "root", "root");
        }
        String sql = "SELECT [ID],[ServerName] ,[ServerIP],[ServerPort],[UserName],[Psd] FROM [dbo].[DB_Servers]";
        ResultSet resultset = null;

        try {
            resultset = SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());
            while (resultset.next()) {
                SQLHelper.addConnectionPoolManager(resultset.getString("ServerIP"), "master", resultset.getInt("ServerPort"), resultset.getString("UserName"), resultset.getString("Psd"));
            }
            SQLHelper.executeCommand(new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "UPDATE dbo.Bots SET QaStatus = 2 WHERE QaStatus = 1", new String[0]), null);
        } catch (Exception ex) {
            Logger.getLogger(InitConfigFilter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Get Connection">
    public static Connection getConnection(String serverName, String databaseName) throws SQLException {
        Connection connection = null;

        if (poolManagers.containsKey(serverName)) {
            MiniConnectionPoolManager manager = poolManagers.get(serverName);
            connection = manager.getConnection();
            if (databaseName != null && databaseName.length() > 0) {
                connection.setCatalog(databaseName);
            }
        }
        if (connection == null) {
            throw new SQLException("Failed to get connection");
        }
        return connection;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Add ConnectionPoolManager">
    public static void addConnectionPoolManager(String serverName, String database, int port, String userName, String password) {
        if (!poolManagers.containsKey(serverName)) {
            ConnectionPoolDataSource dataSource;
            dataSource = DataSourceFactory.createDataSource(serverName, database, port, userName, password, DRIVER);
            MiniConnectionPoolManager poolMgr = new MiniConnectionPoolManager(dataSource, maxConnections, timeout);
            poolManagers.put(serverName, poolMgr);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Execute Command" >
    public static <RES> RES executeCommand(SqlCommand command, ResultHandler<RES> resultHandler) throws SQLException {
        RES res = null;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection(command.server, command.database);

            if (command.commandType == CommandType.Text) {
                preparedStatement = connection.prepareStatement(command.commmandText);
                for (int i = 0; i < command.parameters.length; i++) {
                    preparedStatement.setObject(i + 1, command.parameters[i].getValue());
                }
            } else {
                CallableStatement callableStatement = connection.prepareCall(command.commmandText);
                preparedStatement = callableStatement;
                for (SqlParameter param : command.parameters) {
                    callableStatement.setObject(param.getName(), param.getValue());
                }
            }
            preparedStatement.setQueryTimeout(60 * 30);

            if (preparedStatement.execute() && resultHandler != null) {
                resultSet = preparedStatement.getResultSet();
                res = resultHandler.handle(resultSet);
            }
        } finally {
            closeConnection(connection, resultSet, preparedStatement);
        }
        return res;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="executeNonQuery" >
    public static void executeNonQuery(String server, String database, String queryText) throws SQLException {
        Connection connection = getConnection(server, database);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            statement.execute(queryText);
        } finally {
            closeConnection(connection, null, statement);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Update Many" >
    public static int[] updateMany(SqlCommand command) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        CallableStatement callableStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection(command.server, command.database);
            if (command.commandType == CommandType.Text) {
                preparedStatement = connection.prepareStatement(command.commmandText);
            } else {
                preparedStatement = callableStatement = connection.prepareCall(command.commmandText);
            }
            while (command.nextSQLarguments()) {
                if (command.commandType == CommandType.Text) {
                    for (int i = 0; i < command.parameters.length; i++) {
                        preparedStatement.setObject(i + 1, command.parameters[i].getValue());
                    }
                } else {
                    for (SqlParameter param : command.parameters) {
                        callableStatement.setObject(param.getName(), param.getValue());
                    }
                }
                preparedStatement.addBatch();
            }
            preparedStatement.setQueryTimeout(60 * 30);
            return preparedStatement.executeBatch();
        } finally {
            closeConnection(connection, resultSet, preparedStatement);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Migrate Data" >
    public static void migrateData(String targetServer, String targetDatabase, String destinationServer, String destinationDatabase, String sql, String tableName) throws Exception {
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            SqlCommand command = new SqlCommand(targetServer, targetDatabase, CommandType.Text, sql);
            resultSet = executeCommand(command, new CachedRowSetResultHandler());
            String sql2 = getInsertSql(tableName, resultSet);
            command = new SqlCommand(destinationServer, destinationDatabase, CommandType.Text, sql2, new IteratorSqlParameters(resultSet));
            updateMany(command);
        } finally {
            closeConnection(connection, resultSet, null);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Prepare SQL Arguments">
    /**
     *
     * The getPrepareSQLarguments() method looks for ":paramName" cases If the
     * String[]'s length is 1,No arguments String[0] stored the prepareSQL which
     * the ":paramName" replaced with "?"
     *
     * @param prepareSQL
     * @return the sql arguments,the first argument
     */
    public static String[] getPrepareSQLarguments(String prepareSQL) {
        LinkedList<String> list = new LinkedList<String>();
        Matcher matcher = sqlParamPattern.matcher(prepareSQL);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        prepareSQL = matcher.replaceAll("?");
        list.addFirst(prepareSQL);

        return list.toArray(new String[1]);
    }

    /**
     * set PreparedStatement parameters
     *
     * @param preparedStatements PreparedStatement
     * @param sqlParamNames the parameter sequence
     * @param sqlParamMap Parameters NameValueCollection
     * @throws SQLException
     */
    public static void setPrepareSQLarguments(PreparedStatement preparedStatements, String[] sqlParamNames, HashMap sqlParamMap) throws SQLException {
        for (int i = 1; sqlParamMap != null && i < sqlParamNames.length; i++) {
            preparedStatements.setObject(i, sqlParamMap.get(sqlParamNames[i]));
        }
    }

    public static void setPrepareSQLarguments(PreparedStatement preparedStatements, String[] sqlParamNames, ResultSet resultSet) throws SQLException {
        for (int i = 1; i < sqlParamNames.length; i++) {
            preparedStatements.setObject(i, resultSet.getObject(sqlParamNames[i]));
        }
    }

    public static List<SqlParameter> getStoredProcedureParameters(String serverName, String databaseName, String spName) {
        String key = serverName + databaseName + spName;
        try {
            if (spsParametersMap.containsKey(key)) {
                List<SqlParameter> spParameters = new LinkedList<SqlParameter>();
                for (SqlParameter parameter : spsParametersMap.get(key)) {
                    spParameters.add(parameter.clone());
                }
                return spParameters;
            } else {
                String spArgsSql = "exec sys.sp_sproc_columns '" + spName + "'";
                CachedRowSet resultSet = null;
                //selectTop1000Rows(serverName, databaseName, spArgsSql, null);
                if (resultSet != null && resultSet.size() > 0) {
                    List<SqlParameter> spParameters = new LinkedList<SqlParameter>();
                    while (resultSet.next()) {
                        if (resultSet.getInt("COLUMN_TYPE") == 5) {
                            continue;
                        }
                        SqlParameter parameter = new SqlParameter();
                        parameter.setName(resultSet.getString("COLUMN_NAME").substring(1));
                        parameter.setValue(resultSet.getInt("COLUMN_TYPE") == 1 ? ParameterDirection.INPUT : ParameterDirection.OUTPUT);
                        spParameters.add(parameter);
                    }
                    spsParametersMap.put(key, spParameters);
                }
                return getStoredProcedureParameters(serverName, databaseName, spName);
            }
        } catch (Exception ex) {
            Logger.getLogger(SQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String getCallString(int parameterCount, String spName) {
        spName = "{call " + spName + "(";
        for (int i = 0; i < parameterCount; i++) {
            if (i == 0) {
                spName += "?";
            } else {
                spName += ",?";
            }
        }
        spName += ")}";
        return spName;
    }

    public static String getInsertSql(String tableName, ResultSet resultSet) throws SQLException {
        String sql = "INSERT INTO " + tableName + " ({0}) VALUES({1})";
        String delimite = "";
        String columns = "";
        String values = "";
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columns += delimite + resultSetMetaData.getColumnName(i);
            values += delimite + ":" + resultSetMetaData.getColumnName(i);
            delimite = ",";
        }
        sql = MessageFormat.format(sql, columns, values);
        return sql;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Close Connection" >
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeStatement(Statement stmt) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
    }

    public static void closeConnection(Connection con, ResultSet rs, Statement stmt) {
        closeStatement(stmt);
        closeResultSet(rs);
        closeConnection(con);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Print Methods">
    public static void println(String string) {
        System.out.println(string);
    }

    public static void print(String string) {
        System.out.print(string);
    }

    public static void printMap(Map map) {
        if (map == null) {
            return;
        }
        Set keySet = map.keySet();
        for (Object obj : keySet) {
            println(obj.toString() + "  " + map.get(obj).toString());
        }

    }

    public static void printResultSet(ResultSet rs) {
        if (rs == null) {
            return;
        }
        try {
            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                print(rsm.getColumnName(i) + "    ");
            }
            println("");
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    print(rs.getObject(i) + "  ");
                }
                println("");
            }
        } catch (Exception e) {
            Logger.getLogger(SQLHelper.class
                    .getName()).log(Level.SEVERE, null, e);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="SelectTopSQL">
    public static String selectTopSQL(String queryText, int topCount) {
        Matcher matcher = firstSelectPattern.matcher(queryText);
        return matcher.replaceFirst("select top " + topCount + " ");
    }

    public static String selectTopSQL(String queryText, int topCount, String columns) {
        Matcher matcher = firstSelectFromPattern.matcher(queryText);
        return matcher.replaceFirst("select top " + topCount + " " + columns + " from ");
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="IsStringColumn">
    public static boolean[] isStringColumn(ResultSetMetaData rsmd) throws SQLException {
        /*
        int numColumns = rsmd.getColumnCount();
        boolean[] isString = new boolean[numColumns];
        for (int i = 1; i < numColumns + 1; i++) {
            String column_name = rsmd.getColumnName(i);

            switch (rsmd.getColumnType(i)) {
                case java.sql.Types.ARRAY:
                    obj.put(column_name, rs.getArray(column_name));
                    break;
                case java.sql.Types.BIGINT:
                    obj.put(column_name, rs.getInt(column_name));
                    break;
                case java.sql.Types.BOOLEAN:
                    obj.put(column_name, rs.getBoolean(column_name));
                    break;
                case java.sql.Types.BLOB:
                    obj.put(column_name, rs.getBlob(column_name));
                    break;
                case java.sql.Types.DOUBLE:
                    obj.put(column_name, rs.getDouble(column_name));
                    break;
                case java.sql.Types.FLOAT:
                    obj.put(column_name, rs.getFloat(column_name));
                    break;
                case java.sql.Types.INTEGER:
                    obj.put(column_name, rs.getInt(column_name));
                    break;
                case java.sql.Types.NVARCHAR:
                    obj.put(column_name, rs.getNString(column_name));
                    break;
                case java.sql.Types.VARCHAR:
                    obj.put(column_name, rs.getString(column_name));
                    break;
                case java.sql.Types.TINYINT:
                    obj.put(column_name, rs.getInt(column_name));
                    break;
                case java.sql.Types.SMALLINT:
                    obj.put(column_name, rs.getInt(column_name));
                    break;
                case java.sql.Types.DATE:
                    obj.put(column_name, rs.getDate(column_name));
                    break;
                case java.sql.Types.TIMESTAMP:
                    obj.put(column_name, rs.getTimestamp(column_name));
                    break;
                default:
                    obj.put(column_name, rs.getObject(column_name));
                    break;
            }
        }
        */
        return null;
    }
    // </editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Handle SQLException">
    public static void handleSQLException(SQLException ex) throws SQLException {
        switch (ex.getErrorCode()) {
            case 0:
                LogHelper.logInfo("Connect time out", ex);
                break;
            case 102:
                LogHelper.logInfo("Incorrect syntax", ex);
                throw ex;
            case 201:
                LogHelper.logInfo("Invalid column name", ex);
                throw ex;
            case 208:
                LogHelper.logInfo("Invalid object name", ex);
                throw ex;
            case 406:
                LogHelper.logInfo("Cannot open database", ex);
                throw ex;
            case 8120:
                LogHelper.logInfo(" Column is invalid in the select list because it is not contained in either an aggregate", ex);
                throw ex;
            case 911:
                LogHelper.logInfo("Cannot find database", ex);
                throw ex;
            case 18456:
                LogHelper.logInfo("Login failed for user", ex);
                throw ex;
            default:
                LogHelper.logInfo(ex);
        }
    }
    //</editor-fold>
}
