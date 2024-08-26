package util;

public class Quantization {

    public static int[] quantizeValue(double min, double max, int numberDims, double value) {
        int[] encoding = new int[numberDims];

        // Calculate the width of each bucket
        double bucketWidth = (max - min) / (numberDims - 1);

        // Determine the number of buckets that should be "filled"
        int numFilledBuckets = (int) Math.ceil((value - min) / bucketWidth);

        // Ensure that the number of filled buckets does not exceed numberDims
        numFilledBuckets = Math.min(numFilledBuckets, numberDims);

        // Fill the buckets up to numFilledBuckets
        for (int i = 0; i < numFilledBuckets; i++) {
            encoding[i] = 1;
        }

        return encoding;
    }

    public static void main(String[] args) {
        double min = 0.0;
        double max = 100.0;
        int numberDims = 10;

        double value = 101.0;
        int[] encoding = quantizeValue(min, max, numberDims, value);

        // Print the encoding
        System.out.println("Encoding for value " + value + ": " + java.util.Arrays.toString(encoding));
    }
}
