package com.example.barf_api_25_java.Utils;

public class ApproximateItem {
    private float value;
    private float weight;
    private float normalizedValue;
    private int index;

    public ApproximateItem(float value, float weight, int index) {
        this.value = value;
        this.weight = weight;
        this.index = index;

        this.normalizedValue = value/weight;
    }

    public float getValue() {
        return value;
    }

    public float getWeight() {
        return weight;
    }

    public float getNormalizedValue() {
        return normalizedValue;
    }

    public int getIndex() {
        return index;
    }
}
