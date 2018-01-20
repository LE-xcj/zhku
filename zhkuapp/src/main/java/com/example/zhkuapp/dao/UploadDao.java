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

    /*
    *上传图片
    * 说明：这里我上传图片是以文件的形式进行上传的，而且上传的数量为1
    * 参数一：图片文件
    * 参数二：标志
    *
    * 这里参数二：标志，适用于区分到底上传的图片是属于用户的头像，还是item的图片
    * true：用户
    * false： item图片
    *
     */
    public static void uploadBitmap(File photo, final boolean flag){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        /*
        *设置请求头，这样在后台我的serverlet才能区分上传的是文件
        * 注意：因为上传文件，文件作为参数，还是字符串或者其他雷兴国数据
        * 都是以流的形式进行传输，所以在后台没办法通过getParameter的方法来获取参数或文件
        *
        * 虽然，我能够解决文件的获取，
        * 但是对于字符串这些类型的参数，我就只能通过问号传参的形式将参数拼接到url中
         */
        client.addHeader("ContentType", "multipart/form-data");

        //设置上传图片的类型
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

        //异步请求，并且将type参数拼接到url中
        client.post(UPLOADURL+type, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                //设置相应的返回值
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
