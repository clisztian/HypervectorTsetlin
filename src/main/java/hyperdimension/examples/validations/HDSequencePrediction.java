package hyperdimension.examples.validations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HDSequencePrediction {

    private static final int D = 10000;  // Dimensionality of the hyperdimensional space
    private static final Random rng = new Random();

    public static void main(String[] args) {
        // Example sequences (e.g., sequences of integers representing different patterns)
        int[][] sequences = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 0},
                {1, 3, 5, 7, 9},
                {2, 4, 6, 8, 0}
        };

        // Parameters for n-gram encoding
        int n = 3;  // Length of n-grams

        // Generate random vectors for values and positions
        boolean[][] valueVectors = generateRandomVectors(10, D);  // Assuming values are in the range [0, 9]
        boolean[][] positionVectors = generateRandomVectors(n, D);  // Positions within the n-gram

        // Encode and store the sequences in associative memory
        Map<boolean[], Integer> associativeMemory = new HashMap<>();
        for (int[] sequence : sequences) {
            for (int i = 0; i <= sequence.length - n; i++) {
                int[] nGram = Arrays.copyOfRange(sequence, i, i + n);
                boolean[] encodedVector = encodeSequence(nGram, valueVectors, positionVectors, n);
                int nextElement = (i + n < sequence.length) ? sequence[i + n] : -1;
                associativeMemory.put(encodedVector, nextElement);
            }
        }

        // Query sequence
        int[] querySequence = {1, 3, 5};
        boolean[] queryVector = encodeSequence(querySequence, valueVectors, positionVectors, n);

        // Predict the next element in the sequence
        int predictedElement = predictNextElement(queryVector, associativeMemory);

        // Output the query and predicted next element
        System.out.println("Query Sequence: " + Arrays.toString(querySequence));
        System.out.println("Predicted Next Element: " + predictedElement);
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

    // Encode a sequence using n-gram encoding and temporal binding
    public static boolean[] encodeSequence(int[] sequence, boolean[][] valueVectors, boolean[][] positionVectors, int n) {
        int length = sequence.length;
        int numGrams = length - n + 1;

        boolean[][] nGrams = new boolean[numGrams][D];

        // Generate n-grams with temporal binding
        for (int i = 0; i < numGrams; i++) {
            boolean[] nGramVector = new boolean[D];
            Arrays.fill(nGramVector, false);

            for (int j = 0; j < n; j++) {
                boolean[] permutedValueVector = permute(valueVectors[sequence[i + j]], j);
                boolean[] positionVector = positionVectors[j];
                boolean[] combinedVector = xorVectors(permutedValueVector, positionVector);
                nGramVector = xorVectors(nGramVector, combinedVector);
            }

            nGrams[i] = nGramVector;
        }

        // Aggregate n-grams to form the final sequence vector
        return aggregateVectors(nGrams);
    }

    // Perform permutation on a binary vector using a cyclical shift
    public static boolean[] permute(boolean[] vector, int shift) {
        int length = vector.length;
        boolean[] permutedVector = new boolean[length];
        for (int i = 0; i < length; i++) {
            permutedVector[(i + shift) % length] = vector[i];
        }
        return permutedVector;
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

    // Aggregate encoded vectors into a single hyperdimensional vector using addition (bundling)
    public static boolean[] aggregateVectors(boolean[][] vectors) {
        int dimension = vectors[0].length;
        int numVectors = vectors.length;

        // Count the number of true values for each position
        int[] trueCounts = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            int count = 0;
            for (boolean[] vector : vectors) {
                if (vector[i]) {
                    count++;
                }
            }
            trueCounts[i] = count;
        }

        // Determine the majority value for each position
        boolean[] aggregatedVector = new boolean[dimension];
        for (int i = 0; i < dimension; i++) {
            aggregatedVector[i] = trueCounts[i] > numVectors / 2;
        }

        return aggregatedVector;
    }

    // Predict the next element in the sequence using associative memory
    public static int predictNextElement(boolean[] queryVector, Map<boolean[], Integer> associativeMemory) {
        int bestMatch = -1;
        int minDistance = Integer.MAX_VALUE;

        for (Map.Entry<boolean[], Integer> entry : associativeMemory.entrySet()) {
            int distance = calculateHammingDistance(queryVector, entry.getKey());
            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = entry.getValue();
            }
        }

        return bestMatch;
    }

    // Calculate Hamming distance between two binary vectors
    public static int calculateHammingDistance(boolean[] vector1, boolean[] vector2) {
        int distance = 0;
        for (int i = 0; i < vector1.length; i++) {
            if (vector1[i] != vector2[i]) {
                distance++;
            }
        }
        return distance;
    }
}
