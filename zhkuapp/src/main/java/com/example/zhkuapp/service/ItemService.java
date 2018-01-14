package com.example.zhkuapp.service;

import android.graphics.Bitmap;

import com.example.zhkuapp.dao.ItemContainer;
import com.example.zhkuapp.dao.ItemDao;
import com.example.zhkuapp.dao.ResultVO;
import com.example.zhkuapp.dao.UploadDao;
import com.example.zhkuapp.pojo.Item;
import com.example.zhkuapp.utils.SDCardUtil;

import java.io.File;
import java.util.List;

/**
 * Created by chujian on 2018/1/11.
 */

public class ItemService {

    private static final int LOST = 0;
    private static final int  PICK = 1;

    private static final int DOWNPULL = 0;

    public static void operate(List<Item> items, int time , int type){
        if (DOWNPULL == time){
            setItems(items,type);
        }else{
            addItems(items,type);
        }
    }

    public static void setItems(List<Item> items, int type){
        if (LOST == type){
            ItemContainer.setLostItems(items);
        }else if(PICK == type){
            ItemContainer.setPickItems(items);
        }
    }

    public static void addItems(List<Item> items , int type){
        if (LOST == type){
            ItemContainer.addLost(items);
        }else if(PICK == type){
            ItemContainer.addPick(items);
        }
    }

    public static void publishItem(Item item, Bitmap photo){
        //如果图片不为空
        if (null != photo){
            //获取图片的名字，uuid.jpg
            String itemPhotoName = item.getPhoto();

            /*
            *将图片保存在本地
            * 三个参数
            *   第一个参数：图片
            *   第二个参数：物品图片名称（包括扩展名）
            *   第三个参数：用于分辨保存在sd卡那个目录
            *
             */
            SDCardUtil.setPicToView(photo,itemPhotoName,false);

            /*
            *这里一定要在保存了图片到本地，才进行图片的上传
            * 因为我还没有方法直接上传一个bitmap类型的对象，所以这里我是以文件的形式上传一张图片
            *上传图片到服务器
            *   参数一：图片文件
            *   参数二：用于服务器那边辨别图片保存的位置
             */
            UploadDao.uploadBitmap(new File(SDCardUtil.getAbsolutePath(itemPhotoName,false)),false);
        }else{
            //如果没有item的图片，就直接设置返回结果
            ResultVO.setItemPhotoCode(1);
        }

        //向服务端那边的数据库插入数据
        ItemDao.publishItem(item);
    }

    public static boolean strEmpty(String str){
        if (null == str || "".equals(str))
            return true;
        return false;
    }


    public static boolean canPublish(int itemPublishCode, int itemPhotoCode){
        if ( 1 == itemPublishCode && 1 == itemPhotoCode)
            return true;
        return false;
    }
}
