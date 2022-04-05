package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.unfit2fit.unfit2fit.adapters.MeasurementAdapter;
import com.unfit2fit.unfit2fit.models.Measurement;
import com.unfit2fit.unfit2fit.wrappers.MeasurementsListWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MeasurementActivity extends AppCompatActivity {

    private static final String TAG = "MeasurementActivity";

    BottomNavigationView bottomNavigationView;
    ListView listView_measurements;
    FloatingActionButton floatingActionButton;
    MeasurementAdapter measurementAdapter;

    ArrayList<Measurement> weightList, waistList, stepsList, chestList, calorieIntakeList, itemsList;

    List<String> spinnerItems;

    private String userID, category, met;

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;
    private MeasurementsListWrapper wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measurement);

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.measurements);

        floatingActionButton = findViewById(R.id.measurements_floatingActionButton);

        spinnerItems = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.goals_DL)));


        listView_measurements = findViewById(R.id.measurements_listView);

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();

        userID = myAuth.getCurrentUser().getUid();


        itemsList = new ArrayList<Measurement>();
        weightList = new ArrayList<Measurement>();
        waistList = new ArrayList<Measurement>();
        stepsList = new ArrayList<Measurement>();
        calorieIntakeList = new ArrayList<Measurement>();
        chestList = new ArrayList<Measurement>();

        measurementAdapter = new MeasurementAdapter(itemsList,MeasurementActivity.this);

        listView_measurements.setAdapter(measurementAdapter);

        listView_measurements.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MeasurementActivity.this, "Hold To Delete Measurement", Toast.LENGTH_SHORT).show();

            }
        });



        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.goals:
                        startActivity(new Intent(MeasurementActivity.this, GoalsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.measurements:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(MeasurementActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.stats:
                        startActivity(new Intent(MeasurementActivity.this, StatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(MeasurementActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        loadData();
        measurementAdapter.notifyDataSetChanged();


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMeasurement();

            }
        });



        listView_measurements.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                int pos = i;

                final Dialog d = new Dialog(MeasurementActivity.this);
                d.setContentView(R.layout.measurements_listview_item_delete_update);

                Button delete_measurement = d.findViewById(R.id.measurements_updateDelete_button_delete);
                Button update_measurement = d.findViewById(R.id.measurements_updateDelete_button_update);
                Button cancel = d.findViewById(R.id.measurements_updateDelete_button_cancel);

                delete_measurement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Measurement m = (Measurement) itemsList.get(pos);

                        clearList(m);

                        itemsList.remove(pos);
                        measurementAdapter.notifyDataSetChanged();
                        updateDatabase(weightList, waistList, stepsList, chestList, calorieIntakeList, itemsList);

                        d.dismiss();
                    }
                });

                update_measurement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        itemsList.remove(pos);
                        measurementAdapter.notifyDataSetChanged();
                        updateDatabase(weightList, waistList, stepsList, chestList, calorieIntakeList, itemsList);

                        final Dialog d_update = new Dialog(MeasurementActivity.this);
                        d_update.setContentView(R.layout.measurements_fab_item);

                        Spinner spinner = (Spinner) d_update.findViewById(R.id.measurements_Spinner);
                        final EditText et_current = d_update.findViewById(R.id.measurements_editText_current);
                        Button add_measurement = d_update.findViewById(R.id.measurements_button_addMeasurement);


                        ArrayAdapter spinnerAdapter = new ArrayAdapter(MeasurementActivity.this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
                        spinner.setAdapter(spinnerAdapter);

                        add_measurement.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if(isMeasurementValid(et_current)){

                                    String current = et_current.getText().toString();
                                    String measurement = spinner.getSelectedItem().toString();
                                    String met = processMeasurement(measurement);

                                    if(checkIfMeasurementAdded(measurement))
                                    {
                                        d.dismiss();
                                        Log.d(TAG, "checkIfMeasurementAdded returned true");
                                        showErrorMeasurementAlreadySelected(measurement);
                                        return;
                                    }

                                    Measurement m = new Measurement(measurement, current, met);

                                    addMeasurementToList(m);

                                    itemsList.add(pos,m);
                                    measurementAdapter.notifyDataSetChanged();

                                    updateDatabase(weightList, waistList, stepsList, chestList, calorieIntakeList, itemsList);
                                }
                                else{
                                    Toast.makeText(MeasurementActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                                }

                                d_update.dismiss();
                                d.dismiss();

                            }
                        });

                        d_update.show();
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

                d.show();

                return true;
            }
        });

    }





    private void loadData()
    {

        DocumentReference df = myDatabase.collection("measurements").document(userID);

        df.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d(TAG, "Listen Failed. ", error);
                    return;
                }

                if(value != null && value.exists())
                {
                    MeasurementsListWrapper tempWrapper =  new MeasurementsListWrapper();
                    tempWrapper  = value.toObject(MeasurementsListWrapper.class);

                    wrapper = tempWrapper;
                    itemsList = wrapper.getListView_items();

                    loadListView();
                    loadMeasurementLists();

                }
                else
                {
                    Toast.makeText(MeasurementActivity.this, "Add some measurements", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void loadListView()
    {
        measurementAdapter = new MeasurementAdapter(itemsList,MeasurementActivity.this);
        listView_measurements.setAdapter(measurementAdapter);
    }


    private void loadMeasurementLists()
    {
        weightList = wrapper.getWeightList();
        waistList = wrapper.getWaistList();
        chestList = wrapper.getChestList();
        stepsList = wrapper.getStepsList();
        calorieIntakeList = wrapper.getCalorieIntakeList();
        itemsList = wrapper.getListView_items();

        Log.d(TAG, "SIZE OF :" + Integer.toString(itemsList.size()));
    }

    private void clearList(Measurement m)
    {
        if(m.getMeasurement().equals("Weight"))
        {
            weightList.remove((weightList.size()-1));
        }
        else if(m.getMeasurement().equals("Steps"))
        {
            stepsList.remove((stepsList.size()-1));
        }
        else if(m.getMeasurement().equals("Waist"))
        {
            waistList.remove((waistList.size()-1));
        }
        else if(m.getMeasurement().equals("Chest"))
        {
            chestList.remove((chestList.size()-1));
        }
        else if(m.getMeasurement().equals("Calorie Intake"))
        {
            calorieIntakeList.remove((calorieIntakeList.size()-1));
        }
        else{
            Toast.makeText(MeasurementActivity.this, "Could not Clear Lists", Toast.LENGTH_SHORT).show();
        }
    }




    private void addMeasurement()
    {
        final Dialog d = new Dialog(MeasurementActivity.this);
        d.setContentView(R.layout.measurements_fab_item);

        Spinner spinner = (Spinner) d.findViewById(R.id.measurements_Spinner);
        final EditText et_current = d.findViewById(R.id.measurements_editText_current);
        Button add_measurement = d.findViewById(R.id.measurements_button_addMeasurement);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerItems);
        spinner.setAdapter(spinnerAdapter);


       add_measurement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(isMeasurementValid(et_current)){



                        String current = et_current.getText().toString();
                        String measurement = spinner.getSelectedItem().toString();
                        String met = processMeasurement(measurement);

                        if(checkIfMeasurementAdded(measurement))
                        {
                            d.dismiss();
                            Log.d(TAG, "checkIfMeasurementAdded returned true");
                            showErrorMeasurementAlreadySelected(measurement);
                            return;
                        }

                        Measurement m = new Measurement(measurement, current, met);

                        addMeasurementToList(m);

                        itemsList.add(m);
                        measurementAdapter.notifyDataSetChanged();


                        updateDatabase(weightList, waistList, stepsList, chestList, calorieIntakeList, itemsList);

                    }
                    else{
                        Toast.makeText(MeasurementActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    }

                    d.dismiss();

                }
            });

        d.show();
    }


    private void updateDatabase(ArrayList<Measurement> weightList, ArrayList<Measurement> waistList, ArrayList<Measurement> stepsList, ArrayList<Measurement> chestList,
                                ArrayList<Measurement> calorieIntakeList, ArrayList<Measurement> listView_items)
    {
        DocumentReference documentReference = myDatabase.collection("measurements").document(userID);
        wrapper = new MeasurementsListWrapper(weightList, waistList, stepsList, chestList, calorieIntakeList, listView_items);
        Log.d(TAG, "Hit Update Method");
        myDatabase.collection("measurements").document(userID).set(wrapper).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MeasurementActivity.this, "Measurement Stored", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MeasurementActivity.this, "Failed to Update", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private boolean isMeasurementValid(EditText current){

        boolean isValid = false;

        String s_current = current.getText().toString().trim();

        if(TextUtils.isEmpty(s_current)){
            current.setError("Field cannot be empty!");
            current.requestFocus();
            isValid = false;
        }
        else{
            isValid = true;
        }

        return isValid;
    }

    private String processMeasurement(String measurement)
    {

        if(measurement.equals("Weight"))
        {
            met = "kg";
            category = "Weight";
        }
        else if(measurement.equals("Steps"))
        {
            met = "steps";
            category = "Steps";
        }
        else if(measurement.equals("Waist"))
        {
            met = "in";
            category = "Waist";
        }
        else if(measurement.equals("Chest"))
        {
            met = "in";
            category = "Chest";
        }
        else if(measurement.equals("Calorie Intake"))
        {
            met = "kcal";
            category = "Calorie Intake";
        }
        else{
            Toast.makeText(MeasurementActivity.this, "Could not process measurement", Toast.LENGTH_SHORT).show();
        }

        return met;
    }


    private void addMeasurementToList(Measurement m)
    {
        if(m.getMeasurement().equals("Weight"))
        {
            weightList.add(m);
        }
        else if(m.getMeasurement().equals("Steps"))
        {
            stepsList.add(m);
        }
        else if(m.getMeasurement().equals("Waist"))
        {
            waistList.add(m);
        }
        else if(m.getMeasurement().equals("Chest"))
        {
            chestList.add(m);
        }
        else if(m.getMeasurement().equals("Calorie Intake"))
        {
            calorieIntakeList.add(m);
        }
        else{
            Toast.makeText(MeasurementActivity.this, "Could not Update Lists", Toast.LENGTH_SHORT).show();
        }
    }

    //Check if measurement exists in itemslist
    // if yes return true
    private boolean checkIfMeasurementAdded(String measurement)
    {
        boolean isExist = false;

        ArrayList<String> temp = new ArrayList<String>();
        for(Measurement m : itemsList)
        {
            temp.add(m.getMeasurement());
        }

        if(temp.contains(measurement))
        {
            isExist = true;
        }

        Log.d(TAG, "checkIfMeasurementAdded " + isExist);

        return isExist;
    }

    private void showErrorMeasurementAlreadySelected(String measurement)
    {

        final Dialog d = new Dialog(MeasurementActivity.this);
        d.setContentView(R.layout.measurement_error_popup);

        TextView errorMessage = d.findViewById(R.id.measurements_error_msg);
        Button back = d.findViewById(R.id.measurements_error_button_back);

        String msg = measurement + " has already been added. Please Choose another one measurement";
        errorMessage.setText(msg);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });

        d.show();

    }


}