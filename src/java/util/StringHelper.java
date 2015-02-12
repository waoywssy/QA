/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Administrator
 */
public class StringHelper {

    private static JSONParser jsonParser = new JSONParser();

    public static void main(String[] agrs) throws Exception {
    }

    public static void toJSONString(String value, StringBuilder sb) {
        if (value == null) {
            sb.append("null");
        } else {
            sb.append("\"");
            int valueLength = value.length();
            for (int i = 0; i < valueLength; i++) {
                char c = value.charAt(i);
                switch (c) {
                    case '\\':
                        sb.append("\\\\");
                        break;
                    case '"':
                        sb.append("\\\"");
                        break;
                    case '/':
                        sb.append("\\/");
                        break;
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\f':
                        sb.append("\\f");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    default:
                        sb.append(c);
                }
            }
            sb.append("\"");
        }
    }

    public static String toJSONString(String value) {
        StringBuilder sb = new StringBuilder();
        toJSONString(value, sb);
        return sb.toString();
    }

    public static Object stringToJson(String str) throws ParseException {
        if (str == null || "".equals(str)) {
            return null;
        }
        return jsonParser.parse(str);
    }

    public static String toString(String[] array, String delimit) {
        boolean first = true;
        if (array.length == 1) {
            return array[0].trim();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (first) {
                first = false;
            } else {
                sb.append(delimit);
            }
            sb.append(array[i].trim());
        }
        return sb.toString();
    }

    public static Object stringToObject(String value, String dataType) {
        if (value == null) {
        } else if ("string".equals(dataType) || "date".equals(dataType) || "sql".endsWith(dataType)) {
            return value;
        } else if ("int".equals(dataType)) {
            return Integer.parseInt(value);
        } else if ("float".equals(dataType)) {
            return Float.parseFloat(value);
        }
        return null;
    }
}
