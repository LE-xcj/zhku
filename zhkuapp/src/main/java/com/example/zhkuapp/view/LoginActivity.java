package com.example.zhkuapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.service.LoginService;
import com.example.zhkuapp.utils.MD5Util;
import com.example.zhkuapp.utils.MyProgressDialog;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.NetUtil;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.hyphenate.chat.EMClient;

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
        String userID = SingleUser.getUserID();
        if (null != userID)
            et_userID.setText(userID);
    }

    @Override
    public void onBackPressed() {
        /*
        *监听物理键返回点击事件
        *   一）、不登录，要将MainActivity结束
         */
        if (null != MainActivity.instance){
            MainActivity.instance.finish();
        }
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void click(View view) {
        switch (view.getId()) {
            //这里采用环信的登录验证，这是异步操作
            case R.id.login: {

                String userID = et_userID.getText().toString().trim();
                String password = et_userPwd.getText().toString().trim();

                if (LoginService.isNull(userID) || LoginService.isNull(password)) {
                    MyToast.show(this, "账号和密码不能为空");
                } else if (!NetUtil.isNetworkConnected(this)){
                    MyToast.show(this,"网络不可用");
                }else{
                    mDialog = new MyProgressDialog(this,"正在登陆，请稍后...");
                    mDialog.show();
                    //md5加密，向环信服务端进行用户验证
                    password = MD5Util.MD5Encode(password);
                    LoginService.login(this, userID, password, mDialog);
                }
            }break;

            case R.id.to_regist: {
                startActivity(new Intent(this, RegistActivity.class));
            }break;

            case R.id.showOrhide:{
                //显示密码
                if (showOrhide.isChecked())
                    et_userPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                //隐藏密码
                else
                    et_userPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }break;

            case R.id.forget: {
                MyToast.show(this, "this is forget");
            }break;
        }

    }

}
