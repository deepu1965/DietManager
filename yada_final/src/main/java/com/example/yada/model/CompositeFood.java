package com.example.yada.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.List;

@Data
@NoArgsConstructor // Add this
@AllArgsConstructor
public class CompositeFood {
    private String id;
    private List<String> keywords;
    private Map<String, Double> ingredients; // FoodID -> Servings
}