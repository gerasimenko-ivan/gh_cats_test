package ot.webtest.framework.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpHelper {

    public static String getSubstring(String pattern, String fromText) {
        Pattern patternCompile = Pattern.compile(pattern);
        Matcher matcher = patternCompile.matcher(fromText);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }
}
