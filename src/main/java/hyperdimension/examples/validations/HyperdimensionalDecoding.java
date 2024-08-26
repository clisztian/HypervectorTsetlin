package hyperdimension.examples.validations;

import com.github.kilianB.pcg.sync.PcgRR;

import java.util.Arrays;
import java.util.stream.IntStream;

import static util.Quantize.inverseQuantize;
import static util.Quantize.quantize;

public class HyperdimensionalDecoding {

    private static final int D = 10000;  // Dimensionality of the hyperdimensional space
    private static  int N = 3;  // Number of scalars
    private static int Q = 10; // Number of quantization levels

    private static PcgRR rng = new PcgRR();

    public static void main(String[] args) {
        double[] scalars = {1.5, 3.2, -0.7, -0.1, 1.0, 2.5, 3.2, 1.2, 5.2};
        N = scalars.length;

        //find the min and max values in the scalars
        double minValue = Arrays.stream(scalars).min().getAsDouble();
        double maxValue = Arrays.stream(scalars).max().getAsDouble();


        int[] quantizedScalars = quantizeScalars(scalars, minValue, maxValue, Q);
        boolean[][] basisVectors = generateVectors(N, D);
        boolean[][] levelVectors = generateVectors(Q, D);
        boolean[][] encodedVectors = encodeScalars(basisVectors, levelVectors, quantizedScalars);
        boolean[] hyperdimensionalVector = aggregateVectors(encodedVectors);

        // Decode
        double[] decodedScalars = decodeHyperdimensionalVector(hyperdimensionalVector, basisVectors, levelVectors, minValue, maxValue, Q);
        for (double scalar : decodedScalars) {
            System.out.println("Decoded scalar: " + scalar);
        }
    }

    public static double[] generateRandomVector(int dimension) {
        double[] vector = new double[dimension];
        for (int i = 0; i < dimension; i++) {
            vector[i] = 10.0*rng.nextDouble();
        }
        return vector;
    }

    // Parallelized quantizeScalars function
    public static int[] quantizeScalars(double[] scalars, double minValue, double maxValue, int numLevels) {
        return Arrays.stream(scalars).parallel()
                .mapToInt(scalar -> quantize(scalar, minValue, maxValue, numLevels))
                .toArray();
    }

    // Parallelized function to generate level vectors
    private static boolean[][] generateVectors(int numVectors, int dimension) {
        boolean[][] vectors = new boolean[numVectors][dimension];

        IntStream.range(0, numVectors).parallel().forEach(i -> {
            boolean[] vector = new boolean[dimension];
            IntStream.range(0, dimension).parallel().forEach(j -> vector[j] = rng.nextBoolean());
            vectors[i] = vector;
        });

        return vectors;
    }

    // Encode scalars into binary vectors
    private static boolean[][] encodeScalars(boolean[][] basisVectors, boolean[][] levelVectors, int[] quantizedScalars) {
        boolean[][] encodedVectors = new boolean[quantizedScalars.length][D];
        for (int i = 0; i < quantizedScalars.length; i++) {
            encodedVectors[i] = xorVectors(basisVectors[i], levelVectors[quantizedScalars[i]]);
        }
        return encodedVectors;
    }

    // Perform XOR on two binary vectors using parallel streams
    private static boolean[] xorVectors(boolean[] vector1, boolean[] vector2) {
        int length = vector1.length;
        boolean[] result = new boolean[length];

        IntStream.range(0, length).parallel().forEach(i -> result[i] = vector1[i] ^ vector2[i]);

        return result;
    }

