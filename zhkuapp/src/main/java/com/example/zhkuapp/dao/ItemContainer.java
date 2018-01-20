package com.example.zhkuapp.dao;

import android.util.Log;

import com.example.zhkuapp.pojo.Item;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by chujian on 2018/1/8.
 *
 * 存储丢失物品帖子、捡到物品帖子
 *
 * 这里设置lostItems、pickItems容器是因为：
 *          每一次刷新都是从服务那边获取数据的，然后服务器那边将数据打包成json数组的格式返回给客户端
 *          客户端解析之后得到的是List<Item>类型
 *
 *          说明：
 *          然而，如果是上拉刷新，则需要设置帖子容器即可；
 *          但是，如果是下拉加载，那么就需要将数据add到容器中，而不是set
 *
 *
 */

public class ItemContainer {

    private static final int FIRST = 0;

    //存储丢失物品帖子
    private static List<Item> lostItems = new LinkedList<>();

    //存储捡到物品帖子
    private static List<Item> pickItems = new LinkedList<>();

    //获取lostItems容器
    public static List<Item> getLostItems() {
        return lostItems;
    }

    //设置lostItems容器
    public static void setLostItems(List<Item> items) {
        lostItems = items;
    }

    //设置pickItems容器
    public static void setPickItems(List<Item> items){
        pickItems = items;
    }

    //获取pickItems容器
    public static List<Item> getPickItems() {
        return pickItems;
    }

    public static int getLostCount(){
        return lostItems.size();
    }

    public static int getPickCount(){
        return pickItems.size();
    }

    //往丢失帖子容器添加数据，用于刷新
    public static void addLost(List<Item> list){
        lostItems.addAll(list);
    }

    //往捡到容器添加数据，用于刷新
    public static void addPick(List<Item> list){
        pickItems.addAll(list);
    }

    //往容器首部，添加一条丢失帖子，用于用户发布丢失帖子
    public static void addLost(Item item){
        lostItems.add(FIRST,item);
    }

    //往容器首部，添加一条捡到帖子，用于用户发布捡到帖子
    public static void addPick(Item item){
        pickItems.add(FIRST,item);
    }

    //清空丢失帖子容器中的数据，基本用于上拉刷新时，重新加载数据
    public static void clearLost(){
        lostItems.clear();
    }

    //同上
    public static void clearPick(){
        pickItems.clear();
    }

    //获取容器中某个特定位置的帖子
    public static Item getLostItem(int position){
        return lostItems.get(position);
    }

    //同上
    public static Item getPickItem(int position){
        return pickItems.get(position);
    }

    public static void addLostFirst(List<Item> list){
        lostItems.addAll(0,list);
    }

    public static void addPickFirst(List<Item> list){
        pickItems.addAll(0,list);
    }

    public static int findLostLastItemID(){
        int itemID = 0;

        if (null != lostItems && lostItems.size() > 0){
            Integer temp = lostItems.get(lostItems.size()-1).getId();
            if (null != temp)
                itemID = temp;
        }

        return itemID;
    }

    public static int findPickLastItemID(){
        Integer itemID = 0;
        if (null != pickItems && pickItems.size() > 0){
            Integer temp = pickItems.get(pickItems.size()-1).getId();
            if (null != temp)
                itemID = temp;
        }
        return itemID;
    }

    public static int getLostItemID(){
        int itemID = 0;
        if (null != lostItems && lostItems.size() > 0)
            for (Item item: lostItems){
                Integer temp = item.getId();

                //这里是防止Integer一些特殊情况，就是自动转为0
                if (null != temp && 0 != temp){
                    itemID = temp;
                    break;
                }
            }
        return itemID;
    }

    public static int getPickItemID(){
        int itemID = 0;
        if (null != pickItems && pickItems.size() > 0)
            for (Item item : pickItems){
                Integer temp = item.getId();
                if (null != temp && 0 != temp){
                    itemID = temp;
                    break;
                }
            }

        return itemID;
    }

    public static void clearLostNullItemID(){
        if (null !=lostItems){

            Iterator<Item> it = lostItems.iterator();
            while(it.hasNext()){
                Integer itemID = it.next().getId();
                if (null == itemID)
                    it.remove();
            }

        }

    }

    public static void clearPickNullItemID(){
        if (null != pickItems){
            Iterator<Item> it = pickItems.iterator();
            while(it.hasNext()){
                Integer itemID = it.next().getId();
                if (null == itemID)
                    it.remove();
            }

        }
    }
}
