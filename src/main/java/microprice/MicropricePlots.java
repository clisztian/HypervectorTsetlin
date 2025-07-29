package microprice;

import io.fair_acc.chartfx.XYChart;
import io.fair_acc.chartfx.axes.spi.DefaultNumericAxis;
import io.fair_acc.chartfx.plugins.EditAxis;
import io.fair_acc.chartfx.plugins.ParameterMeasurements;
import io.fair_acc.chartfx.plugins.Zoomer;
import io.fair_acc.chartfx.renderer.ContourType;
import io.fair_acc.chartfx.renderer.LineStyle;
import io.fair_acc.chartfx.renderer.spi.ContourDataSetRenderer;
import io.fair_acc.chartfx.renderer.spi.ErrorDataSetRenderer;
import io.fair_acc.chartfx.renderer.spi.MetaDataRenderer;
import io.fair_acc.chartfx.renderer.spi.utils.ColorGradient;
import io.fair_acc.chartfx.ui.geometry.Side;
import io.fair_acc.dataset.spi.Histogram2;
import io.fair_acc.dataset.utils.DataSetStyleBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static io.fair_acc.dataset.spi.AbstractHistogram.HistogramOuterBounds.BINS_ALIGNED_WITH_BOUNDARY;
import static microprice.MicropriceReader.readMicroprices;

public class MicropricePlots extends Application {


    private  List<Microprice> microprices;

    private double priceChangeMin = -15.0;
    private double priceChangeMax = 15.0;

    private double spreadSizeMin = 0.0;
    private double spreadSizeMax = 10.0;



    private static final int UPDATE_DELAY = 1000; // [ms]
    private static final int UPDATE_PERIOD = 100; // [ms]
    private static final int UPDATE_N_SAMPLES = 10;
    private static final int N_BINS_X = 120;
    private static final int N_BINS_Y = 120;
    private final Random rnd = new Random();

    private  Histogram2 histogram1;
    //private final Histogram2 histogram2;
    private int counter = 0;




    private void fillData() {


        for(int i = 0; i < UPDATE_N_SAMPLES; i++) {

            Microprice microprice = microprices.get(counter);

            double spread = microprice.getImbalance();
            double delta = microprice.getLastPrice() - microprice.getMicroPrice();

            //check to see if the spread is in bounds and the microprice.getLastPrice() - microprice.getMicroPrice() is in bounds

            if(spread <= spreadSizeMin || spread >= spreadSizeMax) {
                counter++;
                continue;
            }

            if(delta  <= priceChangeMin || delta  >= priceChangeMax) {
                counter++;
                continue;
            }

            if(spread == 0.0) {
                counter++;
                continue;
            }

            histogram1.fill(spread, delta);
            counter++;


        }



        if(counter == microprices.size()) {
            counter = 0;
            histogram1.reset();
        }
    }


    public void loadData(List<Microprice> microprices) {
        this.microprices = microprices;


        DescriptiveStatistics spread = new DescriptiveStatistics();
        DescriptiveStatistics priceChange = new DescriptiveStatistics();
        DescriptiveStatistics micropriceDelta = new DescriptiveStatistics();
        DescriptiveStatistics micropriceAdjustments = new DescriptiveStatistics();

        for (Microprice microprice : microprices) {

            spread.addValue(microprice.getSpreadSize());
            priceChange.addValue(microprice.getPriceChange());
            micropriceDelta.addValue(microprice.getLastPrice() - microprice.getMicroPrice());
            micropriceAdjustments.addValue(microprice.getMicroPrice() - microprice.getAdjustedMicroprice());

        }



        priceChangeMin = -.2;
        priceChangeMax =  .2;

        spreadSizeMin = -.8;
        spreadSizeMax = .8;

        histogram1 = new Histogram2("Imbalance x Microprice Delta", N_BINS_X, spreadSizeMin, spreadSizeMax, N_BINS_Y, priceChangeMin, priceChangeMax, BINS_ALIGNED_WITH_BOUNDARY);
        //histogram2 = new Histogram2("hist2", N_BINS_X, 0.0, 20.0, N_BINS_Y, 0.0, 30.0, BINS_ALIGNED_WITH_BOUNDARY);


    }

