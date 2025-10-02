package com.example.shop.dao;

import com.example.shop.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    private final Connection connection;

    public CustomerDAO(Connection connection) {
        this.connection = connection;
    }

    // Создание клиента
    public void insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    customer.setId(rs.getInt(1));
                }
            }
        }
    }

    // Получение клиента по ID
    public Customer getCustomerById(int id) throws SQLException {
        String sql = "SELECT * FROM customer WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }

    // Получение всех клиентов
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customer ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    // Обновление клиента
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customer SET first_name = ?, last_name = ?, phone = ?, email = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            stmt.setString(3, customer.getPhone());
            stmt.setString(4, customer.getEmail());
            stmt.setInt(5, customer.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Удаление клиента
    public boolean deleteCustomer(int id) throws SQLException {
        String sql = "DELETE FROM customer WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Поиск клиента по email
    public Customer findCustomerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customer WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        return null;
    }

    // Получение клиентов без заказов
    public List<Customer> getCustomersWithoutOrders() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.* FROM customer c LEFT JOIN order_table o ON c.id = o.customer_id WHERE o.id IS NULL";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        return customers;
    }

    // Вспомогательный метод для маппинга ResultSet в Customer
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));
        return customer;
    }
}