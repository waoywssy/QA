/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.rowset.CachedRowSet;
import util.LogHelper;
import util.database.SQLHelper;
import util.database.hanlders.impl.CachedRowSetResultHandler;
import util.database.statement.CommandType;
import util.database.statement.IteratorSqlParameters;
import util.database.statement.SqlCommand;

/**
 *
 * @author wei.yin
 */
public class GenerateScriptDao {

    // <editor-fold defaultstate="collapsed" desc="select top">
    public String selectTop(String server, String database, String table) throws SQLException {
        CachedRowSet rowSet = getTableColumns(server, database, table);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("SELECT TOP 1000 ");
        while (rowSet.next()) {
            if (first) {
                first = false;
                sb.append("\n   ").append(rowSet.getString("COLUMN_NAME"));
            } else {
                sb.append("\n  ,").append(rowSet.getString("COLUMN_NAME"));
            }
        }
        sb.append("\nFROM ").append(table);
        return sb.toString();
    }
    // </editor-fold >

    // <editor-fold defaultstate="collapsed" desc="create table">
    public String getTableScript(String serverName, String databaseName, String pattern) {
        ResultSet resultSet = null;
        HashMap map = new HashMap();
        StringBuilder stringBuilder = new StringBuilder();
        String sql = null;

        try {
            List<String> tables = new ArrayList<String>();
            sql = "SELECT Name FROM sys.objects WHERE name like '" + pattern + "' AND TYPE = N'U'";

            resultSet = SQLHelper.executeCommand(new SqlCommand(serverName, databaseName, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());
            while (resultSet.next()) {
                tables.add(resultSet.getString(1));
            }

            for (String tableName : tables) {
                map.clear();
                map.put("table_name", tableName);
                map.put("table_owner", "dbo");

                resultSet = SQLHelper.executeCommand(new SqlCommand(serverName, databaseName, CommandType.StoredProcedure, "sp_columns", map), new CachedRowSetResultHandler());
                String delimit = "   ";
                boolean start = true;
                while (resultSet.next()) {
                    if (start) {
                        start = false;
                        tableName = resultSet.getString("TABLE_NAME");
                        stringBuilder.append("CREATE TABLE dbo.").append(tableName).append("(\n");
                    }

                    stringBuilder.append(delimit).append(resultSet.getString("COLUMN_NAME")).append(" ");
                    stringBuilder.append(getSQLDataType(resultSet));
                    stringBuilder.append(resultSet.getBoolean("NULLABLE") ? " NULL" : " NOT NULL");
                    stringBuilder.append("\n");
                    delimit = "  ,";
                }
                if (!start) {
                    stringBuilder.append(")ON [PRIMARY]").append("\nGO\n\n");
                }

                sql = "select * from sys.check_constraints where parent_object_id = object_id(:tableName)";
                map.clear();
                map.put("tableName", tableName);
                resultSet = SQLHelper.executeCommand(new SqlCommand(serverName, databaseName, CommandType.Text, sql, map), new CachedRowSetResultHandler());
                String checkString = "ALTER TABLE [dbo].[{0}]  WITH CHECK ADD CONSTRAINT [{1}] CHECK ({2})";
                while (resultSet.next()) {
                    stringBuilder.append(MessageFormat.format(checkString, tableName, resultSet.getString("name"), resultSet.getString("definition"))).append("\nGO\n");
                }

                sql = "select dc.name,dc.definition,sc.name as column_name"
                        + " from sys.default_constraints as dc left join sys.columns as sc"
                        + " on dc.parent_object_id = sc.object_id and dc.parent_column_id = sc.column_id"
                        + " where parent_object_id = object_id(:tableName)";
                map.clear();
                map.put("tableName", tableName);
                resultSet = SQLHelper.executeCommand(new SqlCommand(serverName, databaseName, CommandType.Text, sql, map), new CachedRowSetResultHandler());
                String defaultConstraintString = "ALTER TABLE [dbo].[{0}] ADD  CONSTRAINT [{1}]  DEFAULT ({2}) FOR [{3}]";
                while (resultSet.next()) {
                    stringBuilder.append(MessageFormat.format(defaultConstraintString, tableName, resultSet.getString("name"), resultSet.getString("definition"), resultSet.getString("column_name"))).append("\nGO\n\n");
                }

                map.clear();
                map.put("table_name", tableName);

                resultSet = SQLHelper.executeCommand(new SqlCommand(serverName, databaseName, CommandType.StoredProcedure, "sp_indexes_90_rowset", map), new CachedRowSetResultHandler());
                String indexString = "CREATE {0} {1} INDEX [{2}] ON [dbo].[{3}] \n({4}\n)WITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = OFF) ON [PRIMARY]";
                String primaryKeyString = "ALTER TABLE [dbo].[{0}] ADD CONSTRAINT {1}  PRIMARY KEY {2} ({3} )"
                        + "\nWITH (PAD_INDEX  = OFF, STATISTICS_NORECOMPUTE  = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS  = ON, ALLOW_PAGE_LOCKS  = ON) ON [PRIMARY]";
                String string = "";
                String indexName = "";
                String indexColumnsString = "";
                String clusteredString = "";
                String unique = "";
                boolean primaryKey = false;
                boolean save = false;
                delimit = "\n  ";

                while (resultSet.next()) {
                    indexColumnsString += delimit + "[" + resultSet.getString("COLUMN_NAME") + "]" + (resultSet.getInt("COLLATION") == 1 ? " ASC" : " DESC");
                    delimit = ",\n  ";
                    indexName = resultSet.getString("INDEX_NAME");

                    if (resultSet.isLast()) {
                        save = true;
                    } else if (resultSet.next()) {
                        String nextIndexName = resultSet.getString("INDEX_NAME");
                        if (!indexName.endsWith(nextIndexName)) {
                            save = true;
                        }
                        resultSet.previous();
                    }

                    if (save) {
                        primaryKey = resultSet.getBoolean("PRIMARY_KEY");
                        clusteredString = resultSet.getBoolean("CLUSTERED") ? "CLUSTERED" : "NONCLUSTERED";
                        unique = resultSet.getBoolean("UNIQUE") ? "UNIQUE" : "";
                        if (primaryKey) {
                            string = MessageFormat.format(primaryKeyString, tableName, indexName, clusteredString, indexColumnsString);
                        } else {
                            string = MessageFormat.format(indexString, unique, clusteredString, indexName, tableName, indexColumnsString);
                        }

                        stringBuilder.append(string).append("\nGO\n");
                        indexColumnsString = "";
                        delimit = "\n  ";
                    }
                    save = false;
                }
            }
        } catch (Exception ex) {
            stringBuilder.append((ex.getMessage()));
            LogHelper.logInfo(ex);
        }
        return stringBuilder.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="insert">
    public String insert(String server, String database, String table) throws SQLException {
        CachedRowSet rowSet = getTableColumns(server, database, table);
        String pattern = "INSERT INTO {0} ({1}) \nVALUES({2})";
        String columns = "";
        String values = "";
        String delimit = "\n    ";
        boolean first = true;

        while (rowSet.next()) {
            String columnName = rowSet.getString("COLUMN_NAME");
            String dataType = getSQLDataType(rowSet);
            if (dataType.contains("identity")) {
                continue;
            }
            columns += delimit + columnName;
            values += delimit + columnName + " " + dataType;
            if (first) {
                first = false;
                delimit = "\n   ,";
            }
        }

        return MessageFormat.format(pattern, table, columns, values);
    }
    // </editor-fold >

    // <editor-fold defaultstate="collapsed" desc="update">
    public String update(String server, String database, String table) throws SQLException {
        CachedRowSet rowSet = getTableColumns(server, database, table);
        String pattern = "UPDATE " + table + " \nSET ";

        String delimit = "";
        boolean first = true;

        while (rowSet.next()) {
            String columnName = rowSet.getString("COLUMN_NAME");
            String dataType = getSQLDataType(rowSet);
            if (dataType.contains("identity")) {
                continue;
            }
            pattern += delimit + columnName + " = @" + columnName + " " + dataType;

            if (first) {
                first = false;
                delimit = "\n   ,";
            }
        }

        return pattern + "\nWHERE ";
    }
    // </editor-fold >

    // <editor-fold defaultstate="collapsed" desc="insert proc">
    public String insertPROC(String server, String database, String table) throws SQLException {
        CachedRowSet rowSet = getTableColumns(server, database, table);
        String proc = "CREATE PROC SP_ {0} \nAS\nBEGIN\n  SET NOCOUNT ON;\n   IF NOT EXISTS(SELECT TOP 1 * FROM " + table + " WHERE )"
                + "\n       {1}\n   ELSE\n  {2}\nEND";
        String insertPattern = "INSERT INTO {0} ({1}) \n    VALUES({2})";
        String insertColumns = "";
        String insertValues = "";

        String updatePattern = "UPDATE " + table + " \n     SET  ";
        String paramsString = "";
        String delimit = "\n        ";
        boolean first = true;

        while (rowSet.next()) {
            String columnName = rowSet.getString("COLUMN_NAME");
            String dataType = getSQLDataType(rowSet);
            if (dataType.contains("identity")) {
                continue;
            }
            insertColumns += delimit + columnName;
            insertValues += delimit + "@" + columnName;

            updatePattern += delimit + columnName + " = @" + columnName;
            paramsString += delimit + "@" + columnName + " " + dataType;
            if (first) {
                first = false;
                delimit = "\n       ,";
            }
        }
        updatePattern += "\n      WHERE ";
        String insert = MessageFormat.format(insertPattern, table, insertColumns, insertValues);
        return MessageFormat.format(proc, paramsString, insert, updatePattern);
    }
    // </editor-fold >

    // <editor-fold defaultstate="collapsed" desc="create proc">
    public String getSpScript(String server, String database, String pattern) {
        ResultSet resultSet = null;
        StringBuilder sb = new StringBuilder();
        try {

            String sql = "SELECT TOP 100 id, [text] FROM sys.syscomments WHERE id IN (SELECT OBJECT_ID FROM sys.objects WHERE name like '" + pattern + "' AND TYPE = N'P') ORDER BY id,colid ASC";
            resultSet = SQLHelper.executeCommand(new SqlCommand(server, database, CommandType.Text, sql, new String[0]), new CachedRowSetResultHandler());
            String idString = "";
            while (resultSet.next()) {
                if (!"".equals(idString) && !idString.equals(resultSet.getString(1))) {
                    sb.append("\nGO").append("\n\n\n");
                }
                sb.append(resultSet.getString(2));
                idString = resultSet.getString(1);
            }
            if (!"".equals(idString)) {
                sb.append("\nGO").append("\n\n\n");
            }
        } catch (Exception e) {
            sb.append(e.getMessage());
        }
        return sb.toString();
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="syncTable">
    public void syncTable(String server, String database, String table, String node, boolean isTable) throws SQLException, Exception {
        SqlCommand command = new SqlCommand(server, database, CommandType.Text, "SELECT " + node + " AS parentId,name FROM sys." + (isTable ? "tables" : "procedures"));

        CachedRowSet rowSet = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
        IteratorSqlParameters iterator = new IteratorSqlParameters(rowSet);
        //SQLHelper.printResultSet(rowSet);
        command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, " IF NOT EXISTS(SELECT * FROM dbo.Nodes WHERE parentId = :parentId AND name = :name) \nINSERT INTO dbo.Nodes VALUES(:parentId,:name," + (isTable ? 8 : 7) + "," + (isTable ? 0 : 1) + ",1,null)", iterator);
        SQLHelper.updateMany(command);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="syncColumn">
    public void syncColumn(String server, String database, String table, String parentNode) throws SQLException {
        CachedRowSet rowSet = getTableColumns(server, database, table);
        boolean first = true;
        HashMap<String, Object> params = new HashMap<String, Object>();
        SqlCommand command = null;
        int id = Integer.parseInt(parentNode);
        while (rowSet.next()) {
            if (first) {
                first = false;
                SQLHelper.executeNonQuery(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, "DELETE FROM dbo.Nodes WHERE parentId = " + id);
            }
            String columnName = rowSet.getString("COLUMN_NAME") + "(" + getSQLDataType(rowSet) + ")";
            params.clear();
            params.put("parentId", id);
            params.put("id", 0);
            params.put("name", columnName);
            params.put("nodeType", 4);

            command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.StoredProcedure, "sp_updateNode", params);
            SQLHelper.executeCommand(command, null);
        }
    }
    // </editor-fold>

    private CachedRowSet getTableColumns(String server, String database, String table) throws SQLException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("table_name", table);
        map.put("table_owner", "dbo");
        return SQLHelper.executeCommand(new SqlCommand(server, database, CommandType.StoredProcedure, "sp_columns", map), new CachedRowSetResultHandler());
    }

    private String getSQLDataType(ResultSet resultSet) throws SQLException {
        String string = "";
        String typeName = resultSet.getString("TYPE_NAME");
        if (typeName.contains("char")) {
            string = typeName + "(" + resultSet.getString("PRECISION") + ")";
        } else if (typeName.contains("decimal")||typeName.contains("numeric")) {
            string = typeName + "(" + resultSet.getString("PRECISION") + "," + resultSet.getString("SCALE") + ")";
        } else if (typeName.contains("identity")) {
            string = typeName + "(1,1)";
        } else {
            string = typeName;
        }
        return string;
    }
}
