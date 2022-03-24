package com.unfit2fit.unfit2fit.models;

public class Goal {

    private String goal, current, target;

    public Goal(String goal, String current, String target) {
        this.goal = goal;
        this.current = current;
        this.target = target;
    }

    public Goal(){}

    public String getGoal() {
        return goal;
    }

    public String getCurrent() {
        return current;
    }

    public String getTarget() {
        return target;
    }
}
