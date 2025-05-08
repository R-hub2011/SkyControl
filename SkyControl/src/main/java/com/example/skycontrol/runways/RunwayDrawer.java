package com.example.skycontrol.runways;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class RunwayDrawer {

    public void drawRunways(GraphicsContext gc, double width, double height, double scaleFactor, List<Runway> runways) {
        int maxLength = runways.stream().mapToInt(Runway::getLengthFeet).max().orElse(10000);
        double maxVisualLength = width * 0.3;  // Max runway should take up ~30% of canvas width

        double spacing = 75 * scaleFactor;
        double runwayHeight = 20;
        double startY = height / 2.0 - (runways.size() / 2.0 * spacing);

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(6);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 40 * scaleFactor));

        for (int i = 0; i < runways.size(); i++) {
            Runway runway = runways.get(i);

            // Calculate the visual length based on the runway length
            double visualLength = ((runway.getLengthFeet() / (double) maxLength) * maxVisualLength) * 0.3;

            // Calculate the angle based on the start and end runway names
            double startAngle = getAngleForRunway(runway.getStartName());
            double endAngle = getAngleForRunway(runway.getEndName());

            // The position to start drawing the runway
            double startX = (width - visualLength) / 2.0;
            double y = startY + i * spacing;

            // Save the current state of the graphics context
            gc.save();
            gc.translate(startX + visualLength / 2, y);  // Translate to the center of the runway
            gc.rotate(startAngle);  // Rotate based on the runway's start angle

            // Draw the runway line
            gc.strokeLine(-visualLength / 2, 0, visualLength / 2, 0);

            // Draw labels
            gc.fillText(runway.getStartName(), -visualLength / 2 - 40 * scaleFactor, -10 * scaleFactor);
            gc.fillText(runway.getEndName(), visualLength / 2 + 10 * scaleFactor, -10 * scaleFactor);

            // Restore the graphics context state to avoid affecting other elements
            gc.restore();
        }
    }

    // Helper method to convert runway code (e.g., "09", "27") to an angle
    private double getAngleForRunway(String runwayName) {
        switch (runwayName) {
            case "09": case "09L": case "09R": return 90;   // Eastward (90 degrees)
            case "27": case "27L": case "27R": return 270;  // Southward (270 degrees)
            case "15": case "15L": case "15R": return 150;  // Southeast (150 degrees)
            case "33": case "33L": case "33R": return 330;  // Northwest (330 degrees)
            case "03": case "03L": case "03R": return 30;   // Northeast (30 degrees)
            case "21": case "21L": case "21R": return 210;  // Southwest (210 degrees)
            case "12": case "12L": case "12R": return 120;  // Southeast (120 degrees)
            case "30": case "30L": case "30R": return 300;  // Northwest (300 degrees)
            case "360": case "000": return 0;               // Northward (0 degrees)
            case "180": return 180;                         // Westward (180 degrees)
            default: return 0;  // Default case for unrecognized runway codes
        }
    }
}
