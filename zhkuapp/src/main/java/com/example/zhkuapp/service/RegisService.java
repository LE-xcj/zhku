package com.example.zhkuapp.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.health.PackageHealthStats;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.zhkuapp.MainActivity;
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

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * Created by chujian on 2018/1/2.
 */

public class RegisService {

    private static MyProgressDialog dialog;

    //判断单个字符串的内容是否为空
    public static boolean isNull(String str){
        if (null == str || "".equals(str))
            return true;
        return false;
    }

    //判断两个字符串内容是否相同
    public static boolean isEquals(String input, String actual){
        return actual.equals(input);
    }

    //密码不小于6位数
    public static boolean isMinLength(String str){
        return (str.length() >= 6);
    }

    //判断字符串的内容是否为空
    public static boolean areNull(String ...strings){
        for (int i=0; i<strings.length; ++i){
            if (isNull(strings[i]))
                return true;
        }
        return false;
    }

    public static void regist(final Context context, final String userID,
                              final String userPwd, MyProgressDialog mDialog){

        dialog = mDialog;
        Log.e("this is registservice ", " r上一句是dialog = mDialog;");

        final Message msg = new Message();
        msg.obj = context;

        Log.e("this regist service "," 下一句就是开启线程进行注册");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //向环信服务器注册用户
                    Log.e("向环信那边发送注册请求", "id:"+userID);
                    EMClient.getInstance().createAccount(userID, userPwd);

                    Log.e("this is regis success"," to huanxin ");
                    msg.what = EMError.EM_NO_ERROR;

                    Log.e("this is regist service","下一句就是设置single");

                    //设置当前登录用户
                    SingleUser.set(userID, userPwd);

                } catch (HyphenateException e) {
                    msg.what = e.getErrorCode();
                }

                //只有当环信那边注册完，才向服务器那边注册
                handler.sendMessage(msg);
            }
        }).start();

        //之前把senMessage放在这里，导致环信注册的信息还没返回就已经把what给送走了
    }




    //环信用户注册成功之后
    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            int errorCode = msg.what;
            Context context = (Context) msg.obj;

            Log.e("this registservice "," 下一句是dismiss进度框");
            //关闭进度框
            dialog.dismiss();

            switch (errorCode) {
                //注册成功
                case EMError.EM_NO_ERROR:

                    Log.e("this is Registservice"," next go //向服务器的数据库插入一条新用户");

                    //向服务器的数据库插入一条新用户,异步操作
                    RegisService.regist(SingleUser.single);

                    Log.e("this is regist service ", "下一句是执行环信登录");

                    //环信登录，异步操作
                    LoginService.login(RegistActivity.instance,SingleUser.getUserID(),SingleUser.getPwd(),0);

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

    //向服务器那边注册用户信息
    public static void regist(User user){
        UserDao.insertUser(user);
    }


}

