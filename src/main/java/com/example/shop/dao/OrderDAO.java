package com.example.shop.dao;

import com.example.shop.model.Order;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    // Создание заказа
    public void insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO order_table (product_id, customer_id, order_date, quantity, status_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getProductId());
            stmt.setInt(2, order.getCustomerId());
            stmt.setDate(3, order.getOrderDate());
            stmt.setInt(4, order.getQuantity());
            stmt.setInt(5, order.getStatusId());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    order.setId(rs.getInt(1));
                }
            }
        }
    }

    // Получение заказа по ID
    public Order getOrderById(int id) throws SQLException {
        String sql = "SELECT * FROM order_table WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        }
        return null;
    }

    // Получение всех заказов
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_table ORDER BY order_date DESC, id DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }
        return orders;
    }

    // Обновление заказа
    public boolean updateOrder(Order order) throws SQLException {
        String sql = "UPDATE order_table SET product_id = ?, customer_id = ?, order_date = ?, quantity = ?, status_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getProductId());
            stmt.setInt(2, order.getCustomerId());
            stmt.setDate(3, order.getOrderDate());
            stmt.setInt(4, order.getQuantity());
            stmt.setInt(5, order.getStatusId());
            stmt.setInt(6, order.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Удаление заказа
    public boolean deleteOrder(int id) throws SQLException {
        String sql = "DELETE FROM order_table WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Получение заказов по клиенту
    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_table WHERE customer_id = ? ORDER BY order_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }

    // Получение заказов по статусу
    public List<Order> getOrdersByStatus(int statusId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_table WHERE status_id = ? ORDER BY order_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }

    // Получение последних N заказов
    public List<Order> getRecentOrders(int limit) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM order_table ORDER BY order_date DESC, id DESC LIMIT ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }
        return orders;
    }

    // Обновление статуса заказа
    public boolean updateOrderStatus(int orderId, int statusId) throws SQLException {
        String sql = "UPDATE order_table SET status_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Вспомогательный метод для маппинга ResultSet в Order
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setProductId(rs.getInt("product_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setOrderDate(rs.getDate("order_date"));
        order.setQuantity(rs.getInt("quantity"));
        order.setStatusId(rs.getInt("status_id"));
        return order;
    }
}