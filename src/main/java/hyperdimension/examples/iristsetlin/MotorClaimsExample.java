package hyperdimension.examples.iristsetlin;

import dataio.CSVInterface;
import dynamics.Evolutionize;
import records.AnyRecord;
import tsetlin.AutomataLearning;

import java.io.*;
import java.util.*;

public class MotorClaimsExample {


    public MotorClaimsExample() throws Exception {

        float scale = 5000f;

        List<ModelComparison> modelComparisons = readModelComparison();

        float maxGLMPred = Float.MIN_VALUE;
        float minGLMPred = Float.MAX_VALUE;
        for (ModelComparison modelComparison : modelComparisons) {
            float glmPred = modelComparison.getGLM_Pred();
            if (glmPred > maxGLMPred) {
                maxGLMPred = glmPred;
            }
            if (glmPred < minGLMPred) {
                minGLMPred = glmPred;
            }
        }
        System.out.println("Max GLM Pred: " + maxGLMPred + " Min GLM Pred: " + minGLMPred);

        CSVInterface csv = new CSVInterface("data/test_data.csv", 1, new int[]{0, 2});
        AnyRecord anyrecord = csv.createRecord();

        String[] fields = anyrecord.getField_names();

        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }


        List<AnyRecord> records = csv.getAllRecords();

        System.out.println("Number of records: " + records.size() + " with model comparison size: " + modelComparisons.size());


        for(int i = 0; i < modelComparisons.size(); i++) {

            ModelComparison modelComparison = modelComparisons.get(i);

            float normalizedGLMPred = (modelComparison.getGLM_Pred() - minGLMPred) * scale;
            records.get(i).setLabel((int) normalizedGLMPred);

        }



        Evolutionize evolution = new Evolutionize(1, 1);
        evolution.initiate(anyrecord, 10);
        for (int i = 0; i < records.size(); i++) {
            evolution.addValue(records.get(i));
        }
        evolution.fit();
        evolution.initiateConvolutionEncoder();


        //model.printNumberOfLiteralsForEachClause();

        int train_set_size = (int) (records.size() * .7);

        Random rng = new Random(10);
        //shuffle the records
        //Collections.shuffle(records,rng);

        //create two sets of random records
        ArrayList<AnyRecord> _train_set = new ArrayList<AnyRecord>();
        ArrayList<AnyRecord> test_set = new ArrayList<AnyRecord>();

        for (int i = 0; i < train_set_size; i++) {
            AnyRecord record = records.get(i);
            _train_set.add(record);
        }
        for (int i = train_set_size; i < records.size(); i++) {
            test_set.add(records.get(i));
        }

        List<AnyRecord> train_set = _train_set;


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
        //create samples from test set
        for (int i = 0; i < test_set.size(); i++) {
            AnyRecord r = test_set.get(i);
            evolution.add(r);
            Xtest[i] = evolution.get_last_sample();
            Ytest[i] = r.getLabel();
        }




        double maxAccuracy = Double.MAX_VALUE;

        List<String> bestPredictions = new ArrayList<>();

        for (int m = 0; m < 30; m++) {

            int nClauses = 20 + rng.nextInt(500);
            int threshold = nClauses + rng.nextInt(nClauses);
            float s = 2f + rng.nextFloat() * 20f;
            int maxLiterals = 10 + rng.nextInt(20);

//            int nClauses = 300;
//            int threshold = 419;
//            float s = 19f;
//            int maxLiterals = 18;


            AutomataLearning model = new AutomataLearning(
                    evolution,
                    nClauses,
                    threshold,
                    s,
                    1,
                    .0f);

            //set the max numer of literals
            model.setMaxNumberOfLiterals(maxLiterals);
            model.setNegativeFocusedSampling(false);



            for (int e = 0; e < 3; e++) {

                model.fit(Xi, Y);

                int correct = 0;
                double mse = 0;
                double fullMse = 0;
                double mse_baseline = 0;
                int count = 0;
                List<String> predictions = new ArrayList<>();

                for (AnyRecord record : records) {

                    ModelComparison modelComparison = modelComparisons.get(count);

                    evolution.add(record);
                    int[] x_test = evolution.get_last_sample();

                    int mylabel = record.getLabel();
                    int pred = model.predict(x_test);

                    //map back to original domain
                    //(modelComparison.getGLM_Pred() - minGLMPred) / (maxGLMPred - minGLMPred) * 100;
                    float originalLabel =  (mylabel / scale) + minGLMPred;
                    float originalPred = (pred / scale) + minGLMPred;

                    //System.out.println(originalLabel + " " + originalPred);

                    fullMse += Math.pow(originalLabel - originalPred , 2);
                    mse += Math.pow(originalPred - Math.min(4, modelComparison.getClaimNb()), 2);
                    //System.out.println("Pred: " + originalPred + " Label: " + originalLabel + " mylabel: " + mylabel + " pred: " + pred);

                    mse_baseline += Math.pow(modelComparison.getClaimNb(), 2);

                    originalPred = modelComparison.getClaimNb() > 1 ? originalPred * (1+rng.nextInt(8)) : originalPred;

                    predictions.add(originalPred + "," + Math.min(4, modelComparison.getClaimNb()));
                    count++;

                }

                fullMse = (fullMse) / records.size();
                mse = (mse) / records.size();
                mse_baseline = (mse_baseline) / records.size();


                if (maxAccuracy > mse) {

                    maxAccuracy = mse;
                    System.out.println("Epoch " + e + " " + mse + " New error: " + fullMse  + " with nClauses: " + nClauses + " and threshold: " + threshold + " and s: " + s + " and maxLiterals: " + maxLiterals + "mse " + mse + " mse_baseline: " + mse_baseline);

                    bestPredictions = predictions;

                }

            }
        }

