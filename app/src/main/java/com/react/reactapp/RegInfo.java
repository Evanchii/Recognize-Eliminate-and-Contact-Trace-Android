package com.react.reactapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;

public class RegInfo extends AppCompatActivity {

    private EditText fName, mName, lName, cNo, email, dob, addNo, addCo, addPro, addCi, addBa, addZi;
    HashMap<String, String> info = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_info);
    }

    private void findAllEditTexts(ViewGroup viewGroup) {

        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                findAllEditTexts((ViewGroup) view);
            else if (view instanceof EditText) {
                EditText editText = (EditText) view;
                info.put(
                        String.valueOf(editText.getId()).replace("reg_",""),
                        editText.getText().toString()
                );
            }
        }
    }

    public void next(View view) {
        findAllEditTexts((LinearLayout) findViewById(R.id.regInfo_ll));
        if(true) {
            Intent regFace = new Intent(RegInfo.this, RegFace.class);
            regFace.putExtra("userInfo", info);
            startActivity(regFace);
        }
        else {
            //do something
        }
    }
}