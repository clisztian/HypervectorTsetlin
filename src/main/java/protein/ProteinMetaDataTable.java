package protein;

import em.libs.jfxtableview.JFXTableView;
import em.libs.jfxtableview.columns.JFXDoubleTableColumn;
import em.libs.jfxtableview.columns.JFXIntegerTableColumn;
import em.libs.jfxtableview.columns.JFXStringTableColumn;
import em.libs.jfxtableview.columns.JFXTableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class ProteinMetaDataTable {
    
    private JFXTableView<ProteinMetaData> proteinView;



    private ObservableList<ProteinMetaData> proteinMetaData = FXCollections.observableArrayList();
    
    
    public ProteinMetaDataTable(StackPane backpane) {
        
        proteinView = new JFXTableView<>(backpane);
        proteinView.setData(proteinMetaData);

        // Define columns
        JFXTableColumn<ProteinMetaData, String> structureIdCol = new JFXStringTableColumn<>("Structure ID");
        structureIdCol.setCellValueFactory(new PropertyValueFactory<>("structureId"));

        JFXTableColumn<ProteinMetaData, String> classificationCol = new JFXStringTableColumn<>("Classification");
        classificationCol.setCellValueFactory(new PropertyValueFactory<>("classification"));

        JFXTableColumn<ProteinMetaData, String> experimentalTechniqueCol = new JFXStringTableColumn<>("Experimental Technique");
        experimentalTechniqueCol.setCellValueFactory(new PropertyValueFactory<>("experimentalTechnique"));

        JFXTableColumn<ProteinMetaData, String> macromoleculeTypeCol = new JFXStringTableColumn<>("Macromolecule Type");
        macromoleculeTypeCol.setCellValueFactory(new PropertyValueFactory<>("macromoleculeType"));

        JFXTableColumn<ProteinMetaData, Integer> residueCountCol = new JFXIntegerTableColumn<>("Residue Count");
        residueCountCol.setCellValueFactory(new PropertyValueFactory<>("residueCount"));

        JFXTableColumn<ProteinMetaData, Double> resolutionCol = new JFXDoubleTableColumn<>("Resolution");
        resolutionCol.setCellValueFactory(new PropertyValueFactory<>("resolution"));

        JFXTableColumn<ProteinMetaData, Double> structureMolecularWeightCol = new JFXDoubleTableColumn<>("Structure Molecular Weight");
        structureMolecularWeightCol.setCellValueFactory(new PropertyValueFactory<>("structureMolecularWeight"));

        JFXTableColumn<ProteinMetaData, String> crystallizationMethodCol = new JFXStringTableColumn<>("Crystallization Method");
        crystallizationMethodCol.setCellValueFactory(new PropertyValueFactory<>("crystallizationMethod"));

        JFXTableColumn<ProteinMetaData, Double> crystallizationTempKCol = new JFXDoubleTableColumn<>("Crystallization Temp K");
        crystallizationTempKCol.setCellValueFactory(new PropertyValueFactory<>("crystallizationTempK"));

        JFXTableColumn<ProteinMetaData, Double> densityMatthewsCol = new JFXDoubleTableColumn<>("Density Matthews");
        densityMatthewsCol.setCellValueFactory(new PropertyValueFactory<>("densityMatthews"));

        JFXTableColumn<ProteinMetaData, Double> densityPercentSolCol = new JFXDoubleTableColumn<>("Density Percent Sol");
        densityPercentSolCol.setCellValueFactory(new PropertyValueFactory<>("densityPercentSol"));

        JFXTableColumn<ProteinMetaData, String> pdbxDetailsCol = new JFXStringTableColumn<>("PDBx Details");
        pdbxDetailsCol.setCellValueFactory(new PropertyValueFactory<>("pdbxDetails"));

        JFXTableColumn<ProteinMetaData, Double> phValueCol = new JFXDoubleTableColumn<>("pH Value");
        phValueCol.setCellValueFactory(new PropertyValueFactory<>("phValue"));

        JFXTableColumn<ProteinMetaData, Integer> publicationYearCol = new JFXIntegerTableColumn<>("Publication Year");
        publicationYearCol.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));

        proteinView.getColumns().addAll(structureIdCol, classificationCol, experimentalTechniqueCol, macromoleculeTypeCol,
                residueCountCol, resolutionCol, structureMolecularWeightCol, crystallizationMethodCol,
                crystallizationTempKCol, densityMatthewsCol, densityPercentSolCol, pdbxDetailsCol, phValueCol, publicationYearCol);

        // Row coloring based on classification
        proteinView.setRowFactory(tv -> new TableRow<ProteinMetaData>() {
            @Override
            protected void updateItem(ProteinMetaData item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else {
                    switch (item.getClassification()) {
                        case "HYDROLASE":
                            setStyle("-fx-background-color: rgba(30,100,223,.4);");
                            break;
                        case "OXIDOREDUCTASE":
                            setStyle("-fx-background-color: rgba(252, 68, 68,.4);");
                            break;
                        case "TRANSFERASE":
                            setStyle("-fx-background-color: rgba(80, 222, 235,.4);");
                            break;
                        default:
                            setStyle("");
                            break;
                    }
                }
            }
        });

        //set the width to 200 for all columns
        for (TableColumn<ProteinMetaData, ?> column : proteinView.getColumns()) {
            column.setPrefWidth(200);
        }

        proteinView.setEditable(false);
        proteinView.getStylesheets().add(getClass().getClassLoader().getResource("css/TradeTable.css").toExternalForm());


        
        
    }

    public void setData(List<ProteinMetaData> data) {
        proteinMetaData.setAll(data);
    }

    public JFXTableView<ProteinMetaData> getProteinView() {
        return proteinView;
    }

    public void setProteinView(JFXTableView<ProteinMetaData> proteinView) {
        this.proteinView = proteinView;
    }
}
