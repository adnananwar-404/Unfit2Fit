package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.unfit2fit.unfit2fit.models.Goal;
import com.unfit2fit.unfit2fit.wrappers.GoalsListWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GoalsActivity extends AppCompatActivity {

    private static final String TAG = "GoalsActivity";

    BottomNavigationView bottomNavigationView;
    ListView listView_goals;
    ArrayList<Goal> itemsList;
    FloatingActionButton floatingActionButton;
    GoalsContextAdapter goalAdapter;

    private String userID;

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;
    ProgressDialog progressDialog;
    private GoalsListWrapper wrapper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.goals);

        floatingActionButton = findViewById(R.id.goals_floatingActionButton);

        listView_goals = findViewById(R.id.goals_listView);

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();

        userID = myAuth.getCurrentUser().getUid();

        itemsList = new ArrayList<Goal>();
        goalAdapter = new GoalsContextAdapter(itemsList,GoalsActivity.this);

        listView_goals.setAdapter(goalAdapter);



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.goals:
                        return true;
                    case R.id.measurements:
                        startActivity(new Intent(GoalsActivity.this, MeasurementActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(GoalsActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.stats:
                        startActivity(new Intent(GoalsActivity.this, StatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(GoalsActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        loadData();
        goalAdapter.notifyDataSetChanged();
        //Toast.makeText(GoalsActivity.this, itemsList.size(), Toast.LENGTH_SHORT).show();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog d = new Dialog(GoalsActivity.this);
                d.setContentView(R.layout.goals_fab_item);

                final EditText et_current = d.findViewById(R.id.goals_editText_current);
                final EditText et_target = d.findViewById(R.id.goals_editText_target);
                final EditText et_goal = d.findViewById(R.id.goals_editText_goal);
                Button add_goal = d.findViewById(R.id.goals_button_addGoal);

                add_goal.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(isGoalValid(et_goal, et_current, et_target)){

                            String goal = et_goal.getText().toString();
                            String current = et_current.getText().toString();
                            String target = et_target.getText().toString();
                            Goal g = new Goal(goal, current, target);

                            itemsList.add(g);
                            goalAdapter.notifyDataSetChanged();
                            addToDataBase();


                        }
                        else{
                            Toast.makeText(GoalsActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                        }

                        d.dismiss();

                    }
                });
                d.show();
            }
        });
    }


    private void loadData()
    {
        //wrapper = new GoalsListWrapper();

        DocumentReference df = myDatabase.collection("goals").document(userID);

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
                    GoalsListWrapper tempWrapper =  new GoalsListWrapper();
                    tempWrapper  = value.toObject(GoalsListWrapper.class);

                    wrapper = tempWrapper;
                    itemsList = wrapper.getGoalsList();

                    loadListView(tempWrapper);

                }
                else
                {
                    Toast.makeText(GoalsActivity.this, "Add some goals", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void loadListView(GoalsListWrapper wrap)
    {
        goalAdapter = new GoalsContextAdapter(itemsList,GoalsActivity.this);
        listView_goals.setAdapter(goalAdapter);
    }



    private void addToDataBase(){

        DocumentReference documentReference = myDatabase.collection("goals").document(userID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot !=null)
                    {
                        // Add Object
                        wrapper = new GoalsListWrapper(itemsList);
                        myDatabase.collection("goals").document(userID).set(wrapper);
                    }else {
                        wrapper = new GoalsListWrapper(itemsList);

                        documentReference.set(wrapper).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(GoalsActivity.this, "Goal Stored", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(GoalsActivity.this, "Goal Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private boolean isGoalValid(EditText goal, EditText current, EditText target){

        boolean isValid = false;

        String s_goal = goal.getText().toString().trim();
        String s_current = current.getText().toString().trim();
        String s_target = target.getText().toString().trim();

        if(TextUtils.isEmpty(s_goal)){
            goal.setError("Goal cannot be empty!");
            goal.requestFocus();
            isValid = false;
        }
        else if(TextUtils.isEmpty(s_current)){
            current.setError("Field cannot be empty!");
            current.requestFocus();
            isValid = false;
        }
        else if(TextUtils.isEmpty(s_target)){
            target.setError("Field cannot be empty!");
            target.requestFocus();
            isValid = false;
        }
        else{
            isValid = true;
        }

        return isValid;
    }

}