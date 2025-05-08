package com.example.skycontrol;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;
import java.util.List;

public class SkyControlMain extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final List<Aircraft> aircraftList = new ArrayList<>();
    private long lastTime;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Add some sample aircraft
        aircraftList.add(new Aircraft(100, 100, 20, 45));
        aircraftList.add(new Aircraft(200, 300, 10, 90));
        aircraftList.add(new Aircraft(600, 150, 20, 135));

        // Handle mouse click to select aircraft
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            double mx = e.getX();
            double my = e.getY();
            for (Aircraft ac : aircraftList) {
                ac.selected = ac.contains(mx, my);
            }
        });

        // Main game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime > 0) {
                    double deltaTime = (now - lastTime) / 1_000_000_000.0;
                    updateAndRender(gc, deltaTime);
                }
                lastTime = now;
            }
        };
        timer.start();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root);
        primaryStage.setTitle("SkyControl - Radar View");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateAndRender(GraphicsContext gc, double deltaTime) {
        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        // Grid
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(1);
        for (int i = 100; i < WIDTH; i += 100) {
            gc.strokeLine(i, 0, i, HEIGHT);
        }
        for (int i = 100; i < HEIGHT; i += 100) {
            gc.strokeLine(0, i, WIDTH, i);
        }

        // Runways
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(6);
        gc.strokeLine(200, 250, 600, 250); // 08L/26R
        gc.strokeLine(200, 350, 600, 350); // 08R/26L

        gc.setFill(Color.WHITE);
        gc.fillText("08L", 190, 240);
        gc.fillText("26R", 610, 240);
        gc.fillText("08R", 190, 340);
        gc.fillText("26L", 610, 340);

        // Update and draw aircraft
        for (Aircraft ac : aircraftList) {
            ac.update(deltaTime);
            ac.draw(gc);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
