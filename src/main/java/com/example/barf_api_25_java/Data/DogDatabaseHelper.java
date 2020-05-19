package com.example.barf_api_25_java.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.barf_api_25_java.Foods.BreedType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DogDatabaseHelper extends DataBaseHelper {
    public static final String NAME = "Name";
    public static final String AGE = "Age";
    public static final String WEIGHT = "Weight";
    public static final String BREED_TYPE = "BreedType";
    public static final String ACTIVITY_TYPE = "ActivityType";

    public DogDatabaseHelper(@Nullable Context context) throws IOException {
        super(context);
    }

    public void createDog(Dog dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(NAME, dog.getDogName());
        cv.put(AGE, dog.getAge());
        cv.put(WEIGHT, dog.getWeight());
        cv.put(BREED_TYPE, dog.getBreedType().toString());
        cv.put(ACTIVITY_TYPE, dog.getActivity().toString());

        db.insert("DOG", null, cv);
        db.close();
    }

    public void removeDog(Dog dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("DOG", "name=" + dog.getDogName(), null);
        db.close();
    }

    public List<Dog> getDogs(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Dog> dogs = new ArrayList<>();

        Cursor cursor = db.query("DOG", null, "name=" + name, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String dogName = cursor.getString(0);
                int dogAge = cursor.getInt(1);
                float dogWeight = cursor.getFloat(2);
                BreedType dogBreedType = BreedType.valueOf(cursor.getString(3).toUpperCase());
                ActivityType dogActivityType = ActivityType.valueOf(cursor.getString(4).toUpperCase());

                Dog dog = new Dog(dogAge, dogWeight, dogName, dogActivityType, dogBreedType);
                dogs.add(dog);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dogs;
    }

}
