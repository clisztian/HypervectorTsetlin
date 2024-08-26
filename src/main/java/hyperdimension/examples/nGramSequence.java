package hyperdimension.examples;

import hyperdimension.encoders.IntervalEmbedding;
import hyperdimension.encoders.VanillaBHV;

import java.util.*;

public class nGramSequence {

    //we experiment with nGrams of size n, for sequences of length m
    public static void main(String[] args) {

        int nGramSize = 3;
        int sequenceLength = 50;

        int nTrials = 30;
        int[] labels = new int[nTrials];
        // Generate 10 random sequences of length 20 with sd 10 and mean 25
        int[][] sequences = new int[nTrials][];
        for (int i = 0; i < nTrials/2; i++) {
            sequences[i] = generateRandomSequence(sequenceLength, 10, 25);
            labels[i] =0;
        }
        // Generate 10 random sequences of length 20 with sd 3 and mean 50
        for (int i = nTrials/2; i < nTrials; i++) {
            sequences[i] = generateRandomSequence(sequenceLength, 2, 80);
            labels[i] = 1;
        }

        int[][] testSequences = new int[nTrials][];
        for (int i = 0; i < nTrials/2; i++) {
            testSequences[i] = generateRandomSequence(sequenceLength, 10, 25);
        }
        for (int i = nTrials/2; i < nTrials; i++) {
            testSequences[i] = generateRandomSequence(sequenceLength, 2, 80);
        }



        //create a list of nGrams using VanillaBHV
        List<VanillaBHV> gramVectorList = new ArrayList<>();
        for(int i = 0; i < nGramSize; i++) {
            gramVectorList.add(VanillaBHV.randVector());
        }

        //find the max and min of all the sequences
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < nTrials; i++) {
            for (int j = 0; j < sequenceLength; j++) {
                if (sequences[i][j] > max) {
                    max = sequences[i][j];
                }
                if (sequences[i][j] < min) {
                    min = sequences[i][j];
                }
            }
        }
        for (int i = 0; i < nTrials; i++) {
            for (int j = 0; j < sequenceLength; j++) {
                if (testSequences[i][j] > max) {
                    max = testSequences[i][j];
                }
                if (testSequences[i][j] < min) {
                    min = testSequences[i][j];
                }
            }
        }



        //create an interval embedding for the sequence
        IntervalEmbedding sequenceIntervalAtoms = new IntervalEmbedding(min-1, max+1, 100);


        //test the interval embedding
        for (int i = 0; i < sequenceLength; i++) {
            VanillaBHV atom = sequenceIntervalAtoms.forward(sequences[0][i]);
            System.out.println("Atom for " + sequences[0][i] + " is " + atom);
            //back test
            double back = sequenceIntervalAtoms.back(atom);
            System.out.println("Back test: " + back);
        }

        // Encode the sequence using n-grams
        //VanillaBHV sequenceVector = encodeSequence(sequence, gramVectorList, sequenceIntervalAtoms, nGramSize);
        List<VanillaBHV> encodedSequences = new ArrayList<>();
        for (int i = 0; i < nTrials; i++) {
            VanillaBHV sequenceVector = encodeSequence(sequences[i], gramVectorList, sequenceIntervalAtoms, nGramSize);
            encodedSequences.add(sequenceVector);
        }

        List<VanillaBHV> testEncodedSequences = new ArrayList<>();
        for (int i = 0; i < nTrials; i++) {
            VanillaBHV sequenceVector = encodeSequence(testSequences[i], gramVectorList, sequenceIntervalAtoms, nGramSize);
            testEncodedSequences.add(sequenceVector);
        }


        int[] predictedLabels = classifySequences(encodedSequences, labels, testEncodedSequences, 2);

        // Output the classification results
        for (int i = 0; i < predictedLabels.length; i++) {
            System.out.println("Sequence " + Arrays.toString(sequences[i]) + " is classified as " + predictedLabels[i]);
        }

        // Evaluate classification accuracy
        int correct = 0;
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == predictedLabels[i]) {
                correct++;
            }
        }
        double accuracy = (double) correct / labels.length;
        System.out.println("Classification accuracy: " + accuracy);


//        final Umap umap = new Umap();
//        //copy the encoded sequences to float array
//        float[][] data = new float[encodedSequences.size()][VanillaBHV.DIMENSION];
//        for (int i = 0; i < encodedSequences.size(); i++) {
//            boolean[] v = encodedSequences.get(i).toBooleanVector();
//            for (int j = 0; j < VanillaBHV.DIMENSION; j++) {
//                data[i][j] = v[j] ? 1f : 0f;
//            }
//        }
//
//        umap.setNumberComponents(2);         // number of dimensions in result
//        umap.setNumberNearestNeighbours(2);
//        umap.setLocalConnectivity(5);
//        umap.setMetric(HammingMetric.SINGLETON);      // use HAMMING for binary data
//        umap.setThreads(1);                  // use > 1 to enable parallelism
//        final float[][] result = umap.fitTransform(data);
//
//        //print out the result
//        for (int i = 0; i < result.length; i++) {
//            System.out.println("Sequence " + Arrays.toString(sequences[i]) + " is at " + Arrays.toString(result[i]));
//        }

        predictionExample();


    }



    public static VanillaBHV encodeSequence(int[] sequence, List<VanillaBHV> gramVectors, IntervalEmbedding sequenceIntervalAtoms, int n) {
        int length = sequence.length;
        int numGrams = length - n + 1;

        // Generate n-grams with temporal binding
        List<VanillaBHV> nGrams = new ArrayList<>();

        VanillaBHV nGramVector = VanillaBHV.zeroVector();

        for(int i = 0; i < numGrams; i++) {

            nGramVector = VanillaBHV.zeroVector();
            for (int j = 0; j < n; j++) {

                VanillaBHV permutedSj = sequenceIntervalAtoms.forward(sequence[i + j]).permute(j);
                VanillaBHV combinedVector = permutedSj.xor(gramVectors.get(j));
                nGramVector = nGramVector.xor(combinedVector);
            }
            nGrams.add(nGramVector);
        }

        // Aggregate n-grams to form the final sequence vector
        return VanillaBHV.logic_majority(nGrams);

    }


    private static int[] generateRandomSequence(int sequenceLength, int sd, int mean) {

        Random random = new Random();
        int[] sequence = new int[sequenceLength];
        for (int j = 0; j < sequenceLength; j++) {
            sequence[j] = mean + (int) (random.nextGaussian() * sd);
        }
        return sequence;
    }


    public static int predictNextElement(VanillaBHV queryVector, Map<VanillaBHV, Integer> associativeMemory) {
        int bestMatch = -1;
        int minDistance = Integer.MAX_VALUE;

        for (Map.Entry<VanillaBHV, Integer> entry : associativeMemory.entrySet()) {
            int distance =  queryVector.hammingDistance(entry.getKey());
            if (distance < minDistance) {
                minDistance = distance;
                bestMatch = entry.getValue();
            }
        }

        return bestMatch;
    }


    public static void predictionExample() {

        // Example sequences (e.g., sequences of integers representing different patterns)
        int[][] sequences = {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9, 0},
                {1, 3, 5, 7, 9},
                {2, 4, 6, 8, 0}
        };

        int nGramSize = 3;
        //create an interval embedding for the sequence
        IntervalEmbedding sequenceIntervalAtoms = new IntervalEmbedding(0, 10, 10);

        //create a list of nGrams using VanillaBHV
        List<VanillaBHV> gramVectorList = new ArrayList<>();
        for(int i = 0; i < nGramSize; i++) {
            gramVectorList.add(VanillaBHV.randVector());
        }

        int nTrials = sequences.length;
        // Encode the sequence using n-grams
        //VanillaBHV sequenceVector = encodeSequence(sequence, gramVectorList, sequenceIntervalAtoms, nGramSize);
        List<VanillaBHV> encodedSequences = new ArrayList<>();
        for (int i = 0; i < nTrials; i++) {
            VanillaBHV sequenceVector = encodeSequence(sequences[i], gramVectorList, sequenceIntervalAtoms, nGramSize);
            encodedSequences.add(sequenceVector);
        }


        // Encode and store the sequences in associative memory
        Map<VanillaBHV, Integer> associativeMemory = new HashMap<>();
        for (int[] sequence : sequences) {
            for (int i = 0; i <= sequence.length - nGramSize; i++) {
                int[] nGram = Arrays.copyOfRange(sequence, i, i + nGramSize);
                VanillaBHV encodedGram = encodeSequence(nGram, gramVectorList, sequenceIntervalAtoms, nGramSize);
                int nextElement = (i + nGramSize < sequence.length) ? sequence[i + nGramSize] : -1;
                associativeMemory.put(encodedGram, nextElement);
            }
        }

        // Query sequence
        int[] querySequence = {1, 3, 5};
        VanillaBHV queryVector = encodeSequence(querySequence, gramVectorList, sequenceIntervalAtoms, nGramSize);

        // Predict the next element in the sequence
        int predictedElement = predictNextElement(queryVector, associativeMemory);

        // Output the query and predicted next element
        System.out.println("Query Sequence: " + Arrays.toString(querySequence));
        System.out.println("Predicted Next Element: " + predictedElement);


    }




    // Classify sequences using k-NN
    public static int[] classifySequences(List<VanillaBHV> trainVectors, int[] trainLabels, List<VanillaBHV> testVectors, int k) {
        int[] predictedLabels = new int[testVectors.size()];

        for (int i = 0; i < testVectors.size(); i++) {
            predictedLabels[i] = classifySequence(trainVectors, trainLabels, testVectors.get(i), k);
        }
        return predictedLabels;
    }


    // Classify a single sequence using k-NN
    public static int classifySequence(List<VanillaBHV> trainVectors, int[] trainLabels, VanillaBHV testVector, int k) {

        int numTrainVectors = trainVectors.size();
        int[] distances = new int[numTrainVectors];

        // Calculate Hamming distances between the test vector and all training vectors
        for (int i = 0; i < numTrainVectors; i++) {
            distances[i] = testVector.hammingDistance(trainVectors.get(i));
        }

        // Find the k nearest neighbors
        int[] nearestNeighbors = new int[k];
        Arrays.fill(nearestNeighbors, -1);
        for (int i = 0; i < numTrainVectors; i++) {
            for (int j = 0; j < k; j++) {
                if (nearestNeighbors[j] == -1 || distances[i] < distances[nearestNeighbors[j]]) {
                    System.arraycopy(nearestNeighbors, j, nearestNeighbors, j + 1, k - j - 1);
                    nearestNeighbors[j] = i;
                    break;
                }
            }
        }


        // Count the votes for each label
        int maxLabel = Arrays.stream(trainLabels).max().getAsInt();
        int[] votes = new int[maxLabel + 1];
        for (int i = 0; i < k; i++) {
            if (nearestNeighbors[i] != -1) {
                votes[trainLabels[nearestNeighbors[i]]]++;
            }
        }

        // Find the label with the most votes
        int maxVotes = 0;
        int predictedLabel = -1;
        for (int i = 0; i < votes.length; i++) {
            if (votes[i] > maxVotes) {
                maxVotes = votes[i];
                predictedLabel = i;
            }
        }

        return predictedLabel;
    }




}
