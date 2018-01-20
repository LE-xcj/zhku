package com.example.zhkuapp.frgment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.SingleUser;
import com.example.zhkuapp.service.ItemService;
import com.example.zhkuapp.utils.FragmentFactory;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.view.PublishItemActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 *
 * 注意：fragment不能在布局文件设置onClick属性
 * 因为：fragment不是activity，绑定onClick属性，得到的点击事件只会传到fragment的父activity
 * 所以，这里就只能实现点击事件的接口
 */
public class ItemFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


    @Bind(R.id.publish)
    Button publish;

    @Bind(R.id.sbutton)
    Switch sbutton;

    //帖子的标志
    private final int LOST = 0;
    private final int PICK = 1;

    //下拉刷新标志
    private final int PULLDOWN = 0;

    //成功
    private final int SUCCESSFUL = 1;

    public ItemFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        //fragment注入
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //初始化右上角的菜单项
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MainActivity.instance.getMenuInflater().inflate(R.menu.item_menu, menu);
    }


    //初始化
    private void init() {

        //给右上角的“+”添加监听事件
        publish.setOnClickListener(this);

        //给开关按钮设置监听事件
        initSwitchButton();

        //获取事务，注意每一次add、show或者其他事务，都需要重新获取transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        //提交添加fragment的事务,默认显示失物帖子,false代表失物
        transaction.add(R.id.itemContainer, FragmentFactory.getFragment(false)).commit();
    }

    //开关按钮的事件监听
    private void initSwitchButton() {

        /*
        *监听开关按钮的状态，
        *       on：true； off：false
         */
        sbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //开启事务
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                //根据当前开关按钮的状态获取对应的fragment
                Fragment fragment = FragmentFactory.getFragment(isChecked);

                //判断是显示还是添加
                if (fragment.isAdded())
                    transaction.show(fragment).commit();
                else
                   transaction.add(R.id.itemContainer,fragment).commit();

                //隐藏另一个fragment
                transaction.hide(FragmentFactory.getFragment(!isChecked));
            }
        });

    }

    /*
    *处理右上角“+”的点击事件，也就是展示两个菜单项
    *       1：我丢了东西
    *       2：我捡到东西
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.publish: {

                //创建弹出式菜单对象（最低版本11）
                PopupMenu popup = new PopupMenu(getActivity(), view);//第二个参数是绑定的那个view

                //获取菜单填充器
                MenuInflater inflater = popup.getMenuInflater();

                //填充菜单
                inflater.inflate(R.menu.item_menu, popup.getMenu());

                //绑定菜单项的点击事件
                popup.setOnMenuItemClickListener(this);

                //显示(这一行代码不要忘记了)
                popup.show();
            }
            break;
        }
    }

    /*
    *右上角菜单项的事件监听
    * 进入发布帖子界面
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        //跳转到发布帖子的界面
        Intent intent = new Intent(getActivity(), PublishItemActivity.class);

        //传递发布者的id，也就是当前登录的用户id
        intent.putExtra("userID", SingleUser.getUserID());

        //设置发布帖子的类型
        switch (item.getItemId()) {

            //丢失物品帖子
            case R.id.lost_item: {
                intent.putExtra("type",LOST);
                startActivityForResult(intent,LOST);
            }break;

            //捡到物品帖子
            case R.id.pick_item: {
                intent.putExtra("type",PICK);
                startActivityForResult(intent,PICK);
            }break;
        }
        return false;
    }


    /*
    *处理发布帖子界面返回的结果
    *
    * resultCode的取值只有两种：（是根据帖子类型决定的）
    *   0：失物帖子
    *   1：拾物帖子
    *
    * 数据data：只有两个参数
    *   itemPublishCode：
    *   itemPhotoCode：
    *   取值：1或-1
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //带有数据的返回，也就是发表了帖子
        if (null != data){

            int itemPublishCode = data.getIntExtra("itemPublishCode",-1);
            int itemPhotoCode = data.getIntExtra("itemPhotoCode",-1);

            /*
            *更新fragment的显示数据
            * 是否可以更新的依据：
            * 两个返回结果都为1
             */
            if (ItemService.canPublish(itemPublishCode,itemPhotoCode)){

                //判断是什么类型的帖子
                if (resultCode == 0){
                    LostFragment fragment = (LostFragment)FragmentFactory.getFragment(false);
                    fragment.refresh(SUCCESSFUL,PULLDOWN);
                    Log.e("this is itemFragment"," refresh lostItem");
                }else{
                    PickFragment fragment = (PickFragment)FragmentFactory.getFragment(true);
                    fragment.refresh(SUCCESSFUL,PULLDOWN);
                    Log.e("this is itemFragment"," refresh pickItem");
                }
            }else{
                MyToast.show(getActivity(),"发布失败");
            }

        }
    }

}
