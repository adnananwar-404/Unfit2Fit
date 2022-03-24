package com.unfit2fit.unfit2fit.models;

public class BMI {
    private String gender,height,age,weight,uID,bmi,category, weight_range;

    public BMI(String uID, String gender, String height, String user_age, String weight, String bmi, String category, String weight_range) {
        this.uID = uID;
        this.gender = gender;
        this.height = height;
        this.age = user_age;
        this.weight = weight;
        this.bmi = bmi;
        this.category = category;
        this.weight_range = weight_range;
    }

    public String getBmi() {
        return bmi;
    }

    public String getCategory() {
        return category;
    }

    public String getWeight_range() {
        return weight_range;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public String getAge() {
        return age;
    }

    public String getWeight() {
        return weight;
    }

    public String getuID() {
        return uID;
    }
}
