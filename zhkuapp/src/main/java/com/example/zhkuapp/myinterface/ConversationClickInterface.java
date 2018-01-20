package com.example.zhkuapp.myinterface;

/**
 * Created by chujian on 2018/1/14.
 */

/*
* 会话点击事件的回调接口
*
* */
public interface ConversationClickInterface {

    //每一个会话的item被点击之后就会回调
    void conversationClick(String conversationID);

}
