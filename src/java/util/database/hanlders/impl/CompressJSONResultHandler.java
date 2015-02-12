package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.LogHelper;
import util.StringHelper;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author Administrator
 * {"ColumnNames":["ColumnName1","ColumnName2"],Rows[{ColumnValue1,ColumnValue2},{ColumnValue1,ColumnValue2}]}
 */
public class CompressJSONResultHandler
        implements ResultHandler<String> {

    @Override
    public String handle(ResultSet rs) {
        String delimited = "";
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("{");
        try {
            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();

            sBuilder.append("\"ColumnNames\":[");
            for (int i = 1; i <= columnCount; i++) {
                sBuilder.append(delimited);
                sBuilder.append("\"").append(rsm.getColumnName(i)).append("\"");
                delimited = ",";
            }
            sBuilder.append("]");

            delimited = "";
            sBuilder.append(",\"Rows\":[");
            while (rs.next()) {
                String flag = "";
                sBuilder.append(delimited).append("[");
                for (int i = 1; i <= columnCount; i++) {
                    if (rsm.getColumnType(i) == java.sql.Types.INTEGER
                            || rsm.getColumnType(i) == java.sql.Types.BIGINT
                            || rsm.getColumnType(i) == java.sql.Types.DECIMAL
                            || rsm.getColumnType(i) == java.sql.Types.DOUBLE
                            || rsm.getColumnType(i) == java.sql.Types.FLOAT
                            || rsm.getColumnType(i) == java.sql.Types.SMALLINT) {
                        sBuilder.append(flag).append(rs.getObject(i));
                    } else {
                        StringHelper.toJSONString(rs.getString(i), sBuilder);
                    }
                    flag = ",";
                }
                sBuilder.append("]");
                delimited = ",";
            }
            sBuilder.append("]");
        } catch (Exception ex) {
           LogHelper.logInfo(ex);
        }
        sBuilder.append("}");
        return sBuilder.toString();
    }
}