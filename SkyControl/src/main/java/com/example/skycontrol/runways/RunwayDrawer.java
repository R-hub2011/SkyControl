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
        double startY = height / 2.0 - (runways.size() / 2.0 * spacing);

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(6);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", 40 * scaleFactor));

        // Loop over all runways
        for (int i = 0; i < runways.size(); i++) {
            Runway runway = runways.get(i);

            // Calculate visual length of runway based on real-world length and max length
            double visualLength = ((runway.getLengthFeet() / (double) maxLength) * maxVisualLength) * 0.3;

            // Get angle based on runway codes (this needs to match the runway's real direction)
            double angle = getRunwayAngle(runway);

            // Start position for drawing runway
            double startX = (width - visualLength) / 2.0;
            double y = startY + i * spacing;

            // Save current context and apply transformations
            gc.save();
            gc.translate(startX + visualLength / 2, y);  // Move origin to center of runway
            gc.rotate(angle);  // Rotate based on the runway's angle

            // Draw the runway line
            gc.strokeLine(-visualLength / 2, 0, visualLength / 2, 0);

            // Draw runway labels (start and end points)
            gc.fillText(runway.getStartName(), -visualLength / 2 - 40 * scaleFactor, -10 * scaleFactor);
            gc.fillText(runway.getEndName(), visualLength / 2 + 10 * scaleFactor, -10 * scaleFactor);

            // Restore the graphics context
            gc.restore();
        }
    }

    // Helper method to convert runway code to angle in degrees
    private double getRunwayAngle(Runway runway) {
        String startName = runway.getStartName().replaceAll("[LR]", "");
        String endName = runway.getEndName().replaceAll("[LR]", "");

        // Calculate angles based on runway headings
        switch (startName) {
            case "08":
                if (runway.getStartName().contains("L")) return 80;  // Left (top) orientation
                if (runway.getStartName().contains("R")) return 260;  // Right (down) orientation
                return 90; // East
            case "26":
                if (runway.getStartName().contains("L")) return 260;  // Left (down) orientation
                if (runway.getStartName().contains("R")) return 80;   // Right (top) orientation
                return 270; // West
            case "09": return 0;   // North (Top) orientation
            case "27": return 180; // South (Bottom) orientation
            case "16": return 270; // West (270 degrees)
            case "33": return 90;  // East (90 degrees)
            case "15": return 270; // West (270 degrees)
            case "03": return 30;  // Northeast
            case "21": return 210; // Southwest
            case "12": return 120; // Southeast
            case "30": return 300; // Northwest
            case "360":
            case "000": return 0;  // North (0 degrees)
            case "180": return 180; // South (180 degrees)
            default: return 0;      // Default to North if unknown
        }
    }
}
