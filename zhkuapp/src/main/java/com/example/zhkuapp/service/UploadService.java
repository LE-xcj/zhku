package com.example.zhkuapp.service;

import android.graphics.Bitmap;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * Created by chujian on 2018/1/4.
 */

public class UploadService {

    private final static String UPLOADURL = "http://192.168.1.146:8080/zhku/UploadServlet";

    public static void uploadBitmap(File photo){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.addHeader("ContentType", "multipart/form-data");
        try {
            params.put("photo",photo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post(UPLOADURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("this is UploadService"," 成功将图片上传到服务器");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

}
