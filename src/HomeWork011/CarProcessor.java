package HomeWork011;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class CarProcessor {

    public static Set<String> findCarsByColorOrMileage(List<Car> cars, String color, long mileage) {
        return cars.stream()
                .filter(c -> c.getColor().equals(color) || c.getMileage() == mileage)
                .map(Car::getNumber)
                .collect(Collectors.toSet());
    }

    public static long countUniqueModelsInPriceRange(List<Car> cars, long minPrice, long maxPrice) {
        return cars.stream()
                .filter(c -> c.getCost() >= minPrice && c.getCost() <= maxPrice)
                .map(Car::getModel)
                .distinct()
                .count();
    }

    public static Optional<String> findColorOfCheapestCar(List<Car> cars) {
        return cars.stream()
                .min((c1, c2) -> Long.compare(c1.getCost(), c2.getCost()))
                .map(Car::getColor);
    }

    public static double calculateAverageCostByModel(List<Car> cars, String model) {
        return cars.stream()
                .filter(c -> c.getModel().equals(model))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0.0);
    }
}