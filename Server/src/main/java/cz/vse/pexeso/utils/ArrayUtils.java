package cz.vse.pexeso.utils;

public class ArrayUtils {

    public static boolean contains(Iterable<Object> arr, Object o) {
        for(var item : arr) {
            if(item.equals(o)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(Object[] arr, Object o) {
        for(var item : arr) {
            if(item.equals(o)) {
                return true;
            }
        }
        return false;
    }

}
