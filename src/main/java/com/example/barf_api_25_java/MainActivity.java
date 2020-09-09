package com.example.barf_api_25_java;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.nfc.Tag;
import android.os.Bundle;

import com.example.barf_api_25_java.Data.DataBaseHelper;
import com.example.barf_api_25_java.Data.Dog;
import com.example.barf_api_25_java.Data.DogDatabaseHelper;
import com.example.barf_api_25_java.Foods.Food;
import com.example.barf_api_25_java.Foods.Meal;
import com.example.barf_api_25_java.Foods.MealPlan;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.barf_api_25_java.Utils.ImageUtils.stringToBitmap;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<byte[]> images = new ArrayList<>();
    private ArrayList<Integer> ids = new ArrayList<>();

    RecyclerView dogsView;
    RecyclerViewAdapter dogsViewAdapter;

    DogDatabaseHelper dogDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(MainActivity.this, AddDogActivity.class);
                startActivity(intent);
            }
        });

        try {
            initDogsView();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity.this);
            List<Food> foodList = dataBaseHelper.getFoods("", "");
            //Meal meal = new Meal(600);
            //meal.createMeal(foodList);
            MealPlan mealPlan = new MealPlan(7, 600, foodList);
            mealPlan.createMealPlan();
            List<Meal> mealList = mealPlan.getMealList();
            System.out.println("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onResume() {
        super.onResume();
        try {
            List<Dog> dogs = loadDogs();
            clearDogViewData();
            getDogViewData(dogs);
            dogsViewAdapter.updateItemList(names, images, ids);
            dogsViewAdapter.notifyDataSetChanged();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<Dog> loadDogs() throws IOException {
        List<Dog> dogs = new ArrayList<>();
        try {
            dogDatabaseHelper = new DogDatabaseHelper(MainActivity.this);
            dogs = dogDatabaseHelper.getDogs(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dogs;
    }

    private void initDogsView() throws IOException {
        List<Dog> dogs = loadDogs();
        getDogViewData(dogs);
        dogsView = findViewById(R.id.dogs_view);
        dogsViewAdapter = new RecyclerViewAdapter(this, names, images, ids);
        dogsView.setLayoutManager(new LinearLayoutManager(this));
        dogsView.setAdapter(dogsViewAdapter);

        registerForContextMenu(dogsView);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        dogDatabaseHelper.removeDogById(item.getGroupId());
        int position = dogsViewAdapter.getPosition();
        int databaseId = dogsViewAdapter.getItemDatabaseId();

        dogDatabaseHelper.removeDogById(databaseId);
        dogsViewAdapter.removeItem(position);
        dogsViewAdapter.notifyItemRemoved(position);
        dogsViewAdapter.notifyItemRangeChanged(position, dogsViewAdapter.getItemCount());

        return super.onContextItemSelected(item);
    }

    private void getDogViewData(List<Dog> dogs) {
        byte[] defaultImage = getDefaultImage_asBit_array(ContextCompat.getDrawable(MainActivity.this, R.drawable.dog_silhouette));
        for (Dog dog : dogs) {
            names.add(dog.getDogName());
            ids.add(dog.getId());
            if (dog.getStringImage() == null) {
                images.add((defaultImage));
            } else {
                images.add(stringToBitmap(dog.getStringImage()));
            }
        }
    }

    private void clearDogViewData() {
        names.clear();
        images.clear();
        ids.clear();
    }

    private byte[] getDefaultImage_asBit_array(Drawable resource) {
        Bitmap bitmap = ((BitmapDrawable)resource).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }



}
