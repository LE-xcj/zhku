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
    private final int TYPE = 1;

    public PickFragment() {
    }


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
                time = 0;

                //ItemContainer.clearPick();

                ItemDao.getItems(time, TYPE);

                // 下拉刷新监听
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int state = ResultVO.getItemChange();

                        while (0 == state) {
                            state = ResultVO.getItemChange();
                        }

                        ResultVO.setItemDefault();
                        Message msg = new Message();
                        handler.sendMessage(msg);

                    }

                }).start();

            }

            //上拉事件监听
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // 上拉加载监听
                ++time;
                ItemDao.getItems(time, TYPE);

                // 下拉刷新监听
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int state = ResultVO.getItemChange();

                        while (0 == state) {
                            state = ResultVO.getItemChange();
                        }

                        ResultVO.setItemDefault();
                        Message msg = new Message();
                        handler.sendMessage(msg);
                    }

                }).start();

            }
        });

    }

    public void refresh(){
        Message msg = new Message();
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            myAdaptor.setList(ItemContainer.getPickItems());
            myAdaptor.notifyDataSetChanged();

            mainPullRefreshPicklv.onRefreshComplete();

        }
    };


    @Override
    public void itemClik(View view, int position) {
        Item item = ItemContainer.getLostItem(position);
        MyToast.show(getActivity(),"this is itemClick position"+position+"and the userID is " + item.getUserID());

        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("userID",item.getUserID());
        intent.putExtra("userName",item.getUserName());
        startActivity(intent);
    }
}
