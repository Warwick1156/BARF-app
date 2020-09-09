package com.example.barf_api_25_java.Settings;

import com.example.barf_api_25_java.Types.ActivityType;

public class FoodTargetWeight {
    private int targetWeight;

    FoodTargetWeight(float dogWeight, ActivityType activityType) {
        this.targetWeight = calculateFoodTargetWeight(dogWeight, activityType);
    }

    FoodTargetWeight(int targetWeight) {
        this.targetWeight = targetWeight;
    }

    public int getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(int targetWeight) {
        if (targetWeight > 0) {
            this.targetWeight = targetWeight;
        }
    }

    private int calculateFoodTargetWeight(float dogWeight, ActivityType activityType) {
        double activityMultiplier = getMultiplierFromActivityType(activityType);
        return (int) ((dogWeight * 1000) * activityMultiplier);
    }

    private double getMultiplierFromActivityType(ActivityType activityType) {
        double multiplier = 0;
        switch (activityType) {
            case LOW:
                multiplier = 0.01;
                break;
            case MEDIUM:
                multiplier = 0.02;
                break;
            case HIGH:
                multiplier = 0.03;
                break;
        }
        return multiplier;
    }

}
