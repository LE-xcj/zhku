package com.example.zhkuapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.zhkuapp.R;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.service.LoginService;
import com.example.zhkuapp.utils.MD5Util;
import com.example.zhkuapp.utils.MyProgressDialog;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.SharePreferenceUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.userID)
    EditText et_userID;

    @Bind(R.id.userPwd)
    EditText et_userPwd;

    @Bind(R.id.showOrhide)
    CheckBox showOrhide;

    // 弹出框
    private MyProgressDialog mDialog;

    //用于结束登录成功后结束loginactivity
    public static LoginActivity context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        context = this;
        initView();
    }


    private void initView() {
        //从sharePreference读取用户的信息
        User user = SharePreferenceUtil.read(this);
        et_userID.setText(user.getUserID());
    }

    public void click(View view) {
        switch (view.getId()) {
            //这里采用环信的登录验证，这是异步操作
            case R.id.login: {
                mDialog = new MyProgressDialog(this,"正在登陆，请稍后...");
                mDialog.show();
                String userID = et_userID.getText().toString().trim();
                String password = et_userPwd.getText().toString().trim();
                if (LoginService.isNull(userID) || LoginService.isNull(password)) {
                    MyToast.show(this, "账号和密码不能为空");
                } else {
                    //md5加密，向环信服务端进行用户验证
                    password = MD5Util.MD5Encode(password);
                    LoginService.login(this, userID, password, mDialog);
                }
            }break;

            case R.id.to_regist: {
                startActivity(new Intent(this, RegistActivity.class));
                clearAll();
            }break;

            case R.id.showOrhide:{
                if (showOrhide.isChecked())
                    et_userPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    et_userPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }break;

            case R.id.forget: {
                clearAll();
                MyToast.show(this, "this is forget");
            }break;
        }
    }

    private void clearAll() {
        et_userID.setText("");
        et_userPwd.setText("");
    }


}
