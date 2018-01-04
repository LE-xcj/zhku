package com.example.zhkuapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.service.LoginService;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.example.zhkuapp.view.LoginActivity;
import com.example.zhkuapp.view.RegistActivity;
import com.hyphenate.EMCallBack;

import com.hyphenate.chat.EMClient;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;
        /*
        * 这里应该补充：
        *   当sharePreference提取的信息为空，或者密码为空，那么就跳转到登录界面;如果用户ID不为空，就显示用户ID
        *   否则就直接进入主界面
        *
        *   注意：注销之后要把sharePreference记录的密码信息清空
        * */

        //初始化single
        SharePreferenceUtil.read(this);
        if (null == SingleUser.getPwd()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else{
            LoginService.login(MainActivity.this,SingleUser.getUserID(),SingleUser.getPwd());
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @Override
    protected void onDestroy() {
        signOut();
        super.onDestroy();
    }

    /**
     * 退出登录
     */
    private void signOut() {
        // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("lzan13", "logout success");
                // 调用退出成功，结束app
                //finish();
            }

            @Override
            public void onError(int i, String s) {
                Log.i("lzan13", "logout error " + i + " - " + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }
}
