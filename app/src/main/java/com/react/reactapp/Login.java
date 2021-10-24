package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    private EditText login_email;
    private EditText login_password;
    private ProgressDialog dialog;
    private ScrollView scr;
    private FirebaseAuth mAuth;
    private DatabaseReference loginDbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ActivityCompat.requestPermissions(Login.this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET},
                1);

        mAuth=FirebaseAuth.getInstance();
        loginDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void forgotPassword(View view) {
        float scale = getResources().getDisplayMetrics().density;

        EditText reset = new EditText(view.getContext());
        reset.setPadding((int) ( 16*scale + 0.5f),0,(int) ( 16*scale + 0.5f),0);
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Password Reset");
        resetDialog.setMessage("Enter your email");
        resetDialog.setView(reset);

        resetDialog.setPositiveButton("Reset", (dialog, which) -> {
            String email = reset.getText().toString().trim();
//            mAuth.sendPasswordResetEmail(email)
//                    .addOnSuccessListener(aVoid -> Toast.makeText(Login.this, "Password Reset Email sent!", Toast.LENGTH_LONG).show())
//                    .addOnFailureListener(e -> Toast.makeText(Login.this, "An error has occured!", Toast.LENGTH_LONG).show());
//        }).setNegativeButton("Cancel", (dialog, which) -> {});
        resetDialog.create().show();
        });
    }

    public void logIn(View view) {
        Log.d(String.valueOf(this), "Logging in");
        scr = (ScrollView) findViewById(R.id.login_scrView);
        login_email = (EditText) findViewById(R.id.login_eTxtEmail);
        login_password = (EditText) findViewById(R.id.login_eTxtPass);

        if (!login_email.getText().toString().trim().isEmpty() && !login_password.getText().toString().isEmpty()) {
            dialog = ProgressDialog.show(Login.this, "Please wait", "Logging in...", true);
            mAuth.signInWithEmailAndPassword(String.valueOf(login_email.getText()), String.valueOf(login_password.getText())).addOnCompleteListener(task -> {
                if (task.isComplete()) {
                    String userID = mAuth.getCurrentUser().getUid();
                    if (mAuth.getCurrentUser().isEmailVerified()) {
                        finish();
                        startActivity(new Intent(Login.this, Dashboard.class));
                    } else {
                        AlertDialog.Builder confEmail = new AlertDialog.Builder(Login.this);
                        confEmail.setTitle("We sent you an email")
                                .setMessage("Please check your inbox/spam to confirm your email address.")
                                .setPositiveButton("OK", (dialog, which) -> mAuth.signOut())
                                .setNegativeButton("Resend Email", (dialog, which) -> {
                                    mAuth.getCurrentUser().sendEmailVerification();
                                    mAuth.signOut();
                                })
                                .setCancelable(false).show();
                        dialog.dismiss();
                    }
                } else {
                    System.out.println("Error Login");
                    ((TextInputLayout) login_password.getParent().getParent()).setErrorEnabled(true);
//                    error.setText("Wrong Email/Password");
                    scr.smoothScrollTo(0, 0);
//                    error.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });
//            mAuth.signInWithEmailAndPassword(String.valueOf(login_email.getText()),String.valueOf(login_password.getText())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if(task.isSuccessful()){
//                        //Check user exist
//                        String userID = mAuth.getCurrentUser().getUid();
//                        loginDbRef.addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                if(snapshot.hasChild(userID)){
//                                    if(mAuth.getCurrentUser().isEmailVerified()) {
//                                        startActivity(new Intent(Login.this, Dashboard.class));
//                                        dialog.dismiss();
//                                        loginDbRef.removeEventListener(this);
//                                        finish();
//                                    }
//                                    else {
//                                        AlertDialog.Builder confEmail = new AlertDialog.Builder(Login.this);
//                                        confEmail.setTitle("We sent you an email")
//                                                .setMessage("Please check your inbox/spam to confirm your email address.")
//                                                .setPositiveButton("OK", (dialog, which) -> mAuth.signOut())
//                                                .setNegativeButton("Resend Email", (dialog, which) -> {mAuth.getCurrentUser().sendEmailVerification(); mAuth.signOut();})
//                                                .setCancelable(false).show();
//                                        dialog.dismiss();
//                                    }
//                                }else{
//                                    System.out.println("You don't have Account yet");
//                                    error.setText("Account doesn't exist!");
//                                    scr.smoothScrollTo(0,0);
//                                    error.setVisibility(View.VISIBLE);
//                                    dialog.dismiss();
//                                }
//                            }
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {}
//                        });
//                    }else {
//                        System.out.println("Error Login");
//                        error.setText("Wrong Email/Password");
//                        scr.smoothScrollTo(0,0);
//                        error.setVisibility(View.VISIBLE);
//                        dialog.dismiss();
//                    }
//                }
//            });
//        } else {
//            System.out.println("Error Empty");
//            error.setText("Enter all required data!");
//            scr.smoothScrollTo(0,0);
//            error.setVisibility(View.VISIBLE);
//        }
        }
        else {
            //set error
        }
    }

    public void signUp(View view) {
        startActivity(new Intent(Login.this, RegInfo.class));
    }
}