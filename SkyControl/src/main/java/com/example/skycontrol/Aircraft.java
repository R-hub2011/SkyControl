package com.example.skycontrol;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public class Aircraft {
    public double x, y;
    public double speed;    // in pixels/second
    public double heading;  // in degrees
    public boolean selected = false;
    public boolean landing = false;  // is the aircraft landing?
    public boolean departing = false; // is the aircraft departing?

    public Aircraft(double x, double y, double speed, double heading) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.heading = heading;
    }

    public void update(double deltaTime) {
        if (landing) {
            // Slow down as the aircraft approaches the runway
            speed = Math.max(0, speed - 0.5);
        } else if (departing) {
            // Speed up as the aircraft departs
            speed = Math.min(100, speed + 0.5);
        }

        double radians = Math.toRadians(heading);
        x += Math.cos(radians) * speed * deltaTime;
        y += Math.sin(radians) * speed * deltaTime;
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(selected ? Color.YELLOW : Color.LIME);
        gc.fillOval(x - 5, y - 5, 10, 10);

        if (selected) {
            gc.setFill(Color.WHITE);
            String info = String.format("SPD: %.0f HDG: %.0f", speed, heading);
            gc.fillText(info, x + 10, y - 10);
        }
    }

    public boolean contains(double mx, double my) {
        return Math.hypot(mx - x, my - y) <= 10;
    }
}
