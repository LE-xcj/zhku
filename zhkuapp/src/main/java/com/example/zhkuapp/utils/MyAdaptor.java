package com.example.zhkuapp.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhkuapp.R;
import com.example.zhkuapp.dao.ItemContainer;
import com.example.zhkuapp.dao.ItemDao;
import com.example.zhkuapp.dao.UserDao;
import com.example.zhkuapp.myinterface.ItemClickInterface;
import com.example.zhkuapp.pojo.Item;
import com.loopj.android.image.SmartImageView;

import java.util.List;

/**
 * Created by chujian on 2018/1/8.
 */

public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.MyViewHolder> {

    private Context context;
    private ItemClickInterface listener;
    private List<Item> list;


    private static final int LOSTITEMINDEX = 0;
    private static final int TIMEANDPLACEINDEX = 1;
    private static final int CONTACTINDEX = 2;
    private static final int DESCRIPTIONINDEX = 3;
    private static final int USERPHOTOINDEX = 4;
    private static final int ITEMPHOTOINDEX = 5;

    public MyAdaptor(Context context, ItemClickInterface listener, List<Item> list){
        this.context = context;
        this.listener = listener;
        this.list = list;
    }

    public MyAdaptor() {}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        MyViewHolder holder  = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyAdaptor.MyViewHolder holder, final int position) {
        Item item = list.get(position);

        holder.userPhoto.setImageUrl(UserDao.getUserPhotoUrl()+item.getUserPhoto());
        holder.userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.itemClik(v,position);
            }
        });

        //不需要加工处理
        holder.tv_userName.setText(item.getUserName());
        holder.publishTime.setText(item.getPublishTime());

        String[] temp = ItemDao.process(item);

        holder.lostItem.setText(temp[LOSTITEMINDEX]);
        holder.timeAndplace.setText(temp[TIMEANDPLACEINDEX]);
        holder.contact.setText(temp[CONTACTINDEX]);
        holder.description.setText(temp[DESCRIPTIONINDEX]);

        holder.userPhoto.setImageUrl(temp[USERPHOTOINDEX]);

        //如果本地有图片的存储，就直接用本地的
        Bitmap itemPhoto = PhotoUtil.getBitmap(SDCardUtil.getAbsolutePath(item.getPhoto(),false));

        //否则就从服务器那边下载
        if (null == itemPhoto)
            holder.photo.setImageUrl(temp[ITEMPHOTOINDEX]);
        else
            holder.photo.setImageBitmap(itemPhoto);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public SmartImageView userPhoto, photo;
        public TextView tv_userName, lostItem,timeAndplace,contact,description,publishTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            userPhoto = itemView.findViewById(R.id.userPhoto);
            photo = itemView.findViewById(R.id.itemPhoto);

            tv_userName = itemView.findViewById(R.id.tv_userName);
            lostItem = itemView.findViewById(R.id.lostItem);
            timeAndplace = itemView.findViewById(R.id.timeAndplace);
            contact = itemView.findViewById(R.id.contact);
            description = itemView.findViewById(R.id.desciption);
            publishTime = itemView.findViewById(R.id.publishTime);
        }
    }

    public void setList(List<Item> list){
        this.list = list;
    }

}
