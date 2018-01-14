package com.example.zhkuapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.service.RegisService;
import com.example.zhkuapp.utils.MD5Util;
import com.example.zhkuapp.utils.MyProgressDialog;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.NetUtil;
import com.example.zhkuapp.utils.VCodeUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistActivity extends AppCompatActivity {


    @Bind(R.id.userID)
    EditText et_userID;

    @Bind(R.id.userPwd)
    EditText et_userPwd;

    @Bind(R.id.surePwd)
    EditText et_surePwd;

    @Bind(R.id.vcode)
    EditText et_vcode;

    @Bind(R.id.code)
    ImageView code;

    @Bind(R.id.returnLogin)
    TextView returnLogin;

    //验证码
    private String trueCode;
    private MyProgressDialog dialog;
    public static RegistActivity instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        ButterKnife.bind(this);
        instance = this;

        //第一次初始化验证码
        trueCode = VCodeUtil.createCode();
        code.setImageBitmap(VCodeUtil.createBitmap(trueCode));
    }

    public void click(View view) {
        switch (view.getId()) {

            case R.id.regist: {
                String userID = et_userID.getText().toString().trim();
                String password = et_userPwd.getText().toString().trim();
                String surePwd = et_surePwd.getText().toString().trim();
                String vcode = et_vcode.getText().toString().trim();

                //先判断网路是否可用
                if (!NetUtil.isNetworkConnected(this))
                    MyToast.show(this,"当前网络有问题！");
                //判断输入是否为空
                else if (RegisService.areNull(userID,password,surePwd))
                    MyToast.show(this,"输入框不能为空！");
                else if(!RegisService.isMinLength(password))
                    MyToast.show(this,"密码长度不能小于6位！");
                //判断两次密码是否一致
                else if (!RegisService.isEquals(password,surePwd))
                    MyToast.show(this,"两次输入的密码不一致，请重新输入！");
                //验证码是否一致
                else if (!RegisService.isEquals(vcode,trueCode))
                    MyToast.show(this,"验证码错误！");
                else{
                    dialog = new MyProgressDialog(this,"正在注册....");
                    dialog.show();

                    //md5加密
                    password = MD5Util.MD5Encode(password);

                    //环信用户注册
                    RegisService.regist(this,userID,password,dialog);

                }
            }break;

            case R.id.returnLogin: {
                finish();
                return;
            }

            case R.id.code:{
                trueCode = VCodeUtil.createCode();
                Log.e("code",trueCode);
                code.setImageBitmap(VCodeUtil.createBitmap(trueCode));
            }break;

        }

    }
}
