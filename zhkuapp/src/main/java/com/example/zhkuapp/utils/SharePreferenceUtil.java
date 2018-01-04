package com.example.zhkuapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.pojo.User;

/**
 * Created by chujian on 2018/1/1.
 */

public class SharePreferenceUtil {
    private static final String FILENAME = "userImfor";

    //如果注销了的话，一定要将password设置为null
    public static void write(Context context,User user){
        SharedPreferences sp = context.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();

        editor.putString("userID",user.getUserID());
        editor.putString("userName",user.getUserName());
        editor.putString("password",user.getPassword());
        editor.putString("sex",user.getSex());
        editor.putString("email",user.getEmail());
        editor.putString("selfIntroduction",user.getSelfIntroduction());
        editor.putString("photo",user.getPhoto());

        editor.commit();
    }

    /*
    *
    * 这里的read方法，只有在app运行的开始才会使用一次
    * 注意：这里读取的密码一定是已经加密后的密码
     */
    public static void read(Context context){

        SharedPreferences sp = context.getSharedPreferences(FILENAME,context.MODE_PRIVATE);

        String userID = sp.getString("userID",null);

        //只要sharePreference里面的ID记录为空，就可以证明是第一次登录
        if (null == userID)
            return;

        //设置ID与密码
        SingleUser.set(userID,sp.getString("password",null));

        //设置其他信息
        SingleUser.set(sp.getString("userName","仲恺人"),
                       sp.getString("sex","男"),
                       sp.getString("email",null),
                       sp.getString("selfIntroduction","这个人很懒，没有自我介绍！"),
                       sp.getString("photo",null)
        );


    }
}
