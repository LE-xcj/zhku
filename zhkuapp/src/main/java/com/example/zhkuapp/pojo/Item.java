package com.example.zhkuapp.pojo;

/**
 * Created by chujian on 2018/1/8.
 */

public class Item {

    private Integer id;
    private String itemName;
    private String lostTime;
    private String lostPlace;
    private String description;
    private String contactName;
    private String contactWay;
    private String publishTime;
    private int type;
    private int state;
    private String photo;

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
