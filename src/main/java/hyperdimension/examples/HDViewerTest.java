package hyperdimension.examples;

import hyperdimension.encoders.VanillaBHV;
import javafx.application.Application;
import javafx.scene.Scene;
import util.HDViewer;

import java.util.ArrayList;
import java.util.List;

public class HDViewerTest extends Application {

        public static void main(String[] args) {
            launch(args);
        }

        @Override
        public void start(javafx.stage.Stage stage) throws Exception {
            HDViewer hdViewer = new HDViewer();

            List<VanillaBHV> bhvs = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                bhvs.add(VanillaBHV.zeroVector());
            }
            bhvs.add(VanillaBHV.randVector());
            bhvs.add(VanillaBHV.randVector());


            hdViewer.setData(bhvs);

            Scene scene = new Scene(hdViewer.getGridPane(), 200, 1000);

            stage.setScene(scene);
            stage.show();


        }
}
