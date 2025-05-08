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
        // Size of aircraft (let's assume the aircraft width is approximately 60px)
        double aircraftWidth = 50 * scaleFactor;  // Adjust this value if the aircraft size changes

        // Runway width should be larger than the aircraft size, and proportional to the canvas size
        double runwayWidth = aircraftWidth * 20;  // Make runway width 20 times the aircraft width
        double runwayHeight = 300 * scaleFactor;  // Set a reasonable height based on scaleFactor

        // Adjust the runway length and positioning based on the canvas size
        double runwayStartX = (width - runwayWidth) / 2.0;  // Center the runway horizontally
        double runwayStartY = height / 2.0 - runwayHeight / 2.0;  // Center the runway vertically

        // Draw the runways in the center with the updated sizes
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(40 * scaleFactor);  // Adjust line width for visibility
        gc.strokeLine(runwayStartX, runwayStartY, runwayStartX + runwayWidth, runwayStartY);  // 08L/26R
        gc.strokeLine(runwayStartX, runwayStartY + runwayHeight, runwayStartX + runwayWidth, runwayStartY + runwayHeight); // 08R/26L

        // Set font size for runway labels, scaling with the runway size
        gc.setFont(javafx.scene.text.Font.font("Arial", 150 * scaleFactor));  // Scale font with the runway
        gc.setFill(Color.WHITE);

        // Draw runway labels with increased font size
        gc.fillText("08L", runwayStartX - 300 * scaleFactor, runwayStartY - 30 * scaleFactor);  // Move left label further out
        gc.fillText("26R", runwayStartX + runwayWidth + 30 * scaleFactor, runwayStartY - 30 * scaleFactor);  // Right label stays as is
        gc.fillText("08R", runwayStartX - 300 * scaleFactor, runwayStartY + runwayHeight + 100 * scaleFactor);  // Move left label further down
        gc.fillText("26L", runwayStartX + runwayWidth + 30 * scaleFactor, runwayStartY + runwayHeight + 100 * scaleFactor);  // Right label stays as is
    }

    public static void main(String[] args) {
        launch(args);
    }
}
