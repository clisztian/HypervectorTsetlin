package hyperdimension.examples;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SequenceApplication extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        SequenceSeperationPanel sequenceSeperationPanel = new SequenceSeperationPanel();

        Scene scene = new Scene(sequenceSeperationPanel.getVbox(), 1000, 800);
        stage.setScene(scene);
        stage.show();


    }

    public static void main(String[] args) {
        launch(args);
    }
}
