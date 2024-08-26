package hyperdimension.examples.validations;

import java.util.Arrays;
import java.util.Random;

public class HDCImageGeneration {

    private static final int D = 10000;  // Dimensionality of the hyperdimensional space
    private static final Random rng = new Random();

    public static void main(String[] args) {
        // Example grayscale image (4x4 pixels)
        int[][] image = {
                {0, 128, 255, 64},
                {32, 64, 128, 255},
                {255, 0, 64, 128},
                {128, 255, 0, 32}
        };

        // Generate random vectors for pixel values and positions
        boolean[][] pixelValueVectors = generateRandomVectors(256, D);  // 256 possible grayscale values
        boolean[][] positionVectors = generateRandomVectors(16, D);  // 16 possible positions (4x4 image)

        // Encode the image
        boolean[] encodedImage = encodeImage(image, pixelValueVectors, positionVectors);

        // Decode the image
        int[][] decodedImage = decodeImage(encodedImage, pixelValueVectors, positionVectors, 4, 4);

        // Output the original and decoded images
        System.out.println("Original Image:");
        printImage(image);
        System.out.println("Decoded Image:");
        printImage(decodedImage);
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

    // Encode an image using HDC
    public static boolean[] encodeImage(int[][] image, boolean[][] pixelValueVectors, boolean[][] positionVectors) {
        int numRows = image.length;
        int numCols = image[0].length;
        boolean[] encodedVector = new boolean[D];
        Arrays.fill(encodedVector, false);

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int pixelValue = image[i][j];
                int position = i * numCols + j;
                boolean[] pixelVector = pixelValueVectors[pixelValue];
                boolean[] positionVector = positionVectors[position];
                boolean[] combinedVector = xorVectors(pixelVector, positionVector);
                encodedVector = xorVectors(encodedVector, combinedVector);
            }
        }

        return encodedVector;
    }

    // Decode an image using HDC
    public static int[][] decodeImage(boolean[] encodedVector, boolean[][] pixelValueVectors, boolean[][] positionVectors, int numRows, int numCols) {
        int[][] decodedImage = new int[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                int position = i * numCols + j;
                boolean[] positionVector = positionVectors[position];
                int bestMatch = -1;
                int maxSimilarity = Integer.MIN_VALUE;

                for (int k = 0; k < pixelValueVectors.length; k++) {
                    boolean[] pixelVector = pixelValueVectors[k];
                    boolean[] candidateVector = xorVectors(encodedVector, positionVector);
                    int similarity = calculateSimilarity(candidateVector, pixelVector);
                    if (similarity > maxSimilarity) {
                        maxSimilarity = similarity;
                        bestMatch = k;
                    }
                }

                decodedImage[i][j] = bestMatch;
            }
        }

        return decodedImage;
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

    // Print an image
    private static void printImage(int[][] image) {
        for (int[] row : image) {
            for (int pixel : row) {
                System.out.print(pixel + "\t");
            }
            System.out.println();
        }
    }
}
