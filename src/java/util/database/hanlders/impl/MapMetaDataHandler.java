/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.hanlders.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.LinkedHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author wei.yin
 */
public class MapMetaDataHandler implements ResultHandler<LinkedHashMap<String, String>> {

    /**
     * @return <ColumnName,DataType>
     */
    @Override
    public LinkedHashMap<String, String> handle(ResultSet resultSet) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        if (resultSet == null) {
            return null;
        }

        String typeNameString = "";
        try {
            ResultSetMetaData rsm = resultSet.getMetaData();
            int columnCount = rsm.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                typeNameString = rsm.getColumnTypeName(i);
                if (typeNameString.contains("char")) {
                    typeNameString += "(" + rsm.getPrecision(i) + ")";
                } else if (typeNameString.contains("decimal")) {
                    typeNameString += "(" + rsm.getPrecision(i) + "," + rsm.getScale(i) + ")";
                }
                map.put(rsm.getColumnName(i), typeNameString);
            }
        } catch (Exception e) {
            Logger.getLogger(MapMetaDataHandler.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        return map;
    }
}