    // Aggregate encoded vectors into a single hyperdimensional vector using addition (bundling)
    public static boolean[] aggregateVectors(boolean[][] vectors) {
        int dimension = vectors[0].length;
        int numVectors = vectors.length;

        // Count the number of true values for each position
        int[] trueCounts = IntStream.range(0, dimension).parallel()
                .map(i -> (int) Arrays.stream(vectors).filter(vector -> vector[i]).count())
                .toArray();

        // Determine the majority value for each position
        boolean[] aggregatedVector = new boolean[dimension];
        IntStream.range(0, dimension).parallel().forEach(i -> {
            aggregatedVector[i] = trueCounts[i] > numVectors / 2;
        });

        return aggregatedVector;
    }


//    // Decode hyperdimensional vector to original scalar values
//    private static double[] decodeHyperdimensionalVectorSerial(boolean[] hyperVector, boolean[][] basisVectors, boolean[][] levelVectors, double minValue, double maxValue, int numLevels) {
//        int[] votes = new int[basisVectors.length];
//
//        // For each basis vector, calculate similarity and vote on quantization levels
//        for (int i = 0; i < basisVectors.length; i++) {
//            int[] levelVotes = new int[levelVectors.length];
//
//            for (int level = 0; level < levelVectors.length; level++) {
//                boolean[] candidate = xorVectors(basisVectors[i], levelVectors[level]);
//                int similarity = calculateSimilarity(hyperVector, candidate);
//                levelVotes[level] = similarity;
//            }
//
//            int maxVote = 0;
//            for (int j = 1; j < levelVotes.length; j++) {
//                if (levelVotes[j] > levelVotes[maxVote]) {
//                    maxVote = j;
//                }
//            }
//            votes[i] = maxVote;
//        }
//
//        // Map votes back to scalar values
//        double[] decodedScalars = new double[votes.length];
//        for (int i = 0; i < votes.length; i++) {
//            decodedScalars[i] = inverseQuantize(votes[i], minValue, maxValue, numLevels);
//        }
//        return decodedScalars;
//    }

    // Parallelized decode hyperdimensional vector function
    private static double[] decodeHyperdimensionalVector(boolean[] hyperVector, boolean[][] basisVectors, boolean[][] levelVectors, double minValue, double maxValue, int numLevels) {
        int[] votes = IntStream.range(0, basisVectors.length).parallel().map(i -> {
            int[] levelVotes = new int[levelVectors.length];

            IntStream.range(0, levelVectors.length).parallel().forEach(level -> {
                boolean[] candidate = xorVectors(basisVectors[i], levelVectors[level]);
                int similarity = calculateSimilarity(hyperVector, candidate);
                levelVotes[level] = similarity;
            });

            return IntStream.range(1, levelVotes.length).reduce(0, (maxVote, j) -> levelVotes[j] > levelVotes[maxVote] ? j : maxVote);
        }).toArray();

        // Map votes back to scalar values
        return IntStream.range(0, votes.length).parallel()
                .mapToDouble(i -> inverseQuantize(votes[i], minValue, maxValue, numLevels))
                .toArray();
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


    // Perform permutation on a binary vector using a cyclical shift
    public static boolean[] permute(boolean[] vector, int shift) {
        int length = vector.length;
        boolean[] permutedVector = new boolean[length];

        IntStream.range(0, length).parallel().forEach(i -> {
            permutedVector[(i + shift) % length] = vector[i];
        });

        return permutedVector;
    }

    // Perform inverse permutation on a binary vector using a cyclical shift
    public static boolean[] inversePermute(boolean[] vector, int shift) {
        int length = vector.length;
        boolean[] inversePermutedVector = new boolean[length];

        IntStream.range(0, length).parallel().forEach(i -> {
            inversePermutedVector[i] = vector[(i + length - shift) % length];
        });

        return inversePermutedVector;
    }


    public static void speedTest(int dimension) {

        long startTime = System.nanoTime();

        double[] scalars = generateRandomVector(dimension);
        N = scalars.length;

        //find the min and max values in the scalars
        double minValue = Arrays.stream(scalars).min().getAsDouble();
        double maxValue = Arrays.stream(scalars).max().getAsDouble();


        int[] quantizedScalars = quantizeScalars(scalars, minValue, maxValue, Q);
        boolean[][] basisVectors = generateVectors(N, D);
        boolean[][] levelVectors = generateVectors(Q, D);
        boolean[][] encodedVectors = encodeScalars(basisVectors, levelVectors, quantizedScalars);
        boolean[] hyperdimensionalVector = aggregateVectors(encodedVectors);

        // Decode
        double[] decodedScalars = decodeHyperdimensionalVector(hyperdimensionalVector, basisVectors, levelVectors, minValue, maxValue, Q);
        //print out the decoded scalars with the original scalar values for comparison purposes System.out.println("Decoded scalar: " + decodedScalars[i] + " " +  scalars[i]);

//        for (int i = 0; i < decodedScalars.length; i++) {
//            System.out.println("Decoded scalar: " + decodedScalars[i] + " " +  scalars[i]);
//        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        // Print out the duration of the test in milliseconds
        System.out.println("Duration: " + duration / 1000000);



    }


}

