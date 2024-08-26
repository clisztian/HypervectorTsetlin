package hyperdimension.examples.iristsetlin;

import dataio.CSVInterface;
import dynamics.Evolutionize;
import interpretability.Prediction;
import output.CategoryLabel;
import records.AnyRecord;
import tsetlin.AutomataLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class IrisExample {


    public IrisExample() throws IOException, IllegalAccessException {

        CSVInterface csv = new CSVInterface("protein/iris.csv",4);
        AnyRecord anyrecord = csv.createRecord();

        String[] fields = anyrecord.getField_names();

        for(int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }



        HashMap<String, Integer> categoryMap = new HashMap<String, Integer>();
        categoryMap.put("setosa", 0);
        categoryMap.put("versicolor", 1);
        categoryMap.put("virginica", 2);

        CategoryLabel label = new CategoryLabel(categoryMap);

        ArrayList<AnyRecord> records = csv.getAllRecords();

        Evolutionize evolution = new Evolutionize(1, 1);
        evolution.initiate(anyrecord, 10);
        for(int i = 0; i < records.size(); i++) {
            evolution.addValue(records.get(i));
        }
        evolution.fit();
        evolution.initiateConvolutionEncoder();





        AutomataLearning model = new AutomataLearning(
                evolution,
                10,
                15,
                2f,
                3,
                .0f);




        //model.printNumberOfLiteralsForEachClause();

        int train_set_size = (int) (records.size() * .7);

        //shuffle the records
        Collections.shuffle(records);

        //create two sets of random records
        ArrayList<AnyRecord> train_set = new ArrayList<AnyRecord>();
        ArrayList<AnyRecord> test_set = new ArrayList<AnyRecord>();

        for(int i = 0; i < train_set_size; i++) {
            AnyRecord record = records.get(i);
        	train_set.add(record);
        }
        for(int i = train_set_size; i < records.size(); i++) {
        	test_set.add(records.get(i));
        }


        //set the max numer of literals
        model.setMaxNumberOfLiterals(10);
        model.setNegativeFocusedSampling(false);


        int[][] Xi = new int[train_set.size()][];
        int[] Y = new int[train_set.size()];
        //create samples from train set
        for(int i = 0; i < train_set.size(); i++) {
            AnyRecord r = train_set.get(i);
            evolution.add(r);
            Xi[i] = evolution.get_last_sample();
            Y[i] = (int)label.getLabel(r.getLabel_name());
        }

        for(int e = 0; e < 100; e++) {

            model.fit(Xi, Y);

            int correct = 0;
            for(AnyRecord record : test_set) {

                int mylabel = (int)label.getLabel(record.getLabel_name());
                Prediction pred = model.predict(record);
                correct += (mylabel == pred.getPred_class()) ? 1 : 0;

            }
            model.getAutomaton().printWeights();
            System.out.println("Correct: " + correct + " " + 100f*(1f*correct/(1f*test_set.size())));
            //print the weights in model


        }


//        for(int epoch = 0; epoch < 10; epoch++) {
//            System.out.println("Epoch: " + epoch);
//            for(AnyRecord record : train_set) {
//                try {
//
//                    int mylabel = (int)label.getLabel(record.getLabel_name());
//                    int pred = model.update(record, mylabel);
//
//
//                    //model.printNumberOfLiteralsForEachClause();
//
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//            int correct = 0;
//            for(AnyRecord record : test_set) {
//
//                int mylabel = (int)label.getLabel(record.getLabel_name());
//                Prediction pred = model.predict(record);
//                correct += (mylabel == pred.getPred_class()) ? 1 : 0;
//
//            }
//
//            System.out.println("Correct: " + correct + " " + 100f*(1f*correct/(1f*test_set.size())));
//            //print the weights in model
//            model.getAutomaton().printWeights();
//
//
//        }






    }



    public ArrayList<String> getAllLinesFromFileIntoArray() {

        ClassLoader classLoader = IrisExample.class.getClassLoader();
        File file = new File(classLoader.getResource("protein/iris.tsv").getFile());

        ArrayList<String> lines = new ArrayList<String>();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return lines;
    }

    public static void main(String[] args) {

        try {
            IrisExample iris = new IrisExample();
        } catch (IOException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


}
