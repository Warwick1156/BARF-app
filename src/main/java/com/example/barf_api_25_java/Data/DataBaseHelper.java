package com.example.barf_api_25_java.Data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.barf_api_25_java.Foods.Component;
import com.example.barf_api_25_java.Types.ComponentType;
import com.example.barf_api_25_java.Foods.Food;
import com.example.barf_api_25_java.Types.FoodType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "barf.db";
    public static String DB_PATH = "data/data/com.example.barf_api_25_java/databases/";
    private Context context;
    private SQLiteDatabase db;

    public DataBaseHelper(@Nullable Context context) throws IOException {
        super(context, DB_NAME, null, 1);
        this.context = context;

        String path = DB_PATH + DB_NAME;
        if (checkFileExistence(path)) {
            openDataBase();
        }
        else {
            copyDatabase();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void copyDatabase() throws IOException {
        InputStream inputStream = context.getAssets().open(DB_NAME);
        String path = DB_PATH + DB_NAME;

        File file = new File(path);
        file.getParentFile().mkdirs();
        OutputStream outputStream = new FileOutputStream(path, false);

        byte[] buffer = new byte[1024];
        while (inputStream.read(buffer) > 0) {
            outputStream.write(buffer);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public boolean checkFileExistence(String path) {
        File file = new File(path);
        return file.exists();
    }

    public void openDataBase() {
        String dbPath = context.getDatabasePath(DB_NAME).getPath();
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        }
        catch (Exception e) {
            System.out.println("Error occurred during opening database");
        }
    }

    public void closeDatabase() {
        try {
            db.close();
        }
        catch (Exception e) {
            System.out.println("Error occurred during closing database");
        }
    }

    public List<Food> getFoods(String name, String type) {
        List<Food> foodList = new ArrayList<>();

        String query = "SELECT * FROM FOOD";
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query("FOOD", null, null, null, null, null, null);
        // Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                List<Component> componentList = new ArrayList<>();
                String foodName = cursor.getString(0);
                String foodAnimal = cursor.getString(1);
                FoodType foodType = FoodType.valueOf(cursor.getString(2).toUpperCase());
                float foodPortion = cursor.getFloat(3);
                float foodBones = cursor.getFloat(4);

                for (int i = 5; i < cursor.getColumnCount(); i++) {
                    ComponentType id = Component.get_Id(cursor.getColumnName(i));
                    float value = cursor.getFloat(i);

                    Component component = new Component(id, value);
                    componentList.add(component);
                }

                Food food = new Food(foodName, foodAnimal, foodType, foodPortion, foodBones, componentList);
                foodList.add(food);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return foodList;
    }
}
