package hyperdimension.examples.claims;

import dataio.CSVInterface;
import dynamics.Evolutionize;
import interpretability.Prediction;
import records.AnyRecord;
import tsetlin.AutomataLearning;
import util.HVC;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegressionClaimsData {

    public static void main(String[] args) throws Exception {

        //buildClaimsFile();



        HVC.rnd.setSeed(57477); //57477


        //set seed to be current time in milliseconds
        CSVInterface csv = new CSVInterface("protein/freMTPL2freq_modified.csv", 0);
        AnyRecord anyrecord = csv.createRecord();

        String[] fields = anyrecord.getField_names();

        for (String field : fields) {
            System.out.println(field);
        }

        //find the max label


        List<AnyRecord> records = filter(csv.getAllRecords(), 5000);

        Evolutionize evolution = new Evolutionize(1, 1);

        evolution.initiate(anyrecord, 10);
        for (AnyRecord record : records) {
            evolution.addValue(record);
        }
        evolution.fit();
        evolution.initiateConvolutionEncoder();





        //model.printNumberOfLiteralsForEachClause();

        int train_set_size = (int) (records.size() * .7);

        //shuffle the records
        long seed = 1;

        //shuffle the records
        Collections.shuffle(records, HVC.rnd);

        //create two sets of random records
        ArrayList<AnyRecord> train_set = new ArrayList<AnyRecord>();
        ArrayList<AnyRecord> test_set = new ArrayList<AnyRecord>();

        for (int i = 0; i < train_set_size; i++) {
            AnyRecord record = records.get(i);
            train_set.add(record);
        }
        for (int i = train_set_size; i < records.size(); i++) {
            test_set.add(records.get(i));
        }




        int[][] Xi = new int[train_set.size()][];
        int[] Y = new int[train_set.size()];
        //create samples from train set
        for (int i = 0; i < train_set.size(); i++) {
            AnyRecord r = train_set.get(i);
            evolution.add(r);
            Xi[i] = evolution.get_last_sample();
            Y[i] = r.getLabel();
        }

        int[][] Xtest = new int[test_set.size()][];
        int[] Ytest = new int[test_set.size()];
        //create samples from train set
        for (int i = 0; i < test_set.size(); i++) {
            AnyRecord r = test_set.get(i);
            evolution.add(r);
            Xtest[i] = evolution.get_last_sample();
            Ytest[i] = r.getLabel();
        }

        double minError = Double.MAX_VALUE;


        //138 333 36.302986 (20k samples) 348 522 22.963104

        for(int trials = 0; trials < 1; trials++) {

//            int nClauses = 50 + HVC.rnd.nextInt(300);
//            int threshold = nClauses + HVC.rnd.nextInt(200);
//            float max_specificity = 10.0f + HVC.rnd.nextFloat() * 30.0f;

            int nClauses = 348;
            int threshold = 522;
            float max_specificity = 22.96f;


            AutomataLearning model = new AutomataLearning(
                    evolution,
                    nClauses,
                    threshold,
                    max_specificity,
                    1,
                    0);
            //set the max numer of literals
            model.setMaxNumberOfLiterals(10);
            model.setNegativeFocusedSampling(false);


            double error = 0.0;
            double nullError = 0.0;

            for (int e = 0; e < 1; e++) {

                model.fit(Xi, Y);

                int correct = 0;
                for (int i = 0; i < Xtest.length; i++) {

                    int mylabel = Ytest[i];
                    int[] x = Xtest[i];

                    error += Math.abs(mylabel - model.predict(x));
                    nullError += Math.abs(mylabel); //null model predicts 0
                    System.out.println(mylabel + ", prediction: " + model.predict(x));


                }

                error = Math.sqrt(error);
                nullError = Math.sqrt(nullError);

                double residual = error / nullError;

                if(residual < minError) {
                    minError = residual;
                    System.out.println("New minimum error: " + residual );
                    //save the model
                    System.out.println(nClauses + " " + threshold + " " + max_specificity);
                }

            }
            //model.printNumberOfLiteralsForEachClause();
        }


    }

    public static List<AnyRecord> filter(List<AnyRecord> records, int samples) {


        //create a balanced set with equal amount ClaimNb 0 and ClaimNb 1
        List<AnyRecord> balancedClaims = new ArrayList<>();
        int count0 = 0;
        int count1 = 0;


        double max = 0;


        List<AnyRecord> claim3s = new ArrayList<>();
        List<AnyRecord> claim4s = new ArrayList<>();






        for (AnyRecord claim : records) {
            if (claim.getLabel() == 0 && count0 < samples ) {
                balancedClaims.add(claim);
                count0++;
            }
            else if (claim.getLabel() > 0 && count1 < samples ) {
                balancedClaims.add(claim);
                count1++;
            }


            //if all counts are satisfied, break
            if (count0 >= samples  && count1 >= samples ) {
                System.out.println("Breaking out of loop as all counts are satisfied.");
                break;
            }
        }





        return balancedClaims;
    }


    /**
     * This function reads the claims data from a CSV file and builds a new claims file where the label is combining
     * NClaim / Exposure, where NClaim is the number of claims and Exposure is the exposure value.
     * We don't use the first and third columns, which are the index and exposure. The rest of the columns are used used
     * to print the file, thus we have
     * ClaimNb/Exposure,cat_Area,VehPower,VehAge,DrivAge,BonusMalus,cat_VehBrand,cat_VehGas,Density,cat_Region
     */
    private static void buildClaimsFile() throws IOException {

        String fileNameToOutput = "freMTPL2freq_modified.csv";
        StringBuilder sb = new StringBuilder();
        sb.append("ClaimNb/Exposure,cat_Area,VehPower,VehAge,DrivAge,BonusMalus,cat_VehBrand,cat_VehGas,Density,cat_Region\n");

        //open file to read
        String fileNameToRead = "protein/freMTPL2freq.csv";

        //open new file to write
        BufferedWriter writer = new BufferedWriter(new FileWriter(fileNameToOutput));
        //write the header
        writer.write(sb.toString());

        ClassLoader classLoader = RegressionClaimsData.class.getClassLoader();
        File file = new File(classLoader.getResource(fileNameToRead).getFile());

        ArrayList<String> lines = new ArrayList<>();

        List<Double> maxValues = new ArrayList<>();
        double max = 0;
        //read the file line by line
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            if (lineCount == 1) continue; //skip header

            String[] tokens = line.split(",");
            if (tokens.length < 12) continue; // skip invalid lines

            //get the first column, which is the ClaimNb
            double claimNb = Double.parseDouble(tokens[1]);
            //get the third column, which is the Exposure
            double exposure = Double.parseDouble(tokens[2]);

            //calculate the new value
            double newValue = claimNb / exposure;



            //append to the string builder
            sb.append((int) Math.round(Math.min(newValue,100))).append(",");
            for (int i = 3; i < tokens.length; i++) {
                sb.append(tokens[i]).append(",");
            }
            sb.deleteCharAt(sb.length() - 1); //remove last comma
            sb.append("\n");
            writer.write(sb.toString());
            sb.setLength(0); //clear the StringBuilder for the next line
        }

        //now write all lines to the file at once while taking the first value and multiplying it by 100 / max
//
//        //print maxValues
//        for(double maxValue : maxValues) {
//            System.out.println(maxValue);
//        }
//
//
//
//        //skip the header
//        for (int i = 1; i < lines.size(); i++) {
//            String l = lines.get(i);
//            String[] tokens = l.split(",");
//
//            double firstValue = Double.parseDouble(tokens[0]);
//            int newFirstValue = (int) Math.round(Math.min(firstValue,100));
//
//            //System.out.println("New first value: " + firstValue + " " + newFirstValue);
//
//            sb.append(newFirstValue).append(",");
//            for (int k = 1; k < tokens.length; k++) {
//                sb.append(tokens[k]).append(",");
//            }
//            sb.deleteCharAt(sb.length() - 1); //remove last comma
//            //write the line to the file
//
//            //System.out.println("Writing line: " + sb.toString());
//            sb.append("\n");
//            writer.write(sb.toString());
//
//            sb.setLength(0); //clear the StringBuilder for the next line
//        }




        //close the reader and writer
        reader.close();
        writer.close();
        System.out.println("Claims file created: " + fileNameToOutput);







    }


}
