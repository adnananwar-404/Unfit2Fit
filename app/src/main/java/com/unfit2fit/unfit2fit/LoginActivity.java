package com.unfit2fit.unfit2fit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})
public class LoginActivity extends AppCompatActivity {

    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup = (TextView)findViewById(R.id.loginToRegister);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

                Toast.makeText(LoginActivity.this, "Clicked Register", Toast.LENGTH_LONG).show();
            }
        });
    }
}