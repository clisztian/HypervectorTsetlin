package splatting;

import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GaussianSplattingRenderer extends Application {

    private static class Gaussian {
        double[] mean; // [x, y, z]
        double[][] covariance; // 3x3 covariance matrix
        Color color;

        public Gaussian(double[] mean, double[][] covariance, Color color) {
            this.mean = mean;
            this.covariance = covariance;
            this.color = color;
        }
    }

    private List<Gaussian> gaussians = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize sample data
        initializeGaussians();

        // Create the scene and set up the camera
        Group root = new Group();
        Scene scene = new Scene(root, 800, 600, true);
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.getTransforms().addAll(
                new Rotate(-20, Rotate.X_AXIS),
                new Rotate(-20, Rotate.Y_AXIS),
                new Translate(0, 0, -100)
        );
        scene.setCamera(camera);

        // Render Gaussian splats
        for (Gaussian gaussian : gaussians) {
            Sphere sphere = new Sphere(2);
            sphere.setMaterial(new PhongMaterial(gaussian.color));
            sphere.setTranslateX(gaussian.mean[0]);
            sphere.setTranslateY(gaussian.mean[1]);
            sphere.setTranslateZ(gaussian.mean[2]);
            root.getChildren().add(sphere);
        }

        primaryStage.setTitle("3D Gaussian Splatting");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGaussians() {
        // Example: Initialize with some random Gaussians
        gaussians.add(new Gaussian(new double[]{10, 10, 10}, new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, Color.RED));
        gaussians.add(new Gaussian(new double[]{-10, -10, -10}, new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, Color.GREEN));
        gaussians.add(new Gaussian(new double[]{20, 0, 0}, new double[][]{{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, Color.BLUE));
    }
}
