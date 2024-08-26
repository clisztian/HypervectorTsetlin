package protein;

import hyperdimension.encoders.VanillaBHV;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Random;

public class ProteinMetaDataAnalysis extends Application {

    private ProteinMetaDataTable proteinMetaDataTable;
    private ProteinMetaDataStatistics proteinMetaDataStatistics;

    private ProteinEncoderDecoder proteinEncoderDecoder;
    private VBox vbox;

    private ProteinMappingPanel proteinMappingPanel;

    public static void main(String[] args) {
        launch(args);
    }


    public void initProteinMetaDataAnalysis() {

        proteinMappingPanel = new ProteinMappingPanel();

        StackPane backpane = new StackPane();

        proteinMetaDataTable = new ProteinMetaDataTable(backpane);

        vbox = new VBox();
        vbox.getChildren().addAll(proteinMappingPanel.getVbox(), backpane, proteinMetaDataTable.getProteinView());
        vbox.setPadding(new Insets(10, 10, 10, 10));
        VBox.setVgrow(proteinMetaDataTable.getProteinView(), javafx.scene.layout.Priority.ALWAYS);
        vbox.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        VBox.setVgrow(proteinMappingPanel.getVbox(), Priority.ALWAYS);
        HBox.setHgrow(proteinMappingPanel.getVbox(), Priority.ALWAYS);

    }

    public void setData() {

        Random random = new Random(System.currentTimeMillis());

        List<ProteinMetaData> data = ProteinDataReader.getData();
        //filter out all except ProteinMetaData with a classification "HYDROLASE" "OXIDOREDUCTASE" "TRANSFERASE":
        data.removeIf(proteinMetaData -> !proteinMetaData.getClassification().equals("HYDROLASE") &&
                !proteinMetaData.getClassification().equals("OXIDOREDUCTASE") &&
                !proteinMetaData.getClassification().equals("TRANSFERASE"));

        //remove any data points with a zero values for any of the numeric fields
        data.removeIf(proteinMetaData ->
                proteinMetaData.getDensityPercentSol() == 0 ||
                proteinMetaData.getDensityMatthews() == 0 ||
                proteinMetaData.getCrystallizationTempK() == null ||
                proteinMetaData.getPhValue() == null ||
                proteinMetaData.getResolution() == 0);


        //randomly remove 50% of the data points
        //remove if protein sequence is null
        data.removeIf(proteinMetaData -> proteinMetaData.getProteinSequences() == null);
        data.removeIf(proteinMetaData -> proteinMetaData.getProteinSequences().get(0).getSequence().isEmpty());
        data.removeIf(proteinMetaData -> random.nextDouble() < 0.85);


        //print number
        System.out.println("Number of data points: " + data.size());

        proteinMetaDataTable.setData(data);

        proteinMetaDataStatistics = new ProteinMetaDataStatistics(data);

        proteinEncoderDecoder = new ProteinEncoderDecoder(proteinMetaDataStatistics);
        proteinEncoderDecoder.initialize();

        //for each data point, encode the protein sequence and add it to the list of encoded sequences
        for(ProteinMetaData proteinMetaData : data) {
            VanillaBHV encodedProtein = proteinEncoderDecoder.encode(proteinMetaData);
            proteinMetaData.setEncodedProteinMetaData(encodedProtein);
        }

        proteinMappingPanel.setProteinMetaEncoder(proteinEncoderDecoder);
        proteinMappingPanel.setMetaDataList(data);



    }
    public VBox getVbox() {
        return vbox;
    }

    @Override
    public void start(Stage stage) throws Exception {

        initProteinMetaDataAnalysis();
        setData();

        Scene scene = new Scene(getVbox(), 1800, 600);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/Chart.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
