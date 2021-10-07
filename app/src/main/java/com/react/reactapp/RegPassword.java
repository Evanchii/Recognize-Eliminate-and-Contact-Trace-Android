package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.HashMap;

public class RegPassword extends AppCompatActivity {

    HashMap<String, String> info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_password);
        Intent intent = getIntent();
        info = (HashMap<String, String>) intent.getSerializableExtra("userInfo");

        CheckBox ToS = findViewById(R.id.regPass_chkToS);
        String text = "I agree to the Terms and Service\nand Privacy Policy";
        SpannableString ss = new SpannableString(text);
        ClickableSpan cS1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(RegPassword.this, "One", Toast.LENGTH_SHORT).show();
            }
        };
        ClickableSpan cS2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(RegPassword.this, "Two", Toast.LENGTH_SHORT).show();
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

    public void Signup(View view) {
        //Register
        startActivity(new Intent(RegPassword.this, Login.class));
        finish();
    }
}