package com.react.reactapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Profile extends AppCompatActivity {

    boolean edit = false;
    Spinner brgy;
    EditText cNo, bday, add1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Profile");
        setContentView(R.layout.profile);

        cNo = findViewById(R.id.profile_txtcNo);
        bday = findViewById(R.id.profile_txtDoB);
        add1 = findViewById(R.id.profile_txtAdd1);

        brgy = findViewById(R.id.profile_addBa);
        brgy.setEnabled(false);
    }

    public void changeMode(View view) {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.profile_fabEdit);

        if(edit) {
            //call on destroy to save or discard settings(if press back)
            //show if save settings dialog
            if(true/*if said yes*/)
                if(true/*if data is valid*/) {
                    //save to firebase and face API

                    cNo.setEnabled(false);
                    bday.setEnabled(false);
                    add1.setEnabled(false);
                    fab.setImageResource(R.drawable.ic_pencil);
                }
        } else {
            cNo.setEnabled(true);
            bday.setEnabled(true);
            add1.setEnabled(true);
            fab.setImageResource(R.drawable.ic_check);
        }
        edit = !edit;
    }
}