package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lenovo on 2016/7/23.
 */
public class StringsUtils {
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    public static boolean isChinese(String input) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
        Matcher m = p.matcher(input);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

}
