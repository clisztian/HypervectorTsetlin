package hyperdimension.examples.validations;

import java.util.Arrays;
import java.util.Random;

public class HDEncoderDecoder {

    private static final int D = 10000;  // Dimensionality of the hyperdimensional space
    private static final int Q = 50;  // Number of quantization levels
    private static final Random rng = new Random();

    public static void main(String[] args) {
        // Example Iris dataset sample
        double[] sample = {5.1, 3.5, 1.4, 0.2};  // Example features: sepal length, sepal width, petal length, petal width

        // Quantize the features
        double[][] featureRanges = {
                {4.3, 7.9},  // Sepal length range
                {2.0, 4.4},  // Sepal width range
                {1.0, 6.9},  // Petal length range
                {0.1, 2.5}   // Petal width range
        };
        int[] quantizedFeatures = quantizeFeatures(sample, featureRanges, Q);

        // Generate random vectors for quantized levels and positions
        boolean[][] quantizedLevelVectors = generateRandomVectors(Q, D);
        boolean[][] positionVectors = generateRandomVectors(4, D);  // 4 features

        // Encode the sample
        boolean[] encodedSample = encodeSample(quantizedFeatures, quantizedLevelVectors, positionVectors);

        // Decode the sample
        double[] decodedSample = decodeSample(encodedSample, quantizedLevelVectors, positionVectors, featureRanges, Q);

        // Output the original and decoded samples
        System.out.println("Original Sample: " + Arrays.toString(sample));
        System.out.println("Decoded Sample: " + Arrays.toString(decodedSample));
    }

    // Quantize features into discrete levels
    public static int[] quantizeFeatures(double[] features, double[][] ranges, int numLevels) {
        int[] quantized = new int[features.length];
        for (int i = 0; i < features.length; i++) {
            quantized[i] = quantize(features[i], ranges[i][0], ranges[i][1], numLevels);
        }
        return quantized;
    }

    // Generalized quantize function
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

    // Generate random binary vectors
    public static boolean[][] generateRandomVectors(int numVectors, int dimension) {
        boolean[][] vectors = new boolean[numVectors][dimension];
        for (int i = 0; i < numVectors; i++) {
            for (int j = 0; j < dimension; j++) {
                vectors[i][j] = rng.nextBoolean();
            }
        }
        return vectors;
    }

    // Encode a sample using HDC
    public static boolean[] encodeSample(int[] quantizedFeatures, boolean[][] quantizedLevelVectors, boolean[][] positionVectors) {
        boolean[] encodedVector = new boolean[D];
        Arrays.fill(encodedVector, false);

        for (int i = 0; i < quantizedFeatures.length; i++) {
            boolean[] quantizedVector = quantizedLevelVectors[quantizedFeatures[i]];
            boolean[] positionVector = positionVectors[i];
            boolean[] combinedVector = xorVectors(quantizedVector, positionVector);
            encodedVector = xorVectors(encodedVector, combinedVector);
        }

        return encodedVector;
    }

    // Decode a sample using HDC
    public static double[] decodeSample(boolean[] encodedVector, boolean[][] quantizedLevelVectors, boolean[][] positionVectors, double[][] ranges, int numLevels) {
        double[] decodedFeatures = new double[positionVectors.length];

        for (int i = 0; i < positionVectors.length; i++) {
            boolean[] positionVector = positionVectors[i];
            int bestMatch = -1;
            int maxSimilarity = Integer.MIN_VALUE;

            for (int k = 0; k < numLevels; k++) {
                boolean[] quantizedVector = quantizedLevelVectors[k];
                boolean[] candidateVector = xorVectors(encodedVector, positionVector);
                int similarity = calculateSimilarity(candidateVector, quantizedVector);
                if (similarity > maxSimilarity) {
                    maxSimilarity = similarity;
                    bestMatch = k;
                }
            }

            decodedFeatures[i] = inverseQuantize(bestMatch, ranges[i][0], ranges[i][1], numLevels);
        }

        return decodedFeatures;
    }

    // Generalized inverse quantize function
    public static double inverseQuantize(int quantizedValue, double minValue, double maxValue, int numLevels) {
        double range = maxValue - minValue;
        double step = range / numLevels;
        return minValue + step * (quantizedValue + 0.5);
    }

    // Perform XOR on two binary vectors
    private static boolean[] xorVectors(boolean[] vector1, boolean[] vector2) {
        int length = vector1.length;
        boolean[] result = new boolean[length];
        for (int i = 0; i < length; i++) {
            result[i] = vector1[i] ^ vector2[i];
        }
        return result;
    }

    // Calculate similarity between two binary vectors
    private static int calculateSimilarity(boolean[] vector1, boolean[] vector2) {
        int similarity = 0;
        for (int i = 0; i < vector1.length; i++) {
            if (vector1[i] == vector2[i]) {
                similarity++;
            }
        }
        return similarity;
    }
}
