package com.example.skycontrol;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SkyControlMain extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private final List<Aircraft> aircraftList = new ArrayList<>();
    private long lastTime;

    // Zoom properties
    private double zoomLevel = 1.0;
    private final double ZOOM_STEP = 0.1;
    private double MIN_ZOOM = 0.15; // ZOOM OUT
    private double MAX_ZOOM;

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Add some sample aircraft
        aircraftList.add(new Aircraft(100, 100, 30, 45));
        aircraftList.add(new Aircraft(200, 300, 40, 90));
        aircraftList.add(new Aircraft(600, 150, 50, 135));

        // Mouse click to select aircraft
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            double mx = e.getX() / zoomLevel;
            double my = e.getY() / zoomLevel;
            for (Aircraft ac : aircraftList) {
                ac.selected = ac.contains(mx, my);
            }
        });

        // Keyboard input
        canvas.setOnKeyPressed(this::handleKeyPress);

        // Zoom scroll with Ctrl
        Scene scene = new Scene(new StackPane(canvas));
        scene.setOnScroll((ScrollEvent e) -> {
            if (e.isControlDown()) {
                if (e.getDeltaY() > 0) {
                    zoomLevel = Math.min(MAX_ZOOM, zoomLevel + ZOOM_STEP);
                } else {
                    zoomLevel = Math.max(MIN_ZOOM, zoomLevel - ZOOM_STEP);
                }
            }
        });

        primaryStage.setTitle("SkyControl - Radar View");
        primaryStage.setScene(scene);
        primaryStage.show();
        canvas.requestFocus();

        // Set zoom limits
        MAX_ZOOM = computeMaxZoom();

        // Game loop
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

    private void updateAndRender(GraphicsContext gc, double deltaTime) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.save();

        double centerX = WIDTH / 2.0;
        double centerY = HEIGHT / 2.0;

        gc.translate(centerX, centerY);
        gc.scale(zoomLevel, zoomLevel);
        gc.translate(-centerX, -centerY);

        drawGrid(gc);
        drawRunways(gc);

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

        gc.restore();
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(1);
        for (int i = 100; i < WIDTH; i += 100) {
            gc.strokeLine(i, 0, i, HEIGHT);
        }
        for (int i = 100; i < HEIGHT; i += 100) {
            gc.strokeLine(0, i, WIDTH, i);
        }
    }

    private void drawRunways(GraphicsContext gc) {
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(6);
        gc.strokeLine(200, 250, 600, 250); // 08L/26R
        gc.strokeLine(200, 350, 600, 350); // 08R/26L

        gc.setFill(Color.WHITE);
        gc.fillText("08L", 190, 240);
        gc.fillText("26R", 610, 240);
        gc.fillText("08R", 190, 340);
        gc.fillText("26L", 610, 340);
    }

    private double computeMaxZoom() {
        double runwayStartX = 200;
        double runwayEndX = 600;
        double runwayTopY = 250;
        double runwayBottomY = 350;

        double runwayWidth = runwayEndX - runwayStartX;
        double runwayHeight = runwayBottomY - runwayTopY;

        double paddingPixels = 4 * 96; // 4 inches in pixels (~384)

        double neededWidth = runwayWidth + 2 * paddingPixels;
        double neededHeight = runwayHeight + 2 * paddingPixels;

        double zoomX = WIDTH / neededWidth;
        double zoomY = HEIGHT / neededHeight;

        return Math.min(zoomX, zoomY);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
