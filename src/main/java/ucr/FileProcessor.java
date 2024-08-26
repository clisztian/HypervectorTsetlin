package ucr;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class OptimalParameters {
    String dataSetName;
    int clauses;
    int nLiterals;
    double threshold;
    double specificity;
    boolean negativeFocused;
    double errorRate;
    double accuracy;

    String dataSetType;
    int dataSetLength;
    int numberClasses;

    int numberTrain;

    String lengthCategory;
    String classCategory;

    public int getNumberTrain() {
        return numberTrain;
    }

    public void setNumberTrain(int numberTrain) {
        this.numberTrain = numberTrain;
    }

    public String getNumberTrainCategory() {
        return numberTrainCategory;
    }

    public void setNumberTrainCategory(String numberTrainCategory) {
        this.numberTrainCategory = numberTrainCategory;
    }

    String numberTrainCategory;

    public String getLengthCategory() {
        return lengthCategory;
    }

    public void setLengthCategory(String lengthCategory) {
        this.lengthCategory = lengthCategory;
    }

    public String getClassCategory() {
        return classCategory;
    }

    public void setClassCategory(String classCategory) {
        this.classCategory = classCategory;
    }

    double targetErrorRate;

    public String getDataSetType() {
        return dataSetType;
    }

    public void setDataSetType(String dataSetType) {
        this.dataSetType = dataSetType;
    }

    public int getDataSetLength() {
        return dataSetLength;
    }

    public void setDataSetLength(int dataSetLength) {
        this.dataSetLength = dataSetLength;
    }

    public int getNumberClasses() {
        return numberClasses;
    }

    public void setNumberClasses(int numberClasses) {
        this.numberClasses = numberClasses;
    }

    public double getTargetErrorRate() {
        return targetErrorRate;
    }

    public void setTargetErrorRate(double targetErrorRate) {
        this.targetErrorRate = targetErrorRate;
    }

    public String getDataSetName() {
        return dataSetName;
    }

    public void setDataSetName(String dataSetName) {
        this.dataSetName = dataSetName;
    }

    public int getClauses() {
        return clauses;
    }

    public void setClauses(int clauses) {
        this.clauses = clauses;
    }

    public int getnLiterals() {
        return nLiterals;
    }

    public void setnLiterals(int nLiterals) {
        this.nLiterals = nLiterals;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getSpecificity() {
        return specificity;
    }

    public void setSpecificity(double specificity) {
        this.specificity = specificity;
    }

    public boolean isNegativeFocused() {
        return negativeFocused;
    }

    public void setNegativeFocused(boolean negativeFocused) {
        this.negativeFocused = negativeFocused;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    public OptimalParameters(String dataSetName, int clauses, int nLiterals, double threshold,
                             double specificity, boolean negativeFocused, double errorRate, double accuracy) {
        this.dataSetName = dataSetName;
        this.clauses = clauses;
        this.nLiterals = nLiterals;
        this.threshold = threshold;
        this.specificity = specificity;
        this.negativeFocused = negativeFocused;
        this.errorRate = errorRate;
        this.accuracy = accuracy;
    }

    @Override
    public String toString() {
        return "OptimalParameters{" +
                "dataSetName='" + dataSetName + '\'' +
                ", clauses=" + clauses +
                ", nLiterals=" + nLiterals +
                ", threshold=" + threshold +
                ", specificity=" + specificity +
                ", negativeFocused=" + negativeFocused +
                ", errorRate=" + errorRate +
                ", accuracy=" + accuracy +
                ", dataSetType='" + dataSetType +
                ", dataSetLength=" + dataSetLength +
                ", numberClasses=" + numberClasses +
                ", targetErrorRate=" + targetErrorRate +
                '}';
    }

    public String toToolTip() {
        return "dataSetName='" + dataSetName + '\'' +
                "\nclauses=" + clauses +
                "\nnLiterals=" + nLiterals +
                "\nthreshold=" + threshold +
                "\nspecificity=" + specificity +
                "\nnegativeFocused=" + negativeFocused +
                "\nerrorRate=" + errorRate +
                "\naccuracy=" + accuracy +
                "\ndataSetType='" + dataSetType +
                "\ndataSetLength=" + dataSetLength +
                "\nnumberClasses=" + numberClasses +
                "\ntargetErrorRate=" + targetErrorRate;


    }
}

public class FileProcessor {


    //get the UCResult from the file
    public static List<UCResult> getUCResults(String filePath) {

        List<UCResult> resultsList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    isHeader = false; // Skip the header line
                    continue;
                }

                String[] parts = line.split(",");

                int id = Integer.parseInt(parts[0].trim());
                String type = parts[1].trim();
                String name = parts[2].trim();
                int train = Integer.parseInt(parts[3].trim());
                int test = Integer.parseInt(parts[4].trim());
                int classCount = Integer.parseInt(parts[5].trim());

                //if parts[6] not a number, then set length to 0
                int length = 0;
                try {
                    length = Integer.parseInt(parts[6].trim());
                } catch (NumberFormatException e) {
                    length = 0;
                }




                double ed_w0 = Double.parseDouble(parts[7].trim());

                //for parts[8], split by space and get the first element, remove any whitespace before or after the number
                String dtw_learned_w_str = parts[8].split(" ")[0].trim();
                //remove any " " character from the string
                dtw_learned_w_str = dtw_learned_w_str.replace(" ", "");

                //if dtw_learned_w_str not a number or empty, then set dtw_learned_w to 0
                if (dtw_learned_w_str.isEmpty()) {
                    dtw_learned_w_str = "0";
                }
                double dtw_learned_w = Double.parseDouble( dtw_learned_w_str);


                double dtw_w100 = Double.parseDouble(parts[9].trim());
                double defaultRate = Double.parseDouble(parts[10].trim());
                String dataDonorEditor = parts[11].trim();

                UCResult result = new UCResult(id, type, name, train, test, classCount, length, ed_w0,
                        dtw_learned_w, dtw_w100, defaultRate, dataDonorEditor);
                resultsList.add(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



        return resultsList;
    }

    public static List<OptimalParameters> getParameters(String filePath) {

        List<OptimalParameters> parametersList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Processing:")) {
                    String dataSetName = line.split(" ")[1];

                    // Skip the next line
                    reader.readLine();

                    // Read the third line
                    line = reader.readLine();

                    if (line.startsWith("Max Accuracy: ParameterAccuracy{")) {
                        // Extract the parameters from the line
                        String[] parts = line.replace("Max Accuracy: ParameterAccuracy{", "")
                                .replace("}", "").split(", ");

                        int clauses = Integer.parseInt(parts[0].split("=")[1]);
                        int nLiterals = Integer.parseInt(parts[1].split("=")[1]);
                        double threshold = Double.parseDouble(parts[2].split("=")[1]);
                        double specificity = Double.parseDouble(parts[3].split("=")[1]);
                        boolean negativeFocused = Boolean.parseBoolean(parts[4].split("=")[1]);
                        double errorRate = Double.parseDouble(parts[5].split("=")[1]);
                        double accuracy = Double.parseDouble(parts[6].split("=")[1]);

                        // Create the OptimalParameters object and add it to the list
                        OptimalParameters params = new OptimalParameters(dataSetName, clauses, nLiterals, threshold,
                                specificity, negativeFocused, errorRate, accuracy);

                        replaceIfBetter(parametersList, params);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }





        return parametersList;


    }

    private static void replaceIfBetter(List<OptimalParameters> parameters, OptimalParameters newParameters) {
        for (int i = 0; i < parameters.size(); i++) {
            OptimalParameters current = parameters.get(i);
            if (newParameters.getDataSetName().equals(current.getDataSetName())) {
                if (newParameters.getErrorRate() < current.getErrorRate()) {
                    parameters.set(i, newParameters);
                }
                return;
            }
        }
        parameters.add(newParameters);
    }


    public static List<OptimalParameters> pullResults( ) {

        String filePath = "/home/lisztian/UCR_study_first_round.txt"; // Replace with your file path
        String dataSummary = "/home/lisztian/Downloads/DataSummary.csv"; // Replace with your file path

        List<UCResult> results = getUCResults(dataSummary);
        List<OptimalParameters> parameters = getParameters(filePath);

        //with the UCResults, create a map of the results, mapping name of data set to the UCResult object

        Map<String, UCResult> resultsMap = new HashMap<>();
        for (UCResult result : results) {
            resultsMap.put(result.getName(), result);
        }

        //now for every OptimalParameters object, get the corresponding UCResult object and compare the error rate of OptimalParameters with the dtw_w100 of the UCResult object

        List<OptimalParameters> lowerErrorRate = new ArrayList<>();
        List<OptimalParameters> higherErrorRate = new ArrayList<>();

        for (OptimalParameters parameter : parameters) {
            UCResult result = resultsMap.get(parameter.getDataSetName());

            //set the data set type, length, and number of classes
            parameter.setDataSetType(result.getType());
            parameter.setDataSetLength(result.getLength());
            parameter.setNumberClasses(result.getClassCount());
            parameter.setNumberTrain(result.getTrain());


            if (result != null) {
                if (parameter.getErrorRate() <= result.getDtw_w100()) {

                    parameter.setTargetErrorRate(result.getDtw_w100());
                    lowerErrorRate.add(parameter);
                    //System.out.println("Optimal parameters for " + parameter.getDataSetName() + " have a lower error rate than the dtw_w100 value.");
                }
                else if (parameter.getErrorRate()  - .10 < result.getDtw_w100()) {

                    parameter.setTargetErrorRate(result.getDtw_w100());
                    lowerErrorRate.add(parameter);
                    //System.out.println("Optimal parameters for " + parameter.getDataSetName() + " have a higher error rate than the dtw_w100 value.");
                }
                else {
                    parameter.setTargetErrorRate(result.getDtw_w100());
                    higherErrorRate.add(parameter);
                }
            }
        }

//        //print out UCResults names that are not in the OptimalParameters list by using the resultsMap keys
//        System.out.println("\nNot done yet: ");
//        for (String key : resultsMap.keySet()) {
//            boolean found = false;
//            for (OptimalParameters parameter : parameters) {
//                if (parameter.getDataSetName().equals(key)) {
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                System.out.println(key);
//            }
//        }


        //print out the higher and lower error rate lists, with the data set name and the error rate
        System.out.println("\nHigher error rate: " + higherErrorRate.size());
        for (OptimalParameters parameter : higherErrorRate) {
            System.out.println(parameter.getDataSetName() + ": " + parameter.getErrorRate() + " " + parameter.getTargetErrorRate());
        }

        System.out.println("\n\nLower error rate: " + lowerErrorRate.size());
        for (OptimalParameters parameter : lowerErrorRate) {
            System.out.println(parameter.getDataSetName() + ": " + parameter.getErrorRate() + " " + parameter.getTargetErrorRate());
        }

        //print out just the names of the data sets with higher error rates
        System.out.println("\nHigher error rate: " + higherErrorRate.size());
        for (OptimalParameters parameter : higherErrorRate) {
            System.out.println(parameter.getDataSetName());
        }

        return parameters;

    }



    public static void main(String[] args) {


        String filePath = "/home/lisztian/UCR_study_first_round.txt"; // Replace with your file path
        String dataSummary = "/home/lisztian/Downloads/DataSummary.csv"; // Replace with your file path

        List<UCResult> results = getUCResults(dataSummary);
        List<OptimalParameters> parameters = getParameters(filePath);

        System.out.println("UC Results size: " + results.size());
        System.out.println("Optimal Parameters size: " + parameters.size());

        //with the UCResults, create a map of the results, mapping name of data set to the UCResult object

        Map<String, UCResult> resultsMap = new HashMap<>();
        for (UCResult result : results) {
            resultsMap.put(result.getName(), result);
        }

        //now for every OptimalParameters object, get the corresponding UCResult object and compare the error rate of OptimalParameters with the dtw_w100 of the UCResult object

        List<OptimalParameters> lowerErrorRate = new ArrayList<>();
        List<OptimalParameters> higherErrorRate = new ArrayList<>();

        for (OptimalParameters parameter : parameters) {
            UCResult result = resultsMap.get(parameter.getDataSetName());

            //set the data set type, length, and number of classes
            parameter.setDataSetType(result.getType());
            parameter.setDataSetLength(result.getLength());
            parameter.setNumberClasses(result.getClassCount());


            if (result != null) {
                if (parameter.getErrorRate() <= result.getDtw_w100()) {

                    parameter.setTargetErrorRate(result.getDtw_w100());
                    lowerErrorRate.add(parameter);
                    //System.out.println("Optimal parameters for " + parameter.getDataSetName() + " have a lower error rate than the dtw_w100 value.");
                }
                else if (parameter.getErrorRate()  - .10 < result.getDtw_w100()) {

                    parameter.setTargetErrorRate(result.getDtw_w100());
                    lowerErrorRate.add(parameter);
                    //System.out.println("Optimal parameters for " + parameter.getDataSetName() + " have a higher error rate than the dtw_w100 value.");
                }
                else {
                    parameter.setTargetErrorRate(result.getDtw_w100());
                    higherErrorRate.add(parameter);
                }
            }
        }

        //print out UCResults names that are not in the OptimalParameters list by using the resultsMap keys
        System.out.println("\nNot done yet: ");
        for (String key : resultsMap.keySet()) {
            boolean found = false;
            for (OptimalParameters parameter : parameters) {
                if (parameter.getDataSetName().equals(key)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                System.out.println(key);
            }
        }


        //print out the higher and lower error rate lists, with the data set name and the error rate
        System.out.println("\nHigher error rate: " + higherErrorRate.size());
        for (OptimalParameters parameter : higherErrorRate) {
            System.out.println(parameter.getDataSetName() + ": " + parameter.getErrorRate() + " " + parameter.getTargetErrorRate());
        }

        System.out.println("\n\nLower error rate: " + lowerErrorRate.size());
        for (OptimalParameters parameter : lowerErrorRate) {
            System.out.println(parameter.getDataSetName() + ": " + parameter.getErrorRate() + " " + parameter.getTargetErrorRate());
        }

        //print out just the names of the data sets with higher error rates
        System.out.println("\nHigher error rate: " + higherErrorRate.size());
        for (OptimalParameters parameter : higherErrorRate) {
            System.out.println(parameter.getDataSetName());
        }

    }
}
