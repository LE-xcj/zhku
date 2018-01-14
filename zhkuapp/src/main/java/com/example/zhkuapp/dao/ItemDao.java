package com.example.zhkuapp.dao;

import android.util.Log;

import com.example.zhkuapp.MainActivity;
import com.example.zhkuapp.R;
import com.example.zhkuapp.pojo.Item;
import com.example.zhkuapp.service.ItemService;
import com.example.zhkuapp.utils.MyGsonUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.impl.io.IdentityInputStream;

import java.util.List;

/**
 * Created by chujian on 2018/1/8.
 */

public class ItemDao {

    private static final String GETITEMSURL = MainActivity.instance.getString(R.string.getItemUrl);

    private static final String USERPHOTOURL = MainActivity.instance.getString(R.string.userPhotoUrl);

    private static final String ITEMPHOTOURL = MainActivity.instance.getString(R.string.itemPhotoUrl);

    private static final String PUBLISHITEMURL = MainActivity.instance.getString(R.string.publishItemUrl);

/*    private static final String USERPHOTOURL = "http://192.168.1.146:8080/zhku/userPhoto/";

    private static final String ITEMPHOTOURL = "http://192.168.1.146:8080/zhku/itemPhoto/";

    private static final String PUBLISHITEMURL = "http://192.168.1.146:8080/zhku/PublishItemServlet";*/

    private static final int LENGTH = 6;
    private static final int LOSTITEMINDEX = 0;
    private static final int TIMEANDPLACEINDEX = 1;
    private static final int CONTACTINDEX = 2;
    private static final int DESCRIPTIONINDEX = 3;
    private static final int USERPHOTOINDEX = 4;
    private static final int ITEMPHOTOINDEX = 5;


    public static void getItems(final int count, final int type){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("count",count);
        params.put("type",type);

        client.post(GETITEMSURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (0 == count){
                    if (0 == type)
                        ItemContainer.clearLost();
                    else
                        ItemContainer.clearPick();
                }

                //解析服务器返回的json数据
                List list = MyGsonUtil.getItems(new String(responseBody));

                //上拉则重新刷新Container里的数据
                ItemService.operate(list,count,type);

                ResultVO.setItemChange(1);
                Log.e("this is ItemDao"," successful");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("this is ItemDao"," fail to getItems");
                ResultVO.setItemChange(-1);
            }
        });

    }

    public static void publishItem(Item item){

        AsyncHttpClient client  = new AsyncHttpClient();

        RequestParams params =  new RequestParams();

        //将JavaBean对象转为json对象字符串
        String json = MyGsonUtil.item2Json(item);

        //向服务器那边传递字符串
        params.put("itemJson",json);

        client.post(PUBLISHITEMURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                ResultVO.setPublishItemCode(1);
                Log.e("this is item dao "," publish succesful");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                ResultVO.setPublishItemCode(-1);
                Log.e("this is item dao "," publish fail");
            }
        });

    }


    public static String[] process(Item item){

        String[] temp = new String[LENGTH];
        if (item.getType() == 0)
            temp[LOSTITEMINDEX] = "丢失物品 : " + item.getItemName();
        else
            temp[LOSTITEMINDEX] = "捡到物品 : " + item.getItemName();

        temp[TIMEANDPLACEINDEX] = "时间："+ item.getLostTime() +"\t\t地点 : "+ item.getLostPlace();
        temp[CONTACTINDEX] = "联系人 : "+ item.getContactName() + "\t\t联系方式 : "+ item.getContactWay();
        temp[DESCRIPTIONINDEX] = "物品描述 : " +item.getDescription();

        temp[USERPHOTOINDEX] = USERPHOTOURL + item.getUserPhoto();
        temp[ITEMPHOTOINDEX] = ITEMPHOTOURL + item.getPhoto();

        Log.e("the userphoto url",temp[USERPHOTOINDEX]);
        Log.e("the itemphoto url",temp[ITEMPHOTOINDEX]);

        return temp;
    }

}
