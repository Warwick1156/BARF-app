package com.example.barf_api_25_java.Settings;

import com.example.barf_api_25_java.Types.FoodType;

import java.util.EnumMap;

public class MealProportions {
    private EnumMap<FoodType, Float> proportions;

    public MealProportions() {
        this.proportions = new EnumMap<FoodType, Float>(FoodType.class);
        proportions.put(FoodType.MEAT, (float) 0.6);
        proportions.put(FoodType.OFFAL, (float) 0.25);
        proportions.put(FoodType.BONE, (float) 0.15);
    }

    public MealProportions(EnumMap<FoodType, Float> proportions) {
        this.proportions = proportions;
    }

    public MealProportions(float meat, float bones, float offal) {
        this.proportions = new EnumMap<FoodType, Float>(FoodType.class);
        proportions.put(FoodType.MEAT, meat);
        proportions.put(FoodType.BONE, bones);
        proportions.put(FoodType.OFFAL, offal);
    }

    public EnumMap<FoodType, Float> getProportions() {
        return proportions;
    }

    public float getMeat() { return proportions.get(FoodType.MEAT); }
    public float getBone() { return proportions.get(FoodType.BONE); }
    public float getOffal() { return proportions.get(FoodType.OFFAL); }
}


