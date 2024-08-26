package hyperdimension.examples;

import com.github.signaflo.timeseries.TimeSeries;
import com.jfoenix.controls.JFXSlider;
import hyperdimension.encoders.VanillaBHV;
import hyperdimension.sequences.SequenceEncoder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import util.HDViewer;

import javax.swing.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Random;

import static fx.MidPriceExtractor.extractMidPriceAtTime;
import static util.Util.*;

public class HDTimeSeriesPrediction extends Application {

    private final int nSeries = 100;
    private final int seriesLength = 224;

    private final int numberGroups = 2;
    private SequenceEncoder encoder;
    Number val = 0;
    private int nGrams = 5;
    private int quantization = 100;


    private DecimalFormat df = new DecimalFormat("#.##");
    final List<double[]> seriesAll = new ArrayList<>();
    private Stage timeSeriesStage;
    private LineChart<Number, Number> lineChart;
    private Button newSeriesButton;
    private Button forecastButton;
    private ComboBox<String> modelComboBox;
    private double[] nGram;
    private double[] dataSeries;
    private List<Number> forecastedValues = new ArrayList<>();

    private final HDViewer hdViewer = new HDViewer();
    private final HDViewer hdViewerInput = new HDViewer();

    private List<VanillaBHV> latestBHVs = new ArrayList<>();
    private List<VanillaBHV> latestInputs = new ArrayList<>();
    private double[] fx_series;

    //when simulating entire new data sets
    public void initiate(Stage stage) {

        createWindow( stage);

        trainTimeSeries(0);

        plotTimeSeriesData(dataSeries, 0);

    }



    public void forecastOneStep() {

        val = encoder.predict(nGram);
        forecastedValues.add(val);

        latestBHVs.add(encoder.getLatestResidual());
        hdViewer.setData(latestBHVs);

        latestInputs.add(encoder.getLatestQuery());
        hdViewerInput.setData(latestInputs);

        int nForecasts = forecastedValues.size();

        //create new double[] with original data plus forecasted values
        double[] newData = new double[dataSeries.length + nForecasts];
        for(int j = 0; j < dataSeries.length; j++) {
            newData[j] = dataSeries[j];
        }
        for(int j = 0; j < nForecasts; j++) {
            newData[dataSeries.length + j] = forecastedValues.get(j).doubleValue();
        }

        plotTimeSeriesData(newData, nForecasts);

        //update nGram
        for(int j = 0; j < nGrams - 1; j++) {
            nGram[j] = nGram[j + 1];
        }
        nGram[nGrams - 1] = val.doubleValue();

    }


    public void createWindow(Stage state) {

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Value");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Time Series Data");
        lineChart.setAnimated(false);

