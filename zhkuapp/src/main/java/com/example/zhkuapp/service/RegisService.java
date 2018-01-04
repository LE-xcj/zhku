package com.example.zhkuapp.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.health.PackageHealthStats;
import android.util.Log;
import android.widget.Toast;

import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.utils.MD5Util;
import com.example.zhkuapp.utils.MyProgressDialog;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.view.LoginActivity;
import com.example.zhkuapp.view.RegistActivity;
import com.example.zhkuapp.view.SetImforActivity;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by chujian on 2018/1/2.
 */

public class RegisService {

    private static MyProgressDialog dialog;

    public static boolean isNull(String str){
        if (null == str || "".equals(str))
            return true;
        return false;
    }

    public static boolean isEquals(String input, String actual){
        return actual.equals(input);
    }

    public static boolean isMinLength(String str){
        return (str.length() >= 6);
    }

    public static boolean areNull(String ...strings){
        for (int i=0; i<strings.length; ++i){
            if (isNull(strings[i]))
                return true;
        }
        return false;
    }

    public static void regist(final Context context, final String userID, final String userPwd, MyProgressDialog mDialog){
        dialog = mDialog;
        final Message msg = new Message();
        msg.obj = context;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //向环信服务器注册用户
                    EMClient.getInstance().createAccount(userID, userPwd);
                    msg.what = EMError.EM_NO_ERROR;

                } catch (HyphenateException e) {
                    msg.what = e.getErrorCode();

                }
            }
        }).start();

        //设置当前登录用户
        SingleUser.set(userID, userPwd);
        
        handler.sendMessage(msg);
    }

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //关闭进度框
            dialog.dismiss();
            /*if (!RegistActivity.instance.isFinishing()) {
            }*/
            int errorCode = msg.what;
            Context context = (Context) msg.obj;
            switch (errorCode) {
                //注册成功
                case EMError.EM_NO_ERROR:
                    MyToast.show(context,"注册成功！");

                    Log.e("this is Registservice"," next go //向服务器的数据库插入一条新用户");
                    //向服务器的数据库插入一条新用户
                    RegisService.regist(SingleUser.single);

                    //关闭登录界面
                    LoginActivity.context.finish();

                    //关闭注册界面
                    RegistActivity.instance.finish();


                    Log.e("this is Registsevice"," next go 跳转到完善信息的界面");
                    //跳转到完善信息界面
                    context.startActivity(new Intent(context,SetImforActivity.class));
                    break;
                // 网络错误
                case EMError.NETWORK_ERROR:
                    MyToast.show(context,"网络错误");
                    break;
                // 用户已存在
                case EMError.USER_ALREADY_EXIST:
                    MyToast.show(context,"用户已存在");
                    break;
                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                case EMError.USER_ILLEGAL_ARGUMENT:
                    MyToast.show(context,"参数不合法");
                    break;
                // 服务器未知错误
                case EMError.SERVER_UNKNOWN_ERROR:
                    MyToast.show(context,"服务器未知错误");
                    break;
                case EMError.USER_REG_FAILED:
                    MyToast.show(context,"账户注册失败");
                    break;
                default:
                    MyToast.show(context,"注册失败，未知错误");
                    break;
            }
        }
    };

    public static void regist(User user){
        UserDao.insertUser(user);
    }


}

