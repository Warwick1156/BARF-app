package com.example.barf_api_25_java.Settings;

import android.content.Context;

import com.example.barf_api_25_java.Data.FoodDatabaseHelper;
import com.example.barf_api_25_java.Data.SettingsDatabaseHelper;
import com.example.barf_api_25_java.Foods.Food;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AllowedFoods {
    public static final String TRUE = "1";
    public  static final String FALSE = "0";

    private List<Integer> foodIdsList;
    private List<Food> allowedFoods;
    private int dogId;
    private FoodDatabaseHelper foodDatabaseHelper;
    private SettingsDatabaseHelper settingsDatabaseHelper;

    public AllowedFoods(Context context, int dogId) throws IOException {
        this.dogId = dogId;
        foodDatabaseHelper = new FoodDatabaseHelper(context);
        settingsDatabaseHelper = new SettingsDatabaseHelper(context);
    }

    public void allowAll() {
        List<Integer> foodIds = foodDatabaseHelper.getFoodsId();
        for (int id : foodIds) {
            settingsDatabaseHelper.setAllowment(dogId, id, TRUE);
        }
    }

    private List<Food> loadAllowedFoods() {
        List<Integer> foodIds = settingsDatabaseHelper.getAllowedFoodsIds(dogId);
        allowedFoods = foodDatabaseHelper.getFoods(foodIds);
        return allowedFoods;
    }

    public List<Food> getAllowedFoods() {
        if (allowedFoods == null) {
            allowedFoods = loadAllowedFoods();
        }
        return allowedFoods;
    }

}
