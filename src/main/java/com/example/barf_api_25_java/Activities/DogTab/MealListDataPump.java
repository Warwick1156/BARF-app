package com.example.barf_api_25_java.Activities.DogTab;

import android.content.Context;

import com.example.barf_api_25_java.Data.FoodDatabaseHelper;
import com.example.barf_api_25_java.Data.MealDatabaseHelper;
import com.example.barf_api_25_java.Foods.Food;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MealListDataPump {
    private MealDatabaseHelper mealDatabaseHelper;
    private FoodDatabaseHelper foodDatabaseHelper;
    private int dogId;

    public MealListDataPump(Context context, int dogId) throws IOException {
        this.dogId = dogId;
        mealDatabaseHelper = new MealDatabaseHelper(context);
        foodDatabaseHelper = new FoodDatabaseHelper(context);
    }

    public HashMap<String, List<String>> getRelevantData() {
        HashMap<String, List<String>> expandableList = new HashMap<String, List<String>>();

//        List<String> mealDates = mealDatabaseHelper.getMealDates(dogId);
        List<String> mealDates = mealDatabaseHelper.getRelevantMealDates(dogId);
        mealDates.forEach(date -> {
            try { expandableList.put(getMealTitle(date), getMealContent(date));
            } catch (ParseException e) { e.printStackTrace(); }
        });

        return expandableList;
    }

    public HashMap<String, List<String>> getArchiveData() {
        List<String> mealDates = mealDatabaseHelper.getArchiveMealDates(dogId);
        return getDataFromDates(mealDates);
    }

    private HashMap<String, List<String>> getDataFromDates(List<String> dates) {
        HashMap<String, List<String>> expandableList = new HashMap<String, List<String>>();
        dates.forEach(date -> {
            try { expandableList.put(getMealTitle(date), getMealContent(date));
            } catch (ParseException e) { e.printStackTrace(); }
        });

        return expandableList;
    }

    private List<String> getMealContent(String stringDate) {
        int mealId = mealDatabaseHelper.getMealId(dogId, stringDate);
        HashMap<Integer, Integer> mealFoodsData = mealDatabaseHelper.getMealFoodData(mealId);

        HashMap<Food, Integer> mealFoods =  new HashMap<>();
        mealFoodsData.forEach((foodId, portions) -> {mealFoods.put(foodDatabaseHelper.getFoodById(foodId), portions);});

        List<String> mealContent = new ArrayList<>();
        mealFoods.forEach((food, portions) -> {mealContent.add(food.getAnimal() + ", " + food.getFoodName() + " " + portions.toString() + "x" + (int) food.getPortion() + "g");});
        Collections.sort(mealContent);

        return mealContent;
    }

    private String getMealTitle(String stringDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = simpleDateFormat.parse(stringDate);
        simpleDateFormat.applyPattern("d MMM, E");

        return simpleDateFormat.format(date);
    }
}