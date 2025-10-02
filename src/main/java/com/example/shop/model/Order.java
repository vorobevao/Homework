package com.example.shop.model;

import java.sql.Date;

public class Order {
    private int id;
    private int productId;
    private int customerId;
    private Date orderDate;
    private int quantity;
    private int statusId;

    // Конструкторы
    public Order() {}

    public Order(int id, int productId, int customerId, Date orderDate, int quantity, int statusId) {
        this.id = id;
        this.productId = productId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.quantity = quantity;
        this.statusId = statusId;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public Date getOrderDate() { return orderDate; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }

    @Override
    public String toString() {
        return String.format("Order{id=%d, productId=%d, customerId=%d, orderDate=%s, quantity=%d, statusId=%d}",
                id, productId, customerId, orderDate, quantity, statusId);
    }
}