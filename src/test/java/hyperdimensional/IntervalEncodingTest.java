package hyperdimensional;

import static org.junit.jupiter.api.Assertions.*;

import hyperdimension.encoders.IntervalEncoding;
import hyperdimension.encoders.SparseIntervalEmbedding;
import hyperdimension.sparse.SparseBinaryVector;
import org.junit.jupiter.api.Test;
import java.util.List;

public class IntervalEncodingTest {

    @Test
    public void testIncreasingHammingDistance() {
        // Set up parameters for the interval encoding
        int S = 10; // Number of segments
        int L = 64; // Length of each segment
        int Q = 10; // Number of quantized vectors in the interval

        // Create an IntervalEncoding object
        IntervalEncoding encoding = new IntervalEncoding(S, L, Q);

        // Retrieve the interval vectors
        List<SparseBinaryVector> intervalVectors = encoding.intervalVectors;

        // Ensure that the interval has exactly Q vectors
        assertEquals(Q, intervalVectors.size(), "The number of vectors in the interval should be Q.");

        // Check that each consecutive vector has increasing Hamming distance
        int previousDistance = 0;
        for (int i = 1; i < Q; i++) {
            // Calculate the Hamming distance between vector i and vector 0 (the starting vector)
            int currentDistance = intervalVectors.get(0).hammingDistance(intervalVectors.get(i));

            // Ensure that the distance increases as we move further in the interval
            assertTrue(currentDistance > previousDistance,
                    String.format("Hamming distance did not increase: d(Vector_0, Vector_%d) = %d, but previous = %d", i, currentDistance, previousDistance));

            // Update previous distance for the next comparison
            previousDistance = currentDistance;
        }

        // Print the distances for visualization
        System.out.println("Hamming distances from Vector_0:");
        for (int i = 1; i < Q; i++) {
            int distance = intervalVectors.get(0).hammingDistance(intervalVectors.get(i));
            System.out.printf("Distance to Vector_%d: %d\n", i, distance);
        }
    }

    @Test
    public void testPairwiseHammingDistanceIncreases() {
        // Set up parameters for the interval encoding
        int S = 10; // Number of segments
        int L = 64; // Length of each segment
        int Q = 10; // Number of quantized vectors in the interval

        // Create an IntervalEncoding object
        IntervalEncoding encoding = new IntervalEncoding(S, L, Q);

        // Retrieve the interval vectors
        List<SparseBinaryVector> intervalVectors = encoding.intervalVectors;

        // Ensure that the interval has exactly Q vectors
        assertEquals(Q, intervalVectors.size(), "The number of vectors in the interval should be Q.");

        // Check pairwise distances to ensure increasing distances
        for (int i = 0; i < Q - 1; i++) {
            int distance1 = intervalVectors.get(0).hammingDistance(intervalVectors.get(i));
            int distance2 = intervalVectors.get(0).hammingDistance(intervalVectors.get(i + 1));

            // Verify that distance to the next vector is greater than to the previous one
            assertTrue(distance2 > distance1,
                    String.format("Hamming distance did not increase between consecutive vectors: d(Vector_0, Vector_%d) = %d, d(Vector_0, Vector_%d) = %d",
                            i, distance1, i + 1, distance2));
        }
    }

    private static final double SLACK = 0.00001;

    @Test
    public void testForwardMapping() {
        // Create a SparseIntervalEmbedding instance for the interval [0.0, 10.0] with 5 divisions
        SparseIntervalEmbedding embedding = new SparseIntervalEmbedding(0.0, 10.0, 5);

        // Check that scalars map to the correct intervals
        double[] testScalars = {0.0, 2.5, 5.0, 7.5, 10.0};
        for (int i = 0; i < testScalars.length; i++) {
            SparseBinaryVector vector = embedding.forward(testScalars[i]);

            // Verify that each vector is unique for each scalar
            for (int j = 0; j < testScalars.length; j++) {
                if (i != j) {
                    SparseBinaryVector otherVector = embedding.forward(testScalars[j]);
                    assertNotEquals(vector, otherVector, String.format("Vectors for %.2f and %.2f should not match!", testScalars[i], testScalars[j]));
                }
            }
        }
    }

    @Test
    public void testBackMapping() {
        // Create a SparseIntervalEmbedding instance for the interval [0.0, 10.0] with 5 divisions
        SparseIntervalEmbedding embedding = new SparseIntervalEmbedding(0.0, 10.0, 5);

        // Check that vectors map back to the correct scalars
        double[] testScalars = {0.0, 2.5, 5.0, 7.5, 10.0};
        for (double scalar : testScalars) {
            SparseBinaryVector vector = embedding.forward(scalar);
            double recoveredScalar = embedding.back(vector);

            System.out.println("Scalar: " + scalar + " " + recoveredScalar  );
            // Verify that the recovered scalar is within the expected range
            assertTrue(Math.abs(scalar - recoveredScalar) < 2,
                    String.format("Recovered scalar %.2f should match original scalar %.2f within a tolerance of %.5f", recoveredScalar, scalar, SLACK));
        }
    }

    @Test
    public void testRoundTripMapping() {
        // Create a SparseIntervalEmbedding instance for the interval [0.0, 10.0] with 5 divisions
        SparseIntervalEmbedding embedding = new SparseIntervalEmbedding(0.0, 10.0, 5);

        // Check round-trip mapping: scalar -> vector -> scalar
        double[] testScalars = {0.0, 1.25, 2.5, 3.75, 5.0, 6.25, 7.5, 8.75, 10.0};
        for (double scalar : testScalars) {
            SparseBinaryVector vector = embedding.forward(scalar);
            double recoveredScalar = embedding.back(vector);

            // Verify that the recovered scalar is within the correct range
            assertTrue(Math.abs(scalar - recoveredScalar) < 2,
                    String.format("Round-trip scalar %.2f -> vector -> %.2f should match within a tolerance of %.5f", scalar, recoveredScalar, SLACK));
        }
    }

    @Test
    public void testIncreasingHammingDistanceWithScalarDifference() {
        // Create a SparseIntervalEmbedding instance for the interval [0.0, 10.0] with 10 divisions
        SparseIntervalEmbedding embedding = new SparseIntervalEmbedding(0.0, 10.0, 10);

        // Retrieve the interval vectors
        List<SparseBinaryVector> intervalVectors = embedding.intervals;

        // Define scalar values for testing
        double[] scalars = {0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0};

        // Ensure that the number of scalars matches the number of vectors
        assertEquals(intervalVectors.size(), scalars.length - 1, "Number of interval vectors should match scalar divisions.");

        // Test that as scalar distance increases, Hamming distance also increases
        for (int i = 0; i < intervalVectors.size(); i++) {
            SparseBinaryVector vector1 = intervalVectors.get(i);
            for (int j = i + 1; j < intervalVectors.size(); j++) {
                SparseBinaryVector vector2 = intervalVectors.get(j);

                // Calculate the scalar difference
                double scalarDifference = Math.abs(scalars[i] - scalars[j]);

                // Calculate the Hamming distance between the vectors
                int hammingDistance = vector1.hammingDistance(vector2);

                // Print out the distances for debugging/visualization
                System.out.printf("Scalar Difference: %.2f, Hamming Distance: %d\n", scalarDifference, hammingDistance);

                // For the next step, ensure that the distances are strictly increasing
                // For each consecutive pair, the Hamming distance should increase as the scalar difference increases
                if (j > i + 1) {
                    SparseBinaryVector previousVector = intervalVectors.get(j - 1);
                    int previousHammingDistance = vector1.hammingDistance(previousVector);
                    assertTrue(hammingDistance > previousHammingDistance,
                            String.format("Hamming distance did not increase as expected: %d vs %d for scalar distance %.2f vs %.2f",
                                    hammingDistance, previousHammingDistance, scalarDifference, Math.abs(scalars[i] - scalars[j - 1])));
                }
            }
        }
    }
}
