package com.example.barf_api_25_java.Activities.DogTab.ArchiveMeals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.barf_api_25_java.Activities.DogTab.MealListAdapter;
import com.example.barf_api_25_java.Activities.DogTab.MealListDataPump;
import com.example.barf_api_25_java.Data.MealDatabaseHelper;
import com.example.barf_api_25_java.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ArchiveMealsActivity extends AppCompatActivity {
    public static final String DOG_ID = "DOG_ID";

    private int dogId;
    private MealDatabaseHelper mealDatabaseHelper;
    private MealListDataPump mealListDataPump;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive_meals);

        dogId = getIntent().getIntExtra(DOG_ID, -1);
        expandableListView = findViewById(R.id.archiveMealListView);

        try {
            mealDatabaseHelper = new MealDatabaseHelper(ArchiveMealsActivity.this);
            mealListDataPump = new MealListDataPump(ArchiveMealsActivity.this, dogId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        expandableListDetail = mealListDataPump.getArchiveData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        Collections.sort(expandableListTitle, Collections.reverseOrder());

        expandableListAdapter = new MealListAdapter(ArchiveMealsActivity.this, expandableListTitle, expandableListDetail);
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
}