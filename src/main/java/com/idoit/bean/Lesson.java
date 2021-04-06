package com.idoit.bean;

public class Lesson {
    private long id;
    private String name;
    private int orderNumber;
    private String branchName;

    public Lesson() {
    }

    public Lesson(long id, String name, int orderNumber, String branchName) {
        this.id = id;
        this.name = name;
        this.orderNumber = orderNumber;
        this.branchName = branchName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }
}
