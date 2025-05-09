package com.example.skycontrol.runways;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class RunwayDrawer {

    public void drawRunways(GraphicsContext gc, double width, double height, double scaleFactor, List<Runway> runways) {
        double centerX = width / 2;
        double centerY = height / 2;
        gc.setLineWidth(4);
        gc.setStroke(Color.GRAY);
        gc.setFont(Font.font("Arial", 30 * scaleFactor));
        gc.setFill(Color.WHITE);

        // Optional: draw axis for debugging
        gc.setStroke(Color.LIGHTBLUE);
        gc.strokeLine(0, centerY, width, centerY); // Horizontal X (090 - 270)
        gc.strokeLine(centerX, 0, centerX, height); // Vertical Y (000 - 180)

        // Determine lat/lon bounds to project
        double minLat = runways.stream().mapToDouble(Runway::getStartLat).min().orElse(0);
        double maxLat = runways.stream().mapToDouble(Runway::getStartLat).max().orElse(1);
        double minLon = runways.stream().mapToDouble(Runway::getStartLon).min().orElse(0);
        double maxLon = runways.stream().mapToDouble(Runway::getStartLon).max().orElse(1);

        double latRange = maxLat - minLat;
        double lonRange = maxLon - minLon;

        double scale = Math.min(width / lonRange, height / latRange) * 0.5; // Scale down for canvas

        gc.setStroke(Color.GRAY);
        for (Runway runway : runways) {
            double x1 = centerX + (runway.getStartLon() - (minLon + lonRange / 2)) * scale;
            double y1 = centerY - (runway.getStartLat() - (minLat + latRange / 2)) * scale;
            double x2 = centerX + (runway.getEndLon() - (minLon + lonRange / 2)) * scale;
            double y2 = centerY - (runway.getEndLat() - (minLat + latRange / 2)) * scale;

            gc.strokeLine(x1, y1, x2, y2);

            gc.fillText(runway.getStartName(), x1 - 20 * scaleFactor, y1 - 10 * scaleFactor);
            gc.fillText(runway.getEndName(), x2 + 10 * scaleFactor, y2 - 10 * scaleFactor);
        }
    }
}
