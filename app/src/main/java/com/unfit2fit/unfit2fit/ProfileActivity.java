package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import com.mikhaellopez.circularimageview.CircularImageView;
import com.unfit2fit.unfit2fit.models.BMI;
import com.unfit2fit.unfit2fit.models.Measurement;
import com.unfit2fit.unfit2fit.models.User;
import com.unfit2fit.unfit2fit.wrappers.GoalsListWrapper;
import com.unfit2fit.unfit2fit.wrappers.MeasurementsListWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    BottomNavigationView bottomNavigationView;
    CircularImageView imageView;

    Button button_logout;

    String s_fullName, s_email, s_gender, s_height, s_bmi, s_category, s_age, s_weight;

    String weight_bmi, weight_measurements;

    TextView profile_name, fullname, email, gender, weight, height, bmi, bmi_category, age;

    String userID;

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_navbar));

        userID = myAuth.getCurrentUser().getUid();

        button_logout = findViewById(R.id.profile_logout);
        profile_name = findViewById(R.id.profile_image_name);
        fullname = findViewById(R.id.profile_textView_fullName);
        email = findViewById(R.id.profile_textView_email);
        gender = findViewById(R.id.profile_textView_gender);
        weight = findViewById(R.id.profile_textView_weight);
        height = findViewById(R.id.profile_textView_height);
        bmi = findViewById(R.id.profile_textView_bmi);
        bmi_category = findViewById(R.id.profile_textView_bmiCategory);
        age = findViewById(R.id.profile_textView_age);
        imageView = findViewById(R.id.profile_image);

        // Call method to retrieve Values from Database
        // Pass in textViews as params

        bottomNavigationView = findViewById(R.id.bottom_navigation_bar);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.goals:
                        startActivity(new Intent(ProfileActivity.this, GoalsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.measurements:
                        startActivity(new Intent(ProfileActivity.this, MeasurementActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.stats:
                        startActivity(new Intent(ProfileActivity.this, StatsActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        return true;
                }
                return false;
            }
        });

        button_logout.setOnClickListener(view -> {
            myAuth.signOut();
            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        });


        setUpProfilePage();

    }



    private void setUpProfilePage(){

        getUserame_EmailAddress();
        getUserGender_Height_Age_BMI();
        getWeights();

    }


    private void getUserame_EmailAddress()
    {

        DocumentReference df = myDatabase.collection("users").document(userID);

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

                    User user  = value.toObject(User.class);

                    s_fullName = user.getFull_name();
                    s_email = user.getEmail_address();


                    profile_name.setText(s_fullName);
                    fullname.setText(s_fullName);
                    email.setText(s_email);

                }
                else
                {
                    profile_name.setText("-");
                    fullname.setText("-");
                    email.setText("-");
                }
            }
        });
    }


    private void getUserGender_Height_Age_BMI()
    {

        DocumentReference df = myDatabase.collection("bmi").document(userID);

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

                    BMI user_bmi  = value.toObject(BMI.class);

                    s_bmi = user_bmi.getBmi();
                    s_height = user_bmi.getHeight();
                    s_gender = user_bmi.getGender();
                    s_category = user_bmi.getCategory();
                    s_age = user_bmi.getAge();

                    gender.setText(s_gender);
                    height.setText(s_height);
                    bmi.setText(s_bmi);
                    bmi_category.setText(s_category);
                    age.setText(s_age);

                    if(s_gender.equals("Female"))
                    {
                        imageView.setImageResource(R.drawable.vector_woman);
                    }

                }
                else
                {
                    gender.setText("-");
                    height.setText("-");
                    bmi.setText("-");
                    bmi_category.setText("-");
                    age.setText("-");
                }
            }
        });

    }


    private void getWeights()
    {

        //DocumentReference reference_BMI = myDatabase.collection("bmi").document(userID);
        DocumentReference reference_measurements = myDatabase.collection("measurements").document(userID);


//        reference_BMI.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                if(error != null)
//                {
//                    Log.d(TAG, "Listen Failed. ", error);
//                    return;
//                }
//
//                if(value != null && value.exists())
//                {
//
//                    Log.d(TAG, "inside weight_BMI");
//
//                    BMI bmi  = value.toObject(BMI.class);
//
//                    weight_bmi = bmi.getWeight();
//
//                }
//                else
//                {
//                    //Toast.makeText(ProfileActivity.this, "Add some goals", Toast.LENGTH_SHORT).show();
//                    weight_bmi = "-";
//                }
//            }
//        });

        reference_measurements.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null)
                {
                    Log.d(TAG, "Listen Failed. ", error);
                    return;
                }

                if(value != null && value.exists())
                {



                    MeasurementsListWrapper wrapper = value.toObject(MeasurementsListWrapper.class);

                    if(wrapper.getWeightList().isEmpty())
                    {
                        weight.setText("-");
                    }
                    else{

                        ArrayList<Measurement> weightList=  wrapper.getWeightList();
                        int index = weightList.size()-1;

                        weight_measurements = weightList.get(index).getValue().trim();
                        weight.setText(weight_measurements);
                    }




                }
                else
                {
                    //Toast.makeText(ProfileActivity.this, "Add some goals", Toast.LENGTH_SHORT).show();
                    //weight_bmi = "-";
                    //weight_measurements = "-";
                    Log.d(TAG, "Weight Set in Else");
                    weight.setText("-");
                }
            }
        });




    }


}