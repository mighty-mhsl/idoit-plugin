package com.idoit.bean;

public class Block {
    private long id;
    private String name;
    private int orderNumber;

    public Block() {
    }

    public Block(long id, String name, int orderNumber) {
        this.id = id;
        this.name = name;
        this.orderNumber = orderNumber;
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
}
