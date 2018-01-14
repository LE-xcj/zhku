package com.example.zhkuapp.utils;


import android.util.Log;

import com.example.zhkuapp.pojo.Item;
import com.example.zhkuapp.pojo.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


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

    public static List<Item> getItems(String strArray){
        List<Item> list = null;
        Gson gson = new Gson();
        Log.e("strArray",strArray);
        Type listType = new TypeToken<List<Item>>(){}.getType();
        list = gson.fromJson(strArray,listType);
        return list;
    }

    public static String item2Json(Item item){
        Gson gson = new Gson();
        String json = gson.toJson(item);
        Log.e("the json is ",json);
        return json;
    }

}
