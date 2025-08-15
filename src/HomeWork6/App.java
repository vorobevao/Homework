package HomeWork6;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Person> people = new HashMap<>();
        Map<String, Product> products = new HashMap<>();

        // Ввод покупателей
        System.out.println("Введите покупателей в формате: Имя = Сумма (END для завершения)");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;

            try {
                String[] parts = input.split("=");
                if (parts.length < 2) {
                    System.out.println("Ошибка формата. Используйте: Имя = Сумма");
                    continue;
                }
                String name = parts[0].trim();
                double money = Double.parseDouble(parts[1].trim());
                people.put(name, new Person(name, money));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: неверный формат суммы");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("\n=== ВВОД ПРОДУКТОВ ===");
        System.out.println("Формат: Название = Стоимость (END для завершенияF)");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;

            try {
                String[] parts = input.split("=");
                if (parts.length < 2) {
                    System.out.println("Ошибка формата. Используйте: Название = Стоимость");
                    continue;
                }
                String name = parts[0].trim();
                double cost = Double.parseDouble(parts[1].trim());

                System.out.print("Это скидочный продукт? (y/n): ");
                String isDiscount = scanner.nextLine().trim();

                if (isDiscount.equalsIgnoreCase("y")) {
                    System.out.print("Введите размер скидки: ");
                    double discount = Double.parseDouble(scanner.nextLine());

                    System.out.print("Введите дату окончания скидки (ГГГГ-ММ-ДД): ");
                    LocalDate expiryDate = LocalDate.parse(scanner.nextLine());

                    products.put(name, new DiscountProduct(name, cost, discount, expiryDate));
                } else {
                    products.put(name, new Product(name, cost));
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: неверный формат числа");
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: неверный формат даты. Используйте ГГГГ-ММ-ДД");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }


        System.out.println("\n=== ОБРАБОТКА ПОКУПОК ===");
        System.out.println("Формат: Имя Покупателя Название Продукта (END для завершения)");
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;

            // Поиск покупателя
            Person person = null;
            for (String name : people.keySet()) {
                if (input.startsWith(name)) {
                    person = people.get(name);
                    break;
                }
            }

            if (person == null) {
                System.out.println("Покупатель не найден: " + input);
                continue;
            }

            // Извлечение названия продукта
            String productName = input.substring(person.getName().length()).trim();
            Product product = products.get(productName);

            if (product == null) {
                System.out.println("Продукт не найден: " + productName);
                continue;
            }

            // Обработка покупки
            if (person.buyProduct(product)) {
                System.out.println(person.getName() + " купил " + product.getName());
            } else {
                System.out.println(person.getName() + " не может позволить себе " + product.getName());
            }
        }

        // Вывод результатов
        System.out.println("\n=== ИТОГИ ПОКУПОК ===");
        for (Person person : people.values()) {
            System.out.println(person);
        }

    }
}