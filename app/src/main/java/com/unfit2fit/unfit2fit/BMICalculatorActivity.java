package com.unfit2fit.unfit2fit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BMICalculatorActivity extends AppCompatActivity {

    private SeekBar seekBar;

    private TextView tv_userHeight, tv_userAge;
    private EditText et_weight;

    private Button calculateBMI;

    private LinearLayout male_layout, female_layout;
    private RelativeLayout layout_weight_kg, layout_weight_lb;

    private ImageView increment, decrement;

    private String gender;
    private String seekBarProgress = "177";
    private String age;

    private int currentProgress;
    private int intAge = 25;
    private String weight_metric = "kgs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmicalculator);

        seekBar = findViewById(R.id.seekbar_BMI_Height);
        seekBar.setMax(220);
        seekBar.setProgress(170);

        tv_userHeight = findViewById(R.id.textView_BMI_userHeight);
        tv_userAge = findViewById(R.id.textView_BMI_userAge);
        et_weight = findViewById(R.id.editText_BMI_Weight);

        male_layout = findViewById(R.id.layoutMale);
        female_layout = findViewById(R.id.layoutFemale);

        layout_weight_kg = findViewById(R.id.relativeLayout_Weight_KGS);
        layout_weight_lb = findViewById(R.id.relativeLayout_Weight_LBS);

        increment = findViewById(R.id.incrementAge);
        decrement = findViewById(R.id.decrementAge);

        calculateBMI = findViewById(R.id.button_BMI_CalculateBMI);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                currentProgress = i;
                seekBarProgress = String.valueOf(currentProgress);
                tv_userHeight.setText(seekBarProgress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        male_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male_layout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bmi_gender_focus));
                female_layout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_black));
                gender = "Male";
            }
        });

        female_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female_layout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bmi_gender_focus));
                male_layout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_black));
                gender = "Female";
            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intAge == 1 || intAge == 100){
                    Toast.makeText(BMICalculatorActivity.this, "Reached Limit!", Toast.LENGTH_SHORT).show();
                }
                else{
                    intAge = intAge-1;
                    age = String.valueOf(intAge);
                    tv_userAge.setText(age);
                }
            }
        });

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intAge == 1 || intAge == 100) {
                    Toast.makeText(BMICalculatorActivity.this, "Reached Limit!", Toast.LENGTH_SHORT).show();
                } else {
                    intAge = intAge + 1;
                    age = String.valueOf(intAge);
                    tv_userAge.setText(age);
                }
            }
        });

        layout_weight_kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_weight_kg.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bmi_gender_focus));
                layout_weight_lb.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_black));
                et_weight.setHint("Weight in kgs");
                weight_metric = "kgs";
            }
        });

        layout_weight_lb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_weight_lb.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bmi_gender_focus));
                layout_weight_kg.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.background_black));
                et_weight.setHint("Weight in lbs");
                weight_metric = "lbs";
            }
        });

        calculateBMI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(gender)){
                    Toast.makeText(BMICalculatorActivity.this, "Please Select Gender", Toast.LENGTH_SHORT).show();
                }
                else if(Integer.parseInt(seekBarProgress) <= 50){
                    Toast.makeText(BMICalculatorActivity.this, "Please Select A Valid Height", Toast.LENGTH_SHORT).show();
                }
                else if(intAge <= 0 || intAge > 100){
                    Toast.makeText(BMICalculatorActivity.this, "Please Select A valid Age", Toast.LENGTH_SHORT).show();
                }
                else if(et_weight.getText().toString().trim().isEmpty() || Integer.parseInt(et_weight.getText().toString()) <= 10){
                    Toast.makeText(BMICalculatorActivity.this, "Please Enter A valid Weight", Toast.LENGTH_SHORT).show();
                }
                else{
                    String bmi_height;
                    String bmi_age = age;
                    String bmi_gender = gender;
                    String bmi_weight;

                    bmi_height = convertHeightMetric(seekBarProgress);

                    if(weight_metric.equals("lbs")){
                        bmi_weight = convertWeightMetric(et_weight.getText().toString());
                    }
                    else{
                        bmi_weight = et_weight.getText().toString();
                    }

                    Intent intent = new Intent(BMICalculatorActivity.this, BMIResultsActivity.class);
                    intent.putExtra("gender", bmi_gender);
                    intent.putExtra("height", bmi_height);
                    intent.putExtra("weight", bmi_weight);
                    intent.putExtra("age", bmi_age);
                    startActivity(intent);
                }
            }
        });
    }

    private String convertWeightMetric(String weight){

        double weight_lb = Integer.parseInt(weight);
        double weight_kg = (weight_lb/2.2046);

        return Double.toString(weight_kg);
    }

    private String convertHeightMetric(String height){

        double height_cm = Integer.parseInt(height);
        double height_m = (height_cm/100);

        return Double.toString(height_m);
    }
}