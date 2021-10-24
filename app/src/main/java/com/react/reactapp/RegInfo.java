package com.react.reactapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class RegInfo extends AppCompatActivity {

    HashMap<String, String> info = new HashMap<>();
    boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_info);
        findViewById(R.id.reg_addCi).setEnabled(false);
        findViewById(R.id.reg_addCo).setEnabled(false);
        findViewById(R.id.reg_addPro).setEnabled(false);
    }

    private void findAllEditTexts(ViewGroup viewGroup) {

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
//            Log.d("RegInfo.java","Index: "+ String.valueOf(i) + "\tTag: "+ view.getTag() + "\tIs TIET?"+ (view instanceof TextInputEditText));
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
                }
                info.put(
                        String.valueOf(text.getTag()),
                        text.getText().toString().trim()
                );
            }
        }
    }

    public void next(View view) {
        findAllEditTexts((LinearLayout) findViewById(R.id.regInfo_ll));

        //check spinner
        Spinner[] add = new Spinner[] {findViewById(R.id.reg_addCo), findViewById(R.id.reg_addPro), findViewById(R.id.reg_addCi), findViewById(R.id.reg_addBa)};
        for(Spinner spin : add) {
            info.put(
                    String.valueOf(spin.getTag()),
                    spin.getSelectedItem().toString()
            );
            Toast.makeText(RegInfo.this, spin.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
        }

        if(valid) {
            Intent regFace = new Intent(RegInfo.this, RegFace.class);
            regFace.putExtra("userInfo", info);
            startActivity(regFace);
        }
    }
}