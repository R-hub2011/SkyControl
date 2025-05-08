package com.example.skycontrol;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SkyControlMain extends Application {

    private static final int INITIAL_WIDTH = 1600;
    private static final int INITIAL_HEIGHT = 1000;

    private final List<Aircraft> aircraftList = new ArrayList<>();
    private long lastTime;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Sample aircraft
        aircraftList.add(new Aircraft(100, 100, 30, 45));
        aircraftList.add(new Aircraft(200, 300, 40, 90));
        aircraftList.add(new Aircraft(600, 150, 50, 135));

        // Mouse click to select aircraft
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            double mx = e.getX();
            double my = e.getY();
            for (Aircraft ac : aircraftList) {
                ac.selected = ac.contains(mx, my);
            }
        });

        // Keyboard input
        canvas.setOnKeyPressed(this::handleKeyPress);

        // Scene setup
        Scene scene = new Scene(new StackPane(canvas));
        primaryStage.setTitle("SkyControl - Radar View");
        primaryStage.setScene(scene);

        // Canvas resizing
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> canvas.setWidth(newWidth.doubleValue()));
        scene.heightProperty().addListener((obs, oldHeight, newHeight) -> canvas.setHeight(newHeight.doubleValue()));

        primaryStage.show();
        canvas.requestFocus();

        // Game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime > 0) {
                    double deltaTime = (now - lastTime) / 1_000_000_000.0;
                    updateAndRender(gc, deltaTime, canvas.getWidth(), canvas.getHeight());
                }
                lastTime = now;
            }
        };
        timer.start();
    }

    private void handleKeyPress(KeyEvent e) {
        for (Aircraft ac : aircraftList) {
            if (ac.selected) {
                switch (e.getCode()) {
                    case LEFT -> ac.heading = (ac.heading - 5 + 360) % 360;
                    case RIGHT -> ac.heading = (ac.heading + 5) % 360;
                    case UP -> ac.speed += 5;
                    case DOWN -> ac.speed = Math.max(0, ac.speed - 5);
                }
            }
        }
    }

    private void updateAndRender(GraphicsContext gc, double deltaTime, double width, double height) {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        double scaleFactor = 0.30;

        // --- FIX: Scale and center the world properly ---
        gc.save();
        gc.translate(width / 2, height / 2);     // Move origin to center
        gc.scale(scaleFactor, scaleFactor);      // Scale around center
        gc.translate(-width / 2, -height / 2);   // Shift drawing back to top-left

        drawRunways(gc, width, height, scaleFactor);

        for (Aircraft ac : aircraftList) {
            if (ac.y > 230 && ac.y < 270 && !ac.landing && !ac.departing) {
                ac.landing = true;
            }
            if (ac.landing && ac.speed <= 0) {
                ac.landing = false;
                ac.departing = true;
            }

            ac.update(deltaTime);
            ac.draw(gc);
        }

        gc.restore(); // Restore original transform
    }

    private void drawRunways(GraphicsContext gc, double width, double height, double scaleFactor) {
        double runwayWidth = (width * 0.2 + 192); // Don't scale width here
        double runwayHeight = 20;

        double runwayStartX = (width - runwayWidth) / 2.0;
        double runwayStartY = height / 2.0 - 40;

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(6);
        gc.strokeLine(runwayStartX, runwayStartY, runwayStartX + runwayWidth, runwayStartY);
        gc.strokeLine(runwayStartX, runwayStartY + 100, runwayStartX + runwayWidth, runwayStartY + 100);

        gc.setFill(Color.WHITE);
        gc.fillText("08L", runwayStartX - 20, runwayStartY - 10);
        gc.fillText("26R", runwayStartX + runwayWidth + 10, runwayStartY - 10);
        gc.fillText("08R", runwayStartX - 20, runwayStartY + 110);
        gc.fillText("26L", runwayStartX + runwayWidth + 10, runwayStartY + 110);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
