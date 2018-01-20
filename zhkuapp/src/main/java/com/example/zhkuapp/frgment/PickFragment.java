package com.example.zhkuapp.frgment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.ItemContainer;
import com.example.zhkuapp.dao.ItemDao;
import com.example.zhkuapp.dao.ResultVO;
import com.example.zhkuapp.myinterface.ItemClickInterface;
import com.example.zhkuapp.pojo.Item;
import com.example.zhkuapp.utils.MyAdaptor;
import com.example.zhkuapp.utils.MyToast;
import com.example.zhkuapp.view.ChatActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.extras.recyclerview.PullToRefreshRecyclerView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickFragment extends Fragment implements ItemClickInterface{

    @Bind(R.id.main_pull_refresh_picklv)
    PullToRefreshRecyclerView mainPullRefreshPicklv;


    private RecyclerView recyclerView;
    private MyAdaptor myAdaptor;

    private int time = 0;

    //捡到物品的帖子
    private final int PICK = 1;

    //下拉刷新标志
    private final int PULLDOWN = 0;

    //上拉加载的标志
    private final int PULLUP = 1;

    //成功
    private final int SUCCESSFUL = 1;

    private final int ZERO = 0;

    //刷新的数量
    private final int LENGTH = 5;

    public PickFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private void init() {
        //获取recycleView刷新的界面
        recyclerView = mainPullRefreshPicklv.getRefreshableView();

        //给recycleview设置线性布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //设置上拉和下拉刷新
        mainPullRefreshPicklv.setMode(PullToRefreshBase.Mode.BOTH);

        //设置适配器，三个参数：上下文、监听器、数据
        myAdaptor = new MyAdaptor(getActivity(), this, ItemContainer.getPickItems());


        //添加适配器
        recyclerView.setAdapter(myAdaptor);

        //设置上下拉监听事件
        initRefresh();
    }

    private void initRefresh() {

        //下拉事件监听
        mainPullRefreshPicklv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<RecyclerView> refreshView) {

                //当下拉刷新时
                //time = 0;

                ItemDao.getItems(ZERO, PICK);

                // 下拉刷新监听
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        int state = ResultVO.getItemChange();

                        while (ZERO == state) {
                            state = ResultVO.getItemChange();
                        }

                        //还原默认值
                        ResultVO.setItemDefault();

                        //更新界面
                        refresh(state,PULLDOWN);

/*                        Message msg = new Message();
                        handler.sendMessage(msg);*/

                    }

                }).start();

            }

            //上拉事件监听
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {

                // 上拉加载监听
                ItemDao.getItems(time+1, PICK);

                // 上拉刷新监听
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //获取异步请求的状态码
                        int state = ResultVO.getItemChange();

                        /*
                        *不断的循环获取状态码，
                        *           0：默认状态
                        *           1：成功获取数据的状态
                        *           -1：获取失败状态
                         */
                        while (ZERO == state) {
                            state = ResultVO.getItemChange();
                        }

                        //还原状态码的默认值
                        ResultVO.setItemDefault();

                        //更新界面
                        refresh(state,PULLUP);
/*
                        Message msg = new Message();
                        handler.sendMessage(msg);*/
                    }

                }).start();

            }
        });

    }

    //向handler发送更新消息
    public void refresh(int state , int direction){
        Message msg = new Message();
        msg.what = state;
        msg.obj = direction;

        handler.sendMessage(msg);
    }

    /*
    *更新界面，因为这里刷新帖子是联网操作，需要异步加载
    * 所以这里更新需要handler机制
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int state = msg.what;
            int direction = (int) msg.obj;

            if (SUCCESSFUL == state){

                //获取帖子容器
                myAdaptor.setList(ItemContainer.getPickItems());

                //监听适配器中数据的改变，并及时更新
                myAdaptor.notifyDataSetChanged();

                //下拉刷新
                if (PULLDOWN == direction){

                    //更新time的值
                    time = ZERO;

                    //定位到首部
                    recyclerView.smoothScrollToPosition(ZERO);

                }else{

                    //更新time的值
                    ++time;

                    //获取帖子的总数
                    int count = ItemContainer.getPickCount();

                    //是否可以定位到下一条
                    if (count > time*LENGTH)
                        recyclerView.smoothScrollToPosition(time*LENGTH);

                }

            }else{
                MyToast.show(getActivity(),"操作失败");
            }

            //停止显示刷新的展示
            mainPullRefreshPicklv.onRefreshComplete();

        }
    };


    //处理用户点击帖子发布者头像的操作
    @Override
    public void itemClik(View view, int position) {

        //跳转到聊天界面
        Item item = ItemContainer.getPickItem(position);
        Intent intent = new Intent(getActivity(), ChatActivity.class);

        //传递用户id（发送信息的标志）、用户名的参数
        intent.putExtra("userID",item.getUserID());
        intent.putExtra("userName",item.getUserName());

        //跳转
        startActivity(intent);
    }
}
