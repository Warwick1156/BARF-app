package com.example.barf_api_25_java.Activities.DogTab;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.barf_api_25_java.Data.Dog;
import com.example.barf_api_25_java.Data.DogDatabaseHelper;
import com.example.barf_api_25_java.Data.MealDatabaseHelper;
import com.example.barf_api_25_java.Data.SettingsDatabaseHelper;
import com.example.barf_api_25_java.Foods.MealPlan;
import com.example.barf_api_25_java.R;
import com.example.barf_api_25_java.Settings.Settings;
import com.example.barf_api_25_java.Utils.ImageUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.example.barf_api_25_java.Utils.ImageUtils.bitmapToString;
import static com.example.barf_api_25_java.Utils.ImageUtils.stringToBitmap;

public class DogMainTabActivity extends AppCompatActivity implements CreateMealPlanDialog.CreateMealPlanListener{

    public static final String DOG_ID = "DOG_ID";
    public static final int GALLERY_REQUEST = 0;

    private Button btnNewMeal;
    private Button btnEditPhoto;
    private Button btnArchive;
    private TextView tvDogName;
    private ImageView ivDogPhoto;

    private Dog dog;
    private DogDatabaseHelper dogDatabaseHelper;
    private Settings settings;
    private SettingsDatabaseHelper settingsDatabaseHelper;
    private MealPlan mealPlan;

    ExpandableListView expandableListView;
    ExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;

    MealDatabaseHelper mealDatabaseHelper;

    MealListDataPump mealListDataPump;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dog_main_tab);

        tvDogName = findViewById(R.id.tv_dogName);
        ivDogPhoto = findViewById(R.id.img_dogPhoto);

        try {
            dogDatabaseHelper = new DogDatabaseHelper(DogMainTabActivity.this);
            dog = dogDatabaseHelper.getDogFromId(getIntent().getIntExtra(DOG_ID, 0));
            settings = new Settings(DogMainTabActivity.this, dog.getId());
            mealListDataPump = new MealListDataPump(DogMainTabActivity.this, dog.getId());
            mealDatabaseHelper = new MealDatabaseHelper(DogMainTabActivity.this);
        } catch (IOException e) { e.printStackTrace(); }
        tvDogName.setText(dog.getDogName());

        byte[] photoArray = getPhoto();
        Bitmap photo = BitmapFactory.decodeByteArray(photoArray, 0, photoArray.length);
        ivDogPhoto.setImageBitmap(photo);


        btnNewMeal = findViewById(R.id.btn_NewMeal);
        btnNewMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMealPlanDialog();
            }
        });

        btnEditPhoto = findViewById(R.id.btn_changeImage);
        btnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.PickImage)), GALLERY_REQUEST);
            }
        });

        btnArchive = findViewById(R.id.btn_Archive);
        btnArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        expandableListView = (ExpandableListView) findViewById(R.id.mealListView);
        expandableListDetail = mealListDataPump.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        Collections.sort(expandableListTitle);
        expandableListAdapter = new MealListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Expanded.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        expandableListTitle.get(groupPosition) + " List Collapsed.",
                        Toast.LENGTH_SHORT).show();

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                Toast.makeText(
                        getApplicationContext(),
                        expandableListTitle.get(groupPosition)
                                + " -> "
                                + expandableListDetail.get(
                                expandableListTitle.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT
                ).show();
                return false;
            }
        });

//        loadDates();
    }

    public void createMealPlanDialog() {
        CreateMealPlanDialog createMealPlanDialog1 = new CreateMealPlanDialog();
        createMealPlanDialog1.show(getSupportFragmentManager(), "Create meal plan");
    }

    private void loadDates(){
        try {
            mealDatabaseHelper = new MealDatabaseHelper(DogMainTabActivity.this);
            mealDatabaseHelper.saveMealPlan(5, new MealPlan(2, 2, new ArrayList<>()));
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void newMealPlan(int mealsNo) throws ParseException {
        mealPlan = new MealPlan(mealsNo, settings.foodTargetWeight.getTargetWeight(), settings.mealProportions, settings.allowedFoods.getAllowedFoods());
        mealDatabaseHelper.saveMealPlan(dog.getId(), mealPlan);
    }

    private byte[] getPhoto() {
        byte[] photo;
        String stringPhoto = dog.getStringImage();
        if (stringPhoto == null) {
            photo = ImageUtils.getDefaultImage_asBit_array(ContextCompat.getDrawable(DogMainTabActivity.this, R.drawable.dog_silhouette));
        } else {
            photo = stringToBitmap(stringPhoto);
        }
        return photo;
    }

    public int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageData = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(imageData);
                Bitmap bitmapImage = BitmapFactory.decodeStream(imageStream);

                int dimension = getSquareCropDimensionForBitmap(bitmapImage);
                bitmapImage = ThumbnailUtils.extractThumbnail(bitmapImage, dimension, dimension);

                String stringImage = bitmapToString(bitmapImage);
                dog.setStringImage(stringImage);
                dogDatabaseHelper.setPhoto(dog.getId(), stringImage);
                ivDogPhoto.setImageBitmap(bitmapImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }
}