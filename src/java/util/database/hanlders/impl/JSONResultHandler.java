package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.LogHelper;
import util.database.hanlders.ResultHandler;

public class JSONResultHandler
        implements ResultHandler<JSONArray> {

    @Override
    public JSONArray handle(ResultSet rs) {
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            JSONArray json = new JSONArray();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i < numColumns + 1; i++) {
                    String column_name = rsmd.getColumnName(i);
                    switch (rsmd.getColumnType(i)) {
                        case java.sql.Types.ARRAY:
                            obj.put(column_name, rs.getArray(i));
                            break;
                        case java.sql.Types.BIGINT:
                            obj.put(column_name, rs.getLong(i));
                            break;
                        case java.sql.Types.BOOLEAN:
                            obj.put(column_name, rs.getBoolean(i));
                            break;
                        case java.sql.Types.BLOB:
                            obj.put(column_name, rs.getBlob(i));
                            break;
                        case java.sql.Types.DOUBLE:
                            obj.put(column_name, rs.getDouble(i));
                            break;
                        case java.sql.Types.FLOAT:
                            obj.put(column_name, rs.getFloat(i));
                            break;
                        case java.sql.Types.INTEGER:
                            obj.put(column_name, rs.getInt(i));
                            break;
                        case java.sql.Types.NVARCHAR:
                            obj.put(column_name, rs.getNString(i));
                            break;
                        case java.sql.Types.VARCHAR:
                            obj.put(column_name, rs.getString(i));
                            break;
                        case java.sql.Types.TINYINT:
                            obj.put(column_name, rs.getShort(i));
                            break;
                        case java.sql.Types.SMALLINT:
                            obj.put(column_name, rs.getShort(i));
                            break;
                        case java.sql.Types.DATE:
                            obj.put(column_name, String.valueOf(rs.getDate(i)));
                            break;
                        case java.sql.Types.TIMESTAMP:
                            obj.put(column_name, String.valueOf(rs.getTimestamp(i)));
                            break;
                        default:
                            obj.put(column_name, rs.getObject(i));
                            break;
                    }
                    if(rs.wasNull()){
                        obj.put(column_name, null);
                    }
                }
                json.add(obj);
            }
            return json;
        } catch (SQLException ex) {
            LogHelper.logInfo(ex);
        }
        return null;
    }
}