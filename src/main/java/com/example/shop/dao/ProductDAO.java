package com.example.shop.dao;

import com.example.shop.model.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    // Создание товара
    public void insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO product (description, price, quantity, category) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, product.getDescription());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getCategory());
            stmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                }
            }
        }
    }

    // Получение товара по ID
    public Product getProductById(int id) throws SQLException {
        String sql = "SELECT * FROM product WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    // Получение всех товаров
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    // Обновление товара
    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE product SET description = ?, price = ?, quantity = ?, category = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, product.getDescription());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getQuantity());
            stmt.setString(4, product.getCategory());
            stmt.setInt(5, product.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Удаление товара
    public boolean deleteProduct(int id) throws SQLException {
        String sql = "DELETE FROM product WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // Обновление цены товара
    public boolean updateProductPrice(int productId, double newPrice) throws SQLException {
        String sql = "UPDATE product SET price = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newPrice);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Обновление количества товара
    public boolean updateProductQuantity(int productId, int newQuantity) throws SQLException {
        String sql = "UPDATE product SET quantity = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        }
    }

    // Получение товаров по категории
    public List<Product> getProductsByCategory(String category) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product WHERE category = ? ORDER BY id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }

    // Вспомогательный метод для маппинга ResultSet в Product
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getDouble("price"));
        product.setQuantity(rs.getInt("quantity"));
        product.setCategory(rs.getString("category"));
        return product;
    }
}