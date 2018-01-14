package com.example.zhkuapp.frgment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.utils.NetUtil;
import com.example.zhkuapp.utils.PhotoUtil;
import com.example.zhkuapp.utils.SDCardUtil;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.example.zhkuapp.view.EditActivity;
import com.example.zhkuapp.view.LoginActivity;
import com.example.zhkuapp.view.ProtectActivity;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.loopj.android.image.SmartImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonlFragment extends Fragment implements View.OnClickListener {

    @Bind(R.id.img_userphoto)
    SmartImageView imgUserphoto;

    @Bind(R.id.t_username)
    TextView tUsername;

    @Bind(R.id.imge_sex)
    ImageView imgeSex;

    @Bind(R.id.myhistory)
    RelativeLayout myhistory;

    @Bind(R.id.et_edit)
    EditText etEdit;

    @Bind(R.id.et_protect)
    EditText etProtect;

    @Bind(R.id.loginout)
    EditText loginout;



    private MainActivity context = null;
    private final String BOY = "男";
    private final String GIRL = "女";

    public PersonlFragment() {}

    public PersonlFragment(MainActivity context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //获取fragment的视图
        View view = inflater.inflate(R.layout.fragment_personl, container, false);

        //fragment注入
        ButterKnife.bind(this, view);

        return view;
    }

    private void initClick() {
        myhistory.setOnClickListener(this);
        etEdit.setOnClickListener(this);
        etProtect.setOnClickListener(this);
        loginout.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("fragmet onactivitycreat", "下一句是添加监听事件");
        initClick();
        initPersonl(SingleUser.single);
    }

    public void initPersonl(User user) {
        if (null == user)
            return;

        Bitmap photo = PhotoUtil.getBitmap(SDCardUtil.getAbsolutePath(SingleUser.getPhoto(),true));
        String userName = user.getUserName();
        Bitmap sex = null;

        //SD卡有头像的存储
        if (null != photo) {
            imgUserphoto.setImageBitmap(photo);
        } else if (NetUtil.isNetworkAvailable(MainActivity.instance)) {
            //如果有网络，那就到服务器那边下载，并保存在SD卡里
            imgUserphoto.setImageUrl(UserDao.getPhotoUrl(SingleUser.getUserID()));
        } else {
            //使用默认的图片
            imgUserphoto.setImageBitmap(PhotoUtil.getBitmap());
        }

        tUsername.setText(userName);

        if (BOY.equals(user.getSex()))
            sex = PhotoUtil.getBitmap(0);
        else if (GIRL.equals(user.getSex()))
            sex = PhotoUtil.getBitmap(1);


        imgeSex.setImageBitmap(sex);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.myhistory: {
                Log.e("this is personlfragment", " myhistory");
            }
            break;

            case R.id.et_edit: {
                startActivityForResult(new Intent(context, EditActivity.class),0);
            }
            break;

            case R.id.et_protect: {
                startActivity(new Intent(MainActivity.instance, ProtectActivity.class));
            }
            break;

            case R.id.loginout: {
                SingleUser.setPasswordNull();
                SharePreferenceUtil.write(MainActivity.instance,SingleUser.single);

                new AlertDialog.Builder(context)
                        .setMessage("确定要注销吗？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                signOut();
                                startActivity(new Intent(context, LoginActivity.class));
                            }
                        }).create().show();

            }break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (2 == resultCode){
            if (null != data){
                int photoCode = data.getIntExtra("photoCode",-1);
                int basicImforCode = data.getIntExtra("basicImforCode",-1);
                if (1 == photoCode || 1 == basicImforCode)
                    initPersonl(SingleUser.single);
            }
        }
    }

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
