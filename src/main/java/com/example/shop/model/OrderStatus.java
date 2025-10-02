package com.example.shop.model;

public class OrderStatus {
    private int id;
    private String statusName;

    // Конструкторы
    public OrderStatus() {}

    public OrderStatus(int id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }

    @Override
    public String toString() {
        return String.format("OrderStatus{id=%d, statusName='%s'}", id, statusName);
    }
}