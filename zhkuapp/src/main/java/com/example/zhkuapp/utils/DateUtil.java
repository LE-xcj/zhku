package com.example.zhkuapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chujian on 2018/1/11.
 */

public class DateUtil {
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public static String toDateStr(){
        String date = df.format(new Date());
        return date;
    }
}
