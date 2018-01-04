package com.example.zhkuapp.utils;


import com.example.zhkuapp.pojo.User;
import com.google.gson.Gson;


/**
 * Created by chujian on 2018/1/4.
 */

public class MyGsonUtil {

    //注意：Gson用的是反射机制，实体类的属性与json的属性名要一样
    public static User getUser(String strJO){
        User user = null;
        Gson gson = new Gson();
        user = gson.fromJson(strJO,User.class);
        return user;
    }

}
