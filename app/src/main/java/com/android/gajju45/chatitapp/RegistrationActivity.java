package com.android.gajju45.chatitapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegistrationActivity extends AppCompatActivity {

    TextView txt_signIn, btn_signUp;
    CircleImageView profile_image;
    EditText reg_name, reg_email, reg_password, reg_cPassword;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUrl;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;
    ProgressDialog progressDialog;
    String status="I am Using Chat It ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        txt_signIn = findViewById(R.id.txt_signin);
        btn_signUp = findViewById(R.id.signUp_btn);
        profile_image = findViewById(R.id.profile_image);
        reg_name = findViewById(R.id.registration_name);
        reg_email = findViewById(R.id.registration_email);
        reg_password = findViewById(R.id.registration_password);
        reg_cPassword = findViewById(R.id.registration_confirrm_password);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);


        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                String name = reg_name.getText().toString();
                String email = reg_email.getText().toString();
                String password = reg_password.getText().toString();
                String cPassword = reg_cPassword.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cPassword)) {

                    Toast.makeText(RegistrationActivity.this, "Enter Valid Data", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (!email.matches(emailPattern)) {

                    reg_email.setError("Enter Valid Email");
                    Toast.makeText(RegistrationActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                } else if (!password.equals(cPassword)) {

                    Toast.makeText(RegistrationActivity.this, "Password Does Not Match ", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else if (password.length() < 6) {
                    Toast.makeText(RegistrationActivity.this, "Enter 6 Character Password", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {

                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference = storage.getReference().child("uplod").child(auth.getUid());

                                if (imageUrl != null) {
                                    storageReference.putFile(imageUrl).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        imageURI = uri.toString();
                                                        Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                                        reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(RegistrationActivity.this, "Succesfully login", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                                } else {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(RegistrationActivity.this, "Error Creditional", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                });
                                            }

                                        }
                                    });

                                } else {
                                    String status="I am Using Chat It ";
                                    progressDialog.dismiss();
                                    imageURI = "https://firebasestorage.googleapis.com/v0/b/chatitapp-dadba.appspot.com/o/profile.png?alt=media&token=38274871-1c19-4e35-bc27-20ee79d15400";
                                    Users users = new Users(auth.getUid(), name, email, imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                                                finish();
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(RegistrationActivity.this, "Error Creditional", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                }
                            }
                        }
                    });

                }


            }
        });
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Picture"), 10);
            }
        });

      txt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationActivity.this, LogInActivity.class));
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data != null) {
                imageUrl = data.getData();
                profile_image.setImageURI(imageUrl);
            }
        }
    }
}