/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.hanlders.impl;

import entity.TableChart;
import entity.TableColumn;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import util.database.hanlders.ResultHandler;

/**
 *
 * @author wei.yin
 */
public class QaJsonResultHandler implements ResultHandler<JSONObject> {

    private final TableChart reportInfo;

    public QaJsonResultHandler(TableChart reportInfo) {
        this.reportInfo = reportInfo;
    }

    @Override
    public JSONObject handle(ResultSet rs) throws SQLException {
        return resultSetToJson(rs);
    }

    private JSONObject resultSetToJson(ResultSet rs) {
        if (rs == null) {
            return null;
        }

        JSONObject jroot = new JSONObject();
        jroot.put("reportID", reportInfo.reportID);
        jroot.put("referQueryID", reportInfo.referQueryID);

        jroot.put("reportName", reportInfo.reportName);
        jroot.put("startRow", reportInfo.startRow);
        jroot.put("reportType", reportInfo.reportType);
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                String cName = metaData.getColumnName(i);
                TableColumn r = reportInfo.columns.get(cName);
                if (r == null) {
                    if (!"finished".equalsIgnoreCase(cName)) {
                        r = new TableColumn(cName, reportInfo.columns.size() + 1, true);
                        r.valueIndex = i;
                        r.valueType = metaData.getColumnType(i);
                        reportInfo.columns.put(cName, r);
                    }
                } else {
                    r.valueIndex = i;
                    r.valueType = metaData.getColumnType(i);
                }
            }
            Collection<TableColumn> columns = reportInfo.columns.values();
            JSONArray headers = new JSONArray();
            int i = 0;
            for (TableColumn rc : columns) {
                JSONObject jo = new JSONObject();
                rc.ColOrder = i++;
                jo.put("name", rc.ColumnName);
                jo.put("min", rc.MinValue);
                jo.put("max", rc.MaxValue);
                jo.put("minp", rc.AvgValue);
                jo.put("maxp", rc.FluValue);
                jo.put("originalColumn", rc.sourceColumns);
                jo.put("index", rc.ColOrder);
                jo.put("show", rc.Show);
                headers.add(jo);
            }
            jroot.put("headers", headers);

            String[] groupColumns = reportInfo.groupClause != null && reportInfo.groupClause.length() > 0
                    ? reportInfo.groupClause.split(",") : new String[0];
            JSONArray groups = new JSONArray();
            for (String groupName : groupColumns) {
                TableColumn c = reportInfo.columns.get(groupName);
                groups.add(c == null ? -1 : c.ColOrder);
            }
            jroot.put("groupColumns", groups);
            jroot.put("totalColumn", reportInfo.columns.containsKey(reportInfo.totalColumnName) ? reportInfo.columns.get(reportInfo.totalColumnName).ColOrder : -1);
            JSONArray values = new JSONArray();
            while (rs.next()) {
                JSONArray obj = new JSONArray();
                for (TableColumn rc : columns) {
                    if (rc.valueType == java.sql.Types.TIMESTAMP
                            || rc.valueType == java.sql.Types.DATE) {
                        obj.add(String.valueOf(rs.getTimestamp(rc.valueIndex)));
                    } else {
                        obj.add(rs.getObject(rc.valueIndex));
                    }
                }
                values.add(obj);
            }//end while
            jroot.put("values", values);
        } catch (Exception e) {
            jroot.put("error", e.getMessage());
        }

        return jroot;
    }
}
