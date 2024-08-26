package protein;

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
import javafx.scene.control.*;
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
import tsetlin.AutomataLearning;
import tsetlin.ConvolutionEncoder;
import util.DataPair;
import util.HVC;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import static util.Util.sampleMAModel;

public class ProteinMappingPanel {

    private final VBox vbox;


    private final int nSeries = 100;
    private final int seriesLength = 214;

    private final int numberGroups = 2;

    private int nGrams = 3;
    private int quantization = 100;

    private JFXSlider numberClauses = new JFXSlider(10, 1000, 500);
    private JFXSlider numberMaxLiterals = new JFXSlider(0, 200, 25);
    private JFXSlider specificity = new JFXSlider(1, 20, 2);
    private CheckBox negativeFocusedSampling = new CheckBox("Negative Focused Sampling");

    private JFXSlider numberEpochs = new JFXSlider(1,20,4);
    private JFXSlider threshold = new JFXSlider(1, 200, 50);
    private JFXSlider numberGrams = new JFXSlider(1, 8, 3);
    private TextField numberClausesField = new TextField();
    private TextField numberGramsField = new TextField();

    private TextField numberMaxLiteralsField = new TextField();
    private TextField specificityField = new TextField();
    private TextField numberEpochsField = new TextField();
    private TextField thresholdField = new TextField();
    private  ConvolutionEncoder convolutionEncoder = new ConvolutionEncoder(20*7, 1, 1);
    private Button learnButton = new Button("Learn");
    private Button simualteTrialsButton = new Button("Simulate Trials");
    JFXSlider neighbourSlider;
    JFXSlider minDistanceSlider;
    JFXSlider localConnectivitySlider;
    LineChart<Number, Number> lineChart;

    final Umap umap = new Umap();

    private ScatterChart<Number, Number> sc;
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private int[][] X_train;
    private int[][] X_test;
    private int[] y_train;
    private int[] y_test;
    private DataPair pair;
    final List<double[]> seriesAll = new ArrayList<>();

    /**
     *                         case "HYDROLASE":
     *                             setStyle("-fx-background-color: rgba(30,100,223,.4);");
     *                             break;
     *                         case "OXIDOREDUCTASE":
     *                             setStyle("-fx-background-color: rgba(252, 68, 68,.4);");
     *                             break;
     *                         case "TRANSFERASE":
     *                             setStyle("-fx-background-color: rgba(80, 222, 235,.4);");
     */

    final String styleHYDROLASE = "-fx-background-color: rgb(30,100,223);";
    final String styleOXIDOREDUCTASE = "-fx-background-color: rgb(252, 68, 68);";
    final String styleTRANSFERASE = "-fx-background-color: rgb(80, 222, 235);";

    DecimalFormat df = new DecimalFormat("#.####");
    private Stage timeSeriesStage;

    private List<ProteinMetaData> metaDataList;
    private ProteinEncoderDecoder proteinEncoderDecoder;


