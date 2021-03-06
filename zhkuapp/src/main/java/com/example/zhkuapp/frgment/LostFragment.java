package com.example.zhkuapp.frgment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
public class LostFragment extends Fragment implements ItemClickInterface{

    @Bind(R.id.main_pull_refresh_lostlv)
    PullToRefreshRecyclerView mainPullRefreshLostlv;

    private RecyclerView recyclerView;
    private MyAdaptor myAdaptor;

    //刷新帖子数量的倍数
    private int time = 0;

    //丢失物品的帖子
    private final int LOST = 0;

    //下拉刷新标志
    private final int PULLDOWN = 0;

    //上拉加载的标志
    private final int PULLUP = 1;

    //成功
    private final int SUCCESSFUL = 1;

    private final int ZERO = 0;

    //刷新的数量
    private final int LENGTH = 5;

    public LostFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lost, container, false);
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
        recyclerView = mainPullRefreshLostlv.getRefreshableView();

        //给recycleview设置线性布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //设置分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        //设置上拉和下拉刷新
        mainPullRefreshLostlv.setMode(PullToRefreshBase.Mode.BOTH);

        //设置适配器，三个参数：上下文、监听器、数据
        myAdaptor = new MyAdaptor(getActivity(), this, ItemContainer.getLostItems());

        //添加适配器
        recyclerView.setAdapter(myAdaptor);

        //设置上下拉监听事件
        initRefresh();
    }

    private void initRefresh() {

        //下拉事件监听
        mainPullRefreshLostlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(final PullToRefreshBase<RecyclerView> refreshView) {

                //time = 0;

                //只是void类型方法
                ItemDao.getItems(ZERO, LOST);

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

                        /*Message msg = new Message();
                        handler.sendMessage(msg);*/

                    }

                }).start();

            }

            //上拉事件监听
            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // 上拉加载监听

                //++time;
                ItemDao.getItems(time+1, LOST);

                // 下拉刷新监听
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int state = ResultVO.getItemChange();

                        while (0 == state) {
                            state = ResultVO.getItemChange();
                        }

                        ResultVO.setItemDefault();

                        refresh(state,PULLUP);

/*                        Message msg = new Message();
                        handler.sendMessage(msg);*/
                    }

                }).start();

            }
        });

    }

    public void refresh(int state, int direction){
        Message msg = new Message();

        msg.what = state;
        msg.obj = direction;

        handler.sendMessage(msg);
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int state = msg.what;
            int direction = (int) msg.obj;

            //操作成功
            if (SUCCESSFUL == state){

                //刷新数据
                myAdaptor.setList(ItemContainer.getLostItems());
                myAdaptor.notifyDataSetChanged();

                //刷新方向，上拉
                if (PULLDOWN == direction){

                    //设置获取倍数
                    time = ZERO;

                    //定位到头部
                    recyclerView.smoothScrollToPosition(ZERO);

                }else{

                    //更新time
                    ++time;

                    //获取item的总数
                    int all = ItemContainer.getLostCount();

                    //定位下一条数据
                    if (all > time*LENGTH)
                        recyclerView.smoothScrollToPosition(time*LENGTH);
                }

            }else{
                //上拉加载失败，time无需变化

                //下拉加载失败,time无需变化

                MyToast.show(getActivity(),"操作失败");
            }

            //停止刷新
            mainPullRefreshLostlv.onRefreshComplete();

        }
    };


    //监听头像的点击事件
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
