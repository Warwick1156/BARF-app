package com.example.barf_api_25_java.Foods;

import com.example.barf_api_25_java.Types.ComponentType;

public class Component {
    private ComponentType id;
    private float value;
    private String unit;

    public Component(ComponentType id, float value, String unit) {
        this.id = id;
        this.value = value;
        this.unit = unit;
    }

    public Component(ComponentType id, float value) {
        this.id = id;
        this.value = value;
    }

    public static ComponentType get_Id(String name) {
        return ComponentType.valueOf(name.toUpperCase());
    }

    public ComponentType get_Id() {
        return id;
    }


    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
