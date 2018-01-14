package com.example.zhkuapp.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhkuapp.R;
import com.example.zhkuapp.myinterface.ConversationClickInterface;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by chujian on 2018/1/14.
 */

public class ConversationAdaptor extends RecyclerView.Adapter<ConversationAdaptor.MyViewHolder>{

    private List<EMConversation> conversations;

    private ConversationClickInterface listener;

    public void setConversations(List<EMConversation> conversations){
        this.conversations = conversations;
    }

    public ConversationAdaptor(List<EMConversation> conversations,ConversationClickInterface listener){
        this.conversations = conversations;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //先获取会话
        final EMConversation conversation = conversations.get(position);

        //获取会话的id
        final String userID = conversation.conversationId();

        //在获取会话的最后一条message
        EMMessage lastMessage = conversation.getLastMessage();

        //获取消息的消息体
        EMTextMessageBody body = (EMTextMessageBody) lastMessage.getBody();

        //获取消息体的消息内容
        String msg = body.getMessage();

        //再获取最后一条消息的时间
        long msgTime = lastMessage.getMsgTime();

        //获取未读消息的数量
        int count = conversation.getUnreadMsgCount();

        //设置用户名
        holder.tv_userName.setText(userID);

        //设置内容
        holder.tv_message.setText(msg);

        //设置时间
        holder.tv_time.setText(DateUtils.getTimestampString(new Date(msgTime)));

        //设置未读消息数量
        holder.tv_message_count.setText(""+count);

        //给每一个item设置监听器
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.conversationClick(userID);

            }
        });
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_userName, tv_message, tv_time, tv_message_count;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_message_count = itemView.findViewById(R.id.tv_message_count);
        }
    }

}
