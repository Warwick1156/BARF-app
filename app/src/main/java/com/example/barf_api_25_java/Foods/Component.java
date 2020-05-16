package com.example.barf_api_25_java.Foods;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Component {
    @NonNull ComponentType componentType;
    float value;
    String unit;

    public Component(ComponentType componentType, float value) {
        this.componentType = componentType;
        this.value = value;
    }

    public static ComponentType getId(String name) {
        return ComponentType.valueOf(name.toUpperCase());
    }

}