        //set the css style
        lineChart.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());

        ComboBox<String> fxComboBox = new ComboBox<>();
        fxComboBox.getItems().addAll("EURUSD", "GBPUSD", "USDJPY", "USDCHF", "AUDUSD", "NZDUSD", "EURJPY", "AUDJPY");
        fxComboBox.getSelectionModel().select(0);

        JFXSlider dSlider = new JFXSlider(2, 10, 8);
        dSlider.setShowTickLabels(false);
        dSlider.setShowTickMarks(false);
        dSlider.setBlockIncrement(1);
        dSlider.setMajorTickUnit(1);
        dSlider.setMinorTickCount(0);

        TextField dField = new TextField();
        dField.setText(".8");
        dField.setPrefWidth(100);
        dSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            trainFXSeries(fxComboBox.getSelectionModel().getSelectedItem(), newValue.doubleValue() / 10.0);
            dField.setText(df.format(newValue.doubleValue() / 10.0));
        });

        JFXSlider nGramSlider = new JFXSlider(1, 10, 3);
        nGramSlider.setShowTickLabels(false);
        nGramSlider.setShowTickMarks(false);
        nGramSlider.setBlockIncrement(1);
        nGramSlider.setMajorTickUnit(1);
        nGramSlider.setMinorTickCount(0);

        TextField nGramField = new TextField();
        nGramField.setText("3");
        nGramField.setPrefWidth(100);






        GridPane gridPane = new GridPane();
        gridPane.add(fxComboBox, 0, 0);
        gridPane.add(new Label("Fractional Differencing"), 0, 1);
        gridPane.add(dSlider, 1, 1);
        gridPane.add(dField, 2, 1);
        gridPane.add(new Label("nGrams"), 0, 2);
        gridPane.add(nGramSlider, 1, 2);
        gridPane.add(nGramField, 2, 2);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));



        //make a combo box to select the model
        ComboBox<String> modelComboBox = new ComboBox<>();
        modelComboBox.getItems().addAll("MA Model", "Seasonal AR Model", "AR Model", "Harmonics", "FX");
        modelComboBox.getSelectionModel().select(0);

        modelComboBox.setOnAction(event -> {
            int model = modelComboBox.getSelectionModel().getSelectedIndex();
            trainTimeSeries(model);
        });

        newSeriesButton = new Button("Sample Series");
        newSeriesButton.setOnAction(event -> {
            sampleSeries(modelComboBox.getSelectionModel().getSelectedIndex());
        });
        newSeriesButton.getStylesheets().add(getClass().getClassLoader().getResource("css/button.css").toExternalForm());

        forecastButton = new Button("Forecast");
        forecastButton.setOnAction(event -> {
            forecastOneStep();
        });
        forecastButton.getStylesheets().add(getClass().getClassLoader().getResource("css/button.css").toExternalForm());

        nGramSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            nGrams = newValue.intValue();
            nGramField.setText(String.valueOf(newValue.intValue()));

            int model = modelComboBox.getSelectionModel().getSelectedIndex();
            trainTimeSeries(model);

        });

        //add also modelComboBox and newSeriesButton
        gridPane.add(modelComboBox, 0, 3);
        gridPane.add(newSeriesButton, 1, 3);
        gridPane.add(forecastButton, 2, 3);

        VBox vbox = new VBox();
        vbox.getChildren().addAll(lineChart, gridPane);
        vbox.setSpacing(10);
        VBox.setVgrow(lineChart, javafx.scene.layout.Priority.ALWAYS);
        vbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());

        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.getChildren().addAll(hdViewerInput.getGridPane(), vbox, hdViewer.getGridPane());

        HBox.setHgrow(vbox, Priority.ALWAYS);



        //create scene
        Scene scene = new Scene(hbox, 800, 600);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());

        //add a keyboard listener to the scene, when pressing the right arrow key, forecast one step
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case RIGHT:
                    forecastOneStep();
                    break;
                case UP:
                    sampleSeries(modelComboBox.getSelectionModel().getSelectedIndex());
            }
        });


        state.setScene(scene);

    }





    private void plotTimeSeriesData(double[] ts, int forecastLength) {

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.2, 0.2, 0.2));


        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(int i = 0; i < ts.length; i++) {
            series.getData().add(new XYChart.Data<>(i, ts[i]));
        }
        lineChart.getData().setAll(series);


        //paint the final forecastLength values a different color
        //color the points relative to labels
        for (int i = series.getData().size() - forecastLength; i < series.getData().size(); i++) {
            XYChart.Data<Number, Number> data = series.getData().get(i);
            data.getNode().setStyle("-fx-background-color: #7feefa; -fx-effect: dropshadow( three-pass-box , cyan , 6, 0.0 , 0 , 0 );");
            //data.getNode().setEffect(dropShadow);

        }


    }


    public void trainTimeSeries(int model) {

        if(model == 4) {
            trainFXSeries("EURUSD", 1);
            return;
        }

        seriesAll.clear();
        TimeSeries series = null;
        for (int i = 0; i < nSeries; i++) {

            if (model == 0) {
                series = sampleMAModel(seriesLength);
            }
            else if (model == 1) {
                series = sampleSeasonalARModel(seriesLength);
            }
            else if (model == 2) {
                series = sampleARModel(seriesLength);
            }
            else if( model == 3) {
                series = sampleHarmonics(seriesLength);
            }

            double[] data = new double[seriesLength];
            for (int j = 0; j < seriesLength; j++) {
                data[j] = series.at(j);
            }

            seriesAll.add(data);
        }

        //now transform seriesAll into a double[][] array
        double[][] data = new double[seriesAll.size()][seriesLength];

        for (int i = 0; i < seriesAll.size(); i++) {
            for (int j = 0; j < seriesLength; j++) {
                data[i][j] = seriesAll.get(i)[j];
            }
        }

        //instantiate a SequenceEncoder object
        encoder = new SequenceEncoder(data, quantization, nGrams);

        sampleSeries(model);

    }

    public void sampleSeries(int model) {

        TimeSeries series = null;
        //sample from
        if (model == 0) {
            series = sampleMAModel(seriesLength);
        }
        else if (model == 1) {
            series = sampleSeasonalARModel(seriesLength);
        }
        else if (model == 2) {
            series = sampleARModel(seriesLength);
        }
        else if( model == 3) {
            series = sampleHarmonics(seriesLength);
        }
        else if( model == 4) {
            series = sampleFX(seriesLength);
        }
        //now predict the next 12 values
        dataSeries = new double[seriesLength];
        for (int j = 0; j < seriesLength; j++) {
            dataSeries[j] = series.at(j);
        }

        //initialize nGram with the last nGrams values of the series
        nGram = new double[nGrams];
        for(int j = 0; j < nGrams; j++) {
            nGram[j] = dataSeries[dataSeries.length - nGrams + j];
        }
        Number val = 0;

        forecastedValues.clear();
        latestBHVs.clear();
        latestInputs.clear();
        plotTimeSeriesData(dataSeries, 0);

    }

    public TimeSeries sampleHarmonics(int length) {

        Random rng = new Random();
        double amplitude = rng.nextDouble();
        int period = rng.nextInt(10) + 1;
        double[] series = new double[length];
        for (int i = 0; i < length; i++) {
            series[i] = (1 - amplitude) * Math.sin(2 * Math.PI * i / period) + amplitude*Math.sin(2 * Math.PI * i / 12) + (1 - amplitude)*Math.sin(2 * Math.PI * i / 6);
        }

        return TimeSeries.from(series);

    }

    public TimeSeries sampleFX(int length) {

        //get fx_series

        if(fx_series == null) {
            trainFXSeries("EURUSD", 1);
        }

        Random rng = new Random(System.currentTimeMillis());
        int start = rng.nextInt(fx_series.length - length);

        double[] series = new double[length];
        //grab the final length values from the fx_series
        for (int i = 0; i < length; i++) {
            series[i] = fx_series[start + i];
        }

        return TimeSeries.from(series);

    }

    public void trainFXSeries(String symbol, double d) {

        //first get fractional differeced series
        String filePath = "/home/lisztian/FXProjects/TsetlinTraderFX/src/main/resources/data/" + symbol  + "_4_min.csv";
        fx_series = extractMidPriceAtTime(filePath, "08:00", d);

        seriesAll.clear();
        TimeSeries series = null;

        int start = 0;

        for (int i = 0; i < nSeries; i++) {

            //get 200 values from the fx series from start
            double[] data = new double[seriesLength];
            for (int j = 0; j < seriesLength; j++) {
                data[j] = fx_series[start + j];
            }
            seriesAll.add(data);
            start = start + seriesLength;

            if(start + seriesLength > fx_series.length) {
                break;
            }
        }

        //now transform seriesAll into a double[][] array
        double[][] data = new double[seriesAll.size()][seriesLength];

        for (int i = 0; i < seriesAll.size(); i++) {
            for (int j = 0; j < seriesLength; j++) {
                data[i][j] = seriesAll.get(i)[j];
            }
        }

        //instantiate a SequenceEncoder object
        encoder = new SequenceEncoder(data, quantization, nGrams);

        sampleSeries(4);

    }





    @Override
    public void start(Stage stage) throws Exception {
        HDTimeSeriesPrediction tsp = new HDTimeSeriesPrediction();
        tsp.initiate(stage);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
