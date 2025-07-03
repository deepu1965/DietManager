package com.example.yada.config;

import com.example.yada.service.calorie.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalorieConfig {
    @Bean(name = "harrisBenedict")
    public CalorieCalculator harrisBenedictCalculator() {
        return new HarrisBenedictCalculator();
    }

    @Bean(name = "mifflinStJeor")
    public CalorieCalculator mifflinStJeorCalculator() {
        return new MifflinStJeorCalculator();
    }
}