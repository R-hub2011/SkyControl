package com.example.skycontrol.aircraft;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Aircraft {
    public double x, y;
    public double speed;
    public double heading;
    public boolean selected = false;
    public boolean landing = false;
    public boolean departing = false;

    public Aircraft(double x, double y, double speed, double heading) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.heading = heading;
    }

    public void update(double deltaTime) {
        if (landing) {
            speed = Math.max(0, speed - 0.5);
        } else if (departing) {
            speed = Math.min(100, speed + 0.5);
        }

        double radians = Math.toRadians(heading);
        x += Math.cos(radians) * speed * deltaTime;
        y += Math.sin(radians) * speed * deltaTime;
    }

    public void draw(GraphicsContext gc, double scaleFactor) {
        gc.setFill(selected ? Color.YELLOW : Color.LIME);
        gc.fillOval(x - 5, y - 5, 10, 10);  // Reduced from 20x20 to 10x10

        // Always show label
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font("Arial", 20)); // Visible label font
        String flightInfo = "ABX957 | FL060 | SPD " + (int)speed;
        if (landing) flightInfo += " ↓";
        else if (departing) flightInfo += " ↑";

        gc.fillText(flightInfo, x + 15, y - 15);
    }


    public boolean contains(double mx, double my) {
        return Math.hypot(mx - x, my - y) <= 10;
    }
}
