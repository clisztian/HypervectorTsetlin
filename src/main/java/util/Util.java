package util;

import com.github.signaflo.timeseries.TimeSeries;
import com.github.signaflo.timeseries.model.arima.ArimaCoefficients;
import com.github.signaflo.timeseries.model.arima.ArimaProcess;
import com.jfoenix.controls.JFXSlider;

import java.text.DecimalFormat;
import java.text.Format;
import java.time.format.DateTimeFormatter;

public class Util {

    public static Format numberFormat;

    public static double precision3(double x){
        return Math.round(x*100.0)/100.0;
    }

    public static double precision2(double x){
        return Math.round(x*10.0)/10.0;
    }

    public static float precisionFloat3(float x){
        return Math.round(x*100f)/100f;
    }

    public static double precision4(double x){
        return Math.round(x*1000.0)/1000.0;
    }

    public static double precision5(double x){
        return Math.round(x*10000.0)/10000.0;
    }


    public static float precision5Float(float x){
        return Math.round(x*10000f)/10000f;
    }

    public static DecimalFormat df1 = new DecimalFormat("#,###");
    public static DecimalFormat df2 = new DecimalFormat("0.00");
    public static DecimalFormat df3 = new DecimalFormat("0.000");
    public static DecimalFormat df4 = new DecimalFormat("0.0000");
    public static DecimalFormat df5 = new DecimalFormat("#.#####");
    public static DecimalFormat df6 = new DecimalFormat("#.00000");
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");
    public static DateTimeFormatter nanoformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
    public static DateTimeFormatter zformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'");
    public static double precision6(double x){
        return Math.round(x*100000.0)/100000.0;
    }

    public static String getRandomColor() {

        //create a random color in the form rgb(int, int, int) where each int is between 0 and 255
        int r = (int) (Math.random() * 180);
        int g = (int) (Math.random() * 240);
        int b= 240;

        return "rgb(" + r + ", " + g + ", " + b + ")";

    }

    public static double transform(double v, int c) {
        if(c == 0) {
            return v;
        }

        if(v == 0) {
            return 0;
        }

        return Math.log10(v);
    }

    public static double whichValue(double v, double[] values) {

        for(int i = 0; i < values.length; i++) {
            if(v <= values[i]) {
                return values[i];
            }
        }
        return values[values.length - 1];
    }

    public static TimeSeries sampleMAModel(int N) {

        ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
        ArimaCoefficients coefficients = builder.setMACoeffs(-0.1)
                .setARCoeffs(0.9)
                .build();

        ArimaProcess process = ArimaProcess.builder()
                .setCoefficients(coefficients)
                .build();

        TimeSeries myseries = process.simulate(N+100);

        return myseries.slice(100, myseries.size()-1);
    }

    public static TimeSeries sampleCyclicalModel(int N) {

        ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
        ArimaCoefficients coefficients = builder.setMACoeffs(-0.1)
                .setARCoeffs(0.1)
                .build();

        ArimaProcess process = ArimaProcess.builder()
                .setCoefficients(coefficients)
                .build();

        TimeSeries myseries = process.simulate(N+100);


        return myseries.slice(100, myseries.size()-1);
    }



    public static TimeSeries sampleARModel(int N) {

        ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
        ArimaCoefficients coefficients = builder .setARCoeffs(0.7)
                .build();

        ArimaProcess process = ArimaProcess.builder()
                .setCoefficients(coefficients)
                .build();

        TimeSeries myseries = process.simulate(N+100);

        return myseries.slice(100, myseries.size()-1);
    }



    public static TimeSeries sampleSeasonalARModel(int N) {

        ArimaCoefficients.Builder builder = ArimaCoefficients.builder();
        ArimaCoefficients coefficients = builder.setARCoeffs(0.1)
                .setSeasonalARCoeffs(0.71)
                .setSeasonalFrequency(12)
                .build();

        ArimaProcess process = ArimaProcess.builder()
                .setCoefficients(coefficients)
                .build();

        TimeSeries myseries = process.simulate(N+100);

        return myseries.slice(100, myseries.size()-1);
    }

    private static JFXSlider init(JFXSlider slider) {
        slider.setShowTickLabels(false);
        slider.setShowTickMarks(false);
        slider.setMajorTickUnit(1);
        slider.setMinorTickCount(0);
        slider.setBlockIncrement(1);
        return slider;
    }
}
