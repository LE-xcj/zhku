package com.example.zhkuapp.pojo;

/**
 * Created by chujian on 2018/1/8.
 *
 * 这里是帖子的Javabean，这里的属性与数据库的帖子表的字段不同
 *
 * 这里还包含了用户的三个属性字段，就是为了方便统一解析，而且除了物品名称不能为null，其他都是选填
 *
 * 而且这里是丢失物品和捡到物品帖子通用模型，用一个type属性来决定帖子的类型
 *
 * 帖子类型：
 *       0：丢失物品的帖子
 *       1：捡到物品的帖子
 *
 * 帖子状态：
 *       0：未解决
 *       1：已解决
 */

public class Item {

    //帖子的属性
    private Integer id;                 //帖子的id，自增
    private String itemName;            //物品的名称
    private String lostTime;            //时间
    private String lostPlace;           //地点
    private String description;         //物品描述
    private String contactName;         //联系人
    private String contactWay;          //联系方式
    private String publishTime;         //帖子的发表日期，以此作为显示顺序的依据
    private int type;                   //帖子的类型
    private int state;                  //帖子的状态
    private String photo;               //图片名称，包括扩展名

    //用户的属性
    private String userID;
    private String userPhoto;
    private String userName;

    public Item() {
    }

    public Item(Integer id, String itemName, String lostTime, String lostPlace,
                String description, String contactName, String contactWay, String publishTime,
                int type, int state, String photo, String userID, String userPhoto, String userName) {
        this.id = id;
        this.itemName = itemName;
        this.lostTime = lostTime;
        this.lostPlace = lostPlace;
        this.description = description;
        this.contactName = contactName;
        this.contactWay = contactWay;
        this.publishTime = publishTime;
        this.type = type;
        this.state = state;
        this.photo = photo;
        this.userID = userID;
        this.userPhoto = userPhoto;
        this.userName = userName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getLostTime() {
        return lostTime;
    }

    public void setLostTime(String lostTime) {
        this.lostTime = lostTime;
    }

    public String getLostPlace() {
        return lostPlace;
    }

    public void setLostPlace(String lostPlace) {
        this.lostPlace = lostPlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactWay() {
        return contactWay;
    }

    public void setContactWay(String contactWay) {
        this.contactWay = contactWay;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
