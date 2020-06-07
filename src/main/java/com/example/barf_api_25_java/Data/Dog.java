package com.example.barf_api_25_java.Data;

import com.example.barf_api_25_java.Types.ActivityType;
import com.example.barf_api_25_java.Types.BreedType;

public class Dog {
    int id;
    int age;
    float weight;
    String name;
    ActivityType activity;
    BreedType breedType;
    String stringImage;

    public Dog(int age, float weight, String name, ActivityType activity, BreedType breedType) {
        this.age = age;
        this.weight = weight;
        this.name = name;
        this.activity = activity;
        this.breedType = breedType;
    }

    public Dog(int id, int age, float weight, String name, ActivityType activity, BreedType breedType, String stringImage) {
        this.id = id;
        this.age = age;
        this.weight = weight;
        this.name = name;
        this.activity = activity;
        this.breedType = breedType;
        this.stringImage = stringImage;
    }

    public int getAge() {
        return age;
    }

    public float getWeight() {
        return weight;
    }

    public String getDogName() {
        return name;
    }

    public ActivityType getActivity() {
        return activity;
    }

    public BreedType getBreedType() {
        return breedType;
    }

    public int getId() {
        return id;
    }

    public String getStringImage() {
        return stringImage;
    }
}
