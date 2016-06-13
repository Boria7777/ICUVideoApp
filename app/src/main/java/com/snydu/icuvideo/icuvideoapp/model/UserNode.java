package com.snydu.icuvideo.icuvideoapp.model;

import java.io.Serializable;

/**
 * Created by Boria on 2016/4/26.
 */
public class UserNode implements Serializable {


    public UserNode(String userName, String platfrom, String role, String userId, String online, String chat) {
        UserName = userName;
        Platfrom = platfrom;
        Role = role;
        UserId = userId;
        Online = online;
        Chat = chat;
    }

    private String UserName;
    private String Platfrom;

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPlatfrom() {
        return Platfrom;
    }

    public void setPlatfrom(String platfrom) {
        Platfrom = platfrom;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getOnline() {
        return Online;
    }

    public void setOnline(String online) {
        Online = online;
    }

    public String getChat() {
        return Chat;
    }

    public void setChat(String chat) {
        Chat = chat;
    }

    public UserNode() {
        UserName = "";
        Platfrom = "";
        Role = "";
        UserId = "";
        Online = "";
        Chat = "";
    }

    private String Role;
    private String UserId;
    private String Online;
    private String Chat;
}
