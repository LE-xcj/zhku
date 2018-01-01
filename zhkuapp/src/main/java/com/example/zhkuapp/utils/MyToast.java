package com.example.zhkuapp.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by chujian on 2018/1/1.
 */

public class MyToast extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public MyToast(Context context) {
        super(context);
    }

    public static void show(Context context , String msg){
        Toast.makeText(context,msg,LENGTH_SHORT).show();
    }
}
