package com.unfit2fit.unfit2fit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})

public class MainActivity extends AppCompatActivity {

    private Button login_button;
    private Button register_button;
    private FirebaseAuth myAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAuth = FirebaseAuth.getInstance();

        login_button = findViewById(R.id.mainActivityLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Clicked Login", Toast.LENGTH_LONG).show();
            }
        });

        register_button = findViewById(R.id.mainActivityRegister);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Clicked Register", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = myAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        }
    }
}