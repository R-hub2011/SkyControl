package com.example.skycontrol.runways;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RunwayCSVLoader {

    public List<Runway> loadRunwaysForAirport(String icaoCode) {
        List<Runway> runways = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("/runways.csv")))) {

            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 20) continue;

                String airportIdent = parts[2].replace("\"", "");
                if (!airportIdent.equalsIgnoreCase(icaoCode)) continue;

                String startName = parts[8].replace("\"", "");
                String endName = parts[16].replace("\"", "");

                double lat1 = parseDoubleSafe(parts[9]);
                double lon1 = parseDoubleSafe(parts[10]);
                double lat2 = parseDoubleSafe(parts[17]);
                double lon2 = parseDoubleSafe(parts[18]);

                int lengthFeet = parseIntSafe(parts[3]);

                Runway runway = new Runway(startName, endName, lengthFeet, lat1, lon1, lat2, lon2);
                runways.add(runway);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return runways;
    }

    private double parseDoubleSafe(String s) {
        try {
            return Double.parseDouble(s.replace("\"", ""));
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseIntSafe(String s) {
        try {
            return Integer.parseInt(s.replace("\"", ""));
        } catch (Exception e) {
            return 0;
        }
    }
}
