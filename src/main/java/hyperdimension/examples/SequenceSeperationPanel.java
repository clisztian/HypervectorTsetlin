package hyperdimension.examples;

import com.github.signaflo.timeseries.TimeSeries;
import com.jfoenix.controls.JFXSlider;
import hyperdimension.encoders.VanillaBHV;
import hyperdimension.sequences.SequenceEncoder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import tagbio.umap.Umap;
import tagbio.umap.metric.HammingMetric;
import tagbio.umap.metric.Metric;
import util.DataPair;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static util.Util.sampleMAModel;


//        final Umap umap = new Umap();
//        //copy the encoded sequences to float array
//        float[][] data = new float[encodedSequences.size()][VanillaBHV.DIMENSION];
//        for (int i = 0; i < encodedSequences.size(); i++) {
//            boolean[] v = encodedSequences.get(i).toBooleanVector();
//            for (int j = 0; j < VanillaBHV.DIMENSION; j++) {
//                data[i][j] = v[j] ? 1f : 0f;
//            }
//        }
//
//        umap.setNumberComponents(2);         // number of dimensions in result
//        umap.setNumberNearestNeighbours(2);
//        umap.setLocalConnectivity(5);
//        umap.setMetric(HammingMetric.SINGLETON);      // use HAMMING for binary data
//        umap.setThreads(1);                  // use > 1 to enable parallelism
//        final float[][] result = umap.fitTransform(data);
//
//        //print out the result
//        for (int i = 0; i < result.length; i++) {
//            System.out.println("Sequence " + Arrays.toString(sequences[i]) + " is at " + Arrays.toString(result[i]));
//        }

public class SequenceSeperationPanel {

    private final VBox vbox;


    private final int nSeries = 100;
    private final int seriesLength = 214;

    private final int numberGroups = 2;

    private int nGrams = 8;
    private int quantization = 100;

    JFXSlider neighbourSlider;
    JFXSlider minDistanceSlider;
    JFXSlider localConnectivitySlider;
    LineChart<Number, Number> lineChart;

    final Umap umap = new Umap();

    private ScatterChart<Number, Number> sc;
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();

    private DataPair pair;
    final List<double[]> seriesAll = new ArrayList<>();
    final String styleAskHovered = "-fx-background-color: rgb(245, 85, 32);";
    final String styleBidHovered = "-fx-background-color: rgb(85,234,245);";

    DecimalFormat df = new DecimalFormat("#.####");
    private Stage timeSeriesStage;

    public SequenceSeperationPanel() {

        Button simulateButton = new Button("Simulate Data");
        simulateButton.setOnAction(event -> simulateData());
        //set button css
        simulateButton.getStylesheets().add(getClass().getClassLoader().getResource("css/button.css").toExternalForm());



        sc = new ScatterChart<Number,Number>(xAxis,yAxis);

        sc.setAnimated(false);
        sc.setLegendVisible(false);
        sc.getXAxis().setTickLabelsVisible(false);
        sc.getXAxis().setTickMarkVisible(false);
        sc.getYAxis().setTickLabelsVisible(false);
        sc.getYAxis().setTickMarkVisible(false);


        RadialGradient gradient = new RadialGradient(0, 0, 0.5, 0.50, 0.50, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(50, 20, 26)),
                new Stop(1,Color.rgb(47, 60, 66)));

