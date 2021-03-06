package com.example.barf_api_25_java.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.barf_api_25_java.Settings.FoodTargetWeight;
import com.example.barf_api_25_java.Settings.MealProportions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SettingsDatabaseHelper extends DataBaseHelper {
    public static final String MEAL_PROPORTIONS = "MEAL_PROPORTIONS";
    public static final String DOG_ID = "DogId";
    public static final String MEAT = "Meat";
    public static final String BONE = "Bone";
    public static final String OFFAL = "Offal";

    public static final String MEAL_TARGET_WEIGHT = "MEAL_TARGET_WEIGHT";
    public static final String TARGET_WEIGHT = "TargetWeight";

    public static final String ALLOWED_FOODS = "ALLOWED_FOODS";
    public static final String FOOD_ID = "FoodId";
    public static final String ALLOWED = "Allowed";

    public static final String AND = " AND ";
    public static final String TRUE = "1";

    public SettingsDatabaseHelper(@Nullable Context context) throws IOException {
        super(context);
    }

    public void saveFoodTargetWeight(int dogId, int target) {
        deleteFoodTargetWeight(dogId);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DOG_ID, dogId);
        contentValues.put(TARGET_WEIGHT, target);

        db.insert(MEAL_TARGET_WEIGHT, null, contentValues);
        db.close();
    }

    public FoodTargetWeight getFoodTargetWeight(int dogId) {
        int targetWeight = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_TARGET_WEIGHT, null, DOG_ID + "=" + dogId, null, null, null, null);
        if (cursor.moveToFirst()) {
            targetWeight = cursor.getInt(1);
        }

        return new FoodTargetWeight(targetWeight);
    }

    private void deleteFoodTargetWeight(int dogId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MEAL_TARGET_WEIGHT, DOG_ID + "=" + dogId, null);
        db.close();
    }

    public void saveMealProportions(int dogId, MealProportions mealProportions) {
        deleteMealProportions(dogId);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DOG_ID, dogId);
        contentValues.put(MEAT, mealProportions.getMeat());
        contentValues.put(BONE, mealProportions.getBone());
        contentValues.put(OFFAL, mealProportions.getOffal());

        db.insert(MEAL_PROPORTIONS, null, contentValues);
    }

    public MealProportions getMealProportions(int dogId) {
        float meat = 0;
        float bones = 0;
        float offal = 0;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MEAL_PROPORTIONS, null, DOG_ID + "=" + dogId, null, null, null, null);
        if (cursor.moveToFirst()) {
            meat = cursor.getFloat(1);
            bones = cursor.getFloat(2);
            offal = cursor.getFloat(3);
        }
        cursor.close();
        db.close();

        return new MealProportions(meat, bones, offal);
    }

    private void deleteMealProportions(int dogId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(MEAL_PROPORTIONS, DOG_ID + "=" + dogId, null);
        db.close();
    }

    public List<Integer> getAllowedFoodsIds(int dogId) {
        List<Integer> foodIds = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ALLOWED_FOODS, new String[]{FOOD_ID}, DOG_ID + "=" + dogId + AND + ALLOWED + "=" + TRUE, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                foodIds.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return foodIds;
    }

    private void deleteAllowedFoodsRecords(int dogId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(ALLOWED_FOODS, DOG_ID + "=" + dogId, null);
        db.close();
    }

    public void setAllowment(int dogId, int foodId, String allow) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DOG_ID, dogId);
        cv.put(FOOD_ID, foodId);
        cv.put(ALLOWED, allow);

        db.insert(ALLOWED_FOODS, null, cv);
        db.close();
    }

    public void deleteSettings(int dogId) {
        deleteFoodTargetWeight(dogId);
        deleteMealProportions(dogId);
        deleteAllowedFoodsRecords(dogId);
    }

    public HashMap<Integer, Boolean> getAllowedFoodMap(int dogId) {
        HashMap<Integer, Boolean> allowedFoods = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ALLOWED_FOODS, new String[]{FOOD_ID, ALLOWED}, DOG_ID + "=" + dogId, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                allowedFoods.put(cursor.getInt(0), cursor.getInt(1) > 0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return allowedFoods;
    }

    public void setAllowment(int dogId, HashMap<Integer, Boolean> map) {
        deleteAllowedFoodsRecords(dogId);
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            new ArrayList<Integer>(map.keySet()).forEach(foodId -> {
                ContentValues cv = new ContentValues();
                cv.put(DOG_ID, dogId);
                cv.put(FOOD_ID, foodId);
                cv.put(ALLOWED, map.get(foodId) ? 1 : 0);

                db.insert(ALLOWED_FOODS, null, cv);
            });
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        db.close();
    }
}
