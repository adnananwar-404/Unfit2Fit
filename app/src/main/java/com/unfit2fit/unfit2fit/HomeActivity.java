package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.squareup.okhttp.internal.DiskLruCache;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private Button button_logout, button_bmi, reset_steps;
    private FirebaseAuth myAuth;
    private BottomNavigationView bottomNavigationView;


    private TextView textViewStepCounter;
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorActive;

    private CircularProgressBar progressBar;

    float totalStepCount;

    private float total_steps = 0f;
    private float previous_total_steps = 0f;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // Keep the screen active
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myAuth = FirebaseAuth.getInstance();
        button_logout = findViewById(R.id.logout);
        button_bmi = findViewById(R.id.home_calculateBMI);
        reset_steps = findViewById(R.id.home_button_resetCounter);

        progressBar = findViewById(R.id.home_circularProgressBar);

        textViewStepCounter = findViewById(R.id.home_textViewStepsTaken);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorActive = true;
            //Toast.makeText(HomeActivity.this, "Hit", Toast.LENGTH_SHORT).show();
        }
        else{
            textViewStepCounter.setText("Step Counter Sensor Is Not Active");
            isCounterSensorActive = false;
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.goals:
                        startActivity(new Intent(HomeActivity.this, GoalsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.measurements:
                        startActivity(new Intent(HomeActivity.this, MeasurementActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        return true;
                    case R.id.stats:
                        startActivity(new Intent(HomeActivity.this, StatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });

        button_bmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, BMICalculatorActivity.class));
            }
        });

        button_logout.setOnClickListener(view -> {
            myAuth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        });

        reset_steps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previous_total_steps = total_steps;
                textViewStepCounter.setText("0");
                saveData();
            }
        });

        laodData();
        saveData();
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor == mStepCounter)
        {
            totalStepCount = (int) sensorEvent.values[0];
            total_steps = sensorEvent.values[0];

            int currentSteps = (int) (total_steps - previous_total_steps);


            textViewStepCounter.setText(String.valueOf(currentSteps));

            progressBar.setProgressWithAnimation((float) currentSteps);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            sensorManager.registerListener(this, mStepCounter, sensorManager.SENSOR_DELAY_FASTEST);
            //Toast.makeText(HomeActivity.this, "Hit", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(HomeActivity.this, "No Sensor Detected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            sensorManager.unregisterListener(this, mStepCounter);
            Toast.makeText(HomeActivity.this, "Hit", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(HomeActivity.this, "No Sensor Detected", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("key1", previous_total_steps);
        editor.apply();
    }

    private void laodData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        float savedNumber = sharedPreferences.getFloat("key1", 0f);

        previous_total_steps = savedNumber;
    }
}