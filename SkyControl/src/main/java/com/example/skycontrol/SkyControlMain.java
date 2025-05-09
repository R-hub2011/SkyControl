package com.example.skycontrol;

import com.example.skycontrol.runways.Runway;
import com.example.skycontrol.runways.RunwayCSVLoader;
import com.example.skycontrol.runways.RunwayDrawer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class SkyControlMain extends Application {

    private static final double SCALE_FACTOR = 0.6;  // You can adjust this

    @Override
    public void start(Stage stage) {
        double width = 1200;
        double height = 800;

        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Load runway data
        RunwayCSVLoader loader = new RunwayCSVLoader();
        List<Runway> runways = loader.loadRunwaysForAirport("VOMM"); // Chennai Intl example (ICAO code)

        // Draw the runways on the canvas
        RunwayDrawer drawer = new RunwayDrawer();
        drawer.drawRunways(gc, width, height, SCALE_FACTOR, runways);

        Pane root = new Pane(canvas);
        Scene scene = new Scene(root, width, height);

        stage.setTitle("SkyControl - Real Airport Layout");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
