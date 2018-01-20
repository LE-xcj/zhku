package com.example.zhkuapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.service.LoginService;
import com.example.zhkuapp.utils.FragmentFactory;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.utils.NetUtil;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.example.zhkuapp.view.LoginActivity;
import com.hyphenate.EMCallBack;

import com.hyphenate.chat.EMClient;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener;

public class MainActivity extends AppCompatActivity {

    public static MainActivity instance;

    private  PageNavigationView tab;

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
        } else if (NetUtil.isNetworkConnected(this)){
            LoginService.login(MainActivity.this,SingleUser.getUserID(),SingleUser.getPwd(),1);
        }else{
            MyToast.show(this,"网络有问题");
        }
        init();

    }



    private void init(){
        setContentView(R.layout.activity_main);

        initIItemFragment();

        //底部导航栏
        tab = (PageNavigationView) findViewById(R.id.tab);
        NavigationController navigationController = tab.material()
                .addItem(R.drawable.lost, "失物")
                .addItem(R.drawable.classroom, "找课室")
                .addItem(R.drawable.message, "消息")
                .addItem(R.drawable.personal, "个人")
                .build();

        //给底部导航栏设置监听事件
        navigationController.addTabItemSelectedListener(new OnTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                //选中事件处理

                //获取fragment的管理，然后开启事务
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                //获取对应的fragment
                Fragment fragment = FragmentFactory.getFragment(index);

                //如果添加了，就显示
                if(fragment.isAdded())
                    transaction.show(fragment).commit();
                //否则就添加
                else
                    transaction.add(R.id.body,fragment).commit();

                //隐藏上一个显示的fragment
                transaction.hide(FragmentFactory.getFragment(old));
            }

            @Override
            public void onRepeat(int index) {
                //重复选中事件处理
            }
        });

    }

    public void initIItemFragment(){

        //开启事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        Fragment fragment = FragmentFactory.getFragment(0);
        if (fragment.isAdded()){
            //显示fragment
            transaction.show(fragment).commit();
        } else {
            //添加底部导航栏第一个界面
            transaction.add(R.id.body,fragment).commit();
        }
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
        if (EMClient.getInstance().isLoggedInBefore()){
            Log.e("this is mainactivity"," signOut");
            // 调用sdk的退出登录方法，第一个参数表示是否解绑推送的token，没有使用推送或者被踢都要传false
            EMClient.getInstance().logout(false, new EMCallBack() {
                @Override
                public void onSuccess() {
                    // 调用退出成功，结束app
                    //finish();
                }

                @Override
                public void onError(int i, String s) {
                }

                @Override
                public void onProgress(int i, String s) {

                }
            });
        }
    }

}
