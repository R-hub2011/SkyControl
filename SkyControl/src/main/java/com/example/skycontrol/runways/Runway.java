package com.example.skycontrol.runways;

public class Runway {
    private final String startName;
    private final String endName;
    private final int lengthFeet; // Real-world length in feet

    public Runway(String startName, String endName, int lengthFeet) {
        this.startName = startName;
        this.endName = endName;
        this.lengthFeet = lengthFeet;
    }

    public String getStartName() {
        return startName;
    }

    public String getEndName() {
        return endName;
    }

    public int getLengthFeet() {
        return lengthFeet;
    }
}
