package hyperdimension.examples.iris;


import dataio.CSVInterface;
import output.CategoryLabel;
import records.AnyRecord;
import tsetlin.AutomataLearning;
import tsetlin.ConvolutionEncoder;
import util.HVC;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class IrisExample {

    public static void main(String[] args) throws IOException {

        //set seed to be current time in milliseconds
        long seed = System.currentTimeMillis();

        Random random = new Random(seed);

        CSVInterface csv = new CSVInterface("protein/iris.csv", 4);
        AnyRecord anyrecord = csv.createRecord();

        String[] fields = anyrecord.getField_names();

        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }

        HashMap<String, Integer> categoryMap = new HashMap<String, Integer>();
        categoryMap.put("setosa", 0);
        categoryMap.put("versicolor", 1);
        categoryMap.put("virginica", 2);

        CategoryLabel label = new CategoryLabel(categoryMap);

        ArrayList<AnyRecord> records = csv.getAllRecords();

        ArrayList<int[]> allRecords = new ArrayList<int[]>();
        ArrayList<Integer> allLabels = new ArrayList<Integer>();


        ConvolutionEncoder encoder = new ConvolutionEncoder(HVC.DIMENSION, 1, 1);
        records.forEach(record -> {

            Iris iris = new Iris(record.getValues(), record.getLabel_name());

            int[] encoded = encoder.bit_encode(IrisEncoderDecoder.encode(iris).toBooleanIntArray());

            allRecords.add(encoded);
            allLabels.add(categoryMap.get( record.getLabel_name()));

        });

        int[][] X = allRecords.toArray(new int[0][0]);
        int[] y = allLabels.stream().mapToInt(i -> i).toArray();

        //take 80 percent of the data for training
        int trainSize = (int)(X.length * .8);
        //build a random set of unique indices from 1 - X.length
        int[] indices = new int[X.length];
        for(int i = 0; i < X.length; i++) {
            indices[i] = i;
        }
        //shuffle the indices
        for(int i = 0; i < X.length; i++) {
            int j = (int)(random.nextDouble() * X.length);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        int[][] X_train = new int[trainSize][];
        int[] y_train = new int[trainSize];
        for(int i = 0; i < trainSize; i++) {
            X_train[i] = X[indices[i]];
            y_train[i] = y[indices[i]];
        }

        int[][] X_test = new int[X.length - trainSize][];
        int[] y_test = new int[X.length - trainSize];

        for(int i = trainSize; i < X.length; i++) {
            X_test[i - trainSize] = X[indices[i]];
            y_test[i - trainSize] = y[indices[i]];
        }




        AutomataLearning model = new AutomataLearning(
                encoder,
                50,
                20,
                2f,
                3,
                .2f);

        for(int e = 0; e < 20; e++) {

            model.fit(X_train, y_train);

            //compute the accuracy
            int correct = 0;
            for(int i = 0; i < X_test.length; i++) {
                int pred = model.predict(X_test[i]);
                if(pred == y_test[i]) {
                    correct++;
                }
            }
            System.out.println("Epoch: " + e + " Accuracy: " + (float)correct/X_test.length);
        }

















    }



}
