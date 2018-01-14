package com.example.zhkuapp.dao;

import android.util.Log;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by chujian on 2018/1/12.
 */

public class UploadDao {

    private final static String UPLOADURL = MainActivity.instance.getString(R.string.uploadUrl);

    //private final static String UPLOADURL = "http://192.168.1.146:8080/zhku/UploadServlet?type=";


    public static void uploadBitmap(File photo, final boolean flag){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        client.addHeader("ContentType", "multipart/form-data");

        //设置上传是什么图片
        String type = "";
        if (flag)
            type = "user";
        else
            type = "item";


        try {
            params.put("photo",photo);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        client.post(UPLOADURL+type, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (flag)
                    ResultVO.setPhotoResultCode(1);
                else
                    ResultVO.setItemPhotoCode(1);
                Log.e("this is UploadService"," 成功将图片上传到服务器");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (flag)
                    ResultVO.setPhotoResultCode(-1);
                else
                    ResultVO.setItemPhotoCode(-1);

            }
        });

    }
}
