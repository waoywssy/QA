/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.util.HashMap;

/**
 *
 * @author wei.yin
 */
public class TableColumn implements Comparable<TableColumn> {

    public String ColumnName;
    public int ColOrder;
    public boolean Show = true;
    public boolean isLimit = false;
    public Double MinValue;
    public Double MaxValue;
    public Double AvgValue;
    public Double FluValue;
    public boolean IsImage = false;
    public int subReportID;
    public int valueIndex;
    public int valueType;
    public String sourceColumns = "";
    public String subReportName;
    public HashMap<String, String> subReportParams;

    public TableColumn() {
    }

    public TableColumn(String name, int colorder, boolean show) {
        this.ColumnName = name;
        this.ColOrder = colorder;
        this.Show = show;
    }

    public TableColumn(String name, int colorder, boolean show, Object min, Object max, Object avg, Object flu, String sourceColumns) {
        this.ColumnName = name;
        this.ColOrder = colorder;
        this.Show = show;
        this.sourceColumns = sourceColumns;
        if (min != null) {
            this.MinValue = (Double) min;
        }
        if (max != null) {
            this.MaxValue = (Double) max;
        }
        if (avg != null) {
            this.AvgValue = (Double) avg;
        }
        if (flu != null) {
            this.FluValue = (Double) flu;
        }
        this.isLimit = isLimit();
    }

    private boolean isLimit() {
        if (MinValue != null || MaxValue != null || AvgValue != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(TableColumn o) {
        if (ColumnName != null && ColumnName.equals(o.ColumnName)) {
            return 0;
        }
        if (this.ColOrder == o.ColOrder) {
            return 0;
        } else {
            return this.ColOrder > o.ColOrder ? 1 : -1;
        }
    }

    @Override
    public int hashCode() {
        if (ColumnName == null) {
            return super.hashCode();
        } else {
            return ColumnName.hashCode();
        }
    }
}
