package cz.vse.pexeso.utils;

public class Rand {

    public static int between(int min, int max) {
        int start = Math.min(min, max);
        double gen = Math.random();
        int rnd = (int) (gen * (max - min + 1));
        return start + rnd;
    }

}
