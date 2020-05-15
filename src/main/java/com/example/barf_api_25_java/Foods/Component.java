package com.example.barf_api_25_java.Foods;

public class Component {
    private Id id;
    private float value;
    private String unit;

    public Component(Id id, float value, String unit) {
        this.id = id;
        this.value = value;
        this.unit = unit;
    }

    public Component(Id id, float value) {
        this.id = id;
        this.value = value;
    }

    public enum Id {
        ENERGY, PROTEIN, FAT, CALCIUM, PHOSPHORUS, POTASSIUM, SODIUM, MAGNESIUM, COPPER, IRON,
        MANGANESE, ZINC, SELENIUM, IODINE, A, D, K, E, B2, B3, B4, B5, B6, B7, B12, EPA, DHA,
        OMEGA3
    }

    public static Id getId(String name) {
        return Id.valueOf(name.toUpperCase());
    }


    public float getValue() {
        return value;
    }

    public String getUnit() {
        return unit;
    }
}
