package com.unfit2fit.unfit2fit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unfit2fit.unfit2fit.models.User;

import java.util.Objects;

@SuppressWarnings({"FieldCanBeLocal", "Convert2Lambda"})
public class RegisterActivity extends AppCompatActivity {

    private EditText editText_email, editText_password, editText_Username, editText_Full_Name;

    private TextView login_textView;
    private Button register_button;

    private FirebaseAuth myAuth;
    private FirebaseFirestore myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_navbar));

        myAuth = FirebaseAuth.getInstance();
        myDatabase = FirebaseFirestore.getInstance();

        editText_Username = findViewById(R.id.register_username);
        editText_Full_Name = findViewById(R.id.register_full_name);

        editText_email = findViewById(R.id.registerEmail);
        editText_password = findViewById(R.id.registerPassword);

        login_textView = findViewById(R.id.registerToLogin);
        register_button = findViewById(R.id.registerButton);


        login_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                Toast.makeText(RegisterActivity.this, "Clicked Login", Toast.LENGTH_LONG).show();
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();

            }
        });
    }

    private void createUser(){
        String email = editText_email.getText().toString();
        String password = editText_password.getText().toString();
        String username = editText_Username.getText().toString();
        String full_name = editText_Full_Name.getText().toString();


        if(TextUtils.isEmpty(email)){
            editText_email.setError("Email cannot be empty!");
            editText_email.requestFocus();
        }
        else if(TextUtils.isEmpty(password)){
            editText_password.setError("Password cannot be empty!");
            editText_password.requestFocus();
        }
        else if(TextUtils.isEmpty(username)){
            editText_Username.setError("Username cannot be empty!");
            editText_Username.requestFocus();
        }
        else if(TextUtils.isEmpty(full_name)){
            editText_Full_Name.setError("Please enter your name!");
            editText_Full_Name.requestFocus();
        }
        else{
            myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Account Successfully Created", Toast.LENGTH_SHORT).show();

                        String userID = myAuth.getCurrentUser().getUid();
                        User user = new User(userID, username, full_name, email, password);

                        DocumentReference documentReference = myDatabase.collection("users").document(userID);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "onSuccess: User Object stored for " + full_name);
                            }
                        });


                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Registration Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}