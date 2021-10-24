package com.react.reactapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class CommonFunctions extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private StorageReference mStorageRef;

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
                    i = new Intent(con, Cases.class);
                break;
            case R.id.action_health:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Health.class);
                break;
            case R.id.action_location:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Location.class);
                break;
            case R.id.action_settings:
                if(!src.equals(item.getTitle()))
                    i = new Intent(con, Settings.class);
                break;
        }
        if(i!=null) {
            con.startActivity(i);
            return true;
        }
        return false;
    }

    public void fetchHamburgerDetails(NavigationView nv) {
        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getUid()).child("info");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        View headerView = nv.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.header_username);
        ImageView pic = (ImageView) headerView.findViewById(R.id.header_face);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uName = String.valueOf(snapshot.child("lName").getValue()) +", "+ String.valueOf(snapshot.child("fName").getValue()) + " " + String.valueOf(snapshot.child("mName").getValue());
                name.setText(String.valueOf(uName));
                String face = (String) snapshot.child("faceID").getValue();
                StorageReference photoRef = mStorageRef.child(face);
                final long ONE_MB = 1024*1024 * 5;
                photoRef.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                    Bitmap bmpLicense = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    pic.setImageBitmap(bmpLicense);
//                    pic.getLayoutParams().height = pic.getLayoutParams().width; remove
                }).addOnFailureListener(e -> {
                    Log.e("photoRef Error", e.toString());
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }

    public int getBrgyIndex(String brgy) {
        List<String> ArrayBrgy = Arrays.asList(getResources().getStringArray(R.array.barangay));
        return ArrayBrgy.indexOf(brgy);
    }
}
