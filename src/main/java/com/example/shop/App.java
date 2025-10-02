package com.example.shop;

import java.sql.*;
import java.util.Properties;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class App {
    private Connection connection;

    public static void main(String[] args) {
        App app = new App();
        try {
            app.run();
        } catch (Exception e) {
            System.err.println("Ошибка при выполнении приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() throws Exception {
        System.out.println("🚀 Запуск приложения PostgreSQL Shop...");
        connectToDatabase();
        checkAndCreateTables();
        demonstrateCRUDOperations();
        executeTestQueriesFromFile();
        closeConnection();
    }

    private void connectToDatabase() throws Exception {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Драйвер PostgreSQL загружен");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Драйвер PostgreSQL не найден", e);
        }

        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("Не найден application.properties");
            }
            props.load(input);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String password = props.getProperty("db.password");

        System.out.println("🔗 Подключение к: " + url);
        connection = DriverManager.getConnection(url, user, password);
        connection.setAutoCommit(false);
        System.out.println("✅ Подключение к БД установлено");
    }

    private void checkAndCreateTables() throws SQLException {
        if (!tableExists("product")) {
            System.out.println("❌ Таблицы не найдены. Выполните schema.sql вручную:");
            System.out.println("docker exec my-postgres psql -U postgres -d shop -f /tmp/schema.sql");
            throw new SQLException("Таблицы не созданы. Выполните schema.sql сначала.");
        }
        System.out.println("✅ Таблицы существуют");
    }

    private boolean tableExists(String tableName) throws SQLException {
        String sql = "SELECT EXISTS (SELECT FROM information_schema.tables " +
                "WHERE table_schema = 'public' AND table_name = ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getBoolean(1);
        }
    }

    private void demonstrateCRUDOperations() throws SQLException {
        try {
            System.out.println("\n=== ДЕМОНСТРАЦИЯ CRUD ОПЕРАЦИЙ ===");

            readProducts();
            readCustomers();
            insertNewProduct();
            insertNewCustomer();
            updateProductPrice();
            deleteTestData();

            connection.commit();
            System.out.println("\n🎉 Все CRUD операции выполнены успешно!");

        } catch (SQLException e) {
            connection.rollback();
            System.err.println("❌ Ошибка в CRUD операциях: " + e.getMessage());
            throw e;
        }
    }

    private void readProducts() throws SQLException {
        String sql = "SELECT id, description, price, quantity, category FROM product ORDER BY id LIMIT 5";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n📦 Первые 5 товаров:");
            printFormattedTable(rs);
        }
    }

    private void readCustomers() throws SQLException {
        String sql = "SELECT id, first_name, last_name, email FROM customer ORDER BY id LIMIT 5";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n👥 Первые 5 клиентов:");
            printFormattedTable(rs);
        }
    }

    private void insertNewProduct() throws SQLException {
        String sql = "INSERT INTO product (description, price, quantity, category) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String uniqueDescription = "Товар_" + System.currentTimeMillis();
            stmt.setString(1, uniqueDescription);
            stmt.setDouble(2, 199.99);
            stmt.setInt(3, 10);
            stmt.setString(4, "Тест");

            stmt.executeUpdate();
            System.out.println("✅ Добавлен новый товар: " + uniqueDescription);
        }
    }

    private void insertNewCustomer() throws SQLException {
        String sql = "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "Мария");
            stmt.setString(2, "Петрова");
            stmt.setString(3, "+79165555555");

            String uniqueEmail = "maria" + System.currentTimeMillis() + "@test.ru";
            stmt.setString(4, uniqueEmail);

            stmt.executeUpdate();
            System.out.println("✅ Добавлен новый клиент: " + uniqueEmail);
        }
    }

    private void updateProductPrice() throws SQLException {
        String sql = "UPDATE product SET price = price * 1.1 WHERE id = 1";

        try (Statement stmt = connection.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            if (affectedRows > 0) {
                System.out.println("✅ Цена товара ID=1 увеличена на 10%");
            } else {
                System.out.println("⚠️  Товар с ID=1 не найден для обновления");
            }
        }
    }

    private void deleteTestData() throws SQLException {
        String sql = "DELETE FROM customer WHERE email LIKE '%test.ru'";

        try (Statement stmt = connection.createStatement()) {
            int affectedRows = stmt.executeUpdate(sql);
            if (affectedRows > 0) {
                System.out.println("✅ Удалено тестовых клиентов: " + affectedRows);
            } else {
                System.out.println("⚠️  Тестовые клиенты не найдены для удаления");
            }
        }
    }

    private void executeTestQueriesFromFile() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("=== ВЫПОЛНЕНИЕ ТЕСТОВЫХ ЗАПРОСОВ ИЗ ФАЙЛА ===");
        System.out.println("=".repeat(60));

        try {
            String filePath = "sql/test-queries.sql";

            if (!Files.exists(Paths.get(filePath))) {
                System.out.println("❌ Файл test-queries.sql не найден по пути: " + filePath);
                return;
            }

            String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
            System.out.println("✅ Файл test-queries.sql прочитан успешно");

            executeAllQueriesWithDetails(content);

        } catch (Exception e) {
            System.err.println("❌ Ошибка при чтении/выполнении test-queries.sql: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("❌ Ошибка при откате транзакции: " + rollbackEx.getMessage());
            }
        }
    }

    private void executeAllQueriesWithDetails(String content) throws SQLException {
        String[] queries = content.split(";");
        int queryNumber = 0;

        for (String query : queries) {
            query = query.trim();

            if (query.isEmpty() || query.startsWith("--")) {
                continue;
            }

            queryNumber++;

            String queryType = getQueryType(query);
            String queryName = extractQueryName(query);

            System.out.println("\n" + "─".repeat(60));
            System.out.printf("🔹 ЗАПРОС #%d [%s]: %s%n", queryNumber, queryType, queryName);
            System.out.println("─".repeat(60));

            try (Statement stmt = connection.createStatement()) {
                long startTime = System.currentTimeMillis();
                boolean hasResultSet = stmt.execute(query);
                long endTime = System.currentTimeMillis();

                if (hasResultSet) {
                    ResultSet rs = stmt.getResultSet();
                    printFormattedTable(rs);
                } else {
                    int affectedRows = stmt.getUpdateCount();

                    if (affectedRows > 0) {
                        System.out.printf("✅ УСПЕХ: Затронуто %d строк%n", affectedRows);
                    } else {
                        System.out.println("⚠️  Запрос выполнен, но не затронул строки");
                    }
                }

                System.out.printf("⏱️  Время выполнения: %d мс%n", endTime - startTime);

            } catch (SQLException e) {
                System.err.println("❌ ОШИБКА: " + e.getMessage());
            }
        }

        connection.commit();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🎉 ВСЕ ТЕСТОВЫЕ ЗАПРОСЫ ВЫПОЛНЕНЫ!");
        System.out.println("=".repeat(60));
    }

    private String getQueryType(String query) {
        String upperQuery = query.toUpperCase().trim();
        if (upperQuery.startsWith("SELECT")) return "SELECT";
        if (upperQuery.startsWith("UPDATE")) return "UPDATE";
        if (upperQuery.startsWith("DELETE")) return "DELETE";
        if (upperQuery.startsWith("INSERT")) return "INSERT";
        return "OTHER";
    }

    private String extractQueryName(String query) {
        // Извлекаем название из информационных SELECT запросов
        if (query.contains("'") && query.contains("info")) {
            int start = query.indexOf("'") + 1;
            int end = query.indexOf("'", start);
            if (end > start) {
                return query.substring(start, end);
            }
        }

        // Для длинных запросов обрезаем
        if (query.length() > 50) {
            return query.substring(0, 47) + "...";
        }
        return query;
    }

    /**
     * Улучшенный метод для вывода результатов в виде таблицы
     */
    private void printFormattedTable(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Собираем все данные для расчета ширины колонок
        List<String[]> rows = new ArrayList<>();
        String[] headers = new String[columnCount];
        int[] maxWidths = new int[columnCount];

        // Заголовки
        for (int i = 1; i <= columnCount; i++) {
            headers[i-1] = metaData.getColumnName(i);
            maxWidths[i-1] = headers[i-1].length();
        }

        // Данные
        int rowCount = 0;
        while (rs.next() && rowCount < 20) { // Ограничим 20 строками
            String[] row = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                String value = rs.getString(i);
                if (value == null) value = "NULL";
                row[i-1] = value;
                maxWidths[i-1] = Math.max(maxWidths[i-1], value.length());
            }
            rows.add(row);
            rowCount++;
        }

        // Ограничим максимальную ширину колонки
        for (int i = 0; i < columnCount; i++) {
            maxWidths[i] = Math.min(maxWidths[i], 30); // Максимум 30 символов
            maxWidths[i] = Math.max(maxWidths[i], 8);  // Минимум 8 символов
        }

        // Строим разделитель
        StringBuilder separator = new StringBuilder("+");
        for (int width : maxWidths) {
            separator.append("-".repeat(width + 2)).append("+");
        }

        // Вывод заголовков
        System.out.println(separator);
        StringBuilder headerRow = new StringBuilder("|");
        for (int i = 0; i < columnCount; i++) {
            headerRow.append(" ").append(padCenter(headers[i], maxWidths[i])).append(" |");
        }
        System.out.println(headerRow);
        System.out.println(separator);

        // Вывод данных
        if (rows.isEmpty()) {
            // Пустой результат
            StringBuilder emptyRow = new StringBuilder("|");
            for (int i = 0; i < columnCount; i++) {
                emptyRow.append(" ").append(padCenter("нет данных", maxWidths[i])).append(" |");
            }
            System.out.println(emptyRow);
        } else {
            // Данные
            for (String[] row : rows) {
                StringBuilder dataRow = new StringBuilder("|");
                for (int i = 0; i < columnCount; i++) {
                    String value = row[i];
                    if (value.length() > maxWidths[i]) {
                        value = value.substring(0, maxWidths[i] - 3) + "...";
                    }
                    dataRow.append(" ").append(padRight(value, maxWidths[i])).append(" |");
                }
                System.out.println(dataRow);
            }
        }

        System.out.println(separator);
        System.out.println("📊 Показано строк: " + rows.size());
    }

    /**
     * Выравнивание текста по центру
     */
    private String padCenter(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }

        int padding = length - text.length();
        int leftPadding = padding / 2;
        int rightPadding = padding - leftPadding;

        return " ".repeat(leftPadding) + text + " ".repeat(rightPadding);
    }

    /**
     * Выравнивание текста по левому краю
     */
    private String padRight(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }

        return text + " ".repeat(length - text.length());
    }

    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Подключение к БД закрыто");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при закрытии подключения: " + e.getMessage());
        }
    }
}