package com.example.barf_api_25_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.barf_api_25_java.Data.Dog;
import com.example.barf_api_25_java.Data.DogDatabaseHelper;
import com.example.barf_api_25_java.Types.ActivityType;
import com.example.barf_api_25_java.Types.BreedType;

public class AddDogActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button btn_add;
    EditText et_name, et_age, et_weight;
    Spinner breedTypeSpinner, dogActivitySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dog);

        breedTypeSpinner = (Spinner) findViewById(R.id.spinner_BreedType);
        ArrayAdapter<CharSequence> breedTypeAdapter = ArrayAdapter.createFromResource(this, R.array.breed_types, android.R.layout.simple_spinner_dropdown_item);
        breedTypeSpinner.setAdapter(breedTypeAdapter);

        dogActivitySpinner = (Spinner) findViewById(R.id.spinner_ActivityType);
        ArrayAdapter<CharSequence> dogActivityAdapter = ArrayAdapter.createFromResource(this, R.array.dog_activity_type, android.R.layout.simple_spinner_dropdown_item);
        dogActivitySpinner.setAdapter(dogActivityAdapter);

        btn_add = findViewById(R.id.btn_AddDog);
        et_name = findViewById(R.id.et_DogName);
        et_age = findViewById(R.id.et_DogAge);
        et_weight = findViewById(R.id.et_DogWeight);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Dog dog = new Dog(
                            Integer.parseInt(et_age.getText().toString()),
                            Float.parseFloat(et_weight.getText().toString()),
                            et_name.getText().toString(),
                            ActivityType.valueOf(dogActivitySpinner.getSelectedItem().toString().toUpperCase()),
                            BreedType.valueOf(breedTypeSpinner.getSelectedItem().toString().toUpperCase()));

                    DogDatabaseHelper dogDatabaseHelper = new DogDatabaseHelper(AddDogActivity.this);
                    dogDatabaseHelper.createDog(dog);

                    finish();
                } catch (Exception e) {
                    Toast.makeText(AddDogActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
