package com.unfit2fit.unfit2fit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})
public class RegisterActivity extends AppCompatActivity {

    private TextView login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        login = (TextView)findViewById(R.id.registerToLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(RegisterActivity.this, "Clicked Login", Toast.LENGTH_LONG).show();
            }
        });
    }
}