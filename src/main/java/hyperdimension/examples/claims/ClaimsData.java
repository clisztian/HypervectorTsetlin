package hyperdimension.examples.claims;

import dataio.CSVInterface;
import dynamics.Evolutionize;
import interpretability.GlobalRealFeatures;
import interpretability.Prediction;
import records.AnyRecord;
import tsetlin.AutomataLearning;
import util.HVC;

import java.util.*;

import static hyperdimension.examples.claims.MotorClaimsReader.readClaims;

public class ClaimsData {

    public static void main(String[] args) throws Exception {

        HVC.rnd.setSeed(57477); //57477

        int[] ignore = new int[2];
        ignore[0] = 0; //ignore first column, which is the index
        ignore[1] = 2; //ignore second column, which is the exposure
        //set seed to be current time in milliseconds
        CSVInterface csv = new CSVInterface("protein/freMTPL2freq.csv", 1, ignore);
        AnyRecord anyrecord = csv.createRecord();

        String[] fields = anyrecord.getField_names();

        for (int i = 0; i < fields.length; i++) {
            System.out.println(fields[i]);
        }

        List<AnyRecord> records = filter(csv.getAllRecords(), 10000);

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


        double accuracy = -Double.MAX_VALUE;


        //nClauses: 322, threshold: 352, max_specificity: 37.39934 70.4328842163086
        //260, threshold: 321, max_specificity: 35.48784

        for(int trials = 0; trials < 1; trials++) {

//            int nClauses = 50 + HVC.rnd.nextInt(300);
//            int threshold = nClauses + HVC.rnd.nextInt(200);
//            float max_specificity = 10.0f + HVC.rnd.nextFloat() * 30.0f;

            int nClauses = 260;
            int threshold = 321;
            float max_specificity = 35f;

            GlobalRealFeatures[][] realFeatures = new GlobalRealFeatures[0][];
            GlobalRealFeatures[][] riskRrealFeatures = new GlobalRealFeatures[0][];
            AutomataLearning model = new AutomataLearning(
                    evolution,
                    nClauses,
                    threshold,
                    max_specificity,
                    4,
                    0);

            //set the max numer of literals
            model.setMaxNumberOfLiterals(10);
            model.setNegativeFocusedSampling(false);


            int[][] Xi = new int[train_set.size()][];
            int[] Y = new int[train_set.size()];
            //create samples from train set
            for (int i = 0; i < train_set.size(); i++) {
                AnyRecord r = train_set.get(i);
                evolution.add(r);
                Xi[i] = evolution.get_last_sample();
                Y[i] = r.getLabel();
            }

            double[] actual = new double[test_set.size()];
            double[] predicted = new double[test_set.size()];
            int i = 0;
            for (int e = 0; e < 1; e++) {

                model.fit(Xi, Y);

                int correct = 0;
                for (AnyRecord record : test_set) {

                    int mylabel = record.getLabel();
                    Prediction pred = model.predict(record);
                    correct += (mylabel == pred.getPred_class()) ? 1 : 0;

                    actual[i] = mylabel;
                    predicted[i] = pred.getPred_class();

                    //System.out.println(" Predicted: " + pred.getPred_class() + " Actual: " + mylabel);

                    realFeatures = pred.getReal_features();
                    riskRrealFeatures = pred.getRisk_real_features();
                    i++;
                }
                //model.getAutomaton().printWeights();

                double error = 100f * (1f * correct / (1f * test_set.size()));
                if (error > accuracy) {
                    accuracy = error;
                    System.out.println("New min error: " + accuracy + " with nClauses: " + nClauses + ", threshold: " + threshold + ", max_specificity: " + max_specificity);
                }


                //print out realFeatures
                System.out.println("Real Features: ");
                for (GlobalRealFeatures[] features : realFeatures) {
                    for (GlobalRealFeatures feature : features) {
                        System.out.println(feature.getFeatureName() + " " + feature.getStrength() + " " + feature.getNegStrength());
                    }
                }
                System.out.println("RiskReal Features: ");
                for (GlobalRealFeatures[] features : riskRrealFeatures) {
                    for (GlobalRealFeatures feature : features) {
                        System.out.println(feature.getFeatureName() + " " + feature.getStrength() + " " + feature.getNegStrength());
                    }
                }


                //System.out.println("Correct: " + correct + " " + 100f * (1f * correct / (1f * test_set.size())));
                //System.out.println("Poisson Deviance: " + poissonDeviance(actual, predicted));
                //print the weights in model

                //printNumberOfLiteralsForEachClause()
                //model.printNumberOfLiteralsForEachClause();

            }
        }

    }


    public static List<MotorClaim> allClaims(int samples) throws Exception {

        List<MotorClaim> allClaims = readClaims("data/freMTPL2freq.csv");

        //create a balanced set with equal amount ClaimNb 0 and ClaimNb 1
        List<MotorClaim> balancedClaims = new ArrayList<>();
        int count0 = 0;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;


        for (MotorClaim claim : allClaims) {
            if (claim.getClaimNb() == 0 && count0 < samples / 5) {
                balancedClaims.add(claim);
                count0++;
            }
            if (claim.getClaimNb() == 1 && count1 < samples / 5) {
                balancedClaims.add(claim);
                count1++;
            }
            if (claim.getClaimNb() == 2 && count2 < samples / 5) {
                balancedClaims.add(claim);
                count2++;
            }
            if (claim.getClaimNb() >= 3 && count3 < samples / 5) {
                balancedClaims.add(claim);
                count3++;
            }


            //if all counts are satisfied, break
            if (count0 >= samples / 5 && count1 >= samples / 5 && count2 >= samples / 5 && count3 >= samples / 5) {
                break;
            }
        }

        //print out the counts
        System.out.println("Balanced claims size: " + balancedClaims.size() +
                " with ClaimNb 0: " + count0 +
                ", ClaimNb 1: " + count1 +
                ", ClaimNb 2: " + count2 +
                ", ClaimNb 3: " + count3);


        return balancedClaims;
    }


    public static List<AnyRecord> filter(List<AnyRecord> records, int samples) {


        //create a balanced set with equal amount ClaimNb 0 and ClaimNb 1
        List<AnyRecord> balancedClaims = new ArrayList<>();
        int count0 = 0;
        int count1 = 0;
        int count2 = 0;
        int count3 = 0;


        List<AnyRecord> claim3s = new ArrayList<>();
        List<AnyRecord> claim4s = new ArrayList<>();

        //add all the claims with label 3 to claims3s and with label 4 to claims4s
//        for (AnyRecord claim : records) {
//            if (claim.getLabel() == 3) {
//                claim3s.add(claim);
//            } else if (claim.getLabel() >= 4) {
//                claim.setLabel(4); // Treat all claims with label >= 4 as label 3
//                claim4s.add(claim);
//            }
//        }

        for (AnyRecord claim : records) {
            if (claim.getLabel() >= 3) {
                claim.setLabel(3);
                claim3s.add(claim);
            }
//            } else if (claim.getLabel() >= 4) {
//                claim.setLabel(4); // Treat all claims with label >= 4 as label 3
//                claim4s.add(claim);
//            }
        }


        for (AnyRecord claim : records) {
            if (claim.getLabel() == 0 && count0 < samples ) {
                balancedClaims.add(claim);
                count0++;
            }
            if (claim.getLabel() == 1 && count1 < samples / 2) {
                balancedClaims.add(claim);
                count1++;
            }
            if (claim.getLabel() == 2 && count2 < samples / 2) {
                balancedClaims.add(claim);
                count2++;
            }

            //if all counts are satisfied, break
            if (count0 >= samples  && count1 >= samples / 2 && count2 >= samples / 2) {
                System.out.println("Breaking out of loop as all counts are satisfied.");
                break;
            }
        }

        //sample more claim == 2 from balancedClaims
        for (AnyRecord claim : records) {
            if (claim.getLabel() == 2 && count2 < samples / 2) {
                balancedClaims.add(claim);
                count2++;
            }
        }


        // Add 2000 samples from claim3s and claim4s
        for(int i = 0; i < samples/2; i++) {

            //randomly select from claim3s and claim4s
            AnyRecord claim3 = claim3s.get(HVC.rnd.nextInt(claim3s.size()));
            //AnyRecord claim4 = claim4s.get(HVC.rnd.nextInt(claim4s.size()));

            balancedClaims.add(claim3);
            //balancedClaims.add(claim4);

        }



        //print out the counts
        System.out.println("Balanced claims size: " + balancedClaims.size() +
                " with ClaimNb 0: " + count0 +
                ", ClaimNb 1: " + count1 +
                ", ClaimNb 2: " + count2 +
                ", ClaimNb 3: " + claim3s.size() );


        return balancedClaims;
    }

    public static double poissonDeviance(double[] actual, double[] predicted) {
        double sum = 0.0;
        for (int i = 0; i < actual.length; i++) {
            double y = actual[i];
            double yHat = predicted[i];

            if(yHat == 0) {
                yHat = .052;
            }

            if (y == 0) {
                sum += 2 * yHat;
            } else if (y > 0 && yHat > 0) {
                sum += 2 * (yHat - y + y * Math.log(y / yHat));
            } else {
                System.err.println("Invalid values: y = " + y + ", yHat = " + yHat);
                throw new IllegalArgumentException("Predicted value must be positive");
            }
        }
        return sum / actual.length;
    }

    public static double poissonDevianceReal(int[] actual, double[] predictedRate, double[] exposure) {
        double loss = 0.0;
        for (int i = 0; i < actual.length; i++) {
            int Ni = actual[i];
            double vi = exposure[i];
            double lambdaHat = predictedRate[i]; // already rescaled to real-world units

            if (Ni == 0) {
                loss += 2 * lambdaHat * vi;
            } else if (lambdaHat > 0) {
                double ratio = (lambdaHat * vi) / Ni;
                loss += 2 * (Ni * (ratio - 1 - Math.log(ratio)));
            } else {
                lambdaHat = 1e-6; // or throw an error if that fits your style
                double ratio = (lambdaHat * vi) / Ni;
                loss += 2 * (Ni * (ratio - 1 - Math.log(ratio)));
            }
        }
        return loss;
    }
}

