package com.example.skycontrol;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {
    public ResizableCanvas() {
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public void draw() {
        // Drawing triggered externally
    }
}
