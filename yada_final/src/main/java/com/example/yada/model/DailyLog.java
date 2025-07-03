package com.example.yada.model;

import lombok.Data;
import java.time.LocalDate;
import java.util.*;

@Data
public class DailyLog {
    private Map<LocalDate, List<LogEntry>> entries = new HashMap<>();
}
