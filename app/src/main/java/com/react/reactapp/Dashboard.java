package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    private CommonFunctions cf;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("REaCT");
        setContentView(R.layout.dashboard);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Stats");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        cf = new CommonFunctions();
        cf.fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dbRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot entry : snapshot.getChildren()) {
                    String situationer = String.valueOf(entry.child("situationer").getValue());
                    if(!situationer.equals("#")) {

                        StorageReference photoRef = mStorageRef.child("Infographics/"+situationer);
                        final long ONE_MB = 1024*1024 * 5;
                        photoRef.getBytes(ONE_MB).addOnSuccessListener(bytes -> {
                            Bitmap bmpLicense = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            ((ImageView) findViewById(R.id.dash_imgSituationer)).setImageBitmap(bmpLicense);
                        }).addOnFailureListener(e -> {
                            Log.e("photoRef Error", e.toString());
                        });

                    } else {
                        ((ImageView) findViewById(R.id.dash_imgSituationer)).setImageResource(R.drawable.nd_situationer);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (CommonFunctions.menu(this, item, "Dashboard"))
            finish();
        return true;
    }

    public void locationHistory(View view) {
        finish();
        startActivity(new Intent(Dashboard.this, Location.class));
    }

    public void healthStatus(View view) {
        finish();
        startActivity(new Intent(Dashboard.this, Health.class));
    }

    public void showCases(View view) {
        finish();
        startActivity(new Intent(Dashboard.this, Cases.class));
    }

    public void debug(View view) {
        Toast.makeText(Dashboard.this, String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()), Toast.LENGTH_LONG).show();
        Log.d("Debug Dash> ","Current timestamp: " + String.valueOf(new Timestamp(System.currentTimeMillis()).getTime()));
    }
}