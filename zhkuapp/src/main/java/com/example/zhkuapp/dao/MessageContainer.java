package com.example.zhkuapp.dao;

import com.hyphenate.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chujian on 2018/1/14.
 */

public class MessageContainer {

    private static List<EMMessage> messages = new ArrayList<>();


    public static void addMessage(EMMessage message){
        messages.add(message);
    }

    public static List<EMMessage> getMessages(){
        return messages;
    }

    public static void addMessage(List<EMMessage> list){
        messages.addAll(list);
    }


}
