package hyperdimension.encoders;

import hyperdimension.sparse.SparseBinaryVector;
import util.HVC;

import java.util.*;

public class IntervalEncoding {

    public final List<SparseBinaryVector> intervalVectors;

    public IntervalEncoding(int S, int L, int Q) {
        // Generate the interval encoding with Q quantized vectors
        this.intervalVectors = generateInterval(S, L, Q);
    }

    public IntervalEncoding(int Q) {
        this(HVC.SEGMENTS, HVC.LONG_SIZE, Q);
    }

    // Method to generate the interval representation
    private List<SparseBinaryVector> generateInterval(int S, int L, int Q) {

        Random random = new Random(System.currentTimeMillis());

        List<SparseBinaryVector> vectors = new ArrayList<>();

        // Step 1: Create the initial sparse binary vector representing "a"
        SparseBinaryVector initialVector = new SparseBinaryVector(S, L);
        vectors.add(initialVector);

        // Step 2: Create Q-1 additional vectors by modifying one segment at a time
        Set<Integer> usedSegments = new HashSet<>(); // Track segments that have been modified

        for (int q = 1; q < Q; q++) {
            SparseBinaryVector previousVector = vectors.get(q - 1); // Get the last vector in the sequence
            SparseBinaryVector newVector = new SparseBinaryVector(S, L);

            // Copy previous vector's segments into the new vector
            System.arraycopy(previousVector.getSegments(), 0, newVector.getSegments(), 0, S);

            // Randomly select a segment that hasn't been modified yet
            List<Integer> availableSegments = new ArrayList<>();
            for (int i = 0; i < S; i++) {
                if (!usedSegments.contains(i)) {
                    availableSegments.add(i);
                }
            }

            // Select a random segment to modify
            int selectedSegment = availableSegments.get(random.nextInt(availableSegments.size()));
            usedSegments.add(selectedSegment);

            // Modify the selected segment by changing the bit that is set to 1
            newVector.segments[selectedSegment] = generateNewSegment(previousVector.segments[selectedSegment], L, random);

            // Add the new vector to the list
            vectors.add(newVector);
        }

        return vectors;
    }

    // Generate a new segment by flipping the 1-bit to another position
    private long generateNewSegment(long segment, int L, Random random) {
        int oldBitPosition = Long.numberOfTrailingZeros(segment); // Get the current position of the 1-bit
        int newBitPosition;

        // Randomly select a new bit position that is not the same as the old one
        do {
            newBitPosition = random.nextInt(L);
        } while (newBitPosition == oldBitPosition);

        // Create a new segment with the new bit position set to 1
        return 1L << newBitPosition;
    }

    // Method to print the interval vectors
    public void printInterval() {
        for (int i = 0; i < intervalVectors.size(); i++) {
            System.out.println("Vector " + i + ":");
            intervalVectors.get(i).printVector();
            System.out.println();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Create an interval encoding with 4 segments, each 4 bits long, and 8 quantized vectors
        IntervalEncoding encoding = new IntervalEncoding(4, 4, 8);

        // Print the interval encoding
        System.out.println("Interval Encoding:");
        encoding.printInterval();
    }
}
