package hyperdimensional;

import hyperdimension.sparse.SparseBinaryVector;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SparseBinaryVectorTest {

    @Test
    public void testBindAndInverseBind() {
        // Example: Create a vector with 4 segments, each of length 64 bits
        SparseBinaryVector vectorA = new SparseBinaryVector(4, 64); // Vector A
        SparseBinaryVector vectorB = new SparseBinaryVector(4, 64); // Vector B

        // Bind A and B
        SparseBinaryVector boundVector = vectorA.bind(vectorB);

        // Perform inverse bind using vector A to recover vector B
        SparseBinaryVector recoveredVectorB = vectorA.inverseBind(boundVector);

        // Check that recovered vector B is equal to the original vector B
        assertArrayEquals(vectorB.getSegments(), recoveredVectorB.getSegments(),
                "Recovered vector B does not match the original vector B after inverse bind.");
    }

    @Test
    public void testKanervaDollarOfMexico() {
        Random random = new Random();

        // Hypervector length parameters
        int numSegments = 100; // Number of segments
        int segmentLength = 64; // Length of each segment

        // Step 1: Create random hypervectors for roles and values
        // Roles: Pcode, Pcapital, Pcurrency
        SparseBinaryVector Pcode = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Pcapital = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Pcurrency = new SparseBinaryVector(numSegments, segmentLength);

        // Values for Mexico: Cmex, CmexicoCity, Cpeso
        SparseBinaryVector Cmex = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector CmexicoCity = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Cpeso = new SparseBinaryVector(numSegments, segmentLength);

        // Values for the USA: Cusa, Cdc, Cdollar
        SparseBinaryVector Cusa = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Cdc = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Cdollar = new SparseBinaryVector(numSegments, segmentLength);

        // Step 2: Encode the knowledge for Mexico and the USA
        // Cmexico = Pcode ⊗ Cmex ⊕ Pcapital ⊗ CmexicoCity ⊕ Pcurrency ⊗ Cpeso
        SparseBinaryVector Cmexico = Pcode.bind(Cmex).sumset(Pcapital.bind(CmexicoCity)).sumset(Pcurrency.bind(Cpeso));

        // Cus = Pcode ⊗ Cusa ⊕ Pcapital ⊗ Cdc ⊕ Pcurrency ⊗ Cdollar
        SparseBinaryVector Cus = Pcode.bind(Cusa).sumset(Pcapital.bind(Cdc)).sumset(Pcurrency.bind(Cdollar));

        // Step 3: Query the knowledge
        // Query 1: Capital of Mexico (Cmexico ⊘ Pcapital ≈ CmexicoCity)
        SparseBinaryVector recoveredMexicoCity = Cmexico.inverseBind(Pcapital);
        //System.out.println("\nQuery 1: Capital of Mexico");
        //recoveredMexicoCity.printVector();

        // Check that the recovered vector is closest to CmexicoCity
        assertMinimumHammingDistance(recoveredMexicoCity, CmexicoCity, new SparseBinaryVector[] {Cmex, Cpeso, Cusa, Cdc, Cdollar});

        // Query 2: Currency of the United States (Cus ⊘ Pcurrency ≈ Cdollar)
        SparseBinaryVector recoveredDollar = Cus.inverseBind(Pcurrency);
        //System.out.println("\nQuery 2: Currency of United States");
        //recoveredDollar.printVector();

        // Check that the recovered vector is closest to Cdollar
        assertMinimumHammingDistance(recoveredDollar, Cdollar, new SparseBinaryVector[] {Cmex, CmexicoCity, Cpeso, Cusa, Cdc});
    }

    @Test
    public void testKanervaDollarOfMexicoOtherBundle() {
        Random random = new Random();


        // Hypervector length parameters
        int numSegments = 100; // Number of segments
        int segmentLength = 64; // Length of each segment

        // Step 1: Create random hypervectors for roles and values
        // Roles: Pcode, Pcapital, Pcurrency
        SparseBinaryVector Pcode = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Pcapital = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Pcurrency = new SparseBinaryVector(numSegments, segmentLength);

        // Values for Mexico: Cmex, CmexicoCity, Cpeso
        SparseBinaryVector Cmex = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector CmexicoCity = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Cpeso = new SparseBinaryVector(numSegments, segmentLength);

        // Values for the USA: Cusa, Cdc, Cdollar
        SparseBinaryVector Cusa = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Cdc = new SparseBinaryVector(numSegments, segmentLength);
        SparseBinaryVector Cdollar = new SparseBinaryVector(numSegments, segmentLength);

        // Step 2: Encode the knowledge for Mexico and the USA
        // Cmexico = Pcode ⊗ Cmex ⊕ Pcapital ⊗ CmexicoCity ⊕ Pcurrency ⊗ Cpeso
        //SparseBinaryVector Cmexico = Pcode.bind(Cmex).sumset(Pcapital.bind(CmexicoCity)).sumset(Pcurrency.bind(Cpeso));

        SparseBinaryVector Cmexico = SparseBinaryVector.bundle(Arrays.asList(Pcode.bind(Cmex), Pcapital.bind(CmexicoCity), Pcurrency.bind(Cpeso)));
        SparseBinaryVector Cus = SparseBinaryVector.bundle(Arrays.asList(Pcode.bind(Cusa), Pcapital.bind(Cdc), Pcurrency.bind(Cdollar)));

        // Step 3: Query the knowledge
        // Query 1: Capital of Mexico (Cmexico ⊘ Pcapital ≈ CmexicoCity)
        SparseBinaryVector recoveredMexicoCity = Cmexico.inverseBind(Pcapital);
        //System.out.println("\nQuery 1: Capital of Mexico");
        //recoveredMexicoCity.printVector();

        // Check that the recovered vector is closest to CmexicoCity
        assertMinimumHammingDistance(recoveredMexicoCity, CmexicoCity, new SparseBinaryVector[] {Cmex, Cpeso, Cusa, Cdc, Cdollar});

        // Query 2: Currency of the United States (Cus ⊘ Pcurrency ≈ Cdollar)
        SparseBinaryVector recoveredDollar = Cus.inverseBind(Pcurrency);
        //System.out.println("\nQuery 2: Currency of United States");
        //recoveredDollar.printVector();

        // Check that the recovered vector is closest to Cdollar
        assertMinimumHammingDistance(recoveredDollar, Cdollar, new SparseBinaryVector[] {Cmex, CmexicoCity, Cpeso, Cusa, Cdc});
    }


    // Helper method to check that the recovered vector has the minimum Hamming distance to the expected vector
    private void assertMinimumHammingDistance(SparseBinaryVector recoveredVector, SparseBinaryVector expectedVector, SparseBinaryVector[] otherCandidates) {
        int minDistance = recoveredVector.hammingDistance(expectedVector);

        System.out.println("Minimum Hamming Distance: " + minDistance);

        for (SparseBinaryVector candidate : otherCandidates) {
            int candidateDistance = recoveredVector.hammingDistance(candidate);

            System.out.println("Hamming Distance on candidate: " + candidateDistance);

            assertTrue(minDistance <= candidateDistance,
                    "Recovered vector is closer to a wrong candidate than the expected vector.");
        }
    }

    @Test
    public void testPermutePositiveShift() {
        // Create a test vector with 4 segments, each of length 4 bits
        SparseBinaryVector vector = new SparseBinaryVector(4, 4);

        // Manually set the segments for controlled testing
        vector.segments[0] = 0b0010; // Segment 0
        vector.segments[1] = 0b0100; // Segment 1
        vector.segments[2] = 0b1000; // Segment 2
        vector.segments[3] = 0b0001; // Segment 3

        // Perform a positive permutation by 2 places
        SparseBinaryVector permutedVector = vector.permute(2);

        // Expected result: circular shift by 2 places
        long[] expectedSegments = {
                0b1000, // Segment 2 -> Segment 0
                0b0001, // Segment 3 -> Segment 1
                0b0010, // Segment 0 -> Segment 2
                0b0100  // Segment 1 -> Segment 3
        };

        // Assert that the permuted vector matches the expected result
        assertArrayEquals(expectedSegments, permutedVector.getSegments(),
                "Permuted vector does not match the expected result for positive shift.");
    }

    @Test
    public void testPermuteNegativeShift() {
        // Create a test vector with 4 segments, each of length 4 bits
        SparseBinaryVector vector = new SparseBinaryVector(4, 4);

        // Manually set the segments for controlled testing
        vector.segments[0] = 0b0010; // Segment 0
        vector.segments[1] = 0b0100; // Segment 1
        vector.segments[2] = 0b1000; // Segment 2
        vector.segments[3] = 0b0001; // Segment 3

        // Perform a negative permutation by -1 place
        SparseBinaryVector permutedVector = vector.permute(-1);

        // Expected result: circular shift by -1 place
        long[] expectedSegments = {
                0b0100, // Segment 1 -> Segment 0
                0b1000, // Segment 2 -> Segment 1
                0b0001, // Segment 3 -> Segment 2
                0b0010  // Segment 0 -> Segment 3
        };

        // Assert that the permuted vector matches the expected result
        assertArrayEquals(expectedSegments, permutedVector.getSegments(),
                "Permuted vector does not match the expected result for negative shift.");
    }

    @Test
    public void testPermuteFullShift() {
        // Create a test vector with 4 segments, each of length 4 bits
        SparseBinaryVector vector = new SparseBinaryVector(4, 4);

        // Manually set the segments for controlled testing
        vector.segments[0] = 0b0010; // Segment 0
        vector.segments[1] = 0b0100; // Segment 1
        vector.segments[2] = 0b1000; // Segment 2
        vector.segments[3] = 0b0001; // Segment 3

        // Perform a shift by the number of segments (which should result in the original vector)
        SparseBinaryVector permutedVector = vector.permute(4);

        // Expected result: should match the original vector after a full cycle
        long[] expectedSegments = {
                0b0010, // Segment 0
                0b0100, // Segment 1
                0b1000, // Segment 2
                0b0001  // Segment 3
        };

        // Assert that the permuted vector matches the original vector after full cycle
        assertArrayEquals(expectedSegments, permutedVector.getSegments(),
                "Permuted vector does not match the original vector after full cycle shift.");
    }
}
