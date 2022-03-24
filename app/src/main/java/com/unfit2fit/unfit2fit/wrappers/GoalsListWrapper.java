package com.unfit2fit.unfit2fit.wrappers;

import com.unfit2fit.unfit2fit.models.Goal;

import java.util.ArrayList;

public class GoalsListWrapper {
    private ArrayList<Goal> goalsList;

    public GoalsListWrapper(ArrayList<Goal> goalsList) {
        this.goalsList = goalsList;
    }

    public GoalsListWrapper(){}

    public ArrayList<Goal> getGoalsList() {
        return goalsList;
    }

    public void setGoalsList(ArrayList<Goal> list){this.goalsList = list;}

}
