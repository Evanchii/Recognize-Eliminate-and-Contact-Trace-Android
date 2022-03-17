package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class Location extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationAdapter.ItemClickListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference userHisRef, hisRef;
    //<Date, <Timestamp, <key, value>>>
    HashMap<String, HashMap<String, HashMap<String, String>>> history;
    LocationAdapter adapter;
    boolean isFinished = false;
    final AtomicBoolean done = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Location History");
        setContentView(R.layout.location);

        mAuth = FirebaseAuth.getInstance();
        userHisRef = FirebaseDatabase.getInstance().getReference("Users/"+mAuth.getCurrentUser().getUid()+"/history");
        hisRef = FirebaseDatabase.getInstance().getReference("History");

        history = new HashMap<>();

        //get data from rtdb
        userHisRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot userSnap) {
                hisRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot hisSnap) {
                        history = new HashMap<>();
                        for (DataSnapshot date : userSnap.getChildren()) {
                            HashMap<String, HashMap<String, String>> innerMap = new HashMap<>();
                            for (DataSnapshot ts : date.getChildren()) {
                                Log.d("LocationData", ts.getKey());
                                innerMap.put(ts.getKey().toString(), (HashMap<String, String>) hisSnap.child(date.getKey().toString() + "/" + ts.getKey().toString()).getValue());
                            }
                            history.put (date.getKey(), innerMap);
                        }

                        inflateLocation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(3).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
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
        if (CommonFunctions.menu(this, item, "Location History"))
            finish();
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    public void inflateLocation() {
        if(!history.isEmpty()) {
            TextView empty = findViewById(R.id.loc_txtEmpty);
            empty.setVisibility(View.GONE);
            int i = 0;
            LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (String key : history.keySet()) {
                Log.d("Inflator >", key);
                LinearLayout ll = findViewById(R.id.location_ll);
                View v = vi.inflate(R.layout.template_location, ll, false);

                TextView date = (TextView) v.findViewById(R.id.tmpLoc_txtDate);
                try {
                    Date dt = new SimpleDateFormat("yyyy-MM-dd").parse(key);
                    SimpleDateFormat dt1 = new SimpleDateFormat("MMMM dd, yyyy");
                    date.setText(dt1.format(dt));
                } catch (Exception e) {
                    date.setText(key);
                    Log.d("LocInf-Error>", e.getMessage());
                }

                RecyclerView rv = v.findViewById(R.id.tmpLoc_recView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Location.this) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                };
                rv.setLayoutManager(linearLayoutManager);
                rv.setNestedScrollingEnabled(false);
                adapter = new LocationAdapter(Location.this, history.get(key));
                adapter.setClickListener(Location.this);
                rv.setAdapter(adapter);
                ll.addView(v, i++);
            }
        }
    }
}