package com.example.barf_api_25_java.Settings;

import com.example.barf_api_25_java.Foods.FoodType;

import java.util.EnumMap;

public class MealProportions {
    private EnumMap<FoodType, Float> proportions;

    public MealProportions() {
        this.proportions = new EnumMap<FoodType, Float>(FoodType.class);
        proportions.put(FoodType.MEAT, new Float(0.6));
        proportions.put(FoodType.OFFAL, new Float(0.25));
        proportions.put(FoodType.BONE, new Float(0.15));
    }

    public MealProportions(EnumMap<FoodType, Float> proportions) {
        this.proportions = proportions;
    }

    public EnumMap<FoodType, Float> getProportions() {
        return proportions;
    }
}
