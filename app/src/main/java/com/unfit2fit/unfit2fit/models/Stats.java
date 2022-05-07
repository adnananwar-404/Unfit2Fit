package com.unfit2fit.unfit2fit.models;

import java.util.ArrayList;

public class Stats {
    private String stat;
    private String goal;
    private ArrayList<Measurement> measurements;

    public Stats(String stat, String goal, ArrayList<Measurement> measurements) {
        this.stat = stat;
        this.goal = goal;
        this.measurements = measurements;
    }

    public Stats() {
    }

    public Stats(String stat, ArrayList<Measurement> measurements) {
        this.stat = stat;
        this.measurements = measurements;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ArrayList<Measurement> measurements) {
        this.measurements = measurements;
    }
}
