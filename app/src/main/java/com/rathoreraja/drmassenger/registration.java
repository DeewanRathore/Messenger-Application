package com.rathoreraja.drmassenger;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class registration extends AppCompatActivity {
    TextView loginbtn;
    EditText  rg_username,rg_email,rg_password,rg_repassword;
    Button rg_signup;
    CircleImageView rg_profileImg;
    FirebaseAuth auth;
    Uri imageURI;
    String imageuri;
    String emailPattern = "^[_A-Za-z0-9-]+(\\\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\\\.[A-Za-z0-9]+)*(\\\\.[A-Za-z]{2,})$";

    FirebaseDatabase database;
    FirebaseStorage storage;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
//        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        auth =FirebaseAuth.getInstance();

        loginbtn = findViewById(R.id.lodinbtn);
        rg_username = findViewById(R.id.rgusername);
        rg_email = findViewById(R.id.rgemail);
        rg_password = findViewById(R.id.rgpassword);
        rg_repassword = findViewById(R.id.rgrepassword);
        rg_profileImg = findViewById(R.id.profilerg0);
        rg_signup = findViewById(R.id.singupbtn);


        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registration.this,login.class);
                startActivity(intent);
                finish();
            }
        });

        rg_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String namee = rg_username.getText().toString();
                String emaill = rg_email.getText().toString();
                String Password = rg_password.getText().toString();
                String cpassword = rg_repassword.getText().toString();
                String status = "Hey I'm Using This Application";

                if(TextUtils.isEmpty(namee) || TextUtils.isEmpty(emaill) || TextUtils.isEmpty(Password) || TextUtils.isEmpty(cpassword) ){
                    Toast.makeText(registration.this, "Incorrect Information", Toast.LENGTH_SHORT).show();
                } else if (!emaill.matches(emailPattern)) {
                    rg_email.setError("Please Enter Valid Email");
                } else if (Password.length()<6) {
                    rg_password.setError("Password Must Be Greater Than Six Character");
                } else if (!Password.equals(cpassword)) {
                    rg_password.setError("Password Doesn't Match");
                }else {
                    auth.createUserWithEmailAndPassword(emaill,Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String id = task.getResult().getUser().getUid();
                                DatabaseReference reference = database.getReference().child("User").child(id);
                                StorageReference storageReference = storage.getReference().child("Upload").child(id);


                                if(imageURI!= null){
                                    storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if(task.isSuccessful()){
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageuri = uri.toString();
                                                        Users users = new Users(id,namee,emaill,Password,cpassword,imageuri);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Intent intent = new Intent(registration.this,MainActivity.class);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }else {
                                                                    Toast.makeText(registration.this, "User Error Found", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }else {

                                    String status = "Hey I'm Using This Application";

                                    imageuri = "https://firebasestorage.googleapis.com/v0/b/drmassenger.appspot.com/o/3219866.png?alt=media&token=8b2c34ea-76cb-443a-8df9-84c9f34bfa6c";
                                    Users users = new Users(id,namee,emaill,Password,imageuri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(registration.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                Toast.makeText(registration.this, "User Error Found", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }else {
                                Toast.makeText(registration.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });



        rg_profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 10) {
            if(data != null) {
                imageURI = data.getData();
//                rg_profileImg.setImageURI(imageURI);
            }
        }
    }
}