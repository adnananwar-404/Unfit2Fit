package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unfit2fit.unfit2fit.models.BMI;

import java.text.DecimalFormat;
import java.util.Objects;

public class BMIResultsActivity extends AppCompatActivity {

    String gender, age, weight, height, bmi, bmi_category, weight_range, rangeL, rangeH;
    private DecimalFormat df;
    double calculated_bmi, bmi_unrounded, range_high, range_low;
    TextView textView_bmi, textView_category, textView_weightRange;
    Intent intent;
    ImageView bmi_image;
    Button recalculate, finish;

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmiresults);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_navbar));

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();

        intent = getIntent();
        gender = intent.getStringExtra("gender");
        age = intent.getStringExtra("age");
        weight = intent.getStringExtra("weight");
        height = intent.getStringExtra("height");

        df = new DecimalFormat("0.00");

        recalculate = findViewById(R.id.bmiResults_button_Recalculate);
        finish = findViewById(R.id.bmiResults_button_Finish);

        textView_bmi = findViewById(R.id.bmiResults_textView_bmi);
        textView_weightRange = findViewById(R.id.bmiResults_textView_weightRange);
        textView_category = findViewById(R.id.bmiResults_textView_category);

        bmi_image = findViewById(R.id.bmiResults_imageView_result);

        bmi_unrounded = calculateBMI(weight, height);
        
        bmi = df.format(bmi_unrounded);
        //bmi = String.valueOf(calculated_bmi);
        range_low= calculateRangeLow(calculated_bmi, height);
        rangeL = df.format(range_low);
        range_high = calculateRangeHigh(calculated_bmi, height);
        rangeH = df.format(range_high);
        weight_range = rangeL + " - " + rangeH;

        if(bmi_unrounded < 18.5)
        {
            bmi_category = "UnderWeight";
            bmi_image.setImageResource(R.drawable.vector_bmi_warning_orange);
            textView_bmi.setText(bmi);
            textView_category.setText(bmi_category);
            textView_weightRange.setText(weight_range);
        }
        else if(bmi_unrounded >= 18.5 && bmi_unrounded < 25)
        {
            bmi_category = "Normal";
            bmi_image.setImageResource(R.drawable.vector_bmi_normal);
            textView_bmi.setText(bmi);
            textView_category.setText(bmi_category);
            textView_weightRange.setText(weight_range);
        }
        else if(bmi_unrounded >= 25 && bmi_unrounded < 30)
        {
            bmi_category = "OverWeight";
            bmi_image.setImageResource(R.drawable.vector_bmi_warning_orange);
            textView_bmi.setText(bmi);
            textView_category.setText(bmi_category);
            textView_weightRange.setText(weight_range);
        }
        else if(bmi_unrounded >= 30 && bmi_unrounded < 35)
        {
            bmi_category = "Obese";
            bmi_image.setImageResource(R.drawable.vector_bmi_warning_red);
            textView_bmi.setText(bmi);
            textView_category.setText(bmi_category);
            textView_weightRange.setText(weight_range);
        }
        else{
            bmi_category = "Severely Obese";
            bmi_image.setImageResource(R.drawable.vector_bmi_severe);
            textView_bmi.setText(bmi);
            textView_category.setText(bmi_category);
            textView_weightRange.setText(weight_range);
        }

        // Database Operations
        String user_ID = myAuth.getCurrentUser().getUid();

        BMI user_BMI = new BMI(user_ID,gender, height, age, weight, bmi, bmi_category, weight_range);

        //Check if BMI Entry Exists
        DocumentReference documentReference = myDatabase.collection("bmi").document(user_ID);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        myDatabase.collection("bmi").document(user_ID).update("bmi",bmi);
                        myDatabase.collection("bmi").document(user_ID).update("age",age);
                        myDatabase.collection("bmi").document(user_ID).update("height",height);
                        myDatabase.collection("bmi").document(user_ID).update("weight",weight);
                        myDatabase.collection("bmi").document(user_ID).update("gender",gender);
                        myDatabase.collection("bmi").document(user_ID).update("weight_range",weight_range);
                        myDatabase.collection("bmi").document(user_ID).update("category",bmi_category);
                        Toast.makeText(BMIResultsActivity.this, "BMI Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        documentReference.set(user_BMI).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(BMIResultsActivity.this, "BMI Stored", Toast.LENGTH_SHORT).show();
                            }
                        });

                        myDatabase.collection("bmi").document(user_ID).update("age",age);
                    }
                } else {
                    Toast.makeText(BMIResultsActivity.this, "BMI Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        recalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BMIResultsActivity.this, BMICalculatorActivity.class));
            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BMIResultsActivity.this, HomeActivity.class));
            }
        });

    }

    private double calculateBMI(String weight, String height)
    {
        double user_weight = Double.parseDouble(weight);
        double user_height = Double.parseDouble(height);

        double height_squared = user_height*user_height;

        double user_bmi = user_weight/height_squared;

        return user_bmi;
    }

    private double calculateRangeLow(double bmi, String height)
    {
        double user_height = Double.parseDouble(height);
        double height_squared = user_height*user_height;

        double low = 18.5 * height_squared;

        return low;
    }

    private double calculateRangeHigh(double bmi, String height)
    {
        double user_height = Double.parseDouble(height);
        double height_squared = user_height*user_height;

        double high = 24.9 * height_squared;

        return high;
    }

}