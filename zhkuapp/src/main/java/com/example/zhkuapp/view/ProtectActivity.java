package com.example.zhkuapp.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UserDao;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProtectActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_userID)
    EditText etUserID;
    @Bind(R.id.et_email)
    EditText etEmail;
    @Bind(R.id.et_password)
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_protect);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        String userID = "账号 : " + SingleUser.getUserID();
        String email = SingleUser.single.getEmail();
        if (null == email || "".equals(email))
            email = "邮箱 : 暂为绑定邮箱";
        else
            email = "邮箱 : " + email;

        etUserID.setText(userID);
        etEmail.setText(email);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProtectActivity.this.finish();
            }
        });
    }

    public void click(View view){
        if (R.id.et_email==view.getId()){
            bindEmail(etEmail.getText().toString(),etUserID.getText().toString());
        }else if(R.id.et_password == view.getId()){

        }
    }

    private void bindEmail(String previousEmail,String userID) {

        View view = getLayoutInflater().inflate(R.layout.email_dialog,null);

        final EditText et_bing_email = view.findViewById(R.id.bing_email);
        et_bing_email.setText(previousEmail);

        new AlertDialog.Builder(this)
                .setTitle("绑定邮箱")
                .setView(view)
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String email = et_bing_email.getText().toString().trim();

                    }
                }).create().show();
    }


}
