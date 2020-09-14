package com.example.barf_api_25_java.Settings;

import android.content.Context;

import com.example.barf_api_25_java.Data.DataBaseHelper;
import com.example.barf_api_25_java.Data.SettingsDatabaseHelper;
import com.example.barf_api_25_java.Foods.Food;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    private int dogId;
    public FoodTargetWeight foodTargetWeight;
    public MealProportions mealProportions;
    public AllowedFoods allowedFoods;
    private SettingsDatabaseHelper settingsDatabaseHelper;


    public Settings(Context context, int dogId) throws IOException {
        this.dogId = dogId;
        this.settingsDatabaseHelper = new SettingsDatabaseHelper(context);

        foodTargetWeight = settingsDatabaseHelper.getFoodTargetWeight(dogId);
        mealProportions = settingsDatabaseHelper.getMealProportions(dogId);

        allowedFoods = new AllowedFoods(context, dogId);
    }

    public Settings(Context context, int dogId, FoodTargetWeight foodTargetWeight, MealProportions mealProportions) throws IOException {
        this.dogId = dogId;
        this.settingsDatabaseHelper = new SettingsDatabaseHelper(context);
        this.mealProportions = mealProportions;
        this.foodTargetWeight = foodTargetWeight;

        allowedFoods = new AllowedFoods(context, dogId);
    }

    public void saveSettings() {
        settingsDatabaseHelper.saveFoodTargetWeight(dogId, foodTargetWeight.getTargetWeight());
        settingsDatabaseHelper.saveMealProportions(dogId, mealProportions);
    }

    public List<Food> getAllowedFoodsList() {
        return new ArrayList<>();
    }

    public List<Integer> getAllowedFoodsIds() {
        return settingsDatabaseHelper.getAllowedFoodsIds(dogId);
    }

//    public void saveDefaultSettings(int dogId) {
//        foodTargetWeight = new FoodTargetWeight();
//    }

}
