package com.example.barf_api_25_java.Activities.DogTab.Settings.AllowFoods;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.example.barf_api_25_java.Activities.DogTab.ArchiveMeals.ArchiveMealsActivity;
import com.example.barf_api_25_java.Activities.DogTab.MealListAdapter;
import com.example.barf_api_25_java.Activities.DogTab.MealListDataPump;
import com.example.barf_api_25_java.Data.FoodDatabaseHelper;
import com.example.barf_api_25_java.Data.SettingsDatabaseHelper;
import com.example.barf_api_25_java.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AllowFoodsActivity extends AppCompatActivity {
    public static final String DOG_ID = "DOG_ID";

    private AllowFoodsListDataPump allowFoodsListDataPump;
    private SettingsDatabaseHelper settingsDatabaseHelper;
    private FoodDatabaseHelper foodDatabaseHelper;
    private int dogId;

    ExpandableListView expandableListView;
//    ExpandableListAdapter expandableListAdapter;
    AllowFoodsListAdapter expandableListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allow_foods);

        expandableListView = findViewById(R.id.allowFoodsListView);

        try {
            allowFoodsListDataPump = new AllowFoodsListDataPump(AllowFoodsActivity.this);
            settingsDatabaseHelper = new SettingsDatabaseHelper(AllowFoodsActivity.this);
            foodDatabaseHelper = new FoodDatabaseHelper(AllowFoodsActivity.this);
            dogId = getIntent().getIntExtra(DOG_ID, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        expandableListAdapter = new AllowFoodsListAdapter(AllowFoodsActivity.this, allowFoodsListDataPump.getData(dogId));
        expandableListView.setAdapter(expandableListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HashMap<Integer, Boolean> data = new HashMap<>();
        try {
            FoodListDataAdapter foodListDataAdapter = new FoodListDataAdapter(AllowFoodsActivity.this);
            data = foodListDataAdapter.convert(expandableListAdapter.getData());
        } catch (IOException e) { e.printStackTrace(); }
        settingsDatabaseHelper.setAllowment(dogId, data);
    }

    private HashMap<Integer, Boolean> matchFoods(List<Integer> foodsIds, HashMap<Integer, Boolean> allowedFoodsMap) {
        foodsIds.forEach(id -> {
            if (!allowedFoodsMap.containsKey(id)) allowedFoodsMap.put(id, false);
        });
        return allowedFoodsMap;
    }
}