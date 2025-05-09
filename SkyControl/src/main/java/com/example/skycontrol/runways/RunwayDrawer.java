package com.example.skycontrol.runways;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.List;

public class RunwayDrawer {

    public void drawRunways(GraphicsContext gc, double width, double height, double scaleFactor, List<Runway> runways) {
        double centerX = width / 2.0;
        double centerY = height / 2.0;

        gc.save();
        gc.translate(centerX, centerY);
        gc.rotate(-90); // Rotate world -90 degrees for correct compass orientation
        gc.translate(-centerX, -centerY);

        gc.setFont(Font.font("Arial", 27 * scaleFactor));  // 50% larger labels
        gc.setLineWidth(6);

        drawAxes(gc, centerX, centerY);  // Optional for debugging

        int maxLength = runways.stream().mapToInt(Runway::getLengthFeet).max().orElse(10000);

        double maxVisualLength = width * 0.175; // Reduced again: 0.7 → 0.35 → 0.175 (25%)

        double spacing = 100 * scaleFactor;

        for (int i = 0; i < runways.size(); i++) {
            Runway runway = runways.get(i);
            double visualLength = ((runway.getLengthFeet() / (double) maxLength) * maxVisualLength);

            double angle = getRunwayAngle(runway.getStartName());

            double offsetX = Math.cos(Math.toRadians(angle + 90)) * spacing * i;
            double offsetY = Math.sin(Math.toRadians(angle + 90)) * spacing * i;

            drawRunway(gc, centerX + offsetX, centerY + offsetY, visualLength, angle, scaleFactor, runway);
        }

        gc.restore();
    }

    private void drawRunway(GraphicsContext gc, double cx, double cy, double length, double angleDeg,
                            double scaleFactor, Runway runway) {
        gc.save();
        gc.translate(cx, cy);
        gc.rotate(angleDeg);

        gc.setStroke(Color.DARKGRAY);
        gc.strokeLine(-length / 2, 0, length / 2, 0);

        gc.setFill(Color.WHITE);
        gc.fillText(runway.getStartName(), -length / 2 - 40 * scaleFactor, -10 * scaleFactor);
        gc.fillText(runway.getEndName(), length / 2 + 10 * scaleFactor, -10 * scaleFactor);

        gc.restore();
    }

    private double getRunwayAngle(String runwayName) {
        try {
            String digits = runwayName.replaceAll("[^0-9]", "");
            int number = Integer.parseInt(digits);
            return (number % 36) * 10;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void drawAxes(GraphicsContext gc, double centerX, double centerY) {
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);

        gc.strokeLine(centerX - 300, centerY, centerX + 300, centerY);
        gc.strokeText("180°", centerX - 320, centerY - 5);
        gc.strokeText("000° / 360°", centerX + 310, centerY - 5);

        gc.strokeLine(centerX, centerY - 300, centerX, centerY + 300);
        gc.strokeText("270°", centerX + 5, centerY - 310);
        gc.strokeText("090°", centerX + 5, centerY + 320);
    }
}
