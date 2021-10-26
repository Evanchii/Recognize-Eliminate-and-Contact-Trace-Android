package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Password extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Button change = findViewById(R.id.chgPassword_btnChange);
        change.setOnClickListener(v -> {
            boolean valid = true;
            TextInputEditText[] txt = new TextInputEditText[]{findViewById(R.id.chgPassword_oldPW), findViewById(R.id.chgPassword_newPW), findViewById(R.id.chgPassword_confPW)};
            TextInputLayout[] til = new TextInputLayout[]{(TextInputLayout) txt[0].getParent().getParent(), (TextInputLayout) txt[1].getParent().getParent(), (TextInputLayout) txt[2].getParent().getParent()};

            for(int x = 0; x<3; x++) {
                til[x].setErrorEnabled(false);
                if(txt[x].getText().toString().isEmpty()) {
                    til[x].setError("Required");
                    til[x].setErrorEnabled(true);
                    valid = false;
                }
            }

            if(valid) {
                mAuth.signInWithEmailAndPassword(mAuth.getCurrentUser().getEmail(), txt[0].getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (!txt[0].getText().toString().trim().equals(txt[1].getText().toString().trim())) {
                            if (txt[1].getText().toString().trim().equals(txt[2].getText().toString().trim())) {
                                mAuth.getCurrentUser().updatePassword(txt[1].getText().toString().trim());
                                Toast.makeText(Password.this, "Changed Password!", Toast.LENGTH_LONG).show();

                                startActivity(new Intent(Password.this, Dashboard.class));
                                finish();
                            } else {
                                til[1].setError("Passwords do not match");
                                til[1].setErrorEnabled(true);
                                til[2].setError("Passwords do not match");
                                til[2].setErrorEnabled(true);
                            }
                        } else {
                            til[0].setError("Old and New Password can't be the same");
                            til[0].setErrorEnabled(true);
                            til[1].setError("Old and New Password can't be the same");
                            til[1].setErrorEnabled(true);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        til[0].setError("Incorrect Old Password");
                        til[0].setErrorEnabled(true);
                    }
                });
            }
        });
    }
}