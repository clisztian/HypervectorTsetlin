package util;

import java.util.List;
import java.util.stream.IntStream;

public class Quantize {

    public static int quantize(double value, double minValue, double maxValue, int numLevels) {
        if (value <= minValue) {
            return 0;
        } else if (value >= maxValue) {
            return numLevels - 1;
        } else {
            double range = maxValue - minValue;
            double step = range / numLevels;
            return (int) ((value - minValue) / step);
        }
    }

    // Generalized inverse quantize function
    public static double inverseQuantize(int quantizedValue, double minValue, double maxValue, int numLevels) {
        double range = maxValue - minValue;
        double step = range / numLevels;
        return minValue + step * (quantizedValue + 0.5);
    }

    public static double hammingDistance(boolean[] vector1, boolean[] vector2, int D) {
        // Check if vectors are of the same length
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must be of the same length");
        }

        int distance = 0;

        // Compute Hamming distance
        for (int i = 0; i < vector1.length; i++) {
            if (vector1[i] != vector2[i]) {
                distance++;
            }
        }

        return 1.0 - (double) distance/(double)D;
    }


    // Helper function to convert a List<boolean[]> to a boolean[][] matrix using parallel streams
    public static boolean[][] listToMatrix(List<boolean[]> vectors) {
        int numRows = vectors.size();
        if (numRows == 0) {
            return new boolean[0][0]; // Return an empty matrix if the list is empty
        }

        int numCols = vectors.get(0).length;
        boolean[][] matrix = new boolean[numRows][numCols];

        IntStream.range(0, numRows).parallel().forEach(i -> {
            matrix[i] = vectors.get(i);
        });

        return matrix;
    }


}
