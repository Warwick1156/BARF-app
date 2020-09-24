package com.example.barf_api_25_java.Settings;

import com.example.barf_api_25_java.Types.ActivityType;
import com.example.barf_api_25_java.Types.BreedType;

public class FoodTargetWeight {
    private int targetWeight;

    public FoodTargetWeight(float dogWeight, ActivityType activityType, BreedType breedType) {
        this.targetWeight = calculateFoodTargetWeight(dogWeight, activityType, breedType);
    }

    public FoodTargetWeight(int targetWeight) {
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

    private int calculateFoodTargetWeight(float dogWeight, ActivityType activityType, BreedType breedType) {
        double activityMultiplier = getMultiplierFromActivityType(activityType);
        double breedMultiplier = getMultiplierFromBreedType(breedType);
        return (int) ((dogWeight * 1000) * activityMultiplier * breedMultiplier);
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

    private double getMultiplierFromBreedType(BreedType breedType) {
        double multiplier = 0;
        switch (breedType) {
            case MINIATURE:
                multiplier = 2;
                break;
            case SMALL:
                multiplier = 1.5;
                break;
            case MEDIUM:
                multiplier = 1;
                break;
            case LARGE:
                multiplier = 1;
                break;
            case HUGE:
                multiplier = 0.5;
                break;
        }
        return multiplier;
    }

}
