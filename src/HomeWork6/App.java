package HomeWork6;

import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Person> people = new HashMap<>();
        Map<String, Product> products = new HashMap<>();

        // Ввод покупателей
        System.out.println("Введите покупателей в формате: Имя = Сумма (пустая строка для завершения)");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) break;

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

        System.out.println("Введите продукты в формате: Название = Стоимость (пустая строка для завершения)");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) break;

            try {
                String[] parts = input.split("=");
                if (parts.length < 2) {
                    System.out.println("Ошибка формата. Используйте: Название = Стоимость");
                    continue;
                }
                String name = parts[0].trim();
                double cost = Double.parseDouble(parts[1].trim());
                products.put(name, new Product(name, cost));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: неверный формат стоимости");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }


        System.out.println("Введите покупки в формате: Имя Покупателя Название Продукта (END для завершения)");
        List<Person> sortedPeople = people.values().stream()
                .sorted(Comparator.comparing(Person::getName))
                .collect(Collectors.toList());

        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("END")) break;

            try {
                // Поиск покупателя
                Person person = null;
                for (Person p : sortedPeople) {
                    if (input.startsWith(p.getName())) {
                        person = p;
                        break;
                    }
                }

                if (person == null) {
                    System.out.println("Покупатель не найден: " + input);
                    continue;
                }


                String productName = input.substring(person.getName().length()).trim();
                Product product = products.get(productName);

                if (product == null) {
                    System.out.println("Продукт не найден: " + productName);
                    continue;
                }


                if (person.buyProduct(product)) {
                    System.out.println(person.getName() + " купил " + product.getName());
                } else {
                    System.out.println(person.getName() + " не может позволить себе " + product.getName());
                }

            } catch (Exception e) {
                System.out.println("Ошибка обработки: " + e.getMessage());
            }
        }

        System.out.println("\nРезультаты покупок:");
        for (Person person : sortedPeople) {
            System.out.println(person);
        }

        scanner.close();
    }
}
