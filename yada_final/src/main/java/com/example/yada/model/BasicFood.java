package com.example.yada.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicFood {
    private String id;
    private List<String> keywords;
    private int caloriesPerServing;
}