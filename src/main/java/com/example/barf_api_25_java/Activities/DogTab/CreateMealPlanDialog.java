package com.example.barf_api_25_java.Activities.DogTab;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.barf_api_25_java.R;

import java.text.ParseException;

public class CreateMealPlanDialog extends AppCompatDialogFragment {
    Spinner mealsNoSpinner;
    CreateMealPlanListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.create_meal_plan_dialog, null);

        builder.setView(view)
                .setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int mealsNo = Integer.parseInt(mealsNoSpinner.getSelectedItem().toString());
                        try {
                            listener.newMealPlan(mealsNo);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

        mealsNoSpinner = view.findViewById(R.id.spinner_mealsNo);
        ArrayAdapter<CharSequence> mealsNoAdapter = ArrayAdapter.createFromResource(view.getContext(), R.array.meals_No, android.R.layout.simple_spinner_dropdown_item);
        mealsNoSpinner.setAdapter(mealsNoAdapter);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (CreateMealPlanListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CreateMealPlanListener {
        void newMealPlan(int mealsNo) throws ParseException;
    }
}
