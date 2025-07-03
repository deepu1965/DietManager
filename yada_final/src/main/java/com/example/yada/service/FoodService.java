package com.example.yada.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.yada.model.BasicFood;
import com.example.yada.model.CompositeFood;
import com.example.yada.repository.JsonDataStorage;

@Service
public class FoodService {
    @Autowired private JsonDataStorage storage;
    
    // Basic Food Operations
    public void addBasicFood(String id, List<String> keywords, int calories) {
        // Check for duplicates
        if (findFoodById(id).isPresent()) {
            throw new IllegalArgumentException("Food with ID '" + id + "' already exists!");
        }
        
        // Validate input
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Food ID cannot be empty!");
        }
        if (calories < 0) {
            throw new IllegalArgumentException("Calories cannot be negative!");
        }
        
        storage.getBasicFoods().add(new BasicFood(id.trim(), keywords, calories));
        storage.saveBasicFoods();
    }
    
    public Optional<BasicFood> findFoodById(String id) {
        return storage.getBasicFoods().stream()
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst();
    }
    
    public List<BasicFood> getAllBasicFoods() {
        return new ArrayList<>(storage.getBasicFoods());
    }
    
    public boolean deleteBasicFood(String id) {
        boolean removed = storage.getBasicFoods().removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (removed) {
            storage.saveBasicFoods();
        }
        return removed;
    }

    public List<BasicFood> searchBasicFoods(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllBasicFoods();
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        return storage.getBasicFoods().stream()
                .filter(f -> f.getKeywords().stream()
                    .anyMatch(k -> k.toLowerCase().contains(searchTerm)) ||
                    f.getId().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    // Composite Food Operations
    public void addCompositeFood(String id, List<String> keywords, Map<String, Double> ingredients) {
        // Check for duplicates
        if (findCompositeFoodById(id).isPresent()) {
            throw new IllegalArgumentException("Composite food with ID '" + id + "' already exists!");
        }
        
        // Validate input
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Food ID cannot be empty!");
        }
        
        // Validate ingredients exist
        for (String ingredientId : ingredients.keySet()) {
            if (findFoodById(ingredientId).isEmpty() && findCompositeFoodById(ingredientId).isEmpty()) {
                throw new IllegalArgumentException("Ingredient '" + ingredientId + "' does not exist!");
            }
        }
        
        CompositeFood composite = new CompositeFood(id.trim(), keywords, ingredients);
        storage.getCompositeFoods().add(composite);
        storage.saveCompositeFoods();
    }
    
    public Optional<CompositeFood> findCompositeFoodById(String id) {
        return storage.getCompositeFoods().stream()
                .filter(f -> f.getId().equalsIgnoreCase(id))
                .findFirst();
    }
    
    public List<CompositeFood> getAllCompositeFoods() {
        return new ArrayList<>(storage.getCompositeFoods());
    }
    
    public boolean deleteCompositeFood(String id) {
        boolean removed = storage.getCompositeFoods().removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (removed) {
            storage.saveCompositeFoods();
        }
        return removed;
    }
    
    public List<CompositeFood> searchCompositeFoods(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCompositeFoods();
        }
        
        String searchTerm = keyword.toLowerCase().trim();
        return storage.getCompositeFoods().stream()
                .filter(f -> f.getKeywords().stream()
                    .anyMatch(k -> k.toLowerCase().contains(searchTerm)) ||
                    f.getId().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    // Calorie Calculation
    public int calculateCalories(String foodId, double servings) {
        // Check basic foods first
        Optional<BasicFood> basicFood = findFoodById(foodId);
        if (basicFood.isPresent()) {
            return (int) Math.round(basicFood.get().getCaloriesPerServing() * servings);
        }
        
        // Check composite foods
        Optional<CompositeFood> compositeFood = findCompositeFoodById(foodId);
        if (compositeFood.isPresent()) {
            return calculateCompositeCalories(compositeFood.get(), servings);
        }
        
        throw new IllegalArgumentException("Food with ID '" + foodId + "' not found!");
    }
    
    private int calculateCompositeCalories(CompositeFood compositeFood, double servings) {
        double totalCalories = 0;
        
        for (Map.Entry<String, Double> ingredient : compositeFood.getIngredients().entrySet()) {
            String ingredientId = ingredient.getKey();
            double ingredientServings = ingredient.getValue() * servings;
            
            totalCalories += calculateCalories(ingredientId, ingredientServings);
        }
        
        return (int) Math.round(totalCalories);
    }
    
    // Utility Methods
    public int getTotalFoodCount() {
        return storage.getBasicFoods().size() + storage.getCompositeFoods().size();
    }
    
    public boolean foodExists(String id) {
        return findFoodById(id).isPresent() || findCompositeFoodById(id).isPresent();
    }
    
    public List<String> getAllFoodIds() {
        List<String> allIds = new ArrayList<>();
        storage.getBasicFoods().forEach(f -> allIds.add(f.getId()));
        storage.getCompositeFoods().forEach(f -> allIds.add(f.getId()));
        return allIds;
    }
}