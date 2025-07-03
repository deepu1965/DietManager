package com.example.yada.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.yada.model.LogEntry;
import com.example.yada.service.FoodService;
import com.example.yada.service.LogService;
import com.example.yada.service.UserProfileService;

@Component
public class ConsoleUI implements CommandLineRunner {
    private final FoodService foodService;
    private final LogService logService;
    private final UserProfileService profileService;
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public ConsoleUI(FoodService foodService, LogService logService,
                    UserProfileService profileService) {
        this.foodService = foodService;
        this.logService = logService;
        this.profileService = profileService;
    }

    @Override
    public void run(String... args) {
        while(true) {
            System.out.println("\n=== YADA Diet Manager ===");
            System.out.println("1. Add Basic Food");
            System.out.println("2. Create Composite Food");
            System.out.println("3. Log Food Consumption");
            System.out.println("4. View Daily Log");
            System.out.println("5. Undo Last Action");
            System.out.println("6. Set User Profile");
            System.out.println("7. Show Calorie Target");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");
            
            int choice = getIntInput();
            
            switch(choice) {
                case 1 -> addBasicFood();
                case 2 -> createCompositeFood();
                case 3 -> logFoodConsumption();
                case 4 -> viewDailyLog();
                case 5 -> undoLastAction();
                case 6 -> setUserProfile();
                case 7 -> showCalorieTarget();
                case 8 -> System.exit(0);
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void addBasicFood() {
        System.out.print("Enter food ID: ");
        String id = scanner.nextLine();
        
        System.out.print("Enter keywords (comma separated): ");
        String keywords = scanner.nextLine();
        
        System.out.print("Enter calories per serving: ");
        int calories = getIntInput();
        
        foodService.addBasicFood(id, List.of(keywords.split(",")), calories);
        System.out.println("Food added successfully!");
    }

    private void createCompositeFood() {
        System.out.print("Enter composite food ID: ");
        String id = scanner.nextLine();
        
        System.out.print("Enter keywords (comma separated): ");
        String keywords = scanner.nextLine();
        
        System.out.print("Enter ingredients (format: foodId:servings, ...): ");
        String ingredients = scanner.nextLine();
        
        try {
            Map<String, Double> ingredientMap = parseIngredients(ingredients);
            foodService.addCompositeFood(id, List.of(keywords.split(",")), ingredientMap);
            System.out.println("Composite food created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void logFoodConsumption() {
        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        
        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            System.out.print("Enter food ID: ");
            String foodId = scanner.nextLine();
            
            System.out.print("Enter number of servings: ");
            double servings = getDoubleInput();
            
            logService.logFood(date, foodId, servings);
            System.out.println("Food logged successfully!");
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    private void viewDailyLog() {
        System.out.print("Enter date to view (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        
        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            List<LogEntry> entries = logService.getDailyLog(date);
            
            if(entries.isEmpty()) {
                System.out.println("No entries for this date.");
                return;
            }
            
            System.out.println("\n--- Food Log for " + date + " ---");
            entries.forEach(entry -> 
                System.out.printf("- %s: %.1f servings%n", 
                    entry.getFoodId(), entry.getServings())
            );
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format. Please use yyyy-MM-dd.");
        }
    }

    private void undoLastAction() {
        if(logService.undo()) {
            System.out.println("Last action undone successfully.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    private void setUserProfile() {
        System.out.print("Enter gender (male/female): ");
        String gender = scanner.nextLine();
        
        System.out.print("Enter height (cm): ");
        double height = getDoubleInput();
        
        System.out.print("Enter weight (kg): ");
        double weight = getDoubleInput();
        
        System.out.print("Enter age: ");
        int age = getIntInput();
        
        System.out.print("Enter activity level (sedentary/light/moderate/active): ");
        String activityLevel = scanner.nextLine();
        
        profileService.updateProfile(gender, height, weight, age, activityLevel);
        System.out.println("Profile updated successfully!");
    }

    private void showCalorieTarget() {
        try {
            double target = profileService.calculateTargetCalories();
            System.out.printf("Your daily calorie target: %.1f calories%n", target);
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Helper methods
    private Map<String, Double> parseIngredients(String input) {
        Map<String, Double> ingredients = new HashMap<>();
        String[] pairs = input.split(",");
        
        for(String pair : pairs) {
            String[] parts = pair.trim().split(":");
            if(parts.length != 2) {
                throw new IllegalArgumentException("Invalid ingredient format: " + pair);
            }
            try {
                ingredients.put(parts[0].trim(), Double.valueOf(parts[1].trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in: " + pair);
            }
        }
        return ingredients;
    }

    private int getIntInput() {
        while(true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid integer: ");
            }
        }
    }

    private double getDoubleInput() {
        while(true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Please enter a valid value: ");
            }
        }
    }
}