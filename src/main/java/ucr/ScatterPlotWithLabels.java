package ucr;

import hyperdimension.encoders.VanillaBHV;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static ucr.FileProcessor.pullResults;


public class ScatterPlotWithLabels extends Application {

    private static final double ZOOM_FACTOR = 0.1;  // Adjust this factor to control zoom speed
    private static double mouseXStart;
    private static double mouseYStart;

    @Override
    public void start(Stage stage) {

        List<OptimalParameters> data = pullResults( );

        final ScatterChart<Number, Number> scatterChart = createScatterChart(data);

        //create a vbox with 3 stacked bar charts
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));

        vbox.getChildren().addAll(
            buildBarChart(createDataTypeData(data), "Mean Accuracy & Outperformance by Data Type", "Data Type", scatterChart, 2),
            buildBarChart(createDataTypeLengths(data), "Mean Accuracy & Outperformance by Data Set Length", "Data Set Length", scatterChart, 0),
            buildBarChart(createDataNumberTrain(data), "Mean Accuracy & Outperformance by Number of Training Instances", "Number of Training Instances", scatterChart, 3),
            buildBarChart(createDataTypeClasses(data), "Mean Accuracy & Outperformance by Number of Classes", "Number of Classes", scatterChart, 1)
        );

        vbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));




        HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new javafx.geometry.Insets(10, 10, 10, 10));
        hbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        hbox.getChildren().addAll(scatterChart, vbox);

        VBox.setVgrow(scatterChart, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(vbox, javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(vbox.getChildren().get(0), javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(vbox.getChildren().get(1), javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(vbox.getChildren().get(2), javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(vbox.getChildren().get(3), javafx.scene.layout.Priority.ALWAYS);

        HBox.setHgrow(scatterChart, javafx.scene.layout.Priority.ALWAYS);
        HBox.setHgrow(vbox, javafx.scene.layout.Priority.ALWAYS);



        // Creating a scene object
        Scene scene = new Scene(hbox, 2000, 1000);

        scene.getStylesheets().add(getClass().getResource("/css/hvcompare.css").toExternalForm());


        // Setting title to the Stage
        stage.setTitle("Comparing HVTM to DTW");

        // Adding scene to the stage
        stage.setScene(scene);

        // Displaying the contents of the stage
        stage.show();
    }


    public static ScatterChart<Number, Number> createScatterChart(List<OptimalParameters> data) {



        NumberAxis xAxis = new NumberAxis(0, 1.1, 0.1);
        xAxis.setLabel("DTW (Benchmark) Accuracy Axis");

        NumberAxis yAxis = new NumberAxis(0, 1.1, 0.1);
        yAxis.setLabel("HVTM Accuracy Axis");

        // Store the original bounds of the axes
        double originalLowerX = xAxis.getLowerBound();
        double originalUpperX = xAxis.getUpperBound();
        double originalLowerY = yAxis.getLowerBound();
        double originalUpperY = yAxis.getUpperBound();

        // Creating the scatter chart
        ScatterChart<Number, Number> scatterChart = new ScatterChart<>(xAxis, yAxis);
        scatterChart.setTitle("HVTM vs. DTW");
        scatterChart.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        XYChart.Series<Number, Number> hvtm = new XYChart.Series<>();
        XYChart.Series<Number, Number> dtw = new XYChart.Series<>();
        hvtm.setName("HVTM");
        dtw.setName("DTW (Benchmark)");

        for(OptimalParameters entry : data) {

            String name = entry.getDataSetName();

            double x = 1.0 - entry.getTargetErrorRate();
            double y = 1.0 - entry.getErrorRate();

            if(y >= x - 0.02) {

                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, y, entry);
                StackPane stackPane = new StackPane();
                Circle circle = new Circle(5, Color.rgb(66, 151, 160));
                Text label = new Text(name);
                label.setFill(Color.rgb(66, 151, 160));
                stackPane.getChildren().addAll(circle, label);

                dataPoint.setNode(stackPane);

                hvtm.getData().add(dataPoint);
            }
            else {

                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(x, y, entry);
                StackPane stackPane = new StackPane();
                Circle circle = new Circle(5, Color.rgb(229, 127, 132));
                Text label = new Text(name);
                label.setFill(Color.rgb(229, 127, 132));
                stackPane.getChildren().addAll(circle, label);

                dataPoint.setNode(stackPane);

                dtw.getData().add(dataPoint);
            }
        }

        //add a dotted line for the y=x line
        XYChart.Series<Number, Number> yEqualsX = new XYChart.Series<>();
        //set 50 points for the line x=y from 0 to 1
        for (int i = 0; i <= 50; i++) {
            XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(i / 50.0, i / 50.0);
            yEqualsX.getData().add(dataPoint);
        }





        scatterChart.getData().addAll(hvtm, dtw);//, yEqualsX);

        //add ToolTip to show more information from OptimalParameters object
        for (XYChart.Series<Number, Number> series : scatterChart.getData()) {
            for (XYChart.Data<Number, Number> d : series.getData()) {
                OptimalParameters entry = (OptimalParameters) d.getExtraValue();
                if (entry != null) {
                    Tooltip tooltip = new Tooltip(entry.toToolTip());
                    Tooltip.install(d.getNode(), tooltip);
                }
                //set the background color of the data point to transparent
                d.getNode().setStyle("-fx-background-color: transparent;");
            }
        }
        scatterChart.getData().add(yEqualsX);



        scatterChart.setOnScroll((ScrollEvent event) -> {
            double deltaY = event.getDeltaY();
            double mouseX = event.getX();
            double mouseY = event.getY();

            double xAxisRange = xAxis.getUpperBound() - xAxis.getLowerBound();
            double yAxisRange = yAxis.getUpperBound() - yAxis.getLowerBound();

            double xZoomFactor = xAxisRange * ZOOM_FACTOR * (deltaY > 0 ? -1 : 1);
            double yZoomFactor = yAxisRange * ZOOM_FACTOR * (deltaY > 0 ? -1 : 1);

            double newLowerX = xAxis.getLowerBound() + ((mouseX / scatterChart.getWidth()) * xZoomFactor);
            double newUpperX = xAxis.getUpperBound() - ((1 - (mouseX / scatterChart.getWidth())) * xZoomFactor);

            double newLowerY = yAxis.getLowerBound() + ((1 - (mouseY / scatterChart.getHeight())) * yZoomFactor);
            double newUpperY = yAxis.getUpperBound() - ((mouseY / scatterChart.getHeight()) * yZoomFactor);

            xAxis.setLowerBound(newLowerX);
            xAxis.setUpperBound(newUpperX);

            yAxis.setLowerBound(newLowerY);
            yAxis.setUpperBound(newUpperY);
        });

        // Implementing panning functionality
        scatterChart.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                mouseXStart = event.getX();
                mouseYStart = event.getY();
            }
        });

        scatterChart.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                double deltaX = mouseXStart - event.getX();
                double deltaY = mouseYStart - event.getY();

                double xAxisRange = xAxis.getUpperBound() - xAxis.getLowerBound();
                double yAxisRange = yAxis.getUpperBound() - yAxis.getLowerBound();

                double deltaXPercentage = deltaX / scatterChart.getWidth();
                double deltaYPercentage = deltaY / scatterChart.getHeight();

                double xShift = deltaXPercentage * xAxisRange;
                double yShift = deltaYPercentage * yAxisRange;

                xAxis.setLowerBound(xAxis.getLowerBound() + xShift);
                xAxis.setUpperBound(xAxis.getUpperBound() + xShift);

                yAxis.setLowerBound(yAxis.getLowerBound() - yShift);
                yAxis.setUpperBound(yAxis.getUpperBound() - yShift);

                mouseXStart = event.getX();
                mouseYStart = event.getY();
            }
        });

        // Implementing reset zoom functionality on right-click
        scatterChart.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                // Reset the axis bounds to the original values
                xAxis.setLowerBound(originalLowerX);
                xAxis.setUpperBound(originalUpperX);
                yAxis.setLowerBound(originalLowerY);
                yAxis.setUpperBound(originalUpperY);
            }
        });

        //scatterChart.getData().get(2).getNode().setStyle("-fx-stroke: gray; -fx-stroke-width: 2px; -fx-stroke-dash-array: 10 5;");
        return scatterChart;
    }



    public static BarChart<String, Number> buildBarChart(Pair<Map<String, double[]>,
            Map<String, List<OptimalParameters>>> mapMapPair, String title, String xAxisLabel, ScatterChart<Number, Number> scatterChart, int whichPlot) {


        Map<String, double[]> values = mapMapPair.getLeft();
        Map<String, List<OptimalParameters>> optimalParameters = mapMapPair.getRight();

        // Create a DropShadow effect Color.rgb(66, 151, 160)
        DropShadow dropShadow0 = new DropShadow();
        dropShadow0.setRadius(15.0);
        dropShadow0.setOffsetX(0);
        dropShadow0.setOffsetY(0);
        dropShadow0.setColor(Color.rgb(66, 151, 160));

        DropShadow dropShadow1 = new DropShadow();
        dropShadow1.setRadius(10.0);
        dropShadow1.setOffsetX(0);
        dropShadow1.setOffsetY(0);
        dropShadow1.setColor(Color.rgb(229, 127, 132));

        DropShadow dropShadow2 = new DropShadow();
        dropShadow2.setRadius(10.0);
        dropShadow2.setOffsetX(0);
        dropShadow2.setOffsetY(0);
        dropShadow2.setColor(Color.rgb(47, 80, 97));





        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);


        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Mean Accuracy & Outperformance");

        // Creating the StackedBarChart
        BarChart<String, Number> stackedBarChart = new BarChart<>(xAxis, yAxis);
        stackedBarChart.setTitle(title);
        stackedBarChart.setCategoryGap(25);
        stackedBarChart.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName("DTW (Benchmark)" );

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        series2.setName("HVTM");

        //also create a third Series number of times the HVTM value is greater than the DTW value
        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        series3.setName("HVTM > DTW");


        List<String> sortedCategories = new ArrayList<>();



        // Populating the series with data
        for (Map.Entry<String, double[]> entry : values.entrySet()) {
            String category = entry.getKey();
            double[] means = entry.getValue();

            series1.getData().add(new XYChart.Data<>(category, means[1]));
            series2.getData().add(new XYChart.Data<>(category, means[0]));

            List<OptimalParameters> entries = optimalParameters.get(category);
            //count the number of times the HVTM value is greater than the DTW value
            int count = 0;
            for(OptimalParameters e : entries) {
                if (e.getErrorRate() - .03 <= e.getTargetErrorRate()) {
                    count++;
                }
            }
            //now add the percentage
            series3.getData().add(new XYChart.Data<>(category, (double) count / entries.size()));

        }

        sortedCategories = new ArrayList<>(values.keySet());
        //if whichPlot is 0 or 1, sort the categories by their numeric value
        if(whichPlot != 2) {
            sortedCategories.sort(new NumericStringComparator());
        }
        xAxis.setCategories(FXCollections.observableArrayList(sortedCategories));

        // Adding the series to the chart
        stackedBarChart.getData().addAll(series1, series2, series3);

        //apply the drop shadow effect to the bars in the categories such that the series2 value is bigger than series1 value
        for (int i = 0; i < series1.getData().size(); i++) {
            XYChart.Data<String, Number> data1 = series1.getData().get(i);
            XYChart.Data<String, Number> data2 = series2.getData().get(i);
            XYChart.Data<String, Number> data3 = series3.getData().get(i);


            if (data3.getYValue().doubleValue() > .6) {
                data2.getNode().setEffect(dropShadow0);
                data1.getNode().setEffect(dropShadow1);
                data3.getNode().setEffect(dropShadow2);
            }
            //otherwise, add transparency to the bars
            else {
                data2.getNode().setStyle("-fx-bar-fill: rgba(66, 151, 160,.3);");
                data1.getNode().setStyle("-fx-bar-fill: rgba(229, 127, 132, .3);");
                data3.getNode().setStyle("-fx-bar-fill: rgba(47, 80, 97, .3);");
            }
        }

        //when hovering over a bar, get the category corresponding to the bar and display the optimal parameters for that category
        for (XYChart.Series<String, Number> series : stackedBarChart.getData()) {
            for (XYChart.Data<String, Number> d : series.getData()) {
                d.getNode().setOnMouseEntered(event -> {
                    String category = d.getXValue();
                    makeInvisibleFilteredPoints(scatterChart, category, whichPlot);
                });
                d.getNode().setOnMouseExited(event -> {
                    makeInvisibleFilteredPoints(scatterChart, null, whichPlot);
                });
            }
        }



        return stackedBarChart;

    }

    public static void makeInvisibleFilteredPoints(ScatterChart<Number, Number> scatterChart, String category, int whichPlot) {
        for (XYChart.Series<Number, Number> series : scatterChart.getData()) {
            for (XYChart.Data<Number, Number> d : series.getData()) {
                OptimalParameters entry = (OptimalParameters) d.getExtraValue();
                if (entry != null) {

                    if(category == null) {
                        d.getNode().setVisible(true);
                    }
                    else {

                        if(whichPlot == 0) {
                            if (!entry.getLengthCategory().equals(category)) {
                                d.getNode().setVisible(false);
                            }
                        }
                        else if(whichPlot == 1) {
                            if (!entry.getClassCategory().equals(category)) {
                                d.getNode().setVisible(false);
                            }
                        }
                        else if(whichPlot == 3) {
                            if (!entry.getNumberTrainCategory().equals(category)) {
                                d.getNode().setVisible(false);
                            }
                        }
                        else {
                            if (!entry.getDataSetType().equals(category)) {
                                d.getNode().setVisible(false);
                            }
                        }
                    }
                }
            }
        }
    }

    //split data int 10 bins
    public static int[] splitData(int[] dataset, int numBins) {

        //first sort the dataset from smallest to largest using an Array.sort
        int[] sortedData = dataset.clone();
        java.util.Arrays.sort(sortedData);

        int[] bins = new int[numBins];
        int binSize = dataset.length / numBins;

        for(int i = 0; i < numBins; i++) {
            bins[i] = sortedData[(i+1) * binSize];
        }

        return bins;

    }

    public Pair<Map<String, double[]>,
                    Map<String, List<OptimalParameters>>> createDataTypeData(List<OptimalParameters> data) {



        //get distinct data types from the data (data.get(0).getDataType())
        List<String> dataTypes = new ArrayList<>();
        for(OptimalParameters entry : data) {
            if(!dataTypes.contains(entry.getDataSetType())) {
                dataTypes.add(entry.getDataSetType());
            }
        }

        Map<String, DescriptiveStatistics> hvtmstats = new HashMap<>();
        Map<String, DescriptiveStatistics> dtwstats = new HashMap<>();
        Map<String, List<OptimalParameters>> optimalParametersData = new HashMap<>();


        for(OptimalParameters entry : data) {

            if(!hvtmstats.containsKey(entry.getDataSetType())) {
                hvtmstats.put(entry.getDataSetType(), new DescriptiveStatistics());
                optimalParametersData.put(entry.getDataSetType(), new ArrayList<>());
            }

            if(!dtwstats.containsKey(entry.getDataSetType())) {
                dtwstats.put(entry.getDataSetType(), new DescriptiveStatistics());
                optimalParametersData.put(entry.getDataSetType(), new ArrayList<>());
            }

            hvtmstats.get(entry.getDataSetType()).addValue(1.0 - entry.getErrorRate());
            dtwstats.get(entry.getDataSetType()).addValue(1.0 - entry.getTargetErrorRate());

            optimalParametersData.get(entry.getDataSetType()).add(entry);
        }

        //combine the data into a map, taking the mean from HVTM and DTW, respectively
        Map<String, double[]> dataTypeData = new HashMap<>();


        for(String dataType : dataTypes) {
            double[] means = new double[2];
            means[0] = hvtmstats.get(dataType).getMean();
            means[1] = dtwstats.get(dataType).getMean();
            dataTypeData.put(dataType, means);
        }

        return Pair.of(dataTypeData, optimalParametersData);
    }

    public Pair<Map<String, double[]>,
            Map<String, List<OptimalParameters>>> createDataTypeLengths(List<OptimalParameters> data) {

        int[] lengthCount = new int[data.size()];
        for(int i = 0; i < data.size(); i++) {
            lengthCount[i] = data.get(i).dataSetLength;
        }
        int[] lengthBins = splitData(lengthCount, 10);

        String[] lengthCategories = new String[10];
        for(int i = 0; i < 10; i++) {
            lengthCategories[i] = String.valueOf(lengthBins[i]);
        }

        Map<String, DescriptiveStatistics> hvtmstats = new HashMap<>();
        Map<String, DescriptiveStatistics> dtwstats = new HashMap<>();
        Map<String, List<OptimalParameters>> optimalParametersData = new HashMap<>();

        for(OptimalParameters entry : data) {

            //using the lengthBins, determine which bin the data set length falls into
            int bin = 0;
            for(int i = 0; i < 10; i++) {
                if(entry.dataSetLength >= lengthBins[i]) {
                    bin = i;
                }
            }

            if(!hvtmstats.containsKey(lengthCategories[bin])) {
                hvtmstats.put(lengthCategories[bin], new DescriptiveStatistics());
                optimalParametersData.put(lengthCategories[bin], new ArrayList<>());
            }

            if(!dtwstats.containsKey(lengthCategories[bin])) {
                dtwstats.put(lengthCategories[bin], new DescriptiveStatistics());
                optimalParametersData.put(lengthCategories[bin], new ArrayList<>());
            }

            hvtmstats.get(lengthCategories[bin]).addValue(1.0 - entry.getErrorRate());
            dtwstats.get(lengthCategories[bin]).addValue(1.0 - entry.getTargetErrorRate());

            optimalParametersData.get(lengthCategories[bin]).add(entry);

            //set the length category for the entry
            entry.setLengthCategory(lengthCategories[bin]);

        }

        Map<String, double[]> dataTypeData = new HashMap<>();

        for(String dataType : lengthCategories) {
            double[] means = new double[2];
            means[0] = hvtmstats.get(dataType).getMean();
            means[1] = dtwstats.get(dataType).getMean();
            dataTypeData.put(dataType, means);
        }

        return Pair.of(dataTypeData, optimalParametersData);

    }

    public Pair<Map<String, double[]>,
            Map<String, List<OptimalParameters>>> createDataTypeClasses(List<OptimalParameters> data) {

        int[] classCount = new int[data.size()];
        for(int i = 0; i < data.size(); i++) {
            classCount[i] = data.get(i).getNumberClasses();
        }

        int[] classBins = splitData(classCount, 5);
        String[] classCategories = new String[5];

        for(int i = 0; i < 5; i++) {
            classCategories[i] = String.valueOf(classBins[i]);
        }

        Map<String, DescriptiveStatistics> hvtmstats = new HashMap<>();
        Map<String, DescriptiveStatistics> dtwstats = new HashMap<>();
        Map<String, List<OptimalParameters>> optimalParametersData = new HashMap<>();

        for(OptimalParameters entry : data) {


            int bin = 0;
            for(int i = 0; i < 5; i++) {
                if(entry.getNumberClasses() >= classBins[i]) {
                    bin = i;
                }
            }

            if(!hvtmstats.containsKey(classCategories[bin])) {
                hvtmstats.put(classCategories[bin], new DescriptiveStatistics());
                optimalParametersData.put(classCategories[bin], new ArrayList<>());
            }

            if(!dtwstats.containsKey(classCategories[bin])) {
                dtwstats.put(classCategories[bin], new DescriptiveStatistics());
                optimalParametersData.put(classCategories[bin], new ArrayList<>());
            }

            hvtmstats.get(classCategories[bin]).addValue(1.0 - entry.getErrorRate());
            dtwstats.get(classCategories[bin]).addValue(1.0 - entry.getTargetErrorRate());

            optimalParametersData.get(classCategories[bin]).add(entry);


            //set the class category for the entry
            entry.setClassCategory(classCategories[bin]);

        }

        Map<String, double[]> dataTypeData = new HashMap<>();

        for(String dataType : classCategories) {
            double[] means = new double[2];
            means[0] = hvtmstats.get(dataType).getMean();
            means[1] = dtwstats.get(dataType).getMean();
            dataTypeData.put(dataType, means);
        }


        return Pair.of(dataTypeData, optimalParametersData);
    }

    public Pair<Map<String, double[]>,
            Map<String, List<OptimalParameters>>> createDataNumberTrain(List<OptimalParameters> data) {

        int[] trainCount = new int[data.size()];
        for(int i = 0; i < data.size(); i++) {
            trainCount[i] = data.get(i).getNumberTrain();
        }

        int[] trainBins = splitData(trainCount, 8);
        String[] trainCategories = new String[8];

        for(int i = 0; i < 8; i++) {
            trainCategories[i] = String.valueOf(trainBins[i]);
        }

        Map<String, DescriptiveStatistics> hvtmstats = new HashMap<>();
        Map<String, DescriptiveStatistics> dtwstats = new HashMap<>();

        Map<String, List<OptimalParameters>> optimalParametersData = new HashMap<>();

        for(OptimalParameters entry : data) {

            int bin = 0;
            for(int i = 0; i < 8; i++) {
                if(entry.getNumberTrain() >= trainBins[i]) {
                    bin = i;
                }
            }

            if(!hvtmstats.containsKey(trainCategories[bin])) {
                hvtmstats.put(trainCategories[bin], new DescriptiveStatistics());
                optimalParametersData.put(trainCategories[bin], new ArrayList<>());
            }

            if(!dtwstats.containsKey(trainCategories[bin])) {
                dtwstats.put(trainCategories[bin], new DescriptiveStatistics());
                optimalParametersData.put(trainCategories[bin], new ArrayList<>());
            }

            hvtmstats.get(trainCategories[bin]).addValue(1.0 - entry.getErrorRate());
            dtwstats.get(trainCategories[bin]).addValue(1.0 - entry.getTargetErrorRate());

            optimalParametersData.get(trainCategories[bin]).add(entry);


            //set the class category for the entry
            entry.setNumberTrainCategory(trainCategories[bin]);

        }

        Map<String, double[]> dataTypeData = new HashMap<>();

        for(String dataType : trainCategories) {
            double[] means = new double[2];
            means[0] = hvtmstats.get(dataType).getMean();
            means[1] = dtwstats.get(dataType).getMean();
            dataTypeData.put(dataType, means);
        }

        return Pair.of(dataTypeData, optimalParametersData);
    }


    //build a custom Comparator to sort strings by their numeric value
    //we will use the String value, transfer to double, and compare the double values
    public static class NumericStringComparator implements java.util.Comparator<String> {
        @Override
        public int compare(String s1, String s2) {
            double d1 = Double.parseDouble(s1);
            double d2 = Double.parseDouble(s2);
            return Double.compare(d1, d2);
        }
    }






    public static void main(String[] args) {
        launch(args);
    }
}
