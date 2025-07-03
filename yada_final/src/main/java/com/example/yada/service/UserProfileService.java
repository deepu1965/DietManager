package com.example.yada.service;

import com.example.yada.model.UserProfile;
import com.example.yada.repository.JsonDataStorage;
import com.example.yada.service.calorie.CalorieCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {
    private final JsonDataStorage storage;
    private final CalorieCalculator calorieCalculator;

    @Autowired
    public UserProfileService(
        JsonDataStorage storage,
        @Qualifier("harrisBenedict") CalorieCalculator calorieCalculator
    ) {
        this.storage = storage;
        this.calorieCalculator = calorieCalculator;
    }

    public void updateProfile(String gender, double height, double weight, 
                             int age, String activityLevel) {
        UserProfile profile = storage.getUserProfile();
        profile.setGender(gender);
        profile.setHeight(height);
        profile.setWeight(weight);
        profile.setAge(age);
        profile.setActivityLevel(activityLevel);
        storage.saveUserProfile(); 
    }

    public double calculateTargetCalories() {
        return calorieCalculator.calculate(storage.getUserProfile());
    }
}