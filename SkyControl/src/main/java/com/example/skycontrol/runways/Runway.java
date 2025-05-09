package com.example.skycontrol.runways;

public class Runway {
    private String startName, endName;
    private int lengthFeet;
    private double startLat, startLon, endLat, endLon;

    public Runway(String startName, String endName, int lengthFeet,
                  double lat1, double lon1, double lat2, double lon2) {
        this.startName = startName;
        this.endName = endName;
        this.lengthFeet = lengthFeet;
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;
    }

    public String getStartName() { return startName; }
    public String getEndName() { return endName; }
    public int getLengthFeet() { return lengthFeet; }
    public double getStartLat() { return startLat; }
    public double getStartLon() { return startLon; }
    public double getEndLat() { return endLat; }
    public double getEndLon() { return endLon; }
}