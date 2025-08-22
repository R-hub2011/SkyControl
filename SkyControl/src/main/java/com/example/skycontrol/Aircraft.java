package com.example.skycontrol;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Aircraft {
    public double x, y;
    public double speed;
    public double heading;
    public double altitude;  // in feet
    public double lastAltitude;
    public boolean selected;
    public boolean landing, departing;
    public String flightNumber;

    public Aircraft(double x, double y, double speed, double heading) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.heading = heading;
        this.altitude = 6000;
        this.lastAltitude = 6000;


        this.flightNumber = generateFlightNumber();
    }

    private String generateFlightNumber() {
        String[] prefixes = {"ABX", "DLH", "UAL", "BAW", "AFR"};
        String prefix = prefixes[(int) (Math.random() * prefixes.length)];
        int number = 100 + (int) (Math.random() * 900);
        return prefix + number;
    }

    public void update(double dt) {

        lastAltitude = altitude;

        if (landing && altitude > 0) {
            altitude -= 100 * dt;
            if (altitude < 0) altitude = 0;
        } else if (departing && altitude < 12000) {
            altitude += 150 * dt;
        }

        double radians = Math.toRadians(heading);
        x += Math.cos(radians) * speed * dt;
        y += Math.sin(radians) * speed * dt;
    }

    public boolean contains(double mx, double my) {
        double dx = mx - x;
        double dy = my - y;
        return dx * dx + dy * dy < 200; // 14px radius hitbox
    }

    public void draw(GraphicsContext gc) {

        double aircraftSize = 30;

        // Draw aircraft body
        gc.setFill(selected ? Color.YELLOW : Color.LIMEGREEN);
        gc.fillOval(x - aircraftSize / 2, y - aircraftSize / 2, aircraftSize, aircraftSize);

        // Set font for the text next to the aircraft
        gc.setFont(javafx.scene.text.Font.font("Arial", 40));  // Set font size to 20 (adjust as needed)
        gc.setFill(Color.WHITE);

        String altitudeStr = String.format("%03d", (int)(altitude / 100));
        String statusSymbol = altitude > lastAltitude ? "⬆"
                : altitude < lastAltitude ? "⬇" : "➖";
        String label = flightNumber + "  " + altitudeStr + "  " + (int)speed + "kt " + statusSymbol;
        gc.fillText(label, x + 10, y - 10);
    }
}
