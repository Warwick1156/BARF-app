package com.example.barf_api_25_java.Foods;

import java.util.ArrayList;
import java.util.List;

public class MealPlan {
    int mealsNo;
    int targetWeight;
    List<Food> foodList;

    List<Meal> mealList;

    public MealPlan (int mealsNo, int targetWeight, List<Food> foodList) {
        this.mealsNo = mealsNo;
        this.targetWeight = targetWeight;
        this.foodList = foodList;
    }

    public void createMealPlan() {
        mealList = new ArrayList<>();
        PerfectMeal nextMeal = new PerfectMeal(targetWeight);
        for (int i = 0; i < mealsNo; i++) {
            Meal meal = new Meal(nextMeal.getCategoryTargetMap());
            meal.createMeal(foodList);
            nextMeal.update(meal.getMealError());
            mealList.add(meal);
        }
    }

    public List<Meal> getMealList() {
        return mealList;
    }
}
