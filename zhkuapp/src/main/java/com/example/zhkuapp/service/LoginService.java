package com.example.zhkuapp.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.utils.MD5Util;
import com.example.zhkuapp.utils.MyProgressDialog;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.example.zhkuapp.view.LoginActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;

/**
 * Created by chujian on 2018/1/2.
 */

public class LoginService {
    private static MyProgressDialog dialog;

    private static String guserID = "";

    public static boolean isNull(String str){
        if(null == str || "".equals(str))
            return true;
        return false;
    }

    private static Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            dialog.dismiss();
            int flag = msg.what;
            Context context = (Context) msg.obj;
            switch (flag) {
                case EMError.EM_NO_ERROR:

                    //设置single的信息,向服务器那边获取
                    Log.e("this is login service","next to go User.DaoselectUser(userID)");
                    UserDao.selectUser(guserID);

                    MyToast.show(context,"登录成功");

                    //记录登录
                    SharePreferenceUtil.write(LoginActivity.context,SingleUser.single);

                    //关闭登录界面,显示主界面
                    LoginActivity.context.finish();

                    break;
                // 网络异常 2
                case EMError.NETWORK_ERROR:
                    MyToast.show(context,"网络错误");
                    break;
                // 无效的用户名 101
                case EMError.INVALID_USER_NAME:
                    MyToast.show(context,"无效的用户名");
                    break;
                // 无效的密码 102
                case EMError.INVALID_PASSWORD:
                    MyToast.show(context,"无效的密码");
                    break;
                // 用户认证失败，用户名或密码错误 202
                case EMError.USER_AUTHENTICATION_FAILED:
                    MyToast.show(context,"用户名与密码不匹配");
                    break;
                // 用户不存在 204
                case EMError.USER_NOT_FOUND:
                    MyToast.show(context,"用户不存在");
                    break;
                // 无法访问到服务器 300
                case EMError.SERVER_NOT_REACHABLE:
                    MyToast.show(context,"无法访问到服务器");
                    break;
                // 等待服务器响应超时 301
                case EMError.SERVER_TIMEOUT:
                    MyToast.show(context,"等待服务器响应超时");
                    break;
                // 服务器繁忙 302
                case EMError.SERVER_BUSY:
                    MyToast.show(context,"服务器繁忙");
                    break;
                // 未知 Server 异常 303 一般断网会出现这个错误
                case EMError.SERVER_UNKNOWN_ERROR:
                    MyToast.show(context,"未知的服务器异常");
                    break;
                default:
                    MyToast.show(context,"登录失败，其他未知原因");
                    break;
            }
        }
    };

    /**
     * 登录方法
     */
    public static void login(final Context context, final String userID, final String password, final MyProgressDialog mDialog) {
        dialog = mDialog;

        EMClient.getInstance().login(userID, password, new EMCallBack() {
            /**
             * 登陆成功的回调
             */
            @Override
            public void onSuccess() {
                // 加载所有会话到内存
                EMClient.getInstance().chatManager().loadAllConversations();
                // 加载所有群组到内存，如果使用了群组的话
                // EMClient.getInstance().groupManager().loadAllGroups();

                guserID = userID;
                Log.e("this loginService ","上一句是将guserID赋值了");

                /*//设置single的信息,向服务器那边获取
                Log.e("this is login service","next to go User.DaoselectUser(userID)");
                UserDao.selectUser(userID);*/


                Message msg = new Message();
                msg.what = EMError.EM_NO_ERROR;
                msg.obj = context;
                handler.sendMessage(msg);

            }

            /**
             * 登陆错误的回调
             *
             * @param i
             * @param s
             */
            @Override
            public void onError(final int i, final String s) {
                //updateUI(context,i);
                Message msg = new Message();
                msg.what = i;
                msg.obj = context;
                handler.sendMessage(msg);
            }

            @Override
            public void onProgress(int i, String s) {

            }

        });

    }

    public static void login(final Context context,final String userID, final String password){
        EMClient.getInstance().login(userID, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                // 加载所有会话到内存
                EMClient.getInstance().chatManager().loadAllConversations();
            }

            @Override
            public void onError(int i, String s) {
                Message msg = new Message();
                msg.what = i;
                msg.obj = context;
                handler.sendMessage(msg);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

}
