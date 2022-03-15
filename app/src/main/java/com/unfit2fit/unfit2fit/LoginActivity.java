package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})
public class LoginActivity extends AppCompatActivity {

    private TextView signup_textView;
    private Button login_button;
    private EditText editText_email;
    private EditText editText_password;
    private FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        myAuth = FirebaseAuth.getInstance();
        signup_textView = findViewById(R.id.loginToRegister);
        editText_email = findViewById(R.id.loginEmail);
        editText_password = findViewById(R.id.loginPassword);
        login_button = findViewById(R.id.loginButton);

        signup_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

                Toast.makeText(LoginActivity.this, "Clicked Register", Toast.LENGTH_LONG).show();
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
    }

    private void loginUser(){
        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();

        if (TextUtils.isEmpty(email)){
            editText_email.setError("Email cannot be empty");
            editText_email.requestFocus();
        }else if (TextUtils.isEmpty(password)){
            editText_password.setError("Password cannot be empty");
            editText_password.requestFocus();
        }else{
            myAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "User logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(LoginActivity.this, "Log in Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}