package com.example.barf_api_25_java.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.barf_api_25_java.Foods.Food;
import com.example.barf_api_25_java.Foods.Meal;
import com.example.barf_api_25_java.Foods.MealPlan;
import com.example.barf_api_25_java.Settings.Settings;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MealDatabaseHelper extends DataBaseHelper {
    public static final String MEAL_DATE = "MealDate";
    public static final String DOG_ID = "DogId";
    public static final String MEAL_ID = "MealId";
    public static final String AND = " AND ";

    public static final String NAME = "Name";
    public static final String ANIMAL = "Animal";
    public static final String FOOD_ID = "FoodId";
    public static final String PORTIONS = "Portions";
    public static final String PORTION_WEIGHT = "PortionWeight";

    public static final String MEAL_FOODS = "MEAL_FOODS";
    public static final String MEAL = "MEAL";
    public static final String DESCENDING = " DESC";

    public MealDatabaseHelper(@Nullable Context context) throws IOException {
        super(context);
    }

    public void saveMealPlan(int dogId, MealPlan mealPlan) throws ParseException {
        List<Meal> mealList = mealPlan.getMealList();
        Date day = getNextFreeDate(dogId);

        int mealsNo = mealList.size();
        for (int i = 0; i < mealsNo; i++) {
            String stringDate = dateToString(day);
            int mealId = saveMeal(stringDate, dogId);

            Meal meal = mealList.get(i);
            HashMap<Food, Integer> foods = meal.getMealFoods();
            foods.forEach((food, portion) -> saveFood(mealId, portion, food));

            day = addOneDay(day);
        }
    }

    private Date getNextFreeDate(int dogId) throws ParseException {
        Date nextFreeDate = Calendar.getInstance().getTime();
        List<String> mealDates = getMealDates(dogId);
        if (mealDates.size() != 0) {
            Date date = stringToDate(mealDates.get(0));
            nextFreeDate = addOneDay(date);
        }
        return nextFreeDate;
    }

    private Date addOneDay(Date date) {
        LocalDateTime nextDay = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        nextDay = nextDay.plusDays(1);
        return Date.from(nextDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<String> getMealDates(int dogId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<String> mealDates;

        Cursor cursor = db.query(MEAL, new String[]{MEAL_DATE}, DOG_ID + "=" + dogId, null, null, null, MEAL_DATE + DESCENDING);
        mealDates = getDatesFromCursor(cursor);

        cursor.close();
        db.close();
        return mealDates;
    }

    public List<String> getRelevantMealDates(int dogId) {
        List<String> mealDates;
        Date today = Calendar.getInstance().getTime();
        String stringToday = dateToString(today);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL, new String[]{MEAL_DATE}, DOG_ID + "=" + dogId + AND + MEAL_DATE + ">=" + "\'"+stringToday+"\'", null, null, null, MEAL_DATE + DESCENDING);
        mealDates = getDatesFromCursor(cursor);

        cursor.close();
        db.close();
        return mealDates;
    }

    public List<String> getArchiveMealDates(int dogId) {
        List<String> mealDates;
        Date today = Calendar.getInstance().getTime();
        String stringToday = dateToString(today);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL, new String[]{MEAL_DATE}, DOG_ID + "=" + dogId + AND + MEAL_DATE + "<" + "\'"+stringToday+"\'", null, null, null, MEAL_DATE + DESCENDING);
        mealDates = getDatesFromCursor(cursor);

        cursor.close();
        db.close();
        return mealDates;
    }

    private List<String> getDatesFromCursor(Cursor cursor) {
        List<String> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return result;
    }

    private Date stringToDate(String date) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(date);
    }

    private String dateToString(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        return year + "-" + month + "-" + day;
    }

    private int saveMeal(String date, int dogId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DOG_ID, dogId);
        cv.put(MEAL_DATE, date);

        long id = db.insert("MEAL", null, cv);
        db.close();

        return (int) id;
    }

    public int getMealId(int dogId, String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        int mealId = -1;
        Cursor cursor = db.query("MEAL", new String[]{MEAL_ID}, DOG_ID + "=" + dogId + AND + MEAL_DATE + "=" + "\'" + date + "\'", null, null, null, null);
        if (cursor.moveToFirst()) {
            mealId = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return mealId;
    }

    public List<Integer> getMealIds(int dogId) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> mealIds = new ArrayList<>();
        Cursor cursor = db.query("MEAL", new String[]{MEAL_ID}, DOG_ID + "=" + dogId, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                mealIds.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return mealIds;
    }

    public HashMap<Integer, Integer> getMealFoodData(int mealId) {
        HashMap<Integer, Integer> mealFoodsData = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_FOODS, new String[]{FOOD_ID, PORTIONS}, MEAL_ID + "=" + mealId, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                mealFoodsData.put(cursor.getInt(0), cursor.getInt(1));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return mealFoodsData;
    }

    private void saveFood(int mealId, int portions, Food food) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(MEAL_ID, mealId);
        cv.put(FOOD_ID, food.getId());
        cv.put(PORTIONS, portions);

        db.insert("MEAL_FOODS", null, cv);
        db.close();
    }

    public void removeOldMeals(int dogId) throws ParseException {
        Calendar today = Calendar.getInstance();
        List<String> dates = getMealDates(dogId);

        if (dates.size() != 0) {
            int i = 0;
            Date mealDate = stringToDate(dates.get(i));
            while (today.after(mealDate)) {
                try {
                    mealDate = stringToDate(dates.get(i));
                    int mealId = getMealId(dogId, dates.get(i));
                    removeMeal(mealId);
                    removeMealFoods(mealId);
                    i++;
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    private void removeMeal(int mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MEAL", "MealId=" + mealId, null);
        db.close();
    }

    private void removeMealFoods(int mealId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("MEAL_FOODS", "MealId=" + mealId, null);
        db.close();
    }

    public void deleteMealPlan(int dogId) {
        List<Integer> mealIds = getMealIds(dogId);
        mealIds.forEach(id -> {
            removeMealFoods(id);
            removeMeal(id);
        });
    }

    public List<String> getMealPlan(int dogId) {
        return getMealDates(dogId);
    }


}
