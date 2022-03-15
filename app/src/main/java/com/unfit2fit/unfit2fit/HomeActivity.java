package com.unfit2fit.unfit2fit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private Button logout_button;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        myAuth = FirebaseAuth.getInstance();
        logout_button = findViewById(R.id.logout);
        logout_button.setOnClickListener(view -> {
            myAuth.signOut();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
        });
    }
}