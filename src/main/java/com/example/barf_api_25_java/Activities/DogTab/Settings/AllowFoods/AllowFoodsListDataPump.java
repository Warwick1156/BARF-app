package com.example.barf_api_25_java.Activities.DogTab.Settings.AllowFoods;

import android.content.Context;

import com.example.barf_api_25_java.Data.FoodDatabaseHelper;
import com.example.barf_api_25_java.Data.SettingsDatabaseHelper;
import com.example.barf_api_25_java.Utils.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllowFoodsListDataPump {
    private FoodDatabaseHelper foodDatabaseHelper;
    private SettingsDatabaseHelper settingsDatabaseHelper;

    public AllowFoodsListDataPump(Context context) throws IOException {
        foodDatabaseHelper = new FoodDatabaseHelper(context);
        settingsDatabaseHelper = new SettingsDatabaseHelper(context);
    }

    public HashMap<String, List<Entry<String, Boolean>>> getData(int dogId) {
        HashMap<String, List<Entry<String, Boolean>>> expandableList = new HashMap<>();
        List<String> animalNames = foodDatabaseHelper.getAnimals();
        animalNames.forEach(animal -> {
            HashMap<Integer, String> namesIdsMap = foodDatabaseHelper.getNamesAndIDsByAnimal(animal);
            HashMap<Integer, Boolean> foodIdAllowMap = settingsDatabaseHelper.getAllowedFoodMap(dogId);

            List<Entry<String, Boolean>> entryList = new ArrayList<>();
            namesIdsMap.forEach((foodId, name) -> {
                entryList.add(new Entry<>(name, foodIdAllowMap.get(foodId) == null ? false : foodIdAllowMap.get(foodId)));
            });
            expandableList.put(animal, entryList);
        });
        return expandableList;
    }
}
