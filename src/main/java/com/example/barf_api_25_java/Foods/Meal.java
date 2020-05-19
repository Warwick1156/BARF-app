package com.example.barf_api_25_java.Foods;

import com.example.barf_api_25_java.Settings.MealProportions;
import com.example.barf_api_25_java.Utils.ApproximateItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Meal {

    private int targetWeight;
    private EnumMap<FoodType, Float> foodProportions;

    private HashMap<Food, Integer> mealFoods;
    private EnumMap<FoodType, Float> mealCategoryTargetWeights;
    private EnumMap<FoodType, Float> mealCategoryActualWeights;
    private EnumMap<ComponentType, Float> mealComponents; // Not used yet
    private EnumMap<FoodType, Float> mealError;

    public Meal(int targetWeight) {
        this.targetWeight = targetWeight;
        this.foodProportions = new MealProportions().getProportions();
        mealCategoryTargetWeights = splitTotalWeight(foodProportions, targetWeight);
    }

    public Meal(int targetWeight, EnumMap<FoodType, Float> foodProportions) {
        this.targetWeight = targetWeight;
        this.foodProportions = foodProportions;
        mealCategoryTargetWeights = splitTotalWeight(foodProportions, targetWeight);
    }

    public Meal(EnumMap<FoodType, Float> mealCategoryTargetWeights) {
        this.mealCategoryTargetWeights = mealCategoryTargetWeights;
    }

    public Meal() {

    }

    public HashMap<Food, Integer> getMealFoods() {
        return mealFoods;
    }

    public EnumMap<FoodType, Float> getMealError() {
        return mealError;
    }

    public void createMeal(List<Food> foodList) {
        EnumMap<FoodType, List<Food>> foodMap = separateCategories(foodList);
        mealFoods = chooseFoods(foodMap, mealCategoryTargetWeights);
        calculateTotals();
        calculateError();
    }

    public EnumMap splitTotalWeight(EnumMap<FoodType, Float> proportions, int target) {
        EnumMap<FoodType, Float> splited = new EnumMap<FoodType, Float>(FoodType.class);
        for (FoodType type : FoodType.values()) {
            float value = target * proportions.get(type).floatValue();
            Float newValue = new Float(value);
            splited.put(type, newValue);
        }

        return splited;
    }

    private EnumMap separateCategories(List<Food> allFoods) {
        EnumMap<FoodType, List<Food>> separated = new EnumMap<FoodType, List<Food>>(FoodType.class);
        for (FoodType type : FoodType.values()) {
            List<Food> food = allFoods.stream()
                    .filter(x -> type.equals(x.getCategory()))
                    .collect(Collectors.toList());

            separated.put(type, food);
        }

        return separated;
    }

    private HashMap<Food, Integer> chooseFoods(EnumMap<FoodType, List<Food>> foods, EnumMap<FoodType, Float> globalConstraints) {
        List<HashMap<Food, Integer>> result = new ArrayList<>();
        EnumMap<FoodType, Float> localConstraints = globalConstraints.clone();
        for (FoodType type : FoodType.values()) {
            HashMap<Food, Integer> partResult = knapsack_approximate(foods.get(type), null, localConstraints.get(type), type);
            result.add(partResult);
            if (type.equals(FoodType.BONE)) {
                List<Food> separated = separateMeatFromBone(partResult);
                localConstraints = correctMeatConstrains(separated, localConstraints);
            }
        }

        HashMap<Food, Integer> mapResult = mergeHashMapList(result);
        return mapResult;
    }

    // TODO: Refactor.
    // If target is null add random foods till constraint is filled. Else choose foods maximising target value.
    private HashMap<Food, Integer> knapsack_approximate(List<Food> items, ComponentType target, float constraint, FoodType type) {
        List<ApproximateItem> approxItems = new ArrayList<>();
        int n = items.size();

        for (int i = 0; i < n; i++) {
            Food food = items.get(i);
            float foodWeight;
            if (type.equals(FoodType.BONE)) {
                foodWeight = food.getPortion() * food.getBones();
            }
            else {
                foodWeight = food.getPortion();
            }
            if (target == null) {
                approxItems.add(new ApproximateItem(0, foodWeight, i));
            } else {
                List<Component> foodComponentList = food.getComponentList();
                Component targetComponent = foodComponentList.stream()
                        .filter(component -> target.equals(component.get_Id()))
                        .findAny()
                        .orElse(null);

                approxItems.add(new ApproximateItem(targetComponent.getValue(), foodWeight, i));
            }
        }

        if (target == null) {
            Collections.shuffle(approxItems);
        } else {
            approxItems.sort(Comparator.comparing(ApproximateItem::getNormalizedValue));
            Collections.reverse(approxItems);
        }

        int[] selections = new int[n];
        Arrays.fill(selections, 0);
        float w = constraint;
        float total = 0;

        for (int i = 0; i < n; i++) {

            ApproximateItem item = approxItems.get(i);
            if (w == 0) break;

            int numAdd = (int) Math.floor(w / item.getWeight());

            if (numAdd > 0) {
                selections[item.getIndex()] += numAdd;
                w -= numAdd * item.getWeight();
                // total += numAdd * foodRatio * item.getValue();
            }
        }

        HashMap<Food, Integer> result = new HashMap<>();
        for (int i = 0; i < n; i++) {
            if (selections[i] != 0) {
                result.put(items.get(i), selections[i]);
            }
        }
        return result;
    }

    private List<Food> separateMeatFromBone(HashMap<Food, Integer> boneMeat) {
        List<Food> result = new ArrayList<>();
        for (HashMap.Entry<Food, Integer> foodKey : boneMeat.entrySet()) {
            Food food = foodKey.getKey();
            float newPortion = food.getPortion() * (1 - food.getBones()) * foodKey.getValue();
            Food dummy = new Food(null, null, FoodType.MEAT, newPortion, 0, food.getComponentList());
            result.add(dummy);
        }

        return result;
    }

    private EnumMap<FoodType, Float> correctMeatConstrains(List<Food> boneMeat, EnumMap<FoodType, Float> constraints) {
        float meatWeight = (float) boneMeat.stream()
                .filter(food -> food.getCategory().equals(FoodType.MEAT)).mapToDouble(Food::getPortion).sum();

        float newMeatConstrain = constraints.get(FoodType.MEAT).floatValue() - meatWeight;
        constraints.remove(FoodType.MEAT);
        constraints.put(FoodType.MEAT, new Float(newMeatConstrain));

        return constraints;
    }

    private HashMap<Food, Integer> mergeHashMapList(List<HashMap<Food, Integer>> hashMapList) {
        for (int i = 1; i < hashMapList.size(); i++) {
            hashMapList.get(0).putAll(hashMapList.get(i));
        }
        return hashMapList.get(0);
    }

    // TODO: refactor?
    private void calculateTotals() {
        mealCategoryActualWeights = new EnumMap<FoodType, Float>(FoodType.class);
        List<Food> foods = new ArrayList<>(mealFoods.keySet());

        float bone = 0, meat = 0, offal = 0;
        for (Food food : foods) {
            switch (food.getCategory()) {
                case OFFAL:
                    offal += food.getPortion() * mealFoods.get(food);
                    break;
                case MEAT:
                    meat += food.getPortion() * mealFoods.get(food);
                    break;
                case BONE:
                    bone += food.getPortion() * food.getBones() * mealFoods.get(food);
                    meat += food.getPortion() * (1 - food.getBones()) * mealFoods.get(food);
                    break;
            }
        }
        mealCategoryActualWeights.put(FoodType.BONE, bone);
        mealCategoryActualWeights.put(FoodType.MEAT, meat);
        mealCategoryActualWeights.put(FoodType.OFFAL, offal);
    }

    private void calculateError() {
        mealError = new EnumMap<FoodType, Float>(FoodType.class);
        for (FoodType type : FoodType.values()) {
            float error = mealCategoryTargetWeights.get(type) - mealCategoryActualWeights.get(type);
            mealError.put(type, error);
        }
    }

}
