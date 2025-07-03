package com.example.yada.model;

import lombok.Data;

@Data
public class UserProfile {
    private String gender;
    private double height;
    private int age;
    private double weight;
    private String activityLevel;
    private String calorieMethod = "HarrisBenedict";
}