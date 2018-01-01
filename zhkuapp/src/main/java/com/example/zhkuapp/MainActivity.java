package com.example.zhkuapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.example.zhkuapp.view.LoginActivity;
import com.hyphenate.EMCallBack;

import com.hyphenate.chat.EMClient;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    // 弹出框
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 判断sdk是否登录成功过，并没有退出和被踢，否则跳转到登陆界面
        if (!EMClient.getInstance().isLoggedInBefore()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
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
