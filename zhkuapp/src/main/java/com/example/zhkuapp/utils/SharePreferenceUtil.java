package com.example.zhkuapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.zhkuapp.pojo.User;

/**
 * Created by chujian on 2018/1/1.
 */

public class SharePreferenceUtil {
    private static final String FILENAME = "userImfor";

    public static void write(Context context,User user){
        SharedPreferences sp = context.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("userID",user.getUserID());
        editor.putString("userName",user.getUserName());
        editor.putString("password",user.getPassword());
        editor.putString("sex",user.getSex());
        editor.putString("selfIntroduction",user.getSelfIntroduction());
        editor.putString("photo",user.getPhoto());

        editor.commit();
    }

    public static User read(Context context){

        SharedPreferences sp = context.getSharedPreferences(FILENAME,context.MODE_PRIVATE);
        User user = new User();

        user.setUserID(sp.getString("userID",null));
        user.setUserName(sp.getString("userName",null));
        user.setPassword(sp.getString("password",null));
        user.setSex(sp.getString("sex","男"));
        user.setSelfIntroduction(sp.getString("selfIntroduction","这个人很懒，没有自我介绍！"));
        user.setPhoto(sp.getString("photo",null));

        return user;
    }
}
