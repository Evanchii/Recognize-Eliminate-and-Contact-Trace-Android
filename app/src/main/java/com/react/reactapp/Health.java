package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

public class Health extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRef;
    boolean status;
    String vaccination;
    MaterialCardView cardStatus, cardVacc;
    TextView textStatus, textVacc;
    ImageView imgStatus, imgVacc;
    CommonFunctions cf = new CommonFunctions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Health Status");
        setContentView(R.layout.health);

        mAuth = FirebaseAuth.getInstance();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("info");

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(2).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        cardStatus = findViewById(R.id.health_cardStatus);
        textStatus = findViewById(R.id.health_txtStatus);
        imgStatus = findViewById(R.id.health_imgIcon);

        cardVacc = findViewById(R.id.health_cardVaccination);
        textVacc = findViewById(R.id.health_txtVaccination);
        imgVacc = findViewById(R.id.health_iconVaccination);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                status = (boolean) snapshot.child("status").getValue();
                vaccination = snapshot.child("vaccine").getValue().toString();

                setStatus();
                setVaccine();
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
        if(CommonFunctions.menu(this, item, "Health Status"))
            finish();
        return true;
    }

    @Override
    protected void onResume() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                status = (boolean) snapshot.child("status").getValue();
                vaccination = snapshot.child("vaccine").getValue().toString();

                setStatus();
                setVaccine();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}
        });
        super.onResume();
    }

    private void setVaccine() {
        cardVacc.setOnClickListener(null);
        switch (vaccination) {
            case "pending":
                cardVacc.setStrokeColor(Color.parseColor("#FF3c3f41"));
                cardVacc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                    }
                });
                textVacc.setText("Application\nPending");
                imgVacc.setImageResource(R.drawable.ic_pending);
                break;
            case "true":
                cardVacc.setStrokeColor(Color.parseColor("#FF4A9F7E"));
                textVacc.setText("Vaccinated");
                imgVacc.setImageResource(R.drawable.ic_vaccinated);
                break;
            case "false":
                cardVacc.setStrokeColor(Color.parseColor("#FFA83C52"));
                textVacc.setText("Not Vaccinated");
                imgVacc.setImageResource(R.drawable.ic_not_vaccinated);
                break;
        }
    }

    private void setStatus() {
        if(status) {
            cardStatus.setStrokeColor(Color.parseColor("#FFA83C52"));
            textStatus.setText("Positive");
            imgStatus.setColorFilter(Color.parseColor("#FFFE254A"));
        } else {
            cardStatus.setStrokeColor(Color.parseColor("#FF4A9F7E"));
            textStatus.setText("Negative");
            imgStatus.setColorFilter(Color.parseColor("#FF439576"));
        }
    }

    public void healthStatus(View view) {
        AlertDialog.Builder confirm = new AlertDialog.Builder(Health.this)
                .setTitle("Confirm Action")
                .setMessage("Change COVID-19 Status? This will send an alert to the LGU and DOH")
                .setPositiveButton("Yes", (dialog, which) -> {
                    status = !status;
                    if(status) {
//                        cf.covidNotification(mAuth.getCurrentUser().getUid());

                        RunnableFuture<Void> sendCovidNotifs = new FutureTask<>(new Callable<Void>() {
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();
                            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(),
                                    userHisRef = dbRef.child("Users/"+mAuth.getCurrentUser()+"/history"),
                                    hisRef = dbRef.child("History"),
                                    notifRef = dbRef.child("Notifications");

                            @Override
                            public Void call() throws Exception {

                                userHisRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot userHisSnap) {
                                        hisRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot hisSnap) {
                                                ArrayList<String> estUID = new ArrayList<String>();
                                                long DAY_IN_MS = 1000 * 60 * 60 * 24;
                                                Date d14 = new Date(System.currentTimeMillis() - (14 * DAY_IN_MS));
                                                for(DataSnapshot date : userHisSnap.getChildren()) { //date : ts
                                                    try {
                                                        Date date1=new SimpleDateFormat("yyyy/MM/dd").parse(date.getKey());
                                                        if(date1.before(d14))
                                                            continue;
                                                    } catch (ParseException e) {
                                                        e.printStackTrace();
                                                    }

                                                    for(DataSnapshot ts : date.getChildren()) { //ts : ts
                                                        estUID.add(hisSnap.child(date.getKey() + "/" + ts.getKey() + "/estUID").getValue().toString());
                                                    }
                                                }
                                                perEstNotif(estUID);
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
                                return null;
                            }

                            void perEstNotif(ArrayList<String> estUIDs) {
                                notifRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot notifSnap) {
                                        dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                hisRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot hisSnap) {
                                                        Long tsLong = System.currentTimeMillis()/1000;
                                                        while(notifSnap.hasChild(String.valueOf(tsLong))) {
                                                            tsLong++;
                                                        }
                                                        String nTS = tsLong.toString();
                                                        notifRef.child(nTS+"/type").setValue("health-alert");
                                                        notifRef.child(nTS+"/title").setValue("Reported Positive Contact");
                                                        notifRef.child(nTS+"/message").setValue("A user that you\\'ve been in contact within the last 14 days has reported that they\\'ve tested positive. Please be cautious!");
                                                        for(String estUID : estUIDs) {
                                                            for (DataSnapshot date : snapshot.child(estUID + "/history").getChildren()) {
                                                                for(DataSnapshot ts : date.getChildren()) {
                                                                    dbRef.child("Users/"+hisSnap.child(date.getKey() + "/" + ts.getKey() + "/uid").getValue()+"/notifs/"+nTS).setValue(nTS);
                                                                }
                                                            }
                                                        }
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

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });

                        new Thread(sendCovidNotifs).start();

                    }
                    dbRef.setValue(status);
                    setStatus();
                    dialog.dismiss();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                });
        confirm.show();
    }
}