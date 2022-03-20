package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Notifications extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference userNotifs, notifPool;
    HashMap<String, HashMap<String, String>> notifications;
    private CommonFunctions cf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        setContentView(R.layout.notifications);

        cf = new CommonFunctions();
        cf.fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(5).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        mAuth = FirebaseAuth.getInstance();
        userNotifs = FirebaseDatabase.getInstance().getReference().child("Users/"+mAuth.getCurrentUser().getUid()+"/notifs");
        notifPool = FirebaseDatabase.getInstance().getReference().child("Notifications");

        notifications = new HashMap<>();

        userNotifs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnap) {
                notifPool.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot notifSnap) {
                        for(DataSnapshot ts : userSnap.getChildren()) {
                            HashMap<String, String> l2 = new HashMap<>();
                            Log.d("N(69)", "" +ts.getKey());
                            for(DataSnapshot info : notifSnap.child(ts.getKey()).getChildren()) {
                                Log.d("N(71)", "\t" + info.getKey() + " : " + info.getValue().toString());
                                l2.put(info.getKey(), info.getValue().toString());
                            }
                            notifications.put(ts.getKey(), l2);
                        }
                        inflateData();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void inflateData() {
        if(!notifications.isEmpty()) {
            TextView empty = findViewById(R.id.txt_notifsEmpty);
            empty.setVisibility(View.GONE);
            RecyclerView rv = findViewById(R.id.rv_notifs);
            rv.setLayoutManager(new LinearLayoutManager(this));
            NotificationsAdapter adapter = new NotificationsAdapter(this, notifications);
            rv.setAdapter(adapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        if (CommonFunctions.menu(this, item, "Notifications"))
            finish();
        return true;
    }
}