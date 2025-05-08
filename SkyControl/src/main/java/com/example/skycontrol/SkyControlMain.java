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

    private static final int INITIAL_WIDTH = 1600; // Initial canvas width
    private static final int INITIAL_HEIGHT = 1000; // Initial canvas height

    private final List<Aircraft> aircraftList = new ArrayList<>();
    private long lastTime;

    @Override
    public void start(Stage primaryStage) {
        // Create the initial canvas with the default size
        Canvas canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Add some sample aircraft
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

        // Keyboard input for aircraft control
        canvas.setOnKeyPressed(this::handleKeyPress);

        // Set up the scene and link canvas to window resizing
        Scene scene = new Scene(new StackPane(canvas));
        primaryStage.setTitle("SkyControl - Radar View");
        primaryStage.setScene(scene);

        // Resize canvas based on the window size
        scene.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            canvas.setWidth(newWidth.doubleValue());
        });

        scene.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            canvas.setHeight(newHeight.doubleValue());
        });

        primaryStage.show();
        canvas.requestFocus();

        // Game loop for continuous rendering
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
        // Handling aircraft control (speed, heading) based on keyboard input
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
        // Clear the canvas with black background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, width, height);

        gc.save();

        // Set the center of the screen (runways)
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        gc.translate(centerX, centerY); // Translate to center
        gc.translate(-centerX, -centerY);  // Offset back to original center

        // Draw grid and runways
        drawGrid(gc, width, height);
        drawRunways(gc, width, height);

        // Update and render all aircraft
        for (Aircraft ac : aircraftList) {
            if (ac.y > 230 && ac.y < 270 && !ac.landing && !ac.departing) {
                ac.landing = true;  // Begin landing when aircraft is in range
            }
            if (ac.landing && ac.speed <= 0) {
                ac.landing = false;
                ac.departing = true;  // Begin departing when landing is complete
            }

            ac.update(deltaTime);
            ac.draw(gc);
        }

        gc.restore();
    }

    private void drawGrid(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(1);
        for (int i = 100; i < width; i += 100) {
            gc.strokeLine(i, 0, i, height);
        }
        for (int i = 100; i < height; i += 100) {
            gc.strokeLine(0, i, width, i);
        }
    }

    private void drawRunways(GraphicsContext gc, double width, double height) {
        // Reduced runway size and centered on screen
        double runwayWidth = width * 0.4;  // 40% of the canvas width
        double runwayHeight = 20;
        double runwayStartX = (width - runwayWidth) / 2.0;
        double runwayStartY = height / 2.0 - 40;  // Positioned slightly above the center

        // Drawing runways smaller in the center
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(6);
        gc.strokeLine(runwayStartX, runwayStartY, runwayStartX + runwayWidth, runwayStartY);  // 08L/26R
        gc.strokeLine(runwayStartX, runwayStartY + 100, runwayStartX + runwayWidth, runwayStartY + 100); // 08R/26L

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
