package com.example.zhkuapp.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by chujian on 2018/1/2.
 */

public class MyProgressDialog extends ProgressDialog {
    public MyProgressDialog(Context context) {
        super(context);
    }
    public MyProgressDialog(Context context,String msg){
        super(context);
        this.setMessage(msg);
    }
}
