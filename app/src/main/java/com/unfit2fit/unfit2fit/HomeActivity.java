package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.unfit2fit.unfit2fit.models.Steps;
import com.unfit2fit.unfit2fit.wrappers.GoalsListWrapper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "HomeActivity";

    private Button button_bmi, reset_steps;
    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;
    private BottomNavigationView bottomNavigationView;

    Steps steps;

    SharedPreferences sharedP;

    private TextView textViewStepCounter, textViewQuote;
    private SensorManager sensorManager;
    private Sensor mStepCounter;
    private boolean isCounterSensorActive;

    private CircularProgressBar progressBar;
    private ArrayList<String> quotes;
    private int stepsRef1, stepsRef2;

    private String userID;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_navbar));

        textViewQuote = findViewById(R.id.home_textView_quote);

        loadRandomQuote();


        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
        }

        // Keep the screen active
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();
        userID = myAuth.getCurrentUser().getUid();

        button_bmi = findViewById(R.id.home_calculateBMI);
        progressBar = findViewById(R.id.home_circularProgressBar);
        textViewStepCounter = findViewById(R.id.home_textViewStepsTaken);

        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        sharedP = this.getSharedPreferences("steps", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed;

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.home);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        checkNewDay();


        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null)
        {
            mStepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounterSensorActive = true;
            //Toast.makeText(HomeActivity.this, "Hit", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(HomeActivity.this, "Sensor Not Active", Toast.LENGTH_SHORT).show();
            isCounterSensorActive = false;
        }





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
//
//        // Set the alarm to start at 3.46 PM
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 23);
//        calendar.set(Calendar.MINUTE, 58);
//        calendar.set(Calendar.SECOND, 00);
//
//        setAlarm(calendar.getTimeInMillis());
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        String currentDate = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

        if(sharedP.contains(currentDate))
        {
            int currentSteps = sharedP.getInt(currentDate, 1);
            currentSteps = currentSteps + 1;

            sharedP.edit().putInt(currentDate, currentSteps).apply();
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
        }
        else{
            Toast.makeText(HomeActivity.this, "No Sensor Detected", Toast.LENGTH_SHORT).show();
        }
    }
//
//
//
//
//    private void setAlarm(long timeInMillis)
//    {
//        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(HomeActivity.this, BiHourlyStepsAlarm.class);
//
//        String stepsTaken = textViewStepCounter.getText().toString().trim();
//        intent.putExtra("steps", stepsTaken);
//        //Toast.makeText(HomeActivity.this, String.valueOf(steps.getDailyBiHourlySteps().size()), Toast.LENGTH_SHORT).show();
//
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        am.setRepeating(AlarmManager.RTC, timeInMillis, AlarmManager.INTERVAL_HOUR, pendingIntent);
//    }



    public void checkNewDay()
    {

        String currentDate = new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

        if(!sharedP.contains(currentDate))
        {
            sharedP.edit().putInt(currentDate, 0).apply();
        }
        else if(sharedP.contains(currentDate))
        {
            int v = sharedP.getInt(currentDate, 1);
            textViewStepCounter.setText(String.valueOf(v));
        }

    }


    private String chooseRandomQuote()
    {
        Random rand =  new Random();
        int randomIndex = rand.nextInt(quotes.size());

        String qoute = quotes.get(randomIndex);

        return qoute;
    }


    public void loadRandomQuote()
    {
        quotes = new ArrayList<>();
//        quotes.add("\" It Does Not Matter How Slowly You Go, As Long As You Do Not Stop \"");
//        quotes.add("\" Don't Stop When You're Tired, Stop When You're Done \"");
        quotes.add("\" Master Your Mindset \nAnd You'll Master Your Body \"");
        //quotes.add("\" The Pain You Feel Today Will Be The Strength You Feel Tomorrow \"");

        String randomQuote = chooseRandomQuote();

        textViewQuote.setText(randomQuote);
    }


}