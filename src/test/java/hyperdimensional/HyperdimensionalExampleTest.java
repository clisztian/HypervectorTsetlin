package hyperdimensional;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static hyperdimension.examples.validations.HyperdimensionalDecoding.inversePermute;
import static hyperdimension.examples.validations.HyperdimensionalDecoding.permute;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class HyperdimensionalExampleTest {

    @Test
    public void testPermutationAndInversePermutation() {
        // Create a random binary vector
        int dimension = 10000;
        boolean[] originalVector = generateRandomVector(dimension);

        // Perform permutation and inverse permutation
        int shift = 3;
        boolean[] permutedVector = permute(originalVector, shift);
        boolean[] inversePermutedVector = inversePermute(permutedVector, shift);

        // Check that the original vector and the inverse-permuted vector are the same
        assertArrayEquals(originalVector, inversePermutedVector, "Original and Inverse-Permuted vectors should be the same");
    }

    // Helper method to generate a random binary vector
    private boolean[] generateRandomVector(int dimension) {
        boolean[] vector = new boolean[dimension];
        Random random = new Random();
        for (int i = 0; i < dimension; i++) {
            vector[i] = random.nextBoolean();
        }
        return vector;
    }
}
