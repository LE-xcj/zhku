package com.example.zhkuapp.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.example.zhkuapp.dao.SingleUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chujian on 2018/1/3.
 */

public class SDCardUtil {

    private final static String PATH = "/sdcard/zhku_userHead/";    // sd路径
    private final static String ITEMPATH = "/sdcard/itemPhoto/";
    public final static String TYPE = ".jpg";       //图片类型

    /*
    * 这里将bitmap类型，通过流保存在sd卡里
    * 参数：
    *   1、bitmap类型对象
    *   2、图片的名称，包括扩展名
    *   3、flag，用于辨别保存的图片是用户头像还是item的图片
*               true:   用户头像
*               false： item的图像
    * */
    public static void setPicToView(Bitmap mBitmap,String photoName , boolean flag) {

        //获取SD卡的状态
        String sdStatus = Environment.getExternalStorageState();

        // 检测sd是否可用
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
            return;

        //先把目录准备好
        File file = null;
        if (flag)
            file = new File(PATH);
        else
            file = new File(ITEMPATH);

        FileOutputStream b = null;
        file.mkdirs();                      // y有则获取，无则创建目录

        // 获取绝对路径，也就是路径+图像名称(包括扩展名)
        String fileName = getAbsolutePath(photoName,flag);

        try {
            b = new FileOutputStream(fileName);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }  finally {
            try {
                // 关闭流
                b.flush();
                b.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /*
    * 这里是为了方便获取SD卡的绝对路径
    * 传入的参数：
    *   fileName：是指图片的名称，包括扩展名
    *   flag：是为了辨别是获取用户头像的保存路径，还是item图片的路径
    *   true : 用户头像
    *   false：item图片
    * */
    public static String getAbsolutePath(String fileName,boolean flag){
        String path = "";
        if (flag)
            path = PATH + fileName;
        else
            path = ITEMPATH + fileName;
        return path;
    }


}
