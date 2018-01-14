package com.example.zhkuapp.utils;

import java.util.UUID;

/**
 * Created by chujian on 2018/1/12.
 */

public class UUIDUtil {

    public static String getUuid(){
        UUID uuid = UUID.randomUUID();
        String uuidStr =  uuid.toString();
        uuidStr = uuidStr.replace("-", "");
        return uuidStr;
    }

}
