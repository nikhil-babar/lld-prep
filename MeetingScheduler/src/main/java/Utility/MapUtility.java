package Utility;

import java.util.Map;
import java.util.NoSuchElementException;

public class MapUtility {
    public static <T> T require(Map<String, T> map, String id, String kind) {
        T val = map.get(id);
        if(val == null) throw new NoSuchElementException(kind + " with id: " + id + " not found");
        return val;
    }
}
