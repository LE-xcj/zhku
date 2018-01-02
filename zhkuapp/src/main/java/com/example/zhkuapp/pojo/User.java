package com.example.zhkuapp.pojo;

/**
 * Created by chujian on 2018/1/1.
 */

public class User {
    private String userID;
    private String userName;
    private String password;
    private String sex;
    private String selfIntroduction;
    private String photo;
    private String email;

    public User() {
    }

    public User(String userID, String userName, String password, String sex, String selfIntroduction, String photo, String email) {
        this.userID = userID;
        this.userName = userName;
        this.password = password;
        this.sex = sex;
        this.selfIntroduction = selfIntroduction;
        this.photo = photo;
        this.email = email;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSelfIntroduction() {
        return selfIntroduction;
    }

    public void setSelfIntroduction(String selfIntroduction) {
        this.selfIntroduction = selfIntroduction;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
