package HomeWork8_2;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AnagramChecker checker = new AnagramChecker();

        System.out.println("Введите первое слово:");
        String s = scanner.nextLine();

        System.out.println("Введите второе слово:");
        String t = scanner.nextLine();

        boolean result = checker.isAnagram(s, t);
        System.out.println(result);

        scanner.close();
    }
}