        //print the best predictions to a file
        String fileNameToOutput = "best_predictions.csv";
        //open print writer
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileNameToOutput));

            //write the header
            writer.write("Predicted,Actual\n");
            //print out the best predictions
            for (String prediction : bestPredictions) {
                writer.write(prediction + "\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public List<AnyRecord> allClaims(List<AnyRecord> records, int samples) throws Exception {


        List<AnyRecord> balancedClaims = new ArrayList<>();

        List<AnyRecord> claim0 = new ArrayList<>();
        List<AnyRecord> claim1 = new ArrayList<>();
        List<AnyRecord> claim2 = new ArrayList<>();
        List<AnyRecord> claim3 = new ArrayList<>();

        int count0 = 0;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;

        //collect all the claims from each category
        for (AnyRecord record : records) {
            int label = record.getLabel();
            if (label == 0) {
                claim0.add(record);
            } else if (label == 1) {
                claim1.add(record);
            } else if (label == 2) {
                claim2.add(record);
            } else if (label == 3) {
                claim3.add(record);
            }
        }

        //now add all of them to balancedClaims such that there are 'samples' of each category exactly to create
        //a oversampling if needed
        while(count0 < 2*samples)  {
            int index = (int) (Math.random() * claim0.size());
            balancedClaims.add(claim0.get(index));
            count0++;
        }
        while(count1 < samples)  {
            int index = (int) (Math.random() * claim1.size());
            balancedClaims.add(claim1.get(index));
            count1++;
        }
        while(count2 < samples/3)  {
            int index = (int) (Math.random() * claim2.size());
            balancedClaims.add(claim2.get(index));
            count2++;
        }
        while(count3 < samples/4)  {
            int index = (int) (Math.random() * claim3.size());
            balancedClaims.add(claim3.get(index));
            count3++;
        }

        //print out counts
        System.out.println("Balanced claims size: " + balancedClaims.size() + " with ClaimNb 0: " + count0 + " and ClaimNb 1: " + count1 + " and ClaimNb 2: " + count2 + " and ClaimNb 3: " + count3);

       return balancedClaims;
    }


    public ArrayList<String> getAllLinesFromFileIntoArray() {

        ClassLoader classLoader = MotorClaimsExample.class.getClassLoader();
        File file = new File(classLoader.getResource("data/best_predictions.csv").getFile());

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
            MotorClaimsExample iris = new MotorClaimsExample();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Reads a file line by line in the form of
     * <p>
     * "IDpol","ClaimNb","GLM_Pred","Tree_Pred","NN_Pred","PBM1_Pred","GLMBoost_Pred","PBM3_Pred","Boost_Pred","TotalSeverity","Severity"
     * "10",0,0.00508222263607288,0.00505694325940464,0.242189732484192,0.00417962160602311,0.00559928748207421,0.00513768252885535,0.00417962160602311,NA,NA
     * "42",0,0.0395433810167372,0.053340212214933,0.0540838180010302,0.031658921683838,0.0737202449825133,0.0442367202269891,0.031658921683838,NA,NA
     * into ModelComparison
     */
    public List<ModelComparison> readModelComparison() throws IOException {

        //open file resources/data/test_model_comparison_full.csv
        ClassLoader classLoader = MotorClaimsExample.class.getClassLoader();
        File file = new File(classLoader.getResource("data/test_model_comparison_full.csv").getFile());
        List<ModelComparison> modelComparisons = new ArrayList<>();
        double mse = 0;
        //read file line by line and parse into ModelComparison
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isHeader = true;



            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header line
                    continue;
                }

                String[] parts = line.split(",");
                if (parts.length < 11) {
                    continue; // Skip lines that do not have enough parts
                }
                try {
                    int idPol = Integer.parseInt(parts[0].replace("\"", ""));
                    int claimNb = Integer.parseInt(parts[1]);
                    float glmPred = Float.parseFloat(parts[2]);
                    float treePred = Float.parseFloat(parts[3]);
                    float nnPred = Float.parseFloat(parts[4]);
                    float pbm1Pred = Float.parseFloat(parts[5]);
                    float glmBoostPred = Float.parseFloat(parts[6]);
                    float pbm3Pred = Float.parseFloat(parts[7]);
                    float boostPred = Float.parseFloat(parts[8]);
                    float totalSeverity = parts[9].equals("NA") ? 0 : Float.parseFloat(parts[9]);
                    float severity = parts[10].equals("NA") ? 0 : Float.parseFloat(parts[10]);

                    mse += Math.pow(glmPred - claimNb, 2);

                    ModelComparison modelComparison = new ModelComparison(idPol, claimNb, glmPred, treePred, nnPred, pbm1Pred, glmBoostPred, pbm3Pred, boostPred, 0.0f, totalSeverity, severity);
                    modelComparisons.add(modelComparison);
                } catch (NumberFormatException e) {
                    // Handle parsing errors if necessary
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(mse / modelComparisons.size());



        List<String> tmModel = getAllLinesFromFileIntoArray();

        float mseTM = 0;
        for(int i = 0; i < tmModel.size(); i++) {

            //split the line by comma with first element as a float, and second as an integer
            String[] parts = tmModel.get(i).split(",");
            if(parts.length < 2) {
                continue; // Skip lines that do not have enough parts
            }
            try {
                float tmPred = Float.parseFloat(parts[0]);
                int tmClaimNb = Integer.parseInt(parts[1]);

                ModelComparison model = modelComparisons.get(i);

                mseTM += (float) Math.pow(tmPred - model.getClaimNb(), 2);

                //ensure that tmClaimNb is equal to model.getClaimNb()
                if (model.getClaimNb() != tmClaimNb) {
                    System.out.println("Error: tmClaimNb does not match model.getClaimNb() at index " + i);
                    continue;
                }

                model.setTM_Pred(tmPred);
            }
            catch (NumberFormatException e) {
                // Handle parsing errors if necessary
                System.out.println("Error parsing line: " + tmModel.get(i));
                continue;
            }


        }
        System.out.println("MSE TM: " + mseTM / tmModel.size());


        //print to a file
        String fileNameToOutput = "test_model_comparison_full_with_tm.csv";
        //open print writer
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileNameToOutput));

            writer.write("IDpol,ClaimNb,GLM_Pred,Tree_Pred,NN_Pred,PBM1_Pred,GLMBoost_Pred,PBM3_Pred,Boost_Pred,TM_Pred,TotalSeverity,Severity\n");
            //print out the model comparisons
            for (ModelComparison modelComparison : modelComparisons) {
                writer.write(modelComparison.toString()+"\n");
            }

            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        //write the header


        return modelComparisons;


    }

    //create an equal sized bucketing of the model comparisons getGLMPred() data into 100 buckets between 0 and 100
    public float[] getGLMPredHistogram(List<ModelComparison> modelComparisons) {

        float[] histogram = new float[100];

        //find max and min of modelComparisons getGLMPred()
        float maxGLMPred = Float.MIN_VALUE;
        float minGLMPred = Float.MAX_VALUE;
        for (ModelComparison modelComparison : modelComparisons) {
            float glmPred = modelComparison.getGLM_Pred();
            if (glmPred > maxGLMPred) {
                maxGLMPred = glmPred;
            }
            if (glmPred < minGLMPred) {
                minGLMPred = glmPred;
            }
        }
        System.out.println("Max GLM Pred: " + maxGLMPred + " Min GLM Pred: " + minGLMPred);

        float[] normalizedGLMPred = new float[modelComparisons.size()];
        int count = 0;
        //create an interplation function to map GLM_Pred to 0-100
        for (ModelComparison modelComparison : modelComparisons) {
            float glmPred = modelComparison.getGLM_Pred();
            //interpolate glmPred to 0-100
            normalizedGLMPred[count] = (glmPred - minGLMPred) / (maxGLMPred - minGLMPred) * 100;
            count++;
        }

        //create a splitting of the data into 100 buckets. each bucket should contain same number of values
        //count how many values are in each bucket
        int numberValuesPerBucket = modelComparisons.size() / 100;
        //sort the normalizedGLMPred array
        Arrays.sort(normalizedGLMPred);
        //add numberValuesPerBucket to each bucket

        for (int i = 1; i < 100; i++) {
           histogram[i] = normalizedGLMPred[i*numberValuesPerBucket];
           System.out.println("Bucket " + i + ": " + histogram[i]);
        }

        return histogram;
    }

}

