package com.example.zhkuapp.dao;

import com.example.zhkuapp.pojo.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chujian on 2018/1/8.
 */

public class ItemContainer {
    private static final int FIRST = 0;

    private static List<Item> lostItems = new ArrayList<>();

    private static List<Item> pickItems = new ArrayList<>();

    public static List<Item> getLostItems() {
        return lostItems;
    }

    public static void setLostItems(List<Item> items) {
        lostItems = items;
    }

    public static void setPickItems(List<Item> items){
        pickItems = items;
    }

    public static List<Item> getPickItems() {
        return pickItems;
    }

    public static int getLostCount(){
        return lostItems.size();
    }

    public static int getPickCount(){
        return pickItems.size();
    }

    public static void addLost(List<Item> list){
        lostItems.addAll(list);
    }

    public static void addPick(List<Item> list){
        pickItems.addAll(list);
    }

    public static void addLost(Item item){
        lostItems.add(FIRST,item);
    }

    public static void addPick(Item item){
        pickItems.add(FIRST,item);
    }

    public static void clearLost(){
        lostItems.clear();
    }

    public static void clearPick(){
        pickItems.clear();
    }

    public static Item getLostItem(int position){
        return lostItems.get(position);
    }

    public static Item getPickItem(int position){
        return pickItems.get(position);
    }
}
