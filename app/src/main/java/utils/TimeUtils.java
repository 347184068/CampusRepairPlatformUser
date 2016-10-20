package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lenovo on 2016/8/9.
 */
public class TimeUtils {
    public static String transDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date).toString();
    }
    public static String transDate1(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date).toString();
    }
}
