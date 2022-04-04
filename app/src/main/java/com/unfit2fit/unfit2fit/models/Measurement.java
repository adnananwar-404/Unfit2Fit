package com.unfit2fit.unfit2fit.models;

public class Measurement {

    private String measurement, value, metric;
    private int id;

    public Measurement(String measurement, String value, String metric, int id) {
        this.measurement = measurement;
        this.value = value;
        this.metric = metric;
        this.id = id;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }

    public Measurement(){}

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
