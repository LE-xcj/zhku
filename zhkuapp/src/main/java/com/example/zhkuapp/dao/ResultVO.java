package com.example.zhkuapp.dao;

/**
 * Created by chujian on 2018/1/7.
 */

public class ResultVO {

    private static int photoResultCode = 0 ;

    private static int basicImforCode = 0;

    private static int itemChange = 0;

    private static int publishItemCode = 0;

    private static int itemPhotoCode = 0;

    private static final int USER = 1;

    private static final int ITEM = 2;

    //修改信息
    public static int getPhotoResultCode(){
        return photoResultCode;
    }

    public static int getBasicImforCode(){
        return basicImforCode;
    }

    public static void setPhotoResultCode(int code){
        photoResultCode = code;
    }

    public static void setBasicImforCode(int code){
        basicImforCode = code;
    }

    //修改信息知否，重置
    public static void setDefault(int flag){

        if (USER == flag){
            photoResultCode = 0;
            basicImforCode = 0;
        }else if (ITEM == flag){
            publishItemCode = 0;
            itemPhotoCode = 0;
        }

    }

    public static boolean isDefaul(int flag){
        if (USER == flag)
            return (0 == photoResultCode || 0 == basicImforCode);
        else if (ITEM == flag)
            return (0 == publishItemCode || 0 == itemPhotoCode);
        else
            return true;
    }

    //帖子
    public static int getItemChange() {
        return itemChange;
    }

    public static void setItemChange(int itemChange) {
        ResultVO.itemChange = itemChange;
    }

    public static void setItemDefault(){
        itemChange = 0;
    }

    public static int getPublishItemCode() {
        return publishItemCode;
    }

    public static void setPublishItemCode(int publishItemCode) {
        ResultVO.publishItemCode = publishItemCode;
    }

    public static int getItemPhotoCode() {
        return itemPhotoCode;
    }

    public static void setItemPhotoCode(int itemPhotoCode) {
        ResultVO.itemPhotoCode = itemPhotoCode;
    }


}
