package com.unfit2fit.unfit2fit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})

public class MainActivity extends AppCompatActivity {

    private Button login_button;
    private Button register_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button = (Button) findViewById(R.id.mainActivityLogin);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Clicked Login", Toast.LENGTH_LONG).show();
            }
        });

        register_button = (Button) findViewById(R.id.mainActivityRegister);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

                Toast.makeText(MainActivity.this, "Clicked Register", Toast.LENGTH_LONG).show();
            }
        });
    }
}