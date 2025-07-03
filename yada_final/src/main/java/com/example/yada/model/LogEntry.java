package com.example.yada.model;

import java.util.Objects;

import lombok.Data;

@Data
public class LogEntry {  // Now a top-level public class
    private String foodId;
    private double servings;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogEntry logEntry = (LogEntry) o;
        return Double.compare(logEntry.servings, servings) == 0 &&
                Objects.equals(foodId, logEntry.foodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foodId, servings);
    }
}