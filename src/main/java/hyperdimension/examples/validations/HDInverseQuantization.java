package hyperdimension.examples.validations;

import java.util.Arrays;
import java.util.Random;

public class HDInverseQuantization {

    private static final int D = 10000;  // Dimensionality of the hyperdimensional space
    private static final int N = 5;  // Number of scalars
    private static final int Q = 10; // Number of quantization levels
    private static final Random rng = new Random();

    public static void main(String[] args) {
        // Example sequence (e.g., sequence of integers representing a time series)
        double[] originalSequence = {1.5, 3.2, -0.7, -0.1, 1.0};

        // Find the min and max values in the sequence
        double minValue = Arrays.stream(originalSequence).min().getAsDouble();
        double maxValue = Arrays.stream(originalSequence).max().getAsDouble();

        // Quantize the sequence
        int[] quantizedSequence = quantizeScalars(originalSequence, minValue, maxValue, Q);

        // Generate random vectors for values and positions
        boolean[][] valueVectors = generateRandomVectors(Q, D);  // Quantization levels
        boolean[][] positionVectors = generateRandomVectors(N, D);  // Positions within the sequence

        // Encode the sequence
        boolean[] encodedVector = encodeSequence(originalSequence, valueVectors, positionVectors, Q);

        // Decode the sequence
        double[] decodedSequence = decodeSequence(encodedVector, valueVectors, positionVectors, minValue, maxValue, Q);

        // Output the original and decoded sequences for comparison
        System.out.println("Original Sequence: " + Arrays.toString(originalSequence));
        System.out.println("Decoded Sequence: " + Arrays.toString(decodedSequence));
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

    // Quantize a sequence of scalars
    public static int[] quantizeScalars(double[] scalars, double minValue, double maxValue, int numLevels) {
        int[] quantized = new int[scalars.length];
        for (int i = 0; i < scalars.length; i++) {
            quantized[i] = quantize(scalars[i], minValue, maxValue, numLevels);
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

    // Encode a sequence using n-gram encoding and temporal binding
    public static boolean[] encodeSequence(double[] sequence, boolean[][] valueVectors, boolean[][] positionVectors, int numLevels) {
        int length = sequence.length;
        int[] quantizedSequence = quantizeScalars(sequence, Arrays.stream(sequence).min().getAsDouble(), Arrays.stream(sequence).max().getAsDouble(), numLevels);

        boolean[] encodedVector = new boolean[D];
        Arrays.fill(encodedVector, false);

        for (int i = 0; i < length; i++) {
            boolean[] valueVector = valueVectors[quantizedSequence[i]];
            boolean[] positionVector = positionVectors[i];
            boolean[] combinedVector = xorVectors(valueVector, positionVector);
            encodedVector = xorVectors(encodedVector, combinedVector);
        }

        return encodedVector;
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

    // Decode a hyperdimensional vector to the original sequence
    public static double[] decodeSequence(boolean[] hyperVector, boolean[][] valueVectors, boolean[][] positionVectors, double minValue, double maxValue, int numLevels) {
        int length = positionVectors.length;
        double[] decodedSequence = new double[length];

        for (int i = 0; i < length; i++) {
            int[] levelVotes = new int[numLevels];

            for (int level = 0; level < numLevels; level++) {
                boolean[] candidate = xorVectors(hyperVector, positionVectors[i]);
                boolean[] levelVector = xorVectors(candidate, valueVectors[level]);
                int similarity = calculateSimilarity(hyperVector, levelVector);
                levelVotes[level] = similarity;
            }

            int maxVote = 0;
            for (int j = 1; j < levelVotes.length; j++) {
                if (levelVotes[j] > levelVotes[maxVote]) {
                    maxVote = j;
                }
            }
            decodedSequence[i] = inverseQuantize(maxVote, minValue, maxValue, numLevels);
        }

        return decodedSequence;
    }

    // Generalized inverse quantize function
    public static double inverseQuantize(int quantizedValue, double minValue, double maxValue, int numLevels) {
        double range = maxValue - minValue;
        double step = range / numLevels;
        return minValue + step * (quantizedValue + 0.5);
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
