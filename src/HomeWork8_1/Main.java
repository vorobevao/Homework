package HomeWork8_1;

import java.util.ArrayList;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        UniqueElements uniqueElements = new UniqueElements();


        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(2);
        numbers.add(1);

        Set<Integer> uniqueNumbers = uniqueElements.getUniqueElements(numbers);
        System.out.println("Уникальные числа: " + uniqueNumbers);


        ArrayList<String> words = new ArrayList<>();
        words.add("груша");
        words.add("слива");
        words.add("слива");
        words.add("слива");
        words.add("груша");
        words.add("яблоко");
        words.add("яблоко");

        Set<String> uniqueWords = uniqueElements.getUniqueElements(words);
        System.out.println("Уникальные слова: " + uniqueWords);
    }
      }