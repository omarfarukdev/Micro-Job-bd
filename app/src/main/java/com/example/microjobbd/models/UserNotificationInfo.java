package com.example.microjobbd.models;

public class UserNotificationInfo {

    String name;
    String phoneNo;
    String status;
    String time;
    String image;
    String key;
    String isSeen;

    public UserNotificationInfo(String name, String phoneNo, String status, String time, String image, String key,String isSeen) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.status = status;
        this.time = time;
        this.image = image;
        this.key = key;
        this.isSeen=isSeen;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getImage() {
        return image;
    }

    public String getKey() {
        return key;
    }

    public String getIsSeen() {
        return isSeen;
    }
}
