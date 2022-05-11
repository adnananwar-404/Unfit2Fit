package com.unfit2fit.unfit2fit.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Steps implements Serializable {

    private ArrayList<Integer> dailyBiHourlySteps = new ArrayList<>();
    private int totalSteps;

    public Steps(ArrayList<Integer> dailyBiHourlySteps, int totalSteps) {
        this.dailyBiHourlySteps = dailyBiHourlySteps;
        this.totalSteps = totalSteps;
    }

    public Steps(){}

    public ArrayList<Integer> getDailyBiHourlySteps() {
        return dailyBiHourlySteps;
    }

    public void setDailyBiHourlySteps(ArrayList<Integer> dailyBiHourlySteps) {
        this.dailyBiHourlySteps = dailyBiHourlySteps;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }
}
