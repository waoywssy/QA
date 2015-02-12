/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Administrator
 */
public class PatternHelper {

    public static final int PATTERN_OPTIONS = Pattern.CASE_INSENSITIVE | Pattern.COMMENTS | Pattern.DOTALL | Pattern.UNIX_LINES | Pattern.MULTILINE;

    public static String getMatcherValue(Matcher matcher, String groupName) {
        matcher.reset();
        if (matcher.find()) {
            return matcher.group(groupName).trim();
        } else {
            return null;
        }
    }

    public static String getValue(Pattern pattern, String groupName, String inputString) {
        Matcher matcher = pattern.matcher(inputString);
        if (matcher.find()) {
            return matcher.group(groupName).trim();
        } else {
            return null;
        }
    }
}
