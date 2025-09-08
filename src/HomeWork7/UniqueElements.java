package HomeWork7;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UniqueElements {
    public static <T> Set <T> getUniqueElements(ArrayList<T> list) {
        return new HashSet<>(list);
    }

    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(2);
        numbers.add(1);

        Set<Integer> uniqueNumbers = getUniqueElements(numbers);
        System.out.println("Уникальные элементы:" + uniqueNumbers );

        ArrayList<String> words = new ArrayList<>();
        words.add("груша");
        words.add("слива");
        words.add("слива");
        words.add("слива");
        words.add("груша");
        words.add("яблоко");
        words.add("яблоко");
       Set<String>  uniqueWords = getUniqueElements(words);
       System.out.println("Уникальные слова:" + uniqueWords);
    }
}
