package com.react.reactapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class RegInfo extends AppCompatActivity {

    HashMap<String, String> info = new HashMap<>();
    boolean valid = true;
    private FirebaseAuth mAuth;
    final Calendar myCalendar = Calendar.getInstance();
    TextInputEditText edittext;
    ActivityResultLauncher<Intent> openActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_info);

        mAuth= FirebaseAuth.getInstance();

        openActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        finish();
                    }
                });

        findViewById(R.id.reg_addCi).setEnabled(false);
        findViewById(R.id.reg_addCo).setEnabled(false);
        findViewById(R.id.reg_addPro).setEnabled(false);

        edittext= findViewById(R.id.reg_DoB);
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegInfo.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void findAllEditTexts(ViewGroup viewGroup) {

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                findAllEditTexts((ViewGroup) view);
            else if (view instanceof TextInputEditText) {
                TextInputEditText text = (TextInputEditText) view;
                TextInputLayout TIL = (TextInputLayout) text.getParent().getParent();
                TIL.setErrorEnabled(false);
                if(text.getText().toString().trim().isEmpty()) {
                    TIL.setErrorEnabled(true);
                    TIL.setError("Required");
                    valid = false;
                } else if(text.getTag().toString().equals("email") && !android.util.Patterns.EMAIL_ADDRESS.matcher(text.getText().toString().trim()).matches()) {
//                    mAuth.sendPasswordResetEmail(text.getText().toString().trim())
                    TIL.setErrorEnabled(true);
                    TIL.setError("Invalid Email Format");
                    valid = false;
                }
                info.put(
                        String.valueOf(text.getTag()),
                        text.getText().toString().trim()
                );
            }
        }
    }

    public void next(View view) {
        valid = true;

        findAllEditTexts((LinearLayout) findViewById(R.id.regInfo_ll));

        //check spinner
        Spinner[] add = new Spinner[] {findViewById(R.id.reg_addCo), findViewById(R.id.reg_addPro), findViewById(R.id.reg_addCi), findViewById(R.id.reg_addBa)};
        for(Spinner spin : add) {
            info.put(
                    String.valueOf(spin.getTag()),
                    spin.getSelectedItem().toString()
            );
        }

        if(valid) {
            Intent regFace = new Intent(RegInfo.this, RegFace.class);
            regFace.putExtra("userInfo", info);
            regFace.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            openActivity.launch(regFace);
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }
}