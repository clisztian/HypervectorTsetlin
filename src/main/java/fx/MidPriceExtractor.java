package fx;

import org.apache.commons.lang3.ArrayUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class MidPriceExtractor {

    public static void main(String[] args) {
        String filePath = "/home/lisztian/FXProjects/TsetlinTraderFX/src/main/resources/data/EURUSD_4_min.csv";
        extractMidPriceAtTime(filePath, "08:00", .5);
    }

    public static double[] extractMidPriceAtTime(String filePath, String targetTime, double d) {

        double[] frac_w = computeFractionalDifferenceWeights(0.01, d);

        String line;
        String csvSplitBy = ",";

        ArrayList<Double> midPrices = new ArrayList<>();

        // Time formatter
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime targetLocalTime = LocalTime.parse(targetTime + ":00", timeFormatter);

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            // Skip the header
            br.readLine();

            // Process each line in the CSV
            while ((line = br.readLine()) != null) {
                // Split the line by comma
                String[] values = line.split(csvSplitBy);

                // Parse the BidTimestamp and check the time
                String bidTimestamp = values[0];
                LocalTime bidTime = LocalTime.parse(bidTimestamp.split(" ")[1], timeFormatter);

                if (bidTime.getHour()%2 == 0 && bidTime.getMinute() == 0) {
                    // Parse bid and ask prices
                    double bid = Double.parseDouble(values[1]);
                    double ask = Double.parseDouble(values[6]);

                    // Calculate the mid price
                    double midPrice = (bid + ask) / 2;
                    midPrices.add(midPrice);
                    // Print the mid price

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //apply the filter frac_w to the mid prices to get the fractional difference
        double[] midPricesArray = ArrayUtils.toPrimitive(midPrices.toArray(new Double[midPrices.size()]));
        double[] fracDiff = applyFractionalDifference(midPricesArray, frac_w);


        return fracDiff;

    }

    private static double[] applyFractionalDifference(double[] midPricesArray, double[] fracW) {
        double[] fracDiff = new double[midPricesArray.length];
        for(int i = 0; i < midPricesArray.length; i++) {
            double sum = 0;
            int filterLength = 0;
            int wLength = fracW.length;

            filterLength = Math.min(i + 1, wLength);
            sum = 0;
            for (int l = 0; l < filterLength; l++) {
                sum = sum + fracW[l] * Math.log(midPricesArray[i - l]);
            }

            fracDiff[i] = sum;
        }
        return fracDiff;
    }

    private static double[] computeFractionalDifferenceWeights(double thresh, double d) {

        Double[] frac_w = new Double[0];
        if(d >= 0) {

            double wk = 1.0;
            double wk1;
            double k = 1.0;
            boolean overThresh = true;


            ArrayList<Double> myWs = new ArrayList<Double>();
            myWs.add(wk);

            while(overThresh) {

                wk1 = -wk * (d - k + 1.0)/k;

                myWs.add(wk1);
                wk = wk1;
                k = k + 1.0;

                if(Math.abs(wk) < thresh) {
                    overThresh = false;
                }
            }

            frac_w = myWs.toArray(new Double[myWs.size()]);


        }
        return ArrayUtils.toPrimitive(frac_w);

    }
}
