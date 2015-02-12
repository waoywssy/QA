/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class SQLParser {

    static String basePathString = "E:\\temp\\sql\\";
    static String saveFile = "E:\\temp\\temp.sql";
    static Pattern tableNamePattern = Pattern.compile("from(\\s+|\\[)\\S*?(?<Name>\\w+)\\]?(\\s|\\(|\\s*$)", PatternHelper.PATTERN_OPTIONS);
    static Pattern selectContentPattern = Pattern.compile("(?<Start>SELECT(\\s*top\\s+\\d+)?)(?<Content>.*)(?<End>FROM.*)", PatternHelper.PATTERN_OPTIONS);
    static Pattern startBlankPattern = Pattern.compile("^\\s*", PatternHelper.PATTERN_OPTIONS);
    static Pattern columnNamePattern = Pattern.compile("(as|\\))\\s+\\[?(?<Name1>\\w+)\\]?$|^\\s*\\[?(?<Name2>\\w+)\\]?\\s*$|^\\s*\\[(?<Name3>\\w+)\\]?\\s*=", PatternHelper.PATTERN_OPTIONS);
    static Pattern whereClausePattern = Pattern.compile("\\bwhere\\s+(?<Where>.*?)\\b(group|order|having)\\b", PatternHelper.PATTERN_OPTIONS);
    static Pattern groupPattern = Pattern.compile("\\bgroup\\s+by(?<Group>.*?)\\b(order|having)\\b", PatternHelper.PATTERN_OPTIONS);
    static Pattern orderPattern = Pattern.compile("\\border\\s+by(?<Order>.*)", PatternHelper.PATTERN_OPTIONS);
    static Pattern commentsPattern = Pattern.compile("--[^\\n]*|/\\*.*?\\*/", PatternHelper.PATTERN_OPTIONS);
    public static List<String> keywords = new ArrayList<String>();

    static {
        keywords.add("cast");
        keywords.add("day");
        keywords.add("month");
        keywords.add("year");
        keywords.add("convert");
        keywords.add("varchar");
        keywords.add("datetime");
        keywords.add("datedd");
        keywords.add("datediff");
        keywords.add("as");
        keywords.add("null");
        keywords.add("in");
        keywords.add("or");
        keywords.add("then");
        keywords.add("else");
        keywords.add("end");
        keywords.add("between");
        keywords.add("and");
        keywords.add("len");
        keywords.add("min");
        keywords.add("count");
        keywords.add("max");
        keywords.add("avg");
        keywords.add("sum");
        keywords.add("case");
        keywords.add("abs");
        keywords.add("case");
        keywords.add("is");
        keywords.add("when");
        keywords.add("not");
        keywords.add("distinct");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException {
        new SQLParser().run();
    }

    private void run() throws SQLException {
        String pathString = basePathString + "sql1.sql";
        String sql = FileHelper.fileToString(pathString);
        //FileHelper.stringToFile(saveFile, sql);
        SQLEntity entity = parse(sql);
        for (String alias : entity.selectColumns.keySet()) {
            System.out.print(entity.getSourceColumns(alias) + "---");
            System.out.println(alias);
        }
        System.out.println("=======" + entity.getTotalColumn());
//        String sqlString = entity.toString();
//        sqlString = entity.toString(new String[]{"RunDate", "TotalPrice"});
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("RunDate", "2012-3-4");
        //sqlString = entity.getDrillDownSQL("TotalPhoneLenZero", 100, false, params);
        //      FileHelper.stringToFile(saveFile, sqlString);
        /*
         SqlCommand command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT A.ID,A.QueryText FROM dbo.Qa_Queries AS A INNER JOIN dbo.Nodes  AS B ON A.ID = B.id WHERE nodeType=15 AND [Database]='Qa_DataStatistic1'");
         ResultSet set = SQLHelper.executeCommand(command, new CachedRowSetResultHandler());
         while (set.next()) {
         try {
         String sql = set.getString("QueryText");
         String id = set.getString("ID");
         SQLEntity entity = SQLParser.parse(sql);
         if ("Crazy8_Categories".equals(entity.tableName)) {
         int i = 0;
         }
         HashMap<String, Object> map = new HashMap<String, Object>();
         command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "SELECT A.* FROM dbo.Qa_Queries AS A INNER JOIN dbo.ScheduledScripts AS B ON A.ID = B.QueryID WHERE A.Name = '" + entity.tableName + "'", map);
         HashMap<String, Object> values = SQLHelper.executeCommand(command, new MapRowHandler());
         if (entity.tableName.equals("Qa_DataStatistic1")) {
         System.out.println(id);
         }
         if (values.size() == 0) {
         System.out.println(entity.tableName);
         continue;
         }
         String q = (String) values.get("QueryText");
         entity = SQLParser.parse(q);

         String groupString = entity.getGroupClause();
         String n = entity.tableName;
  
         try {
         map.put("ID", set.getInt("ID"));
         map.put("ReferID", values.get("ID"));
         map.put("GroupClause", groupString);
         } catch (Exception ex) {
         ex.printStackTrace();
         }
         command = new SqlCommand(SQLHelper.LOCAL_IP, SQLHelper.DB_QA, CommandType.Text, "UPDATE dbo.Qa_Queries SET ReferQueryID=:ReferID,GroupClause=:GroupClause WHERE ID = :ID", map);
         SQLHelper.executeCommand(command, null);
         } catch (Exception ex) {
         System.out.println(ex.getMessage());
         }
            
         }*/
    }

    public static SQLEntity parse(String sqlString) {
        SQLEntity entity = new SQLEntity();
        sqlString = cleanComments(sqlString).trim();
        Matcher matcher = null;
        try {
            matcher = tableNamePattern.matcher(sqlString);

            matcher.find();
            entity.tableName = matcher.group("Name");

            String startString = null;
            String selectContentString = null;
            String endString = null;
            matcher = selectContentPattern.matcher(sqlString);
            if (matcher.find()) {
                startString = matcher.group("Start").trim();
                selectContentString = matcher.group("Content");
                endString = matcher.group("End").trim();
                endString = startBlankPattern.matcher(endString).replaceAll("");
            }
            entity.topRows = getSelectTopCount(startString);
            entity.selectColumns = getAlias(selectContentString);
            entity.whereClause = getWhereClause(endString);
            entity.setGroups(getGroupClause(endString));
            entity.orderClause = getOrderClause(endString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return entity;
    }

    public static String[] getSelectRows(String selectColumnsContent) {
        String[] columnStrs = selectColumnsContent.split(",");
        int leftBrackets = 0;
        int rightBrackets = 0;
        boolean newStart = true;
        List<String> strs = new ArrayList<String>();
        for (int i = 0; i < columnStrs.length; i++) {
            String str = columnStrs[i];
            for (int j = 0; j < str.length(); j++) {
                if ('(' == str.charAt(j)) {
                    leftBrackets++;
                } else if (')' == str.charAt(j)) {
                    rightBrackets++;
                }
            }

            if (newStart && leftBrackets == rightBrackets) {
                strs.add(str);
            } else {
                if (newStart) {
                    strs.add(str);
                } else {
                    strs.set(strs.size() - 1, strs.get(strs.size() - 1) + "," + str);
                }
                newStart = false;
            }

            if (leftBrackets == rightBrackets) {
                newStart = true;
            }
        }

        String[] args = strs.toArray(new String[0]);
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        return args;
    }

    private static LinkedHashMap<String, String> getAlias(String selectClause) {
        LinkedHashMap<String, String> selectColumns = new LinkedHashMap<String, String>();
        String[] args = getSelectRows(selectClause);
        Matcher matcher = null;
        for (int i = 0; i < args.length; i++) {
            matcher = columnNamePattern.matcher(args[i]);
            if (matcher.find()) {
                String alias = nullToEmpty(matcher.group("Name1"))
                        + nullToEmpty(matcher.group("Name2"))
                        + nullToEmpty(matcher.group("Name3"));

                String original = matcher.replaceAll("").trim();
                original = original.length() == 0 ? alias : original;
                selectColumns.put(alias, original);
            } else {
                System.out.println("Error");
            }
        }
        return selectColumns;
    }

    private static int getSelectTopCount(String selectTopString) {
        if (selectTopString.toLowerCase().contains("top")) {
            return Integer.parseInt(selectTopString.replaceAll("\\D", ""));
        } else {
            return -1;
        }
    }

    private static String getWhereClause(String endPartString) {
        return PatternHelper.getValue(whereClausePattern, "Where", endPartString);
    }

    private static String getGroupClause(String endPartString) {
        return PatternHelper.getValue(groupPattern, "Group", endPartString);
    }

    private static String getOrderClause(String endPartString) {
        return PatternHelper.getValue(orderPattern, "Order", endPartString);
    }

    private static String nullToEmpty(String input) {
        return input == null ? "" : input;
    }

    private static String cleanComments(String sql) {
        sql = commentsPattern.matcher(sql).replaceAll("");
        return sql;
    }
}
