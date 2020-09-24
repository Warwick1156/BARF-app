package com.example.barf_api_25_java.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.barf_api_25_java.Foods.Component;
import com.example.barf_api_25_java.Foods.Food;
import com.example.barf_api_25_java.Types.ComponentType;
import com.example.barf_api_25_java.Types.FoodType;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodDatabaseHelper extends DataBaseHelper {
    public static final String FOOD = "FOOD";
    public static final String ID = "Id";
    public static final String ANIMAL = "Animal";
    public static final String NAME = "Name";
    public static final boolean DISTINCT = true;
    public static final String AND = " AND ";

    public FoodDatabaseHelper(@Nullable Context context) throws IOException {
        super(context);
    }

    public List<Food> getFoods() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD, null, null, null, null, null, null);
        List<Food> foodList = getFoodListFromCursor(cursor);

        cursor.close();
        db.close();

        return foodList;
    }

    // TODO: Handle ids.size() == 0
    public List<Food> getFoods(List<Integer> ids) {
        String[] stringIds = idListToStringArray(ids);

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + FOOD + " WHERE " + ID + " IN (" + makePlaceholders(stringIds.length) + ")";
        Cursor cursor = db.rawQuery(query, stringIds);
        List<Food> foodList = getFoodListFromCursor(cursor);

        cursor.close();
        db.close();

        return foodList;
    }

    public Food getFoodFromCursor(Cursor cursor) {
        List<Component> componentList = new ArrayList<>();
        int foodId = cursor.getInt(0);
        String foodName = cursor.getString(1);
        String foodAnimal = cursor.getString(2);
        FoodType foodType = FoodType.valueOf(cursor.getString(3).toUpperCase());
        float foodPortion = cursor.getFloat(4);
        float foodBones = cursor.getFloat(5);

        for (int i = 6; i < cursor.getColumnCount(); i++) {
            ComponentType id = Component.get_Id(cursor.getColumnName(i));
            float value = cursor.getFloat(i);

            Component component = new Component(id, value);
            componentList.add(component);
        }

        return new Food(foodId, foodName, foodAnimal, foodType, foodPortion, foodBones, componentList);
    }

    public List<Food> getFoodListFromCursor(Cursor cursor) {
        List<Food> foodList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                Food food = getFoodFromCursor(cursor);
                foodList.add(food);
            } while (cursor.moveToNext());
        }
        return foodList;
    }

    public Food getFoodById(int id) {
        Food food = new Food();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD, null, ID + "=" + id, null, null, null, null);

        if (cursor.moveToFirst()) {
            food = getFoodFromCursor(cursor);
        }

        cursor.close();
        db.close();

        return food;
    }

    public List<Integer> getFoodsIds() {
        List<Integer> ids = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD, new String[]{ID}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return ids;
    }

    public List<String> getAnimals() {
        List<String> animals;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DISTINCT, FOOD, new String[]{ANIMAL}, null, null, null, null, null, null);
        animals = getSingleStringListFromCursor(cursor);
        cursor.close();
        db.close();

        return animals;
    }

    public List<String> getNamesByAnimal(String animal) {
        List<String> names;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD, new String[]{NAME}, ANIMAL + "=" + "'"+animal+"'", null, null, null, null);
        names = getSingleStringListFromCursor(cursor);
        cursor.close();
        db.close();

        return names;
    }

    public HashMap<Integer, String> getNamesAndIDsByAnimal(String animal) {
        HashMap<Integer, String> map = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD, new String[]{NAME, ID}, ANIMAL + "=" + "'"+animal+"'", null, null, null, null);
//        names = getSingleStringListFromCursor(cursor);
        if (cursor.moveToFirst()) {
            do {
                map.put(cursor.getInt(1), cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return map;
    }

    private List<String> getSingleStringListFromCursor(Cursor cursor) {
        List<String> result = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                result.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return result;
    }

    private String[] idListToStringArray(List<Integer> ids) {
        String[] stringArray = new String[ids.size()];

        int i = 0;
        for (int id : ids) {
            stringArray[i] = Integer.toString(id);
            i++;
        }
        return stringArray;
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public int getId(String animal, String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(FOOD, new String[]{ID}, ANIMAL + "=" + "'"+animal+"'" + AND + NAME + "=" + "'"+name+"'", null, null, null, null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        cursor.close();
        db.close();
        return result;
    }
}
