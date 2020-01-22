package com.example.microjobbd.models;

public class WorkerInformation {

    String name;
    String address;
    String workerNum;

    public WorkerInformation(String name, String address, String workerNum) {
        this.name = name;
        this.address = address;
        this.workerNum = workerNum;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getWorkerNum() {
        return workerNum;
    }
}
