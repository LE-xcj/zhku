package com.example.zhkuapp.utils;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zhkuapp.R;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by chujian on 2018/1/14.
 */

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MyViewHolder>{

    private List<EMMessage> messages = null;

    private final int RECIEVE = 0;

    public MessageAdaptor(List messages){
        this.messages = messages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        /*
        *根据viewType的值，来判断应该加载那种布局文件
        * 如果是是接收消息，则加载接消息收布局文件
         */
        if (RECIEVE == viewType){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recieve_layout,parent,false);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_layout,parent,false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        EMMessage message = messages.get(position);
        EMMessage.Direct direct = message.direct();

        //这里0代表接收，1代表发送
        return (direct == EMMessage.Direct.RECEIVE ? RECIEVE : 1);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        //获取消息
        EMMessage message = messages.get(position);

        //获取这条消息的发送时间
        long msgTime = message.getMsgTime();

        //第一条消息是与当前系统的时间进行比较
        if (0 == position){

            //判断两个时间差，是否 < 30s
            if (DateUtils.isCloseEnough(msgTime,System.currentTimeMillis())){
                //如果消息是刚发送，则不显示发送时间
                holder.tv_time.setVisibility(View.GONE);
            }else{
                //使用环信第三方的日期工具类，格式化时间
                holder.tv_time.setText(DateUtils.getTimestampString(new Date(msgTime)));

                //将时间视为可视
                holder.tv_time.setVisibility(View.VISIBLE);
            }
        } else{
            //与上一条消息的发送时间进行比较
            if (DateUtils.isCloseEnough(msgTime,messages.get(position-1).getMsgTime())){

                //如果消息是刚发送，则不显示发送时间
                holder.tv_time.setVisibility(View.GONE);

            }else{
                //使用环信第三方的日期工具类，格式化时间
                holder.tv_time.setText(DateUtils.getTimestampString(new Date(msgTime)));

                //将时间视为可视
                holder.tv_time.setVisibility(View.VISIBLE);
            }
        }

        //设置显示消息的内容
        EMTextMessageBody body = (EMTextMessageBody) message.getBody();

        //获取消息内容
        String msg = body.getMessage();
        holder.tv_message.setText(msg);

        //设置发送状态的图标
        if (message.direct() == EMMessage.Direct.SEND){
            EMMessage.Status status = message.status();
            switch (status){
                case SUCCESS:
                    //隐藏发送状态
                    holder.img_statue.setVisibility(View.GONE);
                    break;

                case FAIL:
                    holder.img_statue.setImageResource(R.drawable.send_fail);
                    break;

                case INPROGRESS:
                    holder.img_statue.setImageResource(R.drawable.send_loading);
                    break;
            }
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    ////////////////////////////////////////////////////////////////////////////////////


    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tv_message;
        TextView tv_time;
        ImageView img_statue;


        public MyViewHolder(View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_statue = itemView.findViewById(R.id.status_sned);
        }
    }

}
