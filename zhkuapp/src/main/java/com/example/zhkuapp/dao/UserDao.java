package com.example.zhkuapp.dao;

import android.util.Log;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.pojo.User;
import com.example.zhkuapp.utils.MD5Util;
import com.example.zhkuapp.utils.MyGsonUtil;
import com.example.zhkuapp.utils.SharePreferenceUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

/**
 * Created by chujian on 2018/1/3.
 */

public class UserDao {

    private final static String REGISTURL = "http://192.168.1.146:8080/zhku/RegistServlet";
    private final static String UPDATEURL = "http://192.168.1.146:8080/zhku/UpdateUserServlet";
    private final static String GETUSERURL = "http://192.168.1.146:8080/zhku/GetUserServlet";


    //注册的用户的密码已经通过MD5加密了
    public static void insertUser(User user){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("userID",user.getUserID());
        params.put("password",user.getPassword());

        client.post(REGISTURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {

                Log.e("this is UserDao :","成功向服务器注册一个用户");
                /*StringBuffer result = new StringBuffer("");
                int length = responseBody.length;
                for (int i = 0; i < length; i++) {
                    result.append((char) (responseBody[i] & 0xff));
                }*/

            }

            @Override
            public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("request fail","faill");
            }
        });
    }

    public static void updateUser(User user){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("userID",user.getUserID());
        params.put("password",user.getPassword());
        params.put("userName",user.getUserName());
        params.put("sex",user.getSex());
        params.put("email",user.getEmail());
        params.put("selfIntroduction",user.getSelfIntroduction());
        params.put("photo",user.getPhoto());

        client.post(UPDATEURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.e("this UserDao "," 成功更新用户的基本信息");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("request fail","faill");
            }
        });
    }

    //查找服务端的数据库
    public static void selectUser(String userID){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("userID",userID);
        client.post(GETUSERURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                //这里可以通过handler机制，对ui进行界面更新
                User user = MyGsonUtil.getUser(new String(responseBody));

                Log.e("this is UserDao"," next go SingleUser.setSingle(user);");
                SingleUser.setSingle(user);

                //更新sharePreference
                SharePreferenceUtil.write(MainActivity.instance,SingleUser.single);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("getuser","failllll");
            }
        });

    }

}
