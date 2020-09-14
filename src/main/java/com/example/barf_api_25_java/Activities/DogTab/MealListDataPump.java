package com.example.barf_api_25_java.Activities.DogTab;

import android.content.Context;

import com.example.barf_api_25_java.Data.FoodDatabaseHelper;
import com.example.barf_api_25_java.Data.MealDatabaseHelper;
import com.example.barf_api_25_java.Foods.Food;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MealListDataPump {
    private MealDatabaseHelper mealDatabaseHelper;
    private FoodDatabaseHelper foodDatabaseHelper;
    private int dogId;

    MealListDataPump(Context context, int dogId) throws IOException {
        this.dogId = dogId;
        mealDatabaseHelper = new MealDatabaseHelper(context);
        foodDatabaseHelper = new FoodDatabaseHelper(context);
    }

//    public static HashMap<String, List<String>> getData() {
//        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
//
//        List<String> cricket = new ArrayList<String>();
//        cricket.add("India");
//        cricket.add("Pakistan");
//        cricket.add("Australia");
//        cricket.add("England");
//        cricket.add("South Africa");
//
//        List<String> football = new ArrayList<String>();
//        football.add("Brazil");
//        football.add("Spain");
//        football.add("Germany");
//        football.add("Netherlands");
//        football.add("Italy");
//
//        List<String> basketball = new ArrayList<String>();
//        basketball.add("United States");
//        basketball.add("Spain");
//        basketball.add("Argentina");
//        basketball.add("France");
//        basketball.add("Russia");
//
//        expandableListDetail.put("CRICKET TEAMS", cricket);
//        expandableListDetail.put("FOOTBALL TEAMS", football);
//        expandableListDetail.put("BASKETBALL TEAMS", basketball);
//        return expandableListDetail;
//    }

    public HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableList = new HashMap<String, List<String>>();

        List<String> mealDates = mealDatabaseHelper.getMealDates(dogId);
        mealDates.forEach(date -> {
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
        mealFoods.forEach((food, portions) -> {mealContent.add(food.getFoodName() + " " + portions.toString() + "x" + food.getPortion());});

        return mealContent;
    }

    private String getMealTitle(String stringDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date date = simpleDateFormat.parse(stringDate);
        simpleDateFormat.applyPattern("d MMM, E");

        return simpleDateFormat.format(date);
    }
}