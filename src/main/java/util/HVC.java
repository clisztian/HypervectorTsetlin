package util;

import java.util.Random;

/**
 * Constants for the HVC model.
 */
public class HVC {

    public static final int DIMENSION = 10000;
    public static final int SEGMENTS = 100;
    public static final int LONG_SIZE = 64;
    public static final int SIZE = 8; // a factor of DIMENSION
    public static final int BYTE_SIZE = DIMENSION / SIZE;

    public static final int TSETLIN_BYTE = 32;
    public static final double RELATED_THRESHOLD = .46;

    public static final double SPARSE_RELATED_THRESHOLD = .2;
    public static Random rnd = new Random(34); // Seed for reproducibility
}
