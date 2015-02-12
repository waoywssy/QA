/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.database.statement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author wei.yin
 */
public class SqlCommand {

    private static Pattern sqlCommentPattern = Pattern.compile("/\\*.*?\\*/|--.*?$", Pattern.DOTALL | Pattern.MULTILINE);
    private static Pattern sqlParamPattern = Pattern.compile(":([a-zA-Z]\\w+)");
    //private static Pattern sqlCommentPattern = Pattern.compile("/\\*.*?\\*/|--.*?$", Pattern.DOTALL | Pattern.MULTILINE);
    private Iterator<HashMap<String, Object>> iteratorParameters;
    private Boolean firstFlag = true;
    public String server;
    public String database;
    public CommandType commandType = CommandType.Text;
    public String commmandText;
    public SqlParameter[] parameters;

    public SqlCommand(String server, String database, CommandType commandType, String commmandText, HashMap<String, Object> sqlParamMap) {
        init(server, database, commandType, commmandText, sqlParamMap);
    }

    public SqlCommand(String server, String database, CommandType commandType, String commmandText, Object[] params) {
        this.server = server;
        this.database = database;
        this.commandType = commandType;
        if (commandType == CommandType.StoredProcedure) {
            this.commmandText = getCallString(0, commmandText);
        } else {
            this.commmandText = commmandText;
            if (params == null) {
                parameters = new SqlParameter[0];
            } else {
                parameters = new SqlParameter[params.length];
                for (int i = 0; i < params.length; i++) {
                    parameters[i] = new SqlParameter(null, params[i]);
                }
            }
        }
    }

    public SqlCommand(String server, String database, CommandType commandType, String commmandText) {
        init(server, database, commandType, commmandText, null);
    }

    public SqlCommand(String server, String database, CommandType commandType, String commmandText, Iterator<HashMap<String, Object>> iterator) {
        this.iteratorParameters = iterator;
        init(server, database, commandType, commmandText, null);
    }

    private void init(String server, String database, CommandType commandType, String commmandText, HashMap<String, Object> sqlParamMap) {
        this.server = server;
        this.database = database;
        this.commandType = commandType;
        if (commandType == CommandType.StoredProcedure) {
            parameters = new SqlParameter[sqlParamMap.size()];
            this.commmandText = getCallString(sqlParamMap.size(), commmandText);
            int i = 0;
            for (String name : sqlParamMap.keySet()) {
                parameters[i++] = new SqlParameter(name, sqlParamMap.get(name));
            }
        } else {
            commmandText = sqlCommentPattern.matcher(commmandText).replaceAll("\n");
            String[] args = getPrepareSQLarguments(commmandText);
            this.commmandText = args[0];
            parameters = new SqlParameter[args.length - 1];
            if (iteratorParameters == null) {
                for (int i = 1; i < args.length; i++) {
                    if (sqlParamMap == null || !sqlParamMap.containsKey(args[i])) {
                        throw new RuntimeException("Parameter not found:" + args[i]);
                    }
                    parameters[i - 1] = new SqlParameter(args[i], sqlParamMap.get(args[i]));
                }
            } else {
                for (int i = 1; i < args.length; i++) {
                    parameters[i - 1] = new SqlParameter(args[i], null);
                }
            }
        }
    }

    public Boolean nextSQLarguments() {
        if (iteratorParameters == null || !iteratorParameters.hasNext()) {
            return false;
        } else {
            HashMap<String, Object> sqlParamMap = iteratorParameters.next();
            if (firstFlag && this.commandType == CommandType.StoredProcedure) {
                //StoredProcedure input parameters depond on iteratorParameters
                firstFlag = false;
                parameters = new SqlParameter[sqlParamMap.size()];
                this.commmandText = getCallString(sqlParamMap.size(), commmandText);
                int i = 0;
                for (String name : sqlParamMap.keySet()) {
                    parameters[i++] = new SqlParameter(name, sqlParamMap.get(name));
                }
            }

            for (SqlParameter param : parameters) {
                param.setValue(sqlParamMap.get(param.getName()));
            }
            return true;
        }
    }

    private String[] getPrepareSQLarguments(String prepareSQL) {
        LinkedList<String> list = new LinkedList<String>();
        Matcher matcher = sqlParamPattern.matcher(prepareSQL);
        while (matcher.find()) {
            list.add(matcher.group(1));
        }
        prepareSQL = matcher.replaceAll("?");
        list.addFirst(prepareSQL);

        return list.toArray(new String[1]);
    }

    private String getCallString(int parameterCount, String spName) {
        spName = "{call " + spName + "(";
        for (int i = 0; i < parameterCount; i++) {
            if (i == 0) {
                spName += "?";
            } else {
                spName += ",?";
            }
        }
        spName += ")}";
        return spName;
    }
}
