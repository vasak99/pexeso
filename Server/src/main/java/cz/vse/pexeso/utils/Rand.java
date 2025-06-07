package cz.vse.pexeso.utils;

/**
 * Utility class for handling random number generation
 */
public final class Rand {

    private Rand() {}

    /**
     * Generates a random integer within given bounds
     * @param min lower bound
     * @param max upper bound
     * @return {@link Integer}
     */
    public static int between(int min, int max) {
        int start = Math.min(min, max);
        double gen = Math.random();
        int rnd = (int) (gen * (max - min + 1));
        return start + rnd;
    }

}
