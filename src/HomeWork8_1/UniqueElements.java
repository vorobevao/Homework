package HomeWork8_1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class UniqueElements {

    public <T> Set<T> getUniqueElements(ArrayList<T> list) {

        return new HashSet<>(list);
    }
}