    public StackPane getChartPanel() {

        //create a RadialGradient for the background of the orderBox
        RadialGradient radialGradient = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(25, 32, 36)),
                new Stop(1, Color.rgb(25, 32, 36).darker().darker()));


        final StackPane root = new StackPane();

        final DefaultNumericAxis xAxis = new DefaultNumericAxis("Imbalance Size");
        xAxis.setAutoRanging(true);
        xAxis.setAutoRangeRounding(false);
        xAxis.setSide(Side.BOTTOM);


        final DefaultNumericAxis yAxis = new DefaultNumericAxis("Microprice Delta");
        yAxis.setAutoRanging(true);
        yAxis.setAutoRangeRounding(false);
        yAxis.setSide(Side.LEFT);

        final DefaultNumericAxis zAxis = new DefaultNumericAxis("Count");
        zAxis.setAnimated(false);
        zAxis.setAutoRangeRounding(false);
        zAxis.setAutoRanging(true);
        zAxis.setSide(Side.RIGHT);


        final DefaultNumericAxis yAxis1 = new DefaultNumericAxis("y-Axis x-Projection");
        yAxis1.setLogAxis(false);
        yAxis1.setAnimated(false);
        yAxis1.setAutoRangeRounding(true);
        yAxis1.setAutoRangePadding(2.0);
        yAxis1.setAutoRanging(true);
        yAxis1.setSide(Side.RIGHT);

        final DefaultNumericAxis xAxis1 = new DefaultNumericAxis("x-Axis y-Projection");
        xAxis1.setLogAxis(false);
        xAxis1.setAnimated(false);
        xAxis1.setAutoRangeRounding(true);
        xAxis1.setAutoRangePadding(2.0);
        xAxis1.setAutoRanging(true);
        xAxis1.setSide(Side.TOP);

        final XYChart chart = new XYChart(xAxis, yAxis);
        chart.setAnimated(false);
        chart.getGridRenderer().getHorizontalMajorGrid().setVisible(false);
        chart.getGridRenderer().getVerticalMajorGrid().setVisible(false);


        Stop[] stops = new Stop[5];
        stops[0] = new Stop(0, Color.TRANSPARENT);
        stops[1] = new Stop(0.05, Color.CORNFLOWERBLUE);
        stops[2] = new Stop(0.25, Color.CORNFLOWERBLUE.darker());
        stops[3] = new Stop(0.75, Color.DARKMAGENTA);
        stops[4] = new Stop(1, Color.ORANGERED);

        ColorGradient sunsetGradient = new ColorGradient("Sunset", stops);

        final ContourDataSetRenderer heatMap = new ContourDataSetRenderer();

        heatMap.setContourType(ContourType.HEATMAP);
        heatMap.setColorGradient(ColorGradient.DEFAULT);


        heatMap.getAxes().addAll(xAxis, yAxis, zAxis);
        heatMap.getDatasets().addAll(histogram1);//, histogram2);
        chart.getRenderers().set(0, heatMap);

        final ErrorDataSetRenderer projectionRendererX = new ErrorDataSetRenderer();
        projectionRendererX.getAxes().setAll(xAxis, yAxis1);
        projectionRendererX.getDatasets().setAll(histogram1.getProjectionX());//, histogram2.getProjectionX());
        //histogram1.getProjectionX().setStyle("dsIndex=0");
        String MEAS_STROKE_COLOUR2 = DataSetStyleBuilder.instance().setStroke("cyan").setFill("cyan").build();
        histogram1.getProjectionX().setStyle(MEAS_STROKE_COLOUR2);
        projectionRendererX.setPolyLineStyle(LineStyle.HISTOGRAM_FILLED);
        projectionRendererX.setPointReduction(false);
        chart.getRenderers().add(projectionRendererX);

        final ErrorDataSetRenderer projectionRendererY = new ErrorDataSetRenderer();
        projectionRendererY.getAxes().setAll(xAxis1, yAxis);
        projectionRendererY.getDatasets().setAll(histogram1.getProjectionY());//, histogram2.getProjectionY());
        String MEAS_STROKE_COLOUR = DataSetStyleBuilder.instance().setStroke("lightGray").setFill("lightGray").build();
        histogram1.getProjectionY().setStyle(MEAS_STROKE_COLOUR);
        projectionRendererY.getDatasets().get(0).setStyle(MEAS_STROKE_COLOUR);
        //histogram2.getProjectionY().setStyle("dsIndex=1");
        projectionRendererY.setPolyLineStyle(LineStyle.HISTOGRAM_FILLED);
        projectionRendererY.setPointReduction(false);
        projectionRendererY.setAssumeSortedData(false);
        chart.getRenderers().add(projectionRendererY);

        final MetaDataRenderer metaRenderer = new MetaDataRenderer(chart);
        metaRenderer.getDatasets().addAll(histogram1);
        chart.getRenderers().add(metaRenderer);
        chart.legendVisibleProperty().set(true);

        chart.getPlugins().add(new ParameterMeasurements());
        chart.getPlugins().add(new EditAxis());
        final Zoomer zoomer = new Zoomer();
        zoomer.setSliderVisible(false);
        chart.getPlugins().add(zoomer);
        chart.setBackground(new Background(new BackgroundFill(radialGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        root.getChildren().add(chart);

        root.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());

        final Timer timer = new Timer("sample-update-timer", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fillData();
                chart.invalidate();
            }
        }, UPDATE_DELAY, UPDATE_PERIOD);
        return root;
    }

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {

        String fileName = "microprice_tsla.csv";

        List<Microprice> micropriceData = readMicroprices(fileName);
        loadData(micropriceData);


        StackPane pane = getChartPanel();
        Scene scene = new Scene(pane, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
    


}
