package com.example.guy.journeyblog;

import java.util.List;

public class MarkerLocations {

    private int lastselected;
    private List<Float> colors;
    private List <Double> position;
    public MarkerLocations(){}
    public MarkerLocations(int lastselected, List<Float> colors, List<Double> position) {
        this.lastselected = lastselected;
        this.colors = colors;
        this.position = position;
    }

    public int getLastselected() {
        return lastselected;
    }

    public void setLastselected(int lastselected) {
        this.lastselected = lastselected;
    }

    public List<Float> getColors() {
        return colors;
    }

    public void setColors(List<Float> colors) {
        this.colors = colors;
    }

    public List<Double> getPosition() {
        return position;
    }

    public void setPosition(List<Double> position) {
        this.position = position;
    }
}
