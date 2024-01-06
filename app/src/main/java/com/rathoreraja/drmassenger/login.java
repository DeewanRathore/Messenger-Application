package com.rathoreraja.drmassenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login extends AppCompatActivity {
    Button button;
    EditText email, passward;
    FirebaseAuth auth;
    String emailPattern = "^[_A-Za-z0-9-]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logBotton);
        email = findViewById(R.id.editTextTextlogEmail);
        passward = findViewById(R.id.editTextlogPassword);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = email.getText().toString();
                String pass = passward.getText().toString();

                if(TextUtils.isEmpty(Email)){
                    Toast.makeText(login.this, "Enter The Email Jani ", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(pass)) {
                    Toast.makeText(login.this, "Enter The Password Mitha ", Toast.LENGTH_SHORT).show();
                } else if (!Email.matches(emailPattern)) {

                    email.setError("Incorrect Email Suhna");
                } else if (passward.length()<6) {
                    passward.setError("Please Enter More Than Six Characters Adaa");
                    Toast.makeText(login.this, "Password Must Be Longer Than Six Characters", Toast.LENGTH_SHORT).show();

                }else {

                    auth.signInWithEmailAndPassword(Email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                try {

                                    Intent intent = new Intent(login.this,MainActivity.class);
                                    startActivity(intent);
                                    finish();

                                }catch (Exception e){
                                    Toast.makeText(login.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(login.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });

    }
}