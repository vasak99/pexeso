package cz.vse.pexeso.utils;

/**
 * Custom built utility methods for working with arrays
 */
public final class ArrayUtils {

    private ArrayUtils() {}

    /**
     * Determines whether array contains given object - for <see cref="Iterable">Iterable</see>
     * @param arr An iterable collection of items
     * @param o object to be found in array
     * @return boolean
     */
    public static boolean contains(Iterable<Object> arr, Object o) {
        for(var item : arr) {
            if(item.equals(o)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether array contains given object - for builtin arrays
     * @param arr An iterable collection of items
     * @param o object to be found in array
     * @return boolean
     */
    public static boolean contains(Object[] arr, Object o) {
        for(var item : arr) {
            if(item.equals(o)) {
                return true;
            }
        }
        return false;
    }

}
