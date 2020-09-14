package com.example.barf_api_25_java.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import com.example.barf_api_25_java.Types.ActivityType;
import com.example.barf_api_25_java.Types.BreedType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DogDatabaseHelper extends DataBaseHelper {
    public static final String NAME = "Name";
    public static final String AGE = "Age";
    public static final String WEIGHT = "Weight";
    public static final String BREED_TYPE = "BreedType";
    public static final String ACTIVITY_TYPE = "ActivityType";
    public static final String IMAGE = "Image";

    public DogDatabaseHelper(@Nullable Context context) throws IOException {
        super(context);
    }

    public int createDog(Dog dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert("DOG", null, createDogContentValues(dog));
        db.close();

        return (int) id;
    }

    private ContentValues createDogContentValues(Dog dog) {
        ContentValues cv = new ContentValues();
        cv.put(NAME, dog.getDogName());
        cv.put(AGE, dog.getAge());
        cv.put(WEIGHT, dog.getWeight());
        cv.put(BREED_TYPE, dog.getBreedType().toString());
        cv.put(ACTIVITY_TYPE, dog.getActivity().toString());
        return cv;
    }

    public void removeDog(Dog dog) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("DOG", "id=" + dog.getId(), null);
        db.close();
    }

    public void removeDogById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("DOG", "id=" + id, null);
        db.close();
    }

    public List<Dog> getDogs(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Dog> dogs = new ArrayList<>();

        Cursor cursor;
        if (name == null) {
            cursor = db.rawQuery("SELECT * FROM DOG", null);
        } else {
            cursor = db.query("DOG", null, "name=" + name, null, null, null, null);
        }
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String dogName = cursor.getString(1);
                int dogAge = cursor.getInt(2);
                float dogWeight = cursor.getFloat(3);
                BreedType dogBreedType = BreedType.valueOf(cursor.getString(4).toUpperCase());
                ActivityType dogActivityType = ActivityType.valueOf(cursor.getString(5).toUpperCase());
                String stringImage = cursor.getString(6);

                Dog dog = new Dog(id, dogAge, dogWeight, dogName, dogActivityType, dogBreedType, stringImage);
                dogs.add(dog);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dogs;
    }

    public Dog getDogFromId(int id) {
        Dog dog = new Dog();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("DOG", null, "Id=" + id, null, null, null, null);
        if (cursor.moveToFirst()) {
            String dogName = cursor.getString(1);
            int dogAge = cursor.getInt(2);
            float dogWeight = cursor.getFloat(3);
            BreedType dogBreedType = BreedType.valueOf(cursor.getString(4).toUpperCase());
            ActivityType dogActivityType = ActivityType.valueOf(cursor.getString(5).toUpperCase());
            String stringImage = cursor.getString(6);
            dog = new Dog(id, dogAge, dogWeight, dogName, dogActivityType, dogBreedType, stringImage);
        }
        cursor.close();
        db.close();
        return dog;
    }

    public void setPhoto(int dogId, String image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(IMAGE, image);
        db.update("DOG", cv, "Id=" + dogId, null);

        db.close();
    }

}
