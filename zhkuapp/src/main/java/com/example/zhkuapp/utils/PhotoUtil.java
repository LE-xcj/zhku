package com.example.zhkuapp.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;


import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by chujian on 2018/1/2.
 */

public class PhotoUtil {

    private static final int BOY = 0;
    private static final int GIRL = 1;
    /**
     * 调用系统的裁剪
     *
     * @param uri
     */
    public static void cropPhoto(Uri uri, AppCompatActivity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, 3);
    }


    /**
     * 把bitmap转成圆形
     * */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int r = 0;
        // 取最短边做边长
        if (width < height) {
            r = width;
        } else {
            r = height;
        }
        // 构建一个bitmap
        Bitmap backgroundBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBm);
        Paint p = new Paint();
        // 设置边缘光滑，去掉锯齿
        p.setAntiAlias(true);
        RectF rect = new RectF(0, 0, r, r);
        // 通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        // 且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, p);
        // 设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, p);
        return backgroundBm;
    }

    public static Bitmap getBitmap(String absolutePath){
        return BitmapFactory.decodeFile(absolutePath);
    }


    public static Bitmap getBitmap(int flag){
        if (BOY == flag)
            return BitmapFactory.decodeResource(MainActivity.instance.getResources(), R.drawable.boy);
        else if(GIRL == flag)
            return BitmapFactory.decodeResource(MainActivity.instance.getResources(),R.drawable.girl);
        return null;
    }

    public static Bitmap getBitmap(){
        return BitmapFactory.decodeResource(MainActivity.instance.getResources(),R.drawable.zhku);
    }


}
