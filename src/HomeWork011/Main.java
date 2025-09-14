package HomeWork011;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        // Создаем список автомобилей
        List<Car> cars = new ArrayList<>();
        cars.add(new Car("a123me", "Mercedes", "White", 0, 8300000));
        cars.add(new Car("b873of", "Volga", "Black", 0, 673000));
        cars.add(new Car("w487mn", "Lexus", "Grey", 76000, 900000));
        cars.add(new Car("p987hj", "Volga", "Red", 610, 704340));
        cars.add(new Car("c987ss", "Toyota", "White", 254000, 761000));
        cars.add(new Car("o983op", "Toyota", "Black", 698000, 740000));
        cars.add(new Car("p146op", "BMW", "White", 271000, 850000));
        cars.add(new Car("u893ii", "Toyota", "Purple", 210900, 440000));
        cars.add(new Car("l097df", "Toyota", "Black", 108000, 780000));
        cars.add(new Car("y876wd", "Toyota", "Black", 160000, 1000000));

        // Выводим все автомобили
        System.out.println("Автомобили в базе:");
        System.out.println("Number Model Color Mileage Cost");
        for (Car car : cars) {
            System.out.println(car);
        }

        // Параметры для поиска
        String colorToFind = "Black";
        long mileageToFind = 0L;
        long minPrice = 700000L;
        long maxPrice = 800000L;
        String modelToFind1 = "Toyota";
        String modelToFind2 = "Volvo";

        // 1) Номера всех автомобилей, имеющих заданный цвет или нулевой пробег
        Set<String> carsByColorOrMileage = CarProcessor.findCarsByColorOrMileage(cars, colorToFind, mileageToFind);
        System.out.println("\nНомера автомобилей по цвету или пробегу: " +
                String.join(" ", carsByColorOrMileage));



        // 3) Цвет автомобиля с минимальной стоимостью
        String minCostColor = CarProcessor.findColorOfCheapestCar(cars).orElse("Не найден");
        System.out.println("Цвет автомобиля с минимальной стоимостью: " + minCostColor);

        // 4) Средняя стоимость искомой модели
        double avgCost1 = CarProcessor.calculateAverageCostByModel(cars, modelToFind1);
        double avgCost2 = CarProcessor.calculateAverageCostByModel(cars, modelToFind2);
        System.out.printf("Средняя стоимость модели %s: %.2f%n", modelToFind1, avgCost1);
        System.out.printf("Средняя стоимость модели %s: %.2f%n", modelToFind2, avgCost2);
    }
}