package com.example.yada.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.yada.model.BasicFood;
import com.example.yada.model.CompositeFood;
import com.example.yada.model.DailyLog;
import com.example.yada.model.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PreDestroy;

@Repository
public class JsonDataStorage {
    private final ObjectMapper mapper = new ObjectMapper();
    private static final String DATA_DIR = "src/main/resources/data/";

    private List<BasicFood> basicFoods = new ArrayList<>();
    private List<CompositeFood> compositeFoods = new ArrayList<>();
    private DailyLog dailyLog = new DailyLog();
    private UserProfile userProfile = new UserProfile();

    public JsonDataStorage() {
        loadAllData();
    }

    private void loadAllData() {
        try {
            // Convert fixed-size Arrays.asList() to mutable ArrayList
            basicFoods = new ArrayList<>(Arrays.asList(
                mapper.readValue(Paths.get(DATA_DIR + "basicFoods.json").toFile(), 
                BasicFood[].class)
            ));
            
            compositeFoods = new ArrayList<>(Arrays.asList(
                mapper.readValue(Paths.get(DATA_DIR + "compositeFoods.json").toFile(), 
                CompositeFood[].class)
            ));
        } catch (IOException e) {
            System.out.println("No existing data found. Starting fresh.");
            // Initialize empty lists if files are missing
            basicFoods = new ArrayList<>();
            compositeFoods = new ArrayList<>();
        }
    }
    @PreDestroy
    public void saveAllData() {
        saveBasicFoods();
        saveCompositeFoods();
        saveDailyLogs();
        saveUserProfile();
    }
    public void saveBasicFoods() {
        try {
            mapper.writeValue(new File(DATA_DIR + "basicFoods.json"), basicFoods);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCompositeFoods() {
        try {
            mapper.writeValue(new File(DATA_DIR + "compositeFoods.json"), compositeFoods);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDailyLogs() {
        try {
            mapper.writeValue(new File(DATA_DIR + "dailyLogs.json"), dailyLog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveUserProfile() {
        try {
            mapper.writeValue(new File(DATA_DIR + "userProfile.json"), userProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Getters and setters for all data fields
    public List<BasicFood> getBasicFoods() { return basicFoods; }
    public List<CompositeFood> getCompositeFoods() { return compositeFoods; }
    public DailyLog getDailyLog() { return dailyLog; }
    public UserProfile getUserProfile() { return userProfile; }
}