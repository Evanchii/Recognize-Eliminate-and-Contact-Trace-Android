package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class RegPassword extends AppCompatActivity {

    HashMap<String, String> info;
    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private DatabaseReference signupDbRef;
    CheckBox ToS;
    private ProgressDialog progUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_password);
        Intent intent = getIntent();
        info = (HashMap<String, String>) intent.getSerializableExtra("info");
        mAuth= FirebaseAuth.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();


        ToS = findViewById(R.id.regPass_chkToS);
        String text = "I agree to the Terms and Service\nand Privacy Policy";
        SpannableString ss = new SpannableString(text);
        ClickableSpan cS1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://react.alevan.ga/terms/")));
            }
        };
        ClickableSpan cS2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://react.alevan.ga/privacy/")));
            }
        };
        ss.setSpan(cS1, 15, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(cS2, 37, 51, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ToS.setText(ss);
        ToS.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public void returnLogin(View view) {
        startActivity(new Intent(RegPassword.this, Login.class));
        finish();
    }

    public void upload() {
        Uri face = Uri.parse(info.get("faceID")),
                ID = Uri.parse(info.get("ID"));

        StorageReference filepathFace = mStorage.child("Face").child(mAuth.getCurrentUser().getUid().toString() +"."+ face.getLastPathSegment().split("\\.")[1]);
        filepathFace.putFile(face).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                signupDbRef.child("faceID").setValue("Face/"+mAuth.getCurrentUser().getUid().toString() +"."+ face.getLastPathSegment().split("\\.")[1]);
//                progUp.dismiss();
                finish();
            }
        });

        StorageReference filepathID = mStorage.child("ID").child(mAuth.getCurrentUser().getUid().toString() +"."+ ID.getLastPathSegment().split("\\.")[1]);
        filepathID.putFile(ID).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                signupDbRef.child("ID").setValue("ID/"+mAuth.getCurrentUser().getUid().toString() +"."+ ID.getLastPathSegment().split("\\.")[1]);
//                progUp.dismiss();
                finish();
            }
        });
    }

    public void Signup(View view) {
        progUp = ProgressDialog.show(this, "Registering","Please wait as we register you in our database.", true);
        progUp.setCancelable(false);
        Toast.makeText(RegPassword.this, "Signing up...", Toast.LENGTH_LONG).show();
        TextInputEditText pass = findViewById(R.id.password_txtPass),
                conf = findViewById(R.id.password_txtConf);
        TextInputLayout layPass = (TextInputLayout) pass.getParent().getParent(),
                layConf = (TextInputLayout) pass.getParent().getParent();
        layPass.setErrorEnabled(false);
        layConf.setErrorEnabled(false);
        if(pass.getText().toString().trim().equals(conf.getText().toString().trim())) {
            if(pass.getText().toString().trim().isEmpty()) {
                layPass.setError("Field is required");
                layPass.setErrorEnabled(true);
            }
            if(conf.getText().toString().trim().isEmpty()) {
                layPass.setError("Field is required");
                layPass.setErrorEnabled(true);
            }
            //Register
            if(ToS.isChecked()) {
                Log.d("PASSWORD>", info.keySet().toString());
                mAuth.createUserWithEmailAndPassword(info.get("email"), pass.getText().toString().trim())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                mAuth.getCurrentUser().sendEmailVerification();
                                AlertDialog.Builder confEmail = new AlertDialog.Builder(RegPassword.this);
                                String userID = mAuth.getCurrentUser().getUid();
                                signupDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("info");
                                signupDbRef.child("Type").setValue("User");
                                for (String key : info.keySet()) {
                                    if (key.equals("email") || key.equals("faceID") || key.equals("ID")) {
                                        continue;
                                    } else {
                                        signupDbRef.child(key).setValue(info.get(key));
                                    }
                                }
                                signupDbRef.child("status").setValue(false);

                                upload();

                                progUp.dismiss();

                                confEmail.setTitle("We sent you an email")
                                        .setMessage("Please check your inbox/spam to confirm your email address.")
                                        .setPositiveButton("OK", (dialog, which) -> {
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }).setCancelable(false).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        progUp.dismiss();
                        if (e.getMessage().contains("badly formatted")) {
                            //show dialog
//                    error.setText("Please enter a valid Email Address");
                        } else if (e.getMessage().contains("address is in use by another account")) {
                            //show dialog
//                    error.setText("Email Address is in use by another account!");
                        } else if (e.getMessage().contains("6 character")) {
                            layPass.setError("Password should be at least 6 characters long");
                        } else {
                            layPass.setError(e.getMessage());
                        }
                        layPass.setErrorEnabled(true);
                    }
                });
            } else {
                progUp.dismiss();
                Toast.makeText(RegPassword.this, "Terms of Service Agreement Required!", Toast.LENGTH_LONG).show();
            }
        }
        else {//if password not match
            layPass.setError("Passwords does not match");
            layConf.setError("Passwords does not match");
            layPass.setErrorEnabled(true);
            layConf.setErrorEnabled(true);
            progUp.dismiss();
        }
    }
}