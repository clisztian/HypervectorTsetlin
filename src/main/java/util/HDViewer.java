package util;

import hyperdimension.encoders.VanillaBHV;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

public class HDViewer {


    final int NROWS = 100;
    final int NCOLS = 10;


    final GridPane gridPane;
    private final DropShadow dropShadow;

    public GridPane getGridPane() {
        return gridPane;
    }

    
    
    public HDViewer() {

        // Define the dimensions of the grid
        int rows = NROWS;
        int columns = NCOLS;
        int cellSize = 12; // Size of each cell in pixels

        // Create a GridPane
        gridPane = new GridPane();

        // Populate the GridPane with rectangles
        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows; j++) {
                // Create a rectangle for each cell
                Rectangle rect = new Rectangle(cellSize, cellSize);
                rect.setFill(Color.BLACK);

                //create a border around the rectangle
                rect.setStroke(Color.rgb(10,15,15));
                // Add the rectangle to the grid
                gridPane.add(rect, i, j);
            }
        }

        //Color.rgb(20,180,220)
        dropShadow = new DropShadow();
        dropShadow.setRadius(15.0);
        dropShadow.setOffsetX(0.0);
        dropShadow.setOffsetY(0.0);
        dropShadow.setColor(Color.rgb(20,180,220));
        
    }

    public void setData(List<VanillaBHV> vanillaBHVS) {

        int size = vanillaBHVS.size();
        //get the final NCOLS vectors, and display the first 100 bits of each on the gridPane
        for(int i = 0; i < Math.min(NCOLS, size); i++) {
            VanillaBHV vanillaBHV = vanillaBHVS.get(size - i - 1);
            int[] bits = getBitArray(vanillaBHV);


            for (int j = 0; j < NROWS; j++) {
                int bit = bits[j];
                Rectangle rect = (Rectangle) gridPane.getChildren().get(i * NROWS + j);
                if (bit == 1) {
                    rect.setFill(Color.rgb(20,180,220));
                    rect.setEffect(dropShadow);
                } else {
                    rect.setFill(Color.rgb(10,15,15));
                    rect.setEffect(null);
                }
            }
        }
    }

    public int[] getBitArray(VanillaBHV vanillaBHV) {

        int[] vanillaBitArray = vanillaBHV.toBooleanIntArray();
        int[] compactBitArray = new int[NROWS];
        //take chunks of the vanillaBHV and do majority voting for that chunk. There are HVC.DIMENSION / NROWS chunk sizes
        int chunkSize = VanillaBHV.DIMENSION / NROWS;
        for (int i = 0; i < NROWS; i++) {
            int sum = 0;
            for (int j = 0; j < chunkSize; j++) {
                sum += vanillaBitArray[i * chunkSize + j];
            }
            compactBitArray[i] = sum > chunkSize / 2 ? 1 : 0;
        }
        return compactBitArray;

    }


}
