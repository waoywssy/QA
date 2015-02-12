/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.hanlders.impl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author wei.yin
 */
public class JsonTreeResultHandler implements ResultHandler<JSONObject> {

    private int lostPoint = 1;
    private String idColumn = "id";
    private String nameColumn = "name";
    private String parentIdColumn = "parentId";
    private String levelColumn = "level";
    private String lastfoundColumn = "lastfound";

    public JsonTreeResultHandler() {
    }

    public JsonTreeResultHandler(HashMap<String, String> columnMapping) {
        idColumn = columnMapping.get("id");
        nameColumn = columnMapping.get("name");
        parentIdColumn = columnMapping.get("parentId");
        levelColumn = columnMapping.get("level");
        lastfoundColumn = columnMapping.get("lastfound");
    }

    public JsonTreeResultHandler(int lostPoint, String idColumn, String nameColumn, String parentIdColumn, String levelColumn, String lastfoundColumn) {
        this.lostPoint = lostPoint;
        this.idColumn = idColumn;
        this.nameColumn = nameColumn;
        this.parentIdColumn = parentIdColumn;
        this.levelColumn = levelColumn;
        this.lastfoundColumn = lastfoundColumn;
    }

    @Override
    public JSONObject handle(ResultSet resultSet) throws SQLException {
        HashMap<String, JSONObject> map = new HashMap<String, JSONObject>();
        long maxLastFound = 0;
        long maxLastFound1 = 0;
        long temp = 0;
        JSONObject root = new JSONObject();
        root.put("id", "root");
        root.put("name", "root");
        root.put("leaf", true);
        root.put("count", 0);
        map.put("root", root);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        boolean isDate = false;
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            if (lastfoundColumn.equalsIgnoreCase(
                    rsmd.getColumnName(i))) {
                isDate = rsmd.getColumnType(i) == java.sql.Types.DATE
                        || rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP;
            }

        }

        while (resultSet.next()) {
            JSONObject obj = new JSONObject();
            obj.put("id", resultSet.getString(idColumn));
            obj.put("name", resultSet.getString(nameColumn));
            if (resultSet.getInt(levelColumn) == 1) {
                obj.put("parentId", "root");
            } else {
                obj.put("parentId", resultSet.getString(parentIdColumn));
            }
            obj.put("level", resultSet.getString(levelColumn));

            if (isDate) {
                temp = resultSet.getDate(lastfoundColumn).getTime();
            } else {
                temp = resultSet.getLong(lastfoundColumn);
            }
            if (temp > maxLastFound) {
                maxLastFound = temp;
            } else if (temp > maxLastFound1 && temp != maxLastFound) {
                maxLastFound1 = temp;
            }
            obj.put("lastfound", temp);
            obj.put("leaf", true);
            obj.put("count", 0);
            JSONObject parent = map.get(obj.get("parentId").toString());
            if (parent == null) {
                continue;
            } else {
                if (parent.get("children") == null) {
                    parent.put("children", new JSONArray());
                    parent.put("leaf", false);
                }
                ((JSONArray) parent.get("children")).add(obj);
            }
            int count = (Integer) parent.get("count");
            parent.put("count", count + 1);
            map.put(obj.get("id").toString(), obj);
        }
        if (maxLastFound1 == 0) {
            maxLastFound1 = maxLastFound;
        }
        JSONObject jSONObject = new JSONObject();
        JSONArray array = new JSONArray();
        root.put("lastfound", maxLastFound);
        array.add(root);
        jSONObject.put("maxLastFound", lostPoint==1?maxLastFound:maxLastFound1);
        jSONObject.put("children", array);

        return jSONObject;
    }
}
