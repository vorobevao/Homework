package com.example.shop.model;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    // Конструкторы
    public Customer() {}

    public Customer(int id, String firstName, String lastName, String phone, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("Customer{id=%d, firstName='%s', lastName='%s', phone='%s', email='%s'}",
                id, firstName, lastName, phone, email);
    }
}