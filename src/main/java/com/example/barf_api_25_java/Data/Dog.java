package com.example.barf_api_25_java.Data;

import com.example.barf_api_25_java.Foods.BreedType;

public class Dog {
    int age;
    float weight;
    String name;
    ActivityType activity;
    BreedType breedType;

    public Dog(int age, float weight, String name, ActivityType activity, BreedType breedType) {
        this.age = age;
        this.weight = weight;
        this.name = name;
        this.activity = activity;
        this.breedType = breedType;
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
}
