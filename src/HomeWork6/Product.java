package HomeWork6;

import java.util.Objects;

public class Product {
    private String name;
    private double cost;

    public Product(String name, double cost) {
        setName(name);
        setCost(cost);
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public void setCost(double cost) {
        if (cost <= 0) {
            throw new IllegalArgumentException("Стоимость продукта должна быть положительной");
        }
        this.cost = cost;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя продукта не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Имя продукта не может быть короче 3 символов");
        }
        if (name.matches("\\d+")) {
            throw new IllegalArgumentException("Имя продукта не может содержать только цифры");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.cost, cost) == 0 &&
                Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cost);
    }

    @Override
    public String toString() {
        return name + " (Цена: " + cost + ")";
    }
}