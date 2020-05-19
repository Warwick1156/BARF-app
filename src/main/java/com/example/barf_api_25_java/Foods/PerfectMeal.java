package com.example.barf_api_25_java.Foods;

import com.example.barf_api_25_java.Settings.MealProportions;
import com.example.barf_api_25_java.Types.FoodType;

import java.util.EnumMap;

public class PerfectMeal extends Meal {

    private EnumMap<FoodType, Float>baseTarget;
    private EnumMap<FoodType, Float>categoryTargetMap;
    private MealProportions proportions;

    public PerfectMeal(int targetWeight) {
        this.proportions = new MealProportions();
        this.baseTarget = splitTotalWeight(proportions.getProportions(), targetWeight);
        this.categoryTargetMap = baseTarget;
    }

    public void update(EnumMap<FoodType, Float> errorMap) {
        categoryTargetMap = new EnumMap<FoodType, Float>(FoodType.class);
        for (FoodType type : FoodType.values()) {
            float newValue = errorMap.get(type).floatValue() + baseTarget.get(type).floatValue();
            categoryTargetMap.put(type, newValue);
        }
    }

    public EnumMap<FoodType, Float> getCategoryTargetMap() {
        return categoryTargetMap;
    }
}
