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
 */
public class ItemFragment extends Fragment implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {


    @Bind(R.id.publish)
    Button publish;

    @Bind(R.id.sbutton)
    Switch sbutton;

    private final int LOST = 0;
    private final int PICK = 1;

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

        //提交添加fragment的事务
        transaction.add(R.id.itemContainer, FragmentFactory.getFragment(false)).commit();
    }

    private void initSwitchButton() {
        sbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment fragment = FragmentFactory.getFragment(isChecked);
                if (fragment.isAdded()){
                    transaction.show(fragment).commit();

                }else{
                   transaction.add(R.id.itemContainer,fragment).commit();
                }
                transaction.hide(FragmentFactory.getFragment(!isChecked));
            }
        });
    }

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

/*            case R.id.userPhoto: {
                //进入聊天界面
                MyToast.show(getActivity(), "this user photo");
            }
            break;*/
        }
    }

    /*
    *右上角菜单项的事件监听
    * 进入发布帖子界面
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Intent intent = new Intent(getActivity(), PublishItemActivity.class);

        intent.putExtra("userID", SingleUser.getUserID());

        switch (item.getItemId()) {
            case R.id.lost_item: {
                intent.putExtra("type",LOST);
                startActivityForResult(intent,LOST);
            }
            break;

            case R.id.pick_item: {
                intent.putExtra("type",PICK);
                startActivityForResult(intent,PICK);
            }
            break;
        }
        return false;
    }


    //处理发布帖子界面返回的结果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //带有数据的返回，也就是发表了帖子
        if (null != data){

            int itemPublishCode = data.getIntExtra("itemPublishCode",-1);
            int itemPhotoCode = data.getIntExtra("itemPhotoCode",-1);

            //更新fragment的显示数据
            if (ItemService.canPublish(itemPublishCode,itemPhotoCode)){

                if (resultCode == 0){
                    LostFragment fragment = (LostFragment)FragmentFactory.getFragment(false);
                    fragment.refresh();
                }else{
                    PickFragment fragment = (PickFragment)FragmentFactory.getFragment(true);
                    fragment.refresh();
                }
            }else{
                MyToast.show(getActivity(),"发布失败");
            }

        }
    }

}