    public ProteinMappingPanel() {


        Button button = new Button("Plot");
        button.setOnAction(event -> {
            assempbleData();
        });
        button.setPrefWidth(100);
        //add css
        button.getStylesheets().add(getClass().getClassLoader().getResource("css/button.css").toExternalForm());

        learnButton.setOnAction(event -> {
            learnTM();
        });
        learnButton.setPrefWidth(100);
        learnButton.getStylesheets().add(getClass().getClassLoader().getResource("css/button.css").toExternalForm());



        sc = new ScatterChart<Number,Number>(xAxis,yAxis);

        sc.setAnimated(false);
        sc.setLegendVisible(false);
        sc.getXAxis().setTickLabelsVisible(false);
        sc.getXAxis().setTickMarkVisible(false);
        sc.getYAxis().setTickLabelsVisible(false);
        sc.getYAxis().setTickMarkVisible(false);


        RadialGradient gradient = new RadialGradient(0, 0, 0.5, 0.50, 0.50, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10,12,20)),
                new Stop(1,Color.rgb(10,12,20).brighter()));

        sc.setBackground(new Background(new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY)));


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

        GridPane umapGridPane = new GridPane();
        umapGridPane.setHgap(15);
        umapGridPane.setVgap(15);
        umapGridPane.add(new Label("Neighbour:"), 0, 0);
        umapGridPane.add(neighbourSlider, 1, 0);
        umapGridPane.add(neighbourTextField, 2, 0);
        umapGridPane.add(new Label("MinDistance:"), 0, 1);
        umapGridPane.add(minDistanceSlider, 1, 1);
        umapGridPane.add(minDistField, 2, 1);
        umapGridPane.add(new Label("Local Connectivity:"), 0, 2);
        umapGridPane.add(localConnectivitySlider, 1, 2);
        umapGridPane.add(localConnectivityField, 2, 2);
        //add button
        umapGridPane.add(button, 0, 3);
        umapGridPane.add(metric_box, 1, 3);

        addSliderToGrid(gridPane, 0, "Number Clauses", numberClauses, numberClausesField);
        addSliderToGrid(gridPane, 1, "Number Max Literals", numberMaxLiterals, numberMaxLiteralsField);
        addSliderToGrid(gridPane, 2, "Specificity", specificity, specificityField);
        addSliderToGrid(gridPane, 3, "Number Epochs", numberEpochs, numberEpochsField);
        addSliderToGrid(gridPane, 4, "Threshold", threshold, thresholdField);
        addSliderToGrid(gridPane, 5, "Number Grams", numberGrams, numberGramsField);

        gridPane.add(learnButton,0, 6);
        gridPane.add(simualteTrialsButton, 1, 6);

        numberGrams.valueProperty().addListener((observable, oldValue, newValue) -> {
            nGrams = newValue.intValue();
            numberGramsField.setText(String.valueOf(newValue.intValue()));
            proteinEncoderDecoder.setnGramSize(nGrams);
        });

        //when numberClauses slider is changed, change the TextField
        numberClauses.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberClausesField.setText(String.valueOf(newValue.intValue()));
        });

        numberMaxLiterals.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberMaxLiteralsField.setText(String.valueOf(newValue.intValue()));
        });

        specificity.valueProperty().addListener((observable, oldValue, newValue) -> {
            specificityField.setText(String.valueOf(newValue.intValue()));
        });


        numberEpochs.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberEpochsField.setText(String.valueOf(newValue.intValue()));
        });

        threshold.valueProperty().addListener((observable, oldValue, newValue) -> {
            thresholdField.setText(String.valueOf(newValue.intValue()));
        });

        //set the default values in the TextFields
        numberClausesField.setText("500");
        numberMaxLiteralsField.setText("25");
        numberEpochsField.setText("20");
        thresholdField.setText("50");
        numberGramsField.setText("3");

        metric_box.setOnAction((event) -> {

            String selectedItem = (String)metric_box.getSelectionModel().getSelectedItem();
            umap.setTargetMetric(Metric.getMetric(selectedItem));
            replot();
        });

        simualteTrialsButton.setOnAction(event -> {
            randomizeTMLearnTrialsParallel(200);
        });

        HBox hbox = new HBox();
        hbox.getChildren().addAll(umapGridPane, gridPane);
        hbox.setPadding(new Insets(15));
        hbox.setSpacing(15);
        hbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        vbox = new VBox();
        vbox.getChildren().addAll(sc, hbox);
        VBox.setVgrow(sc, Priority.ALWAYS);
        HBox.setHgrow(sc, Priority.ALWAYS);

        vbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        //set padding
        vbox.setPadding(new Insets(10));
        vbox.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());



    }

    //when simulating entire new data sets
    public void assempbleData() {

        int[] labels = new int[metaDataList.size()];
        float[][] dataFloat = new float[metaDataList.size()][VanillaBHV.DIMENSION];


        for (int i = 0; i < metaDataList.size(); i++) {

            VanillaBHV encodedSequence = proteinEncoderDecoder.encodeSequence(metaDataList.get(i));
            boolean[] v = encodedSequence.toBooleanVector();
            for (int j = 0; j < VanillaBHV.DIMENSION; j++) {
                dataFloat[i][j] = v[j] ? 1f : 0f;
            }

            //dataFloat[i] = metaDataList.get(i).getVector();

            //labels = //"HYDROLASE = 0" "OXIDOREDUCTASE = 1" "TRANSFERASE = 2"
            labels[i] = metaDataList.get(i).getClassification().equals("HYDROLASE") ? 0 :
                    metaDataList.get(i).getClassification().equals("OXIDOREDUCTASE") ? 1 : 2;

        }

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


    private void encode() {

        int[] labels = new int[metaDataList.size()];
        int[][] X_encoded = new int[metaDataList.size()][];


        convolutionEncoder = new ConvolutionEncoder(HVC.DIMENSION, 1, 1);

        int count = 0;
        for(ProteinMetaData proteinMetaData : metaDataList) {

            VanillaBHV encodedSequence = proteinEncoderDecoder.encodeSequence(proteinMetaData);


            //int[] encoded = proteinEncoderDecoder.simpleEncoder(proteinMetaData);
            X_encoded[count] = convolutionEncoder.bit_encode(encodedSequence.toBooleanIntArray());
            labels[count] = metaDataList.get(count).getClassification().equals("HYDROLASE") ? 0 :
                    metaDataList.get(count).getClassification().equals("OXIDOREDUCTASE") ? 1 : 2;

            count++;
        }

        createTrainingData(X_encoded, labels);


    }


    private void createTrainingData(int[][] X_encoder, int[] y_encoder) {


        Random random = new Random();
        //take 80 percent of the data for training
        int trainSize = (int)(X_encoder.length * .8);
        //build a random set of unique indices from 1 - X.length
        int[] indices = new int[X_encoder.length];
        for(int i = 0; i < X_encoder.length; i++) {
            indices[i] = i;
        }
        //shuffle the indices
        for(int i = 0; i < X_encoder.length; i++) {
            int j = (int)(random.nextDouble() * X_encoder.length);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }

        X_train = new int[trainSize][];
        y_train = new int[trainSize];
        for(int i = 0; i < trainSize; i++) {
            X_train[i] = X_encoder[indices[i]];
            y_train[i] = y_encoder[indices[i]];
        }

        X_test = new int[X_encoder.length - trainSize][];
        y_test = new int[X_encoder.length - trainSize];

        for(int i = trainSize; i < X_encoder.length; i++) {
            X_test[i - trainSize] = X_encoder[indices[i]];
            y_test[i - trainSize] = y_encoder[indices[i]];
        }
    }

    public void learnTM() {

        AutomataLearning model = new AutomataLearning(
                convolutionEncoder,
                (int)numberClauses.getValue(),
                (int)threshold.getValue(),
                (float) specificity.getValue(),
                3,
                .1f);

        model.setMaxNumberOfLiterals((int)numberMaxLiterals.getValue());
        model.setNegativeFocusedSampling(negativeFocusedSampling.isSelected());

        System.out.println("Number of clauses: " + numberClauses.getValue());
        System.out.println("Number of literals: " + numberMaxLiterals.getValue());
        System.out.println("Specificity: " + specificity.getValue());
        System.out.println("Negative Focused Sampling: " + negativeFocusedSampling.isSelected());



        for(int e = 0; e < (int)numberEpochs.getValue(); e++) {

            model.fit(X_train, y_train);

            //compute the accuracy of each class
            int[] classPredicted = new int[3];
            int[] classCorrect = new int[3];
            //compute the accuracy
            int correct = 0;
            for(int i = 0; i < X_test.length; i++) {
                int pred = model.predict(X_test[i]);
                //System.out.println("Predicted: " + pred + " Actual: " + y_test[i]);
                if(pred == y_test[i]) {
                    correct++;
                    classCorrect[pred]++;
                }
                classPredicted[pred]++;
            }
            System.out.println("Epoch: " + e + " Accuracy: " + (float)correct/X_test.length);
            System.out.println("Class 0: " + (float)classCorrect[0]/classPredicted[0]);
            System.out.println("Class 1: " + (float)classCorrect[1]/classPredicted[1]);
            System.out.println("Class 2: " + (float)classCorrect[2]/classPredicted[2]);
        }


    }

    private float learning(int clauses, int nLiterals, int threshold, float specificity, boolean negativeFocused) {

        AutomataLearning model = new AutomataLearning(
                convolutionEncoder,
                clauses,
                threshold,
                specificity,
                3,
                .1f);

        model.setMaxNumberOfLiterals(nLiterals);
        model.setNegativeFocusedSampling(negativeFocused);

        //print out parameters
        System.out.println("Number of clauses: " + clauses + " Number of literals: " + nLiterals + " Threshold: " + threshold + " Specificity: " + specificity + " Negative Focused Sampling: " + negativeFocused);


        float maxAccuracy = 0;

        for(int e = 0; e < 4; e++) {

            model.fit(X_train, y_train);

            //compute the accuracy of each class
            int[] classPredicted = new int[3];
            int[] classCorrect = new int[3];
            //compute the accuracy
            int correct = 0;
            for(int i = 0; i < X_test.length; i++) {
                int pred = model.predict(X_test[i]);
                //System.out.println("Predicted: " + pred + " Actual: " + y_test[i]);
                if(pred == y_test[i]) {
                    correct++;
                    classCorrect[pred]++;
                }
                classPredicted[y_test[i]]++;
            }
            System.out.println("Epoch: " + e + " Accuracy: " + (float)correct/X_test.length);
            System.out.println("Class 0: " + (float)classCorrect[0]/classPredicted[0]);
            System.out.println("Class 1: " + (float)classCorrect[1]/classPredicted[1]);
            System.out.println("Class 2: " + (float)classCorrect[2]/classPredicted[2]);

            maxAccuracy = Math.max(maxAccuracy, (float)correct/X_test.length);
        }

        return maxAccuracy;
    }


    public void randomizeTMLearnTrials(int numberSimulations) {

        //best with 7 grams
        //0.60 - Number of clauses: 2649 Number of literals: 205 Threshold: 155 Specificity: 19.952291 Negative Focused Sampling: true
        //0.60 - Number of clauses: 3253 Number of literals: 263 Threshold: 158 Specificity: 11.060814 Negative Focused Sampling: true

        //best with 8 grams
        //Number of clauses: 2275 Number of literals: 86 Threshold: 164 Specificity: 12.378882 Negative Focused Sampling: true

        //best with 3 grams
        //Number of clauses: 3835 Number of literals: 75 Threshold: 299 Specificity: 16.413372 Negative Focused Sampling: true

        float maxAccuracy = 0;
        //randomize the parameters numberSimulations
        for(int i = 0; i < numberSimulations; i++) {

            int clauses = 2000 + (int)(Math.random() * 2000);
            int nLiterals = 50 + (int)(Math.random() * 300);
            int threshold = 100 + (int)(Math.random() * 200);
            float specificity = 10f + (float)(Math.random() * 10);
            boolean negativeFocused = true;

            float accuracy = learning(clauses, nLiterals, threshold, specificity, negativeFocused);
            maxAccuracy = Math.max(maxAccuracy, accuracy);
        }

        System.out.println("Max Accuracy: " + maxAccuracy);
    }



    public void computeScatterPlot(float[][] result, int[] labels) {


        //initialize the series
        XYChart.Series<Number, Number> series = new XYChart.Series<>();

        for (int i = 0; i < result.length; i++) {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(result[i][0], result[i][1], metaDataList.get(i));
            series.getData().add(data);
        }

        sc.getData().setAll(series);

        //color the points relative to labels
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<Number, Number> data = series.getData().get(i);

            if(labels[i] == 0) {
                data.getNode().setStyle(styleHYDROLASE);
            }
            else if(labels[i] == 1) {
                data.getNode().setStyle(styleOXIDOREDUCTASE);
            }
            else {
                data.getNode().setStyle(styleTRANSFERASE);
            }

            //on mouse hover
            data.getNode().setOnMouseEntered(event -> {
                data.getNode().setEffect(new Glow(1.0));

                ProteinMetaData s = (ProteinMetaData) data.getExtraValue();

                //plot the time series, use the timeSeriesStage, make it pop up
                //print out the protein sequence
                System.out.println(s.getProteinSequences().get(0).getSequence());

            });
            //on mouse exit, remove the stage/hide it
            data.getNode().setOnMouseExited(event -> {
                data.getNode().setEffect(null);
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



    public void setMetaDataList(List<ProteinMetaData> metaDataList) {
        this.metaDataList = metaDataList;

        //encode the data for TMs
        encode();
    }


    public VBox getVbox() {
        return vbox;
    }

    private void addSliderToGrid(GridPane gridPane, int row, String labelText, JFXSlider slider, TextField textField) {
        Label label = new Label(labelText);

        gridPane.add(label, 0, row);
        gridPane.add(slider, 1, row);
        gridPane.add(textField, 2, row);
    }


    public void setProteinMetaEncoder(ProteinEncoderDecoder proteinEncoderDecoder) {
        this.proteinEncoderDecoder = proteinEncoderDecoder;
    }




    public void randomizeTMLearnTrialsParallel(int numberSimulations) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        MaxAccuracyTask task = new MaxAccuracyTask(0, numberSimulations);
        float maxAccuracy = forkJoinPool.invoke(task);

        System.out.println("Max Accuracy: " + maxAccuracy);
    }

    private class MaxAccuracyTask extends RecursiveTask<Float> {
        private static final int THRESHOLD = 20; // Adjust this based on your performance needs
        private int start, end;

        public MaxAccuracyTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Float compute() {
            if (end - start <= THRESHOLD) {
                // Perform sequential computation for small range
                float maxAccuracy = 0;
                for (int i = start; i < end; i++) {
                    int clauses = 2000 + (int) (Math.random() * 2000);
                    int nLiterals = 50 + (int) (Math.random() * 300);
                    int threshold = 100 + (int) (Math.random() * 200);
                    float specificity = 10f + (float) (Math.random() * 10);
                    boolean negativeFocused = true;

                    System.out.println("Start, End: " + start + " " + end);
                    float accuracy = learning(clauses, nLiterals, threshold, specificity, negativeFocused);
                    maxAccuracy = Math.max(maxAccuracy, accuracy);
                }
                return maxAccuracy;
            } else {
                // Split the task into smaller tasks
                int mid = (start + end) / 2;
                MaxAccuracyTask leftTask = new MaxAccuracyTask(start, mid);
                MaxAccuracyTask rightTask = new MaxAccuracyTask(mid, end);

                leftTask.fork(); // Asynchronously execute the left task
                float rightResult = rightTask.compute(); // Compute the right task
                float leftResult = leftTask.join(); // Wait for left task result

                // Return the maximum of the two results
                return Math.max(leftResult, rightResult);
            }
        }
    }
}
