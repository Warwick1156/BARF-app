package com.example.barf_api_25_java.Activities.DogTab.Settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.barf_api_25_java.Activities.DogTab.Settings.AllowFoods.AllowFoodsActivity;
import com.example.barf_api_25_java.Data.Dog;
import com.example.barf_api_25_java.Data.DogDatabaseHelper;
import com.example.barf_api_25_java.R;
import com.example.barf_api_25_java.Settings.Settings;
import com.example.barf_api_25_java.Types.ActivityType;
import com.example.barf_api_25_java.Types.FoodType;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {
    public static final String DOG_ID = "DOG_ID";

    private int dogId;
    private Dog dog;
    private DogDatabaseHelper dogDatabaseHelper;

    private Settings settings;

    EditText etTargetWeight;

    SeekBar seekBarMeat;
    SeekBar seekBarOffal;
    SeekBar seekBarBone;

    TextView tvMeatPercentage;
    TextView tvBonePercentage;
    TextView tvOffalPercentage;

    Spinner dogActivitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dogId = getIntent().getIntExtra(DOG_ID, -1);
        try {
            settings = new Settings(SettingsActivity.this, dogId);
            dogDatabaseHelper = new DogDatabaseHelper(SettingsActivity.this);
            dog = dogDatabaseHelper.getDogFromId(dogId);
        } catch (IOException e) { e.printStackTrace(); }

        etTargetWeight = findViewById(R.id.teTargetWeight);
        etTargetWeight.setText(Integer.toString(settings.foodTargetWeight.getTargetWeight()));

        setUpSeekBars();
        seekBarsSetValue(settings);
        setUpSeekBarsTextView();
        setUpSpinner();

        seekBarMeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvMeatPercentage.setText(Integer.toString(seekBarMeat.getProgress()) + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarBone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvBonePercentage.setText(Integer.toString(seekBarBone.getProgress()) + "%");
        }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        seekBarOffal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvOffalPercentage.setText(Integer.toString(seekBarOffal.getProgress()) + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.foodTargetWeight.setTargetWeight(Integer.parseInt(etTargetWeight.getText().toString()));
                normalizeSeekBars();
                settings.mealProportions.update(FoodType.MEAT, ((float) seekBarMeat.getProgress()/100));
                settings.mealProportions.update(FoodType.BONE, ((float) seekBarBone.getProgress()/100));
                settings.mealProportions.update(FoodType.OFFAL, ((float) seekBarOffal.getProgress()/100));
                settings.saveSettings();

                dogDatabaseHelper.updateActivity(dogId, dogActivitySpinner.getSelectedItem().toString());

                finish();
            }
        });

        Button btnEditAllowed = findViewById(R.id.btnEditAllowedFoods);
        btnEditAllowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, AllowFoodsActivity.class);
                intent.putExtra(DOG_ID, dogId);
                startActivity(intent);
            }
        });

        Button btnResetProportions = findViewById(R.id.btnReserProportions);
        btnResetProportions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.resetMealProportions();
                seekBarsSetValue(settings);
                setUpSeekBarsTextView();
            }
        });

        Button btnResetTargetWeight = findViewById(R.id.btnRecalculate);
        btnResetTargetWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.newTargetWeight(dog.getWeight(), ActivityType.valueOf(dogActivitySpinner.getSelectedItem().toString().toUpperCase()), dog.getBreedType());
                setTargetWeight();

            }
        });

    }

    private void normalizeSeekBars() {
        int meat = seekBarMeat.getProgress();
        int bone = seekBarBone.getProgress();
        int offal = seekBarOffal.getProgress();

        float total = meat + bone + offal;

        seekBarMeat.setProgress((int)(meat/total * 100));
        seekBarBone.setProgress((int)(bone/total * 100));
        seekBarOffal.setProgress((int)(offal/total * 100));
    }

    private void setUpSeekBars() {
        seekBarMeat = findViewById(R.id.seekBarMeat);
        seekBarOffal = findViewById(R.id.seekBarOffal);
        seekBarBone = findViewById(R.id.seekBarBone);

        seekBarBone.setMax(100);
        seekBarMeat.setMax(100);
        seekBarOffal.setMax(100);
    }

    private void seekBarsSetValue(Settings settings) {
        seekBarMeat.setProgress((int) (settings.mealProportions.getMeat() * 100));
        seekBarOffal.setProgress((int) (settings.mealProportions.getOffal() * 100));
        seekBarBone.setProgress((int) (settings.mealProportions.getBone() * 100));
    }

    private void updateSeekBarTextView() {
        tvMeatPercentage.setText(Integer.toString((int) (settings.mealProportions.getMeat() * 100)) + "%");
        tvOffalPercentage.setText(Integer.toString((int) (settings.mealProportions.getOffal() * 100)) + "%");
        tvBonePercentage.setText(Integer.toString((int) (settings.mealProportions.getBone() * 100)) + "%");
    }

    private void setUpSeekBarsTextView() {
        tvMeatPercentage = findViewById(R.id.tvMeatPercentage);
        tvBonePercentage = findViewById(R.id.tvBonePercentage);
        tvOffalPercentage = findViewById(R.id.tvOffalPercentage);
        updateSeekBarTextView();
    }

    private void setUpSpinner() {
        dogActivitySpinner = findViewById(R.id.spinnerActivityType);
        ArrayAdapter<CharSequence> dogActivityAdapter = ArrayAdapter.createFromResource(this, R.array.dog_activity_type, android.R.layout.simple_spinner_dropdown_item);
        dogActivitySpinner.setAdapter(dogActivityAdapter);
        ActivityType activity = dog.getActivity();
        dogActivitySpinner.setSelection(activity.ordinal());
    }

    private void setTargetWeight() {
        etTargetWeight.setText(Integer.toString(settings.foodTargetWeight.getTargetWeight()));
    }
}