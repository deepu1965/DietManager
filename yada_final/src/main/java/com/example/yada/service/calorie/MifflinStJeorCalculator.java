package com.example.yada.service.calorie;

import com.example.yada.model.UserProfile;

public class MifflinStJeorCalculator implements CalorieCalculator {
    @Override
    public double calculate(UserProfile profile) {
        double bmr = (10 * profile.getWeight()) 
                    + (6.25 * profile.getHeight()) 
                    - (5 * profile.getAge()) 
                    + (profile.getGender().equalsIgnoreCase("male") ? 5 : -161);

        return bmr * HarrisBenedictCalculator.getActivityMultiplier(profile.getActivityLevel());
    }
}