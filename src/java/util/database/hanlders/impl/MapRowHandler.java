package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import util.database.hanlders.ResultHandler;

public class MapRowHandler
        implements ResultHandler<HashMap<String, Object>> {

    @Override
    public HashMap<String, Object> handle(ResultSet resultSet)
            throws SQLException {
        HashMap<String, Object> colMap = new HashMap();
        if (resultSet.next()) {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            int numColumns = resultSetMetaData.getColumnCount();
            for (int i = 1; i <= numColumns; i++) {
                colMap.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));
            }
        }
        return colMap;
    }
}