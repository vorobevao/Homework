package HomeWork7;

import java.util.Arrays;
import java.util.Scanner;

public class AnagramChecker {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите первое слово:");
        String s = scanner.nextLine();

        System.out.println("Введите второе слово:");
        String t = scanner.nextLine();

        System.out.println(isAnagram(s, t));
    }
    public static boolean isAnagram(String s, String t) {
        String s1 = s.replaceAll("[^\\p{L}]", "").toLowerCase();
        String t1 = t.replaceAll("[^\\p{L}]", "").toLowerCase();

        if (s1.length() != t1.length()) {
            return false;
        }
        char[] s1Array = s1.toCharArray();
        char[] t1Array = t1.toCharArray();
        Arrays.sort(s1Array);
        Arrays.sort(t1Array);

    return Arrays.equals(s1Array,t1Array);
    }

}
