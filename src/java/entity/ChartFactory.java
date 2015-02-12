/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.database.SQLHelper;
import util.database.hanlders.impl.UniqueResultHandler;
import util.database.statement.CommandType;
import util.database.statement.SqlCommand;

/**
 *
 * @author Administrator
 */
public class ChartFactory {

    private static HashMap<Integer, Chart> cachedCharts = new HashMap<Integer, Chart>();

    public static void removeKey(int reportID) {
        cachedCharts.remove(reportID);
    }

    public static Chart getChart(int reportID) {
        if (cachedCharts.containsKey(reportID)) {
            return cachedCharts.get(reportID);
        }

        Chart chart = null;
        try {
            String reportType = "tablechart";
            if (reportID > 0) {
                SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT QueryType FROM dbo.Qa_Queries WHERE ID = " + String.valueOf(reportID));
                reportType = SQLHelper.executeCommand(command, new UniqueResultHandler<String>());
            }
            if ("tablechart".equals(reportType)) {
                chart = new TableChart(reportID);
            } else if ("linechart".equals(reportType)) {
                chart = new LineChart(reportID);
            } else if ("treechart".equals(reportType)) {
                chart = new TreeChart(reportID);
            } else if ("pivotchart".equals(reportType)) {
                chart = new PivotChart(reportID);
            }

            if (chart == null) {
                throw new RuntimeException("Not found chart:" + reportID);
            } else {
                cachedCharts.put(reportID, chart);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        return chart;
    }
}
