package HomeWork8_2;

import java.util.Arrays;

public class AnagramChecker {

    public boolean isAnagram(String s, String t) {

        String cleanedS = cleanString(s);
        String cleanedT = cleanString(t);


        if (cleanedS.length() != cleanedT.length()) {
            return false;
        }

        char[] sArray = cleanedS.toCharArray();
        char[] tArray = cleanedT.toCharArray();
        Arrays.sort(sArray);
        Arrays.sort(tArray);


        return Arrays.equals(sArray, tArray);
    }

    private String cleanString(String input) {
        return input.replaceAll("[^\\p{L}]", "").toLowerCase();
    }
}