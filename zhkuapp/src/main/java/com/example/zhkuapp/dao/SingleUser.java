package com.example.zhkuapp.dao;

import com.example.zhkuapp.pojo.User;

import java.util.ArrayList;

/**
 * Created by chujian on 2018/1/2.
 */

public class SingleUser {
    public static User single = new User();

    private SingleUser(){}

    public static void set(String userID, String password){
        single.setUserID(userID);
        single.setPassword(password);
    }

    public static void set(String userName,String sex, String email, String selfIntroduction ,String photo){
        single.setUserName(userName);
        single.setSex(sex);
        single.setEmail(email);
        single.setSelfIntroduction(selfIntroduction);
        single.setPhoto(photo);
    }

    public static void updatePwd(String password){

        single.setPassword(password);
    }

    public static void setSingle(User user){
        single = user;
    }

    public static String getPwd(){
        return single.getPassword();
    }

    public static String getUserID(){
        return single.getUserID();
    }

    public static void setPhoto(String photo){
        single.setPhoto(photo);
    }

}
