package com.example.zhkuapp.pojo;

/**
 * Created by chujian on 2018/1/1.
 *
 * 用户的Javabean
 *
 * 用户id、密码都是不能为空
 *
 * 用户名：默认为“仲恺人”
 * 性别： 默认为“男”
 * 自我介绍：默认“这个人很懒，什么也没写”
 *
 *
 */

public class User {
    private String userID;
    private String password;
    private String userName;
    private String sex;
    private String email;
    private String selfIntroduction;
    private String photo;

    public User() {}

    public User(String userID, String password) {
        this.userID = userID;
        this.password = password;
    }

    public User(String userID, String userName, String password, String sex,
                String selfIntroduction, String photo, String email) {
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
