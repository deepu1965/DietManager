package com.example.yada.service;

import com.example.yada.model.LogEntry;
import com.example.yada.repository.JsonDataStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LogService {
    @Autowired private JsonDataStorage storage;
    @Autowired private FoodService foodService;
    private final Deque<Runnable> undoStack = new ArrayDeque<>();

    public void logFood(LocalDate date, String foodId, double servings) {
        // Validate input
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null!");
        }
        if (foodId == null || foodId.trim().isEmpty()) {
            throw new IllegalArgumentException("Food ID cannot be empty!");
        }
        if (servings <= 0) {
            throw new IllegalArgumentException("Servings must be positive!");
        }
        if (!foodService.foodExists(foodId)) {
            throw new IllegalArgumentException("Food with ID '" + foodId + "' does not exist!");
        }
        
        LogEntry entry = new LogEntry();
        entry.setFoodId(foodId.trim());
        entry.setServings(servings);
        
        List<LogEntry> dailyEntries = storage.getDailyLog().getEntries()
            .computeIfAbsent(date, k -> new ArrayList<>());
        
        dailyEntries.add(entry);
        
        // Add undo operation
        undoStack.push(() -> dailyEntries.remove(entry));
        
        storage.saveDailyLogs();
    }

    public List<LogEntry> getDailyLog(LocalDate date) {
        if (date == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(storage.getDailyLog().getEntries().getOrDefault(date, Collections.emptyList()));
    }
    
    public boolean removeLogEntry(LocalDate date, String foodId, double servings) {
        List<LogEntry> entries = storage.getDailyLog().getEntries().get(date);
        if (entries == null) {
            return false;
        }
        
        LogEntry toRemove = new LogEntry();
        toRemove.setFoodId(foodId);
        toRemove.setServings(servings);
        
        boolean removed = entries.remove(toRemove);
        if (removed) {
            // Add undo operation to restore the entry
            undoStack.push(() -> entries.add(toRemove));
            storage.saveDailyLogs();
        }
        return removed;
    }
    
    public boolean clearDailyLog(LocalDate date) {
        List<LogEntry> entries = storage.getDailyLog().getEntries().get(date);
        if (entries == null || entries.isEmpty()) {
            return false;
        }
        
        // Store copy for undo
        List<LogEntry> entriesCopy = new ArrayList<>(entries);
        entries.clear();
        
        // Add undo operation
        undoStack.push(() -> storage.getDailyLog().getEntries().put(date, entriesCopy));
        
        storage.saveDailyLogs();
        return true;
    }
    
    public int calculateDailyCalories(LocalDate date) {
        List<LogEntry> entries = getDailyLog(date);
        return entries.stream()
                .mapToInt(entry -> foodService.calculateCalories(entry.getFoodId(), entry.getServings()))
                .sum();
    }
    
    public Map<String, Integer> getWeeklyCalories(LocalDate startDate) {
        Map<String, Integer> weeklyCalories = new LinkedHashMap<>();
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            int calories = calculateDailyCalories(date);
            weeklyCalories.put(date.toString(), calories);
        }
        
        return weeklyCalories;
    }
    
    public Map<String, Double> getFoodFrequency(LocalDate startDate, LocalDate endDate) {
        Map<String, Double> frequency = new HashMap<>();
        
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            List<LogEntry> entries = getDailyLog(current);
            for (LogEntry entry : entries) {
                frequency.merge(entry.getFoodId(), entry.getServings(), Double::sum);
            }
            current = current.plusDays(1);
        }
        
        return frequency.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    public List<LocalDate> getDatesWithLogs() {
        return storage.getDailyLog().getEntries().keySet().stream()
                .filter(date -> !storage.getDailyLog().getEntries().get(date).isEmpty())
                .sorted()
                .collect(Collectors.toList());
    }
    
    public int getTotalLoggedDays() {
        return (int) storage.getDailyLog().getEntries().entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .count();
    }

    public boolean undo() {
        if (!undoStack.isEmpty()) {
            undoStack.pop().run();
            storage.saveDailyLogs();
            return true;
        }
        return false;
    }
    
    public int getUndoStackSize() {
        return undoStack.size();
    }
    
    public void clearUndoStack() {
        undoStack.clear();
    }
}