package com.unfit2fit.unfit2fit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.androidplot.xy.Step;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.unfit2fit.unfit2fit.models.Steps;
import com.unfit2fit.unfit2fit.models.User;
import com.unfit2fit.unfit2fit.wrappers.GoalsListWrapper;
import com.unfit2fit.unfit2fit.wrappers.MeasurementsListWrapper;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Objects;

public class BiHourlyStepsAlarm extends BroadcastReceiver {

    private static final String TAG = "Alarm24Hour";

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;
    private String userID;
    Steps steps;
    private boolean isObjectExist;

    @Override
    public void onReceive(Context context, Intent intent) {

        String stepsTaken = intent.getStringExtra("steps");
        steps = new Steps();

        if (steps.equals(null)) {
            Toast.makeText(context, "Steps is Null", Toast.LENGTH_SHORT).show();
            return;
        }

        loadData(stepsTaken, context);
    }



    private void loadData(String stepsTaken, Context context)
    {

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        userID = myAuth.getCurrentUser().getUid();

        DocumentReference df = myDatabase.collection("step_counter").document(userID);

        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d("GoalsActivity", "Listen Failed. ", error);
                    return;
                }

                if(value != null && value.exists())
                {
                    Steps tempSteps = new Steps();
                    tempSteps = value.toObject(Steps.class);

                    if(tempSteps.getDailyBiHourlySteps() != null)
                    {
                        steps = tempSteps;
                    }

                    processObject(stepsTaken, context, steps);

                }
                else
                {
                    processObject(stepsTaken, context, steps);
                }
            }
        });

    }

    private void processObject(String stepsTaken, Context context, Steps steps)
    {
        ArrayList<Integer> stepsList;
        Toast.makeText(context, steps.getDailyBiHourlySteps().size() + " " + String.valueOf(steps == null), Toast.LENGTH_SHORT).show();

        if(steps.getDailyBiHourlySteps().isEmpty()) {

            stepsList = new ArrayList<>();
            Toast.makeText(context, "set", Toast.LENGTH_SHORT).show();

        }
        else {
            stepsList = steps.getDailyBiHourlySteps();
            Toast.makeText(context, "update", Toast.LENGTH_SHORT).show();

        }

        stepsList.add(Integer.valueOf(stepsTaken));
        steps.setDailyBiHourlySteps(stepsList);

        updateData(context, steps);
    }



    private void updateData(Context context, Steps steps) {


        myDatabase.collection("step_counter").document(userID).set(steps).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(context, "Goal Stored", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
