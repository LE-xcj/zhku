package com.example.zhkuapp.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import com.example.zhkuapp.dao.SingleUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chujian on 2018/1/3.
 */

public class SDCardUtil {

    public final static String PATH = "/sdcard/zhku_userHead/";    // sd路径
    public final static String TYPE = ".jpg";       //图片类型

    public static void setPicToView(Bitmap mBitmap) {
        //获取SD卡的状态
        String sdStatus = Environment.getExternalStorageState();

        // 检测sd是否可用
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED))
            return;

        File file = new File(PATH);
        FileOutputStream b = null;
        file.mkdirs();                      // y有则获取，无则创建目录

        String fileName = PATH + SingleUser.getUserID() +".jpg";    // 图片名字
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

}
