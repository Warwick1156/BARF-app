package com.example.barf_api_25_java.Foods;

import java.util.List;

public class Food {
    private String name;
    private String animal;
    private FoodType category;
    private float portion;
    private float bones;
    private List<Component> componentList;

    public Food(String name, String animal, FoodType category, float portion, float bones, List<Component> componentList) {
        this.name = name;
        this.animal = animal;
        this.category = category;
        this.portion = portion;
        this.bones = bones;
        this.componentList = componentList;
    }

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
}
