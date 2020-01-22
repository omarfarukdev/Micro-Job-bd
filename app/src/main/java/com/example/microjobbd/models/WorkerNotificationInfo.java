package com.example.microjobbd.models;

public class WorkerNotificationInfo {

    String name;
    String phoneNumber;
    String requestTime;
    String key;
    String image;

    public WorkerNotificationInfo(String name,String phoneNumber, String requestTime,String key,String image) {
        this.name=name;
        this.phoneNumber = phoneNumber;
        this.requestTime = requestTime;
        this.key=key;
        this.image=image;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public String getKey() {
        return key;
    }

    public String getImage() {
        return image;
    }
}
