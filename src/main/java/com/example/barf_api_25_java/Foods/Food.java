package com.example.barf_api_25_java.Foods;

import com.example.barf_api_25_java.Types.FoodType;

import java.util.List;

public class Food {

    private int id;
    private String name;
    private String animal;
    private FoodType category;
    private float portion;
    private float bones;
    private List<Component> componentList;

    public Food(int id, String name, String animal, FoodType category, float portion, float bones, List<Component> componentList) {
        this.id = id;
        this.name = name;
        this.animal = animal;
        this.category = category;
        this.portion = portion;
        this.bones = bones;
        this.componentList = componentList;
    }

    public Food(){};

    public FoodType getCategory() {
        return category;
    }

    public float getPortion() {
        return portion;
    }

    public float getBones() {
        return bones;
    }

    public List<Component> getComponentList() {
        return componentList;
    }

    public String getFoodName() {
        return name;
    }

    public String getAnimal() {
        return animal;
    }

    public int getId() { return id; }
}
