package com.example.barf_api_25_java.Activities.DogTab.Settings.AllowFoods;

import android.content.Context;

import com.example.barf_api_25_java.Data.FoodDatabaseHelper;
import com.example.barf_api_25_java.Utils.Entry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodListDataAdapter {
    private FoodDatabaseHelper foodDatabaseHelper;

    public FoodListDataAdapter(Context context) throws IOException {
        foodDatabaseHelper = new FoodDatabaseHelper(context);
    }
    public HashMap<Integer, Boolean> convert(HashMap<String, List<Entry<String, Boolean>>> data) {
        HashMap<Integer, Boolean> resultMap = new HashMap<>();

        new ArrayList<String>(data.keySet()).forEach(animal -> {
            data.get(animal).forEach(name -> {
                int id = foodDatabaseHelper.getId(animal, name.getKey());
                resultMap.put(id, name.getValue());
            });
        });

        return resultMap;
    }
}
