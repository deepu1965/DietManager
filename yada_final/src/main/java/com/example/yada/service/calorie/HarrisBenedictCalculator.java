package com.example.yada.service.calorie;

import com.example.yada.model.UserProfile;
// Must match qualifier exactly
public class HarrisBenedictCalculator implements CalorieCalculator {
    // Change to package-private access
    @Override // Mandatory annotation to enforce override check
    public double calculate(UserProfile profile) {
        // Correct implementation
        double bmr = profile.getGender().equalsIgnoreCase("male") ?
            88.362 + (13.397 * profile.getWeight()) 
            + (4.799 * profile.getHeight()) - (5.677 * profile.getAge()) :
            447.593 + (9.247 * profile.getWeight()) 
            + (3.098 * profile.getHeight()) - (4.330 * profile.getAge());

        return bmr * getActivityMultiplier(profile.getActivityLevel());
    }
    static double getActivityMultiplier(String level) {
        return switch (level.toLowerCase()) {
            case "sedentary" -> 1.2;
            case "light" -> 1.375;
            case "moderate" -> 1.55;
            case "active" -> 1.725;
            default -> 1.2;
        };
    }
}