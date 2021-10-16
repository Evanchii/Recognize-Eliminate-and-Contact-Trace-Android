package com.react.reactapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class Login extends AppCompatActivity {

    private EditText login_email;
    private EditText login_password;
    private ProgressDialog dialog;
    private ScrollView scr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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

    public void logIn(View view){
        Log.d(String.valueOf(this), "Logging in");
        scr = (ScrollView) findViewById(R.id.login_scrView);
        login_email=(EditText)findViewById(R.id.login_eTxtEmail);
        login_password=(EditText)findViewById(R.id.login_eTxtPass);

        finish();
        startActivity(new Intent(Login.this, Dashboard.class));
//        if(!login_email.getText().toString().trim().equals("") && !login_password.getText().toString().trim().equals("")){
//            dialog = ProgressDialog.show(Login.this, "Please wait","Logging in...", true);
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

    public void signUp(View view) {
        startActivity(new Intent(Login.this, RegInfo.class));
    }
}