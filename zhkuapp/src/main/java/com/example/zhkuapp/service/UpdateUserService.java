package com.example.zhkuapp.service;

import android.util.Log;

import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.pojo.User;

/**
 * Created by chujian on 2018/1/3.
 */

public class UpdateUserService {


    /*
        更新用户信息
        在服务器的数据库更新完用户的信息，一定要及时更新手机内存运行的single变量
     */
    public static void update(User user){
        UserDao.updateUser(user);
    }

    //更新密码
    public static void update(String userID, String newPwd, String oldPwd){

    }

    public static boolean usernameIsNull(String userName){
        if (null == userName || "".equals(userName))
            return true;
        return false;
    }

    public static boolean isPreviousEmail(String p_email, String n_email){

        return true;
    }
}
