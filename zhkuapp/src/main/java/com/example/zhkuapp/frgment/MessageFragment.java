package com.example.zhkuapp.frgment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.myinterface.ConversationClickInterface;
import com.example.zhkuapp.utils.ConversationAdaptor;
import com.example.zhkuapp.view.ChatActivity;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment implements ConversationClickInterface{


    @Bind(R.id.message_recycleview)
    RecyclerView messageRecycleview;

    private List<EMConversation> conversations;

    private ConversationAdaptor myAdaptor;

    public MessageFragment() {}

    private void init() {

        conversations = new ArrayList<>();

        //布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        //设置管理器
        messageRecycleview.setLayoutManager(layoutManager);

        //初始化adaptor
        myAdaptor = new ConversationAdaptor(conversations,this);

        //设置adaptor
        messageRecycleview.setAdapter(myAdaptor);

    }


    //获取所有会话
    public void getConversations(){
        //获取所有会话
        Map<String,EMConversation> allConversations = EMClient.getInstance().chatManager().getAllConversations();

        for (String key : allConversations.keySet()){
            Log.e("this is messagefragment","the key is "+ key);
        }

        //获取map的value值，也就是获取所有conversation
        Collection<EMConversation> values = allConversations.values();

        //将会话集合转变成list的形式存储
        List<EMConversation> conversationList = new ArrayList<>(values);

        //自定义排序规则，对会话进行排序
        Collections.sort(conversationList, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                //根据最近的消息的时间进行排序
                return (int) (o2.getLastMessage().getMsgTime() - o1.getLastMessage().getMsgTime());
            }
        });

        //添加会话集合
        setConversations(conversationList);

        //更新界面
        updateConversationView();
    }


    //添加会话
    private void setConversations( List<EMConversation> list){
        conversations = list;
        myAdaptor.setConversations(conversations);
    }

    //更新界面
    private void updateConversationView(){
        myAdaptor.notifyDataSetChanged();
    }


    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);

        init();

        initMessageListener();

        getConversations();

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //监听会话的点击事件
    @Override
    public void conversationClick(String conversationID) {
        if (null != conversationID){
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("userID",conversationID);
            intent.putExtra("userName",conversationID);
            startActivity(intent);
        }
    }

    private void initMessageListener() {

        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                Log.e("this is message ","fragemtn received");
                getConversations();
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageRecalled(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });

    }

}