        sc.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));
        sc.setOpacity(.7);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(5);
        shadow.setOffsetY(5);
        shadow.setColor(Color.BLACK);
        shadow.setBlurType(BlurType.GAUSSIAN);

        Glow glow = new Glow(1.0);
        createWindow();

        umap.setNumberComponents(2);         // number of dimensions in result
        umap.setNumberNearestNeighbours(15);
        umap.setLocalConnectivity(1);
        umap.setMinDist(0.1f);
        umap.setThreads(1);
        umap.setMetric(HammingMetric.SINGLETON);

        neighbourSlider = new JFXSlider(5, 50, 15);
        minDistanceSlider = new JFXSlider(1, 100, 1);

        neighbourSlider.setShowTickLabels(false);
        neighbourSlider.setShowTickMarks(false);
        neighbourSlider.setMajorTickUnit(10);
        neighbourSlider.setMinorTickCount(1);
        neighbourSlider.setBlockIncrement(1);
        neighbourSlider.setSnapToTicks(true);
        neighbourSlider.setPrefWidth(200);

        minDistanceSlider.setShowTickLabels(false);
        minDistanceSlider.setShowTickMarks(false);
        minDistanceSlider.setMajorTickUnit(10);
        minDistanceSlider.setMinorTickCount(1);
        minDistanceSlider.setBlockIncrement(1);
        minDistanceSlider.setSnapToTicks(true);
        minDistanceSlider.setPrefWidth(200);

        localConnectivitySlider = new JFXSlider(1, 10, 1);
        localConnectivitySlider.setShowTickLabels(false);
        localConnectivitySlider.setShowTickMarks(false);
        localConnectivitySlider.setMajorTickUnit(1);
        localConnectivitySlider.setMinorTickCount(1);
        localConnectivitySlider.setBlockIncrement(1);
        localConnectivitySlider.setSnapToTicks(true);
        localConnectivitySlider.setPrefWidth(200);


        //create TextFields for the sliders
        TextField neighbourTextField = new TextField();
        neighbourTextField.textProperty().bind(neighbourSlider.valueProperty().asString("%.0f"));

        TextField minDistField = new TextField();
        TextField localConnectivityField = new TextField();

        minDistField.setText("0.1");
        localConnectivityField.textProperty().bind(localConnectivitySlider.valueProperty().asString("%.0f"));

        //add listeners to the sliders
        neighbourSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            umap.setNumberNearestNeighbours(newValue.intValue());
            replot();
        });

        minDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            umap.setMinDist(.01f*newValue.intValue());
            minDistField.setText(String.format("%.2f", .01f*newValue.intValue()));
            replot();
        });

        localConnectivitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            umap.setLocalConnectivity(newValue.intValue());
            System.out.println("Local Connectivity: " + newValue.intValue());
            replot();
        });

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "euclidean",
                        "l2",
                        "manhattan",
                        "l1",
                        "taxicab",
                        "chebyshev",
                        "linfinity",
                        "linfty",
                        "linf",
                        "canberra",
                        "cosine",
                        "correlation",
                        "haversine",
                        "braycurtis",
                        "jaccard",
                        "dice",
                        "matching",
                        "kulsinski",
                        "rogerstanimoto",
                        "russellrao",
                        "sokalsneath",
                        "sokalmichener",
                        "yule"
                );


        ComboBox<String> metric_box = new ComboBox<String>(options);
        GridPane gridPane = new GridPane();
        //set spacing
        gridPane.setHgap(15);
        gridPane.setVgap(15);

        gridPane.add(new Label("Neighbour:"), 0, 0);
        gridPane.add(neighbourSlider, 1, 0);
        gridPane.add(neighbourTextField, 2, 0);
        gridPane.add(new Label("MinDistance:"), 0, 1);
        gridPane.add(minDistanceSlider, 1, 1);
        gridPane.add(minDistField, 2, 1);
        gridPane.add(new Label("Local Connectivity:"), 0, 2);
        gridPane.add(localConnectivitySlider, 1, 2);
        gridPane.add(localConnectivityField, 2, 2);
        //add button
        gridPane.add(simulateButton, 0, 3);
        gridPane.add(metric_box, 1, 3);





        metric_box.setOnAction((event) -> {

            String selectedItem = (String)metric_box.getSelectionModel().getSelectedItem();
            umap.setTargetMetric(Metric.getMetric(selectedItem));
            replot();
        });


        vbox = new VBox();
        vbox.getChildren().addAll(sc, gridPane);
        VBox.setVgrow(sc, Priority.ALWAYS);
        HBox.setHgrow(sc, Priority.ALWAYS);

        vbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        //set padding
        vbox.setPadding(new Insets(10));
        vbox.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());



    }

    //when simulating entire new data sets
    public void simulateData() {

        //simulates many different types of sequences

        Random rng = new Random(0);
        long timestart = System.currentTimeMillis();

        //simulate 100 sequences of length 144
        seriesAll.clear();

        int[] labels = new int[2*nSeries];

        for(int i = 0; i < 2*nSeries; i++) {
            if(i < nSeries) {
                TimeSeries series = sampleMAModel(seriesLength);
                double[] data = new double[seriesLength];
                int phase = rng.nextInt(24);
                for (int j = 0; j < seriesLength; j++) {
                    data[j] =  4*Math.cos(2 * Math.PI * j / 12) * Math.sin(phase * Math.PI * j / 12);
                }
                seriesAll.add(data);
                labels[i] = 0;
            } else {

                TimeSeries series = sampleMAModel(seriesLength);

                double[] data = new double[seriesLength];
                for (int j = 0; j < seriesLength; j++) {
                    data[j] = series.at(j);
                }
                seriesAll.add(data);
                labels[i] = 1;
            }
        }

        //now transform seriesAll into a double[][] array
        double[][] data = new double[seriesAll.size()][seriesLength];

        for(int i = 0; i < seriesAll.size(); i++) {
            for(int j = 0; j < seriesLength; j++) {
                data[i][j] = seriesAll.get(i)[j];
            }
        }

        //instantiate a SequenceEncoder object
        final SequenceEncoder encoder = new SequenceEncoder(data,  quantization, nGrams);
        List<VanillaBHV> encodedSequences = encoder.getEncodedSequences();

        //copy the encoded sequences to float array
        float[][] dataFloat = new float[encodedSequences.size()][VanillaBHV.DIMENSION];
        for (int i = 0; i < encodedSequences.size(); i++) {
            boolean[] v = encodedSequences.get(i).toBooleanVector();
            for (int j = 0; j < VanillaBHV.DIMENSION; j++) {
                dataFloat[i][j] = v[j] ? 1f : 0f;
            }
        }

        long timeend = System.currentTimeMillis();
        System.out.println("Time to simulate data: " + (timeend - timestart) + " ms");


        final float[][] result = umap.fitTransform(dataFloat);

        pair = new DataPair(dataFloat, labels);

        Platform.runLater(() -> {
            computeScatterPlot(result, pair.getLabels());
        });


    }


    /**
     * When changing the sliders
     */
    public void replot() {


        final float[][] result = umap.fitTransform(pair.getData());

        Platform.runLater(() -> {
            computeScatterPlot(result, pair.getLabels());
        });

    }

    public void computeScatterPlot(float[][] result, int[] labels) {


        //initialize the series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < result.length; i++) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(result[i][0], result[i][1], seriesAll.get(i));
            series.getData().add(data);
        }

        sc.getData().setAll(series);

        //color the points relative to labels
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<Number, Number> data = series.getData().get(i);

            if(labels[i] == 0) {
                data.getNode().setStyle(styleAskHovered);
            } else {
                data.getNode().setStyle(styleBidHovered);
            }

            //on mouse hover
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setEffect(new Glow(1.0));

                double[] s = (double[])data.getExtraValue();

                //plot the time series, use the timeSeriesStage, make it pop up
                timeSeriesStage.show();
                plotTimeSeriesData(s);

            });
            //on mouse exit, remove the stage/hide it
            data.getNode().setOnMouseExited(event -> {
                data.getNode().setEffect(null);
                if(timeSeriesStage != null) {
                    timeSeriesStage.hide();
                }
            });
        }

    }

    //create a Window popop showing the time series
    public void createWindow() {

        timeSeriesStage = new Stage();
        timeSeriesStage.setTitle("Time Series");

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Days");
        yAxis.setLabel("Value");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Time Series Data");
        lineChart.setAnimated(false);

        //set the css style
        lineChart.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());
        //create scene
        Scene scene = new Scene(lineChart, 800, 600);

        timeSeriesStage.setScene(scene);


    }


    /**
     * Plot time series data on the line chart
     * @param ts
     */
    private void plotTimeSeriesData(double[] ts) {

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for(int i = 0; i < ts.length; i++) {
            series.getData().add(new XYChart.Data<>(i, ts[i]));
        }
        lineChart.getData().setAll(series);



    }




    public VBox getVbox() {
        return vbox;
    }

}
