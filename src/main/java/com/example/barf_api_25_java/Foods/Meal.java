package com.example.barf_api_25_java.Foods;

import java.util.EnumMap;
import java.util.List;

public class Meal {
    private float minimalFoodWeight = 50;
    private float foodRatio;
    private int foodWeight;

    private float[] requirements;
    private List<Food> mealFoods;
    private float[] error;

    public Meal(int foodWeight, float[] requirements) {
        this.foodWeight = foodWeight;
        this.requirements = requirements;

        this.foodRatio = minimalFoodWeight / 100;
        createMeal();
    }

    public float[] getRequirements() {
        return requirements;
    }

    public List<Food> getMealFoods() {
        return mealFoods;
    }

    public float[] getError() {
        return error;
    }

    private float[] createMeal() {

        return error;
    }
}
