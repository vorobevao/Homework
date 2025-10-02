

-- Таблица статусов
CREATE TABLE order_status (
    id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица продуктов
CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL,
    category VARCHAR(100) NOT NULL
);

-- Таблица клиентов
CREATE TABLE customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100) UNIQUE NOT NULL
);

-- Таблица заказов
CREATE TABLE order_table (
    id SERIAL PRIMARY KEY,
    product_id INTEGER REFERENCES product(id),
    customer_id INTEGER REFERENCES customer(id),
    order_date DATE DEFAULT CURRENT_DATE,
    quantity INTEGER NOT NULL,
    status_id INTEGER REFERENCES order_status(id)
);

-- Заполнение данными
INSERT INTO order_status (status_name) VALUES
('pending'), ('confirmed'), ('shipped'), ('delivered'), ('cancelled');

INSERT INTO product (description, price, quantity, category) VALUES
('iPhone 14', 999.99, 50, 'Electronics'),
('MacBook Pro', 2499.99, 25, 'Electronics'),
('Samsung Galaxy', 899.99, 30, 'Electronics');

INSERT INTO customer (first_name, last_name, email) VALUES
('John', 'Doe', 'john@example.com'),
('Jane', 'Smith', 'jane@example.com'),
('Bob', 'Johnson', 'bob@example.com');

INSERT INTO order_table (product_id, customer_id, quantity, status_id) VALUES
(1, 1, 2, 1), (2, 2, 1, 2), (3, 3, 1, 1);