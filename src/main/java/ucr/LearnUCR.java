package ucr;

import hyperdimension.sequences.SequenceEncoder;
import tsetlin.AutomataLearning;
import tsetlin.ConvolutionEncoder;
import util.HVC;
import util.TradeMarker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

import static ucr.TimeSeriesProcessor.readTimeSeriesFile;

public class LearnUCR {

    ConvolutionEncoder convolutionEncoder = new ConvolutionEncoder(HVC.DIMENSION, 1, 1);
    private int numberClasses;

    //create a list with the following names
    /**
     * TwoLeadECG
     * FreezerRegularTrain
     * ECG200
     * PLAID
     * UWaveGestureLibraryZ
     * Beef
     * UMD
     * Phoneme
     * PhalangesOutlinesCorrect
     */


    static String[] namelist = {
            "GestureMidAirD1",
    };


    public List<TimeSeriesProcessor.TimeSeriesData> uploadData(String filename) {

        try {
            List<TimeSeriesProcessor.TimeSeriesData> dataList = readTimeSeriesFile(filename);
            return dataList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public TrainingData encodeData(SequenceEncoder sequenceEncoder) {

        numberClasses = sequenceEncoder.getNumberClasses();

        int[][] X_encoder = new int[sequenceEncoder.getEncodedSequences().size()][];
        int[] y_encoder = new int[sequenceEncoder.getEncodedSequences().size()];

        for(int i = 0; i < sequenceEncoder.getEncodedSequences().size(); i++) {
            int[] encoded = convolutionEncoder.bit_encode(sequenceEncoder.getEncodedSequences().get(i).toBooleanIntArray());

            X_encoder[i] = encoded;
            y_encoder[i] = sequenceEncoder.getLabel(i);
        }

        return createTrainingData(X_encoder, y_encoder);

    }

    public TrainingData encodeDataClassic(SequenceEncoder sequenceEncoder) {

        convolutionEncoder = new ConvolutionEncoder(sequenceEncoder.getNumberDims(), 1, 1);

        numberClasses = sequenceEncoder.getNumberClasses();

        int[][] X = sequenceEncoder.getEncodedSequencesClassic();

        int[][] X_encoder = new int[X.length][];
        int[] y_encoder = new int[X.length];

        for(int i = 0; i < X.length; i++) {
            int[] encoded = convolutionEncoder.bit_encode(X[i]);

            X_encoder[i] = encoded;
            y_encoder[i] = sequenceEncoder.getLabel(i);
        }

        return createTrainingData(X_encoder, y_encoder);

    }


    private TrainingData createTrainingData(int[][] X_encoder, int[] y_encoder) {


        Random random = new Random();
        //take 80 percent of the data for training
        int trainSize = (int)(X_encoder.length * .75);
        //build a random set of unique indices from 1 - X.length
        int[] indices = new int[X_encoder.length];
        for(int i = 0; i < X_encoder.length; i++) {
            indices[i] = i;
        }
        //shuffle the indices
        for(int i = 0; i < X_encoder.length; i++) {
            int j = (int)(random.nextDouble() * X_encoder.length);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        final int[][] X_train = new int[trainSize][];
        final int[]y_train = new int[trainSize];
        for(int i = 0; i < trainSize; i++) {
            X_train[i] = X_encoder[indices[i]];
            y_train[i] = y_encoder[indices[i]];
        }

        final int[][] X_test = new int[X_encoder.length - trainSize][];
        final int[]y_test = new int[X_encoder.length - trainSize];

        for(int i = trainSize; i < X_encoder.length; i++) {
            X_test[i - trainSize] = X_encoder[indices[i]];
            y_test[i - trainSize] = y_encoder[indices[i]];
        }

        return new TrainingData(X_train, X_test, y_train, y_test);
    }

    private ParameterAccuracy learning(int clauses, int nLiterals, int threshold, float specificity, boolean negativeFocused, TrainingData trainingData) {

        //need a local copy of the convolution encoder
        AutomataLearning model = new AutomataLearning(
                convolutionEncoder,
                clauses,
                threshold,
                specificity,
                numberClasses,
                .1f);

        model.setMaxNumberOfLiterals(nLiterals);
        model.setNegativeFocusedSampling(negativeFocused);

        float maxAccuracy = 0;

        int[][] X_train = trainingData.getArray1();
        int[] y_train = trainingData.getArray3();

        int[][] X_test = trainingData.getArray2();
        int[] y_test = trainingData.getArray4();

        for(int e = 0; e < 6; e++) {

            model.fit(X_train, y_train);

            //compute the accuracy of each class
            int[] classPredicted = new int[numberClasses];
            int[] classCorrect = new int[numberClasses];
            //compute the accuracy
            int correct = 0;
            for(int i = 0; i < X_test.length; i++) {
                int pred = model.predict(X_test[i]);
                //System.out.println("Predicted: " + pred + " Actual: " + y_test[i]);
                if(pred == y_test[i]) {
                    correct++;
                    classCorrect[pred]++;
                }
                classPredicted[y_test[i]]++;
            }
            //System.out.println("Epoch: " + e + " Accuracy: " + (float)correct/X_test.length);
            maxAccuracy = Math.max(maxAccuracy, (float)correct/X_test.length);
        }

        //create a parameter accuracy object
        ParameterAccuracy parameterAccuracy = new ParameterAccuracy(clauses, nLiterals, threshold, specificity, negativeFocused, maxAccuracy);

        return parameterAccuracy;
    }

    public static void main(String[] args) {




        LearnUCR learnUCR = new LearnUCR();

        List<String> names = learnUCR.buildNameBase("/home/lisztian/UCR/UCRArchive_2018");

        //for each name, get the data
        for(String name : names) {

            //System.out.println(name);
            //if name in namelist
            if (Arrays.asList(namelist).contains(name)) {

                System.out.println("Processing: " + name);
                List<TimeSeriesProcessor.TimeSeriesData> data = learnUCR.getData(name);
                SequenceEncoder sequenceEncoder = new SequenceEncoder(data, 20, 5);

                TrainingData trainingData = learnUCR.encodeData(sequenceEncoder);

                //run the learning
                learnUCR.threadLearning(5, 5, trainingData);
            }

        }

    }

    List<TimeSeriesProcessor.TimeSeriesData> getData(String dataName) {

        List<TimeSeriesProcessor.TimeSeriesData> data = uploadData("/home/lisztian/UCR/UCRArchive_2018/" + dataName + "/" + dataName + "_TRAIN.tsv");
        List<TimeSeriesProcessor.TimeSeriesData> test = uploadData("/home/lisztian/UCR/UCRArchive_2018/" + dataName + "/" + dataName + "_TEST.tsv");

        data.addAll(test);
        return data;
    }


//    public ParameterAccuracy seriesTMLearn(int numberSimulations) {
//
//        ParameterAccuracy maxAccuracy = null;
//        for (int i = 0; i < numberSimulations; i++) {
//            int clauses = 100+(int) (Math.random() * 2000);
//            int nLiterals = (int) (Math.random() * 100);
//            int threshold = (int) (Math.random() * 200);
//            float specificity = (float) (Math.random() * 20);
//            boolean negativeFocused = true;
//
//            //System.out.println("Start, End: " + start + " " + end);
//            ParameterAccuracy parameterAccuracy = learning(clauses, nLiterals, threshold, specificity, negativeFocused);
//
//            maxAccuracy = max(maxAccuracy, parameterAccuracy);
//            System.out.print(i + ",");
//        }
//        System.out.println("\nMax Accuracy: " + maxAccuracy);
//        return maxAccuracy;
//    }



    private class LearningTask implements Callable<ParameterAccuracy> {

        private int numberSimulations;
        private int threadNumber;
        private TrainingData trainingData;
        public LearningTask(int threadNumber, int numberSimulations, TrainingData trainingData) {
            this.numberSimulations = numberSimulations;
            this.threadNumber = threadNumber;
            this.trainingData = trainingData;
        }

        @Override
        public ParameterAccuracy call() throws Exception {
            ParameterAccuracy maxAccuracy = null;

            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < numberSimulations; i++) {
                int clauses = 100+random.nextInt(2000);
                int nLiterals = random.nextInt(100);
                int threshold = random.nextInt(200);
                float specificity = (random.nextFloat() * 20);
                boolean negativeFocused = true;

                //System.out.println("Start, End: " + start + " " + end);
                ParameterAccuracy parameterAccuracy = learning(clauses, nLiterals, threshold, specificity, negativeFocused, trainingData);

                maxAccuracy = max(maxAccuracy, parameterAccuracy);
            }
            System.out.print(threadNumber + ",");
            return maxAccuracy;
        }
    }

    public void threadLearning(int numberOfThreads, int numberSimulations, TrainingData trainingData) {

        //each thread will run numberSimulations
        List<Future<ParameterAccuracy>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

        for(int i = 0; i < numberOfThreads; i++) {
            LearningTask task = new LearningTask(i, numberSimulations, trainingData);
            Future<ParameterAccuracy> future = executor.submit(task);
            tasks.add(future);
        }

        executor.shutdown();

        ParameterAccuracy maxAccuracy = null;
        for(Future<ParameterAccuracy> future : tasks) {
            try {
                ParameterAccuracy parameterAccuracy = future.get();
                maxAccuracy = max(maxAccuracy, parameterAccuracy);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nMax Accuracy: " + maxAccuracy);

    }





    /**
     * Given a directory name, build a list of the base names of the files. The names are the same as the directory names
     *
     * @param directory
     * @return
     */
    private List<String> buildNameBase(String directory) {

        List<String> names = new ArrayList<>();
        File dir = new File(directory);
        File[] files = dir.listFiles();
        for(File file : files) {
            if(file.isDirectory()) {
                names.add(file.getName());
            }
        }
        return names;

    }




    //build a class for the parameters and the accuracy
    private class ParameterAccuracy {
        private int clauses;
        private int nLiterals;
        private int threshold;
        private float specificity;
        private boolean negativeFocused;
        private float accuracy;

        public ParameterAccuracy(int clauses, int nLiterals, int threshold, float specificity, boolean negativeFocused, float accuracy) {
            this.clauses = clauses;
            this.nLiterals = nLiterals;
            this.threshold = threshold;
            this.specificity = specificity;
            this.negativeFocused = negativeFocused;
            this.accuracy = accuracy;
        }

        public int getClauses() {
            return clauses;
        }

        public int getnLiterals() {
            return nLiterals;
        }

        public int getThreshold() {
            return threshold;
        }

        public float getSpecificity() {
            return specificity;
        }

        public boolean isNegativeFocused() {
            return negativeFocused;
        }

        public float getAccuracy() {
            return accuracy;
        }

        //toString
        @Override
        public String toString() {
            return "ParameterAccuracy{" +
                    "clauses=" + clauses +
                    ", nLiterals=" + nLiterals +
                    ", threshold=" + threshold +
                    ", specificity=" + specificity +
                    ", negativeFocused=" + negativeFocused +
                    ", error rate=" + (1.0 - accuracy) +
                    ", accuracy=" + accuracy +
                    '}';
        }
    }

    public class TrainingData {
        private final int[][] array1;
        private final int[][] array2;
        private final int[] array3;
        private final int[] array4;

        public TrainingData(int[][] array1, int[][] array2, int[] array3, int[] array4) {
            this.array1 = array1;
            this.array2 = array2;
            this.array3 = array3;
            this.array4 = array4;
        }

        public int[][] getArray1() {
            return array1;
        }

        public int[][] getArray2() {
            return array2;
        }

        public int[] getArray3() {
            return array3;
        }

        public int[] getArray4() {
            return array4;
        }
    }


    public static ParameterAccuracy max(ParameterAccuracy a, ParameterAccuracy b) {
        if(a == null) {
            return b;
        }
        if(b == null) {
            return a;
        }
        if(a.getAccuracy() > b.getAccuracy()) {
            return a;
        }
        return b;
    }
}
