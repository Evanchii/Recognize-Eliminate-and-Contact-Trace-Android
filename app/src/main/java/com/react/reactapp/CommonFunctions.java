package com.react.reactapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;

public class CommonFunctions extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    public static boolean menu(Context con, MenuItem item, String src) {
        Intent i = null;
//        mAuth = FirebaseAuth.getInstance();
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Dashboard.class);
                break;
            case R.id.action_covid:
                if(!src.equals(item.getTitle()))
//                    i = new Intent(con, COVID.class);
                break;
            case R.id.action_health:
                if(!src.equals(item.getTitle()))
//                    i = new Intent(con, Health.class);
                break;
            case R.id.action_location:
                if(!src.equals(item.getTitle()))
//                    i = new Intent(con, Location.class);
                break;
            case R.id.action_settings:
                if(!src.equals(item.getTitle()))
//                    i = new Intent(con, Settings.class);
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails(NavigationView nv) {
//        mAuth = FirebaseAuth.getInstance();
//        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid());

        View headerView = nv.getHeaderView(0);
//        TextView name = (TextView) headerView.findViewById(R.id.header_username);
//        TextView email = (TextView) headerView.findViewById(R.id.header_email);

//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String uName = String.valueOf(snapshot.child("Username").getValue());
//                name.setText(String.valueOf(uName));
//                email.setText(mAuth.getCurrentUser().getEmail());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
    }
}
