package com.unfit2fit.unfit2fit.wrappers;

import com.unfit2fit.unfit2fit.models.Goal;
import com.unfit2fit.unfit2fit.models.Measurement;

import java.util.ArrayList;

public class MeasurementsListWrapper {

    private ArrayList<Measurement> weightList;
    private ArrayList<Measurement> waistList;
    private ArrayList<Measurement> stepsList;
    private ArrayList<Measurement> chestList;
    private ArrayList<Measurement> calorieIntakeList;

    public MeasurementsListWrapper(ArrayList<Measurement> weightList, ArrayList<Measurement> waistList, ArrayList<Measurement> stepsList, ArrayList<Measurement> chestList, ArrayList<Measurement> calorieIntakeList) {
        this.weightList = weightList;
        this.waistList = waistList;
        this.stepsList = stepsList;
        this.chestList = chestList;
        this.calorieIntakeList = calorieIntakeList;
    }

    public MeasurementsListWrapper() {
    }

    public ArrayList<Measurement> getWeightList() {
        return weightList;
    }

    public void setWeightList(ArrayList<Measurement> weightList) {
        this.weightList = weightList;
    }

    public ArrayList<Measurement> getWaistList() {
        return waistList;
    }

    public void setWaistList(ArrayList<Measurement> waistList) {
        this.waistList = waistList;
    }

    public ArrayList<Measurement> getStepsList() {
        return stepsList;
    }

    public void setStepsList(ArrayList<Measurement> stepsList) {
        this.stepsList = stepsList;
    }

    public ArrayList<Measurement> getChestList() {
        return chestList;
    }

    public void setChestList(ArrayList<Measurement> chestList) {
        this.chestList = chestList;
    }

    public ArrayList<Measurement> getCalorieIntakeList() {
        return calorieIntakeList;
    }

    public void setCalorieIntakeList(ArrayList<Measurement> calorieIntakeList) {
        this.calorieIntakeList = calorieIntakeList;
    }
}
