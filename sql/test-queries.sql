-- =============================================
-- ТЕСТОВЫЕ ЗАПРОСЫ (упрощенная версия)
-- =============================================

-- 1. SELECT: Список всех заказов
SELECT '=== 1. ВСЕ ЗАКАЗЫ ===' as info;
SELECT o.id, c.first_name, p.description, o.quantity, o.order_date
FROM order_table o
JOIN customer c ON o.customer_id = c.id
JOIN product p ON o.product_id = p.id
LIMIT 5;

-- 2. SELECT: Топ-3 самых популярных товаров
SELECT '=== 2. ТОП-3 ТОВАРА ===' as info;
SELECT p.description, SUM(o.quantity) as total_sold
FROM product p
JOIN order_table o ON p.id = o.product_id
GROUP BY p.id, p.description
ORDER BY total_sold DESC
LIMIT 3;

-- 3. SELECT: Выручка по категориям
SELECT '=== 3. ВЫРУЧКА ПО КАТЕГОРИЯМ ===' as info;
SELECT p.category, SUM(p.price * o.quantity) as revenue
FROM product p
JOIN order_table o ON p.id = o.product_id
GROUP BY p.category;

-- 4. SELECT: Клиенты с заказами
SELECT '=== 4. КЛИЕНТЫ С ЗАКАЗАМИ ===' as info;
SELECT c.first_name, c.last_name, COUNT(o.id) as order_count
FROM customer c
JOIN order_table o ON c.id = o.customer_id
GROUP BY c.id, c.first_name, c.last_name;

-- 5. SELECT: Статистика по дням
SELECT '=== 5. СТАТИСТИКА ПО ДНЯМ ===' as info;
SELECT order_date, COUNT(*) as orders_per_day
FROM order_table
GROUP BY order_date
ORDER BY order_date DESC;

-- 6. UPDATE: Уменьшение количества товара
SELECT '=== 6. ДО УМЕНЬШЕНИЯ КОЛИЧЕСТВА ===' as info;
SELECT id, description, quantity FROM product WHERE id = 1;

UPDATE product SET quantity = quantity - 1 WHERE id = 1;

SELECT '=== 6. ПОСЛЕ УМЕНЬШЕНИЯ КОЛИЧЕСТВА ===' as info;
SELECT id, description, quantity FROM product WHERE id = 1;

-- 7. UPDATE: Изменение статуса заказа
SELECT '=== 7. ДО ИЗМЕНЕНИЯ СТАТУСА ===' as info;
SELECT o.id, os.status_name FROM order_table o
JOIN order_status os ON o.status_id = os.id WHERE o.id = 1;

UPDATE order_table SET status_id = 4 WHERE id = 1;

SELECT '=== 7. ПОСЛЕ ИЗМЕНЕНИЯ СТАТУСА ===' as info;
SELECT o.id, os.status_name FROM order_table o
JOIN order_status os ON o.status_id = os.id WHERE o.id = 1;

-- 8. UPDATE: Увеличение цены
SELECT '=== 8. ДО УВЕЛИЧЕНИЯ ЦЕНЫ ===' as info;
SELECT id, description, price FROM product WHERE category = 'Electronics';

UPDATE product SET price = price * 1.1 WHERE category = 'Electronics';

SELECT '=== 8. ПОСЛЕ УВЕЛИЧЕНИЯ ЦЕНЫ ===' as info;
SELECT id, description, price FROM product WHERE category = 'Electronics';

-- 9. DELETE: Удаление тестового клиента (сначала создадим)
SELECT '=== 9. СОЗДАЕМ ТЕСТОВОГО КЛИЕНТА ===' as info;
INSERT INTO customer (first_name, last_name, email)
VALUES ('Test', 'ToDelete', 'test.delete@example.com');

SELECT '=== 9. ДО УДАЛЕНИЯ ===' as info;
SELECT id, first_name, last_name, email FROM customer
WHERE email = 'test.delete@example.com';

DELETE FROM customer WHERE email = 'test.delete@example.com';

SELECT '=== 9. ПОСЛЕ УДАЛЕНИЯ ===' as info;
SELECT id, first_name, last_name, email FROM customer
WHERE email = 'test.delete@example.com';

-- 10. DELETE: Удаление тестового заказа (сначала создадим)
SELECT '=== 10. СОЗДАЕМ ТЕСТОВЫЙ ЗАКАЗ ===' as info;
INSERT INTO order_table (product_id, customer_id, quantity, status_id)
VALUES (1, 1, 1, 5);

SELECT '=== 10. ДО УДАЛЕНИЯ ===' as info;
SELECT id, product_id, customer_id FROM order_table
WHERE status_id = 5 ORDER BY id DESC LIMIT 1;

DELETE FROM order_table WHERE status_id = 5;

SELECT '=== 10. ПОСЛЕ УДАЛЕНИЯ ===' as info;
SELECT id, product_id, customer_id FROM order_table
WHERE status_id = 5 ORDER BY id DESC LIMIT 1;

SELECT '=== ВСЕ ЗАПРОСЫ ВЫПОЛНЕНЫ ===' as info;