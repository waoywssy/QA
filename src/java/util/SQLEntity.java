/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class SQLEntity {

    public int topRows = -1;
    public String tableName;
    public LinkedHashMap<String, String> selectColumns;//key:alias;value orignal
    public String whereClause;
    public List<String> groups = new ArrayList<String>(5);
    public List<String> groupAliases = new ArrayList<String>(5);
    public String orderClause;
    private final Pattern columnNamePattern = Pattern.compile("^(?<Agg>\\w+)\\s*\\(\\s*(CASE\\s+WHEN(?<Case>.*?)\\bEND|((?<Fun>\\w+)\\s*\\(\\s*)?(?<Distinct>DISTINCT\\s*)?(?<ColumnName>[\\[\\]\\w_*]+)\\s*\\))", PatternHelper.PATTERN_OPTIONS);
    private final Pattern casePattern = Pattern.compile("^(?<Exp>.*?)\\bThEN\\b\\s*(?<Cond>\\w+)", PatternHelper.PATTERN_OPTIONS);
    private final Pattern sourceColumns = Pattern.compile("\\[[^]]*\\]|\\b[a-z]\\w+\\b", PatternHelper.PATTERN_OPTIONS);

    public String toString(String[] columns) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        if (topRows > 0) {
            sb.append("TOP ").append(topRows);
        }
        String delimit = "\r\n      ";
        if (columns != null) {
            for (int i = 0; i < columns.length; i++) {
                if (selectColumns.get(columns[i]).equals(columns[i])) {
                    sb.append(delimit).append(columns[i]);
                } else {
                    sb.append(delimit).append(selectColumns.get(columns[i])).append(" AS ").append(columns[i]);
                }
                delimit = "\r\n     ,";
            }
        } else {
            for (String alias : selectColumns.keySet()) {
                if (selectColumns.get(alias).equals(alias)) {
                    sb.append(delimit).append(alias);
                } else {
                    sb.append(delimit).append(selectColumns.get(alias)).append(" AS ").append(alias);
                }
                delimit = "\r\n     ,";
            }
        }
        sb.append("\r\nFROM ").append(tableName);
        if (whereClause != null && whereClause.length() > 0) {
            sb.append("\r\nWHERE ").append(whereClause);
        }

        delimit = "";
        if (groups.size() > 0) {
            sb.append("\r\nGROUP BY ");
            for (String name : groups) {
                sb.append(delimit).append(name);
                delimit = ",";
            }
        }

        if (orderClause != null && orderClause.length() > 0) {
            sb.append("\r\nORDER BY ").append(orderClause);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String getDrillDownSQL(int topCount, boolean isRows, HashMap<String, Object> params) {
        String alias = (String) params.get("SelectedColumn");
        String original = selectColumns.get(alias);
        StringBuilder sb = new StringBuilder();
        Matcher matcher = null;
        String where = "";
        String delimit = "";
        Object value = null;
        for (int j = 0; j < groups.size(); j++) {
            if (params.containsKey(groupAliases.get(j))) {
                value = params.get(groupAliases.get(j));
            } else {
                return groupAliases.get(j) + " not assign value";
            }

            if (value instanceof String) {
                where += delimit + groups.get(j) + "='" + value + "'";
            } else {
                where += delimit + groups.get(j) + "=" + value;
            }
            delimit = " AND ";
        }

        if (groups.size() == 0) {
            value = params.get(alias);
            where = original + "=";
            if (value instanceof String) {
                where += "'" + value + "'";
            } else {
                where += value;
            }
            return "SELECT TOP 1 * FROM " + tableName + " " + where + " ORDER BY " + orderClause;
        }

        String agg = null;
        String fun = null;
        String caseStr = null;
        String distinct = null;
        String columnName = null;
        matcher = columnNamePattern.matcher(original);

        if (matcher.find()) {
            agg = matcher.group("Agg").toLowerCase();
            fun = matcher.group("Fun");
            caseStr = matcher.group("Case");
            distinct = matcher.group("Distinct");
            columnName = matcher.group("ColumnName");

        } else {
            columnName = original;
        }
        if (distinct
                != null) {
            sb.append("SELECT TOP ").append(topCount).append(" * FROM (")
                    .append("\r\nSELECT DISTINCT ").append(columnName).append(" FROM ").append(tableName)
                    .append("\r\nWHERE ").append(where)
                    .append("\r\n)AS A1");
        } else if (caseStr != null) {
            matcher = casePattern.matcher(caseStr);
            matcher.find();

            String exp = matcher.group("Exp").trim();
            String cond = matcher.group("Cond").trim();
            if ("1".equals(cond)) {
                sb.append("SELECT TOP ").append(topCount).append(" ").append(isRows ? "*" : columnName).append(" FROM ").append(tableName)
                        .append("\r\nWHERE ");
                sb.append("(").append(exp).append(")").append(" AND ").append(where);
            } else {
                sb.append("SELECT TOP ").append(topCount).append(" ").append(isRows ? "*" : columnName).append(" FROM ").append(tableName)
                        .append("\r\nWHERE ").append(where).append(" AND 1 = CASE WHEN").append(caseStr).append(" END ");
            }
        } else if ("min".equals(agg)) {
            sb.append("SELECT TOP 1 * FROM ").append(tableName)
                    .append("\r\nWHERE ").append(columnName).append(" IS NOT NULL AND ").append(where)
                    .append("\r\nORDER BY ").append(fun == null ? columnName : fun + "(" + columnName + ")").append(" ASC");
        } else if ("max".equals(agg)) {
            sb.append("SELECT TOP 1 * FROM ").append(tableName)
                    .append("\r\nWHERE ").append(columnName).append(" IS NOT NULL AND ").append(where)
                    .append("\r\nORDER BY ").append(fun == null ? columnName : fun + "(" + columnName + ")").append(" DESC");
        } else if (Pattern.matches("count\\s*\\(\\s*\\*\\s*\\)\\s*-.*", original.toLowerCase())) {
            sb.append("SELECT TOP 200 * FROM ").append(tableName)
                    .append("\r\nWHERE ").append(getSourceColumns(alias)).append(" IS NULL AND ").append(where);
        } else if (original.toLowerCase().startsWith("count") && !original.contains("*")) {
            sb.append("SELECT TOP 200 * FROM ").append(tableName)
                    .append("\r\nWHERE ").append(getSourceColumns(alias)).append(" IS NOT NULL AND ").append(where);
        } else {
            sb.append("SELECT TOP ").append(topCount).append(" ").append(isRows ? "*" : columnName).append(" FROM ").append(tableName)
                    .append("\r\nWHERE ").append(where);
        }

        return sb.toString();
    }

    private String getGroupAlias(String original) {
        original = original.replaceAll("\\s|\\[|\\]", "");
        if (original.contains("(")) {
            for (String key : selectColumns.keySet()) {
                String alias = selectColumns.get(key).replaceAll("\\s|\\[|\\]", "");
                if (original.equalsIgnoreCase(alias)) {
                    return key;
                }
            }
        } else {
            return original;
        }
        return null;
    }

    public void setGroups(String groupClause) {
        if (groupClause != null && groupClause.length() > 0) {
            String[] groupColumns = SQLParser.getSelectRows(groupClause);
            for (int i = 0; i < groupColumns.length; i++) {
                groups.add(groupColumns[i]);
                groupAliases.add(getGroupAlias(groupColumns[i]));
            }
        }
    }

    public String getGroupClause() {
        String resultString = "";
        String delimit = "";
        for (String name : groups) {
            resultString += delimit + name;
            delimit = ",";
        }
        return resultString;
    }

    public String getSourceColumns(String alias) {
        if (!selectColumns.containsKey(alias)) {
            return "";
        }
        String nameString = "";
        String delimitString = "";
        String sourceClause = selectColumns.get(alias);
        Matcher matcher = sourceColumns.matcher(sourceClause);
        while (matcher.find()) {
            String name = matcher.group();
            boolean f = name.contains("[");
            name = name.replaceAll("\\[|\\]", "").toLowerCase();
            if (f || !SQLParser.keywords.contains(name)) {
                if (nameString.contains(name)) {
                    continue;
                }
                nameString += delimitString + name;
                delimitString = ",";
            }
        }
        return nameString;
    }

    public String getTotalColumn() {
        for (String key : selectColumns.keySet()) {
            String v = selectColumns.get(key);
            if (v.replaceAll(" ", "").toLowerCase().equals("count(*)")) {
                return key;
            }
        }
        return null;
    }
}
