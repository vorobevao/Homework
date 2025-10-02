package com.example.shop.model;

public class Product {
    private int id;
    private String description;
    private double price;
    private int quantity;
    private String category;

    // Конструкторы
    public Product() {}

    public Product(int id, String description, double price, int quantity, String category) {
        this.id = id;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    @Override
    public String toString() {
        return String.format("Product{id=%d, description='%s', price=%.2f, quantity=%d, category='%s'}",
                id, description, price, quantity, category);
    }
}