package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class Health extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    boolean status = false;
    MaterialCardView cardStatus;
    TextView textStatus;
    ImageView imgStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Health Status");
        setContentView(R.layout.health);

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
        status = false;
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

    private void setStatus() {
        if(status) {
            //set confirm dialog box
            cardStatus.setStrokeColor(Color.parseColor("#FFA83C52"));
            textStatus.setText("Positive");
            imgStatus.setColorFilter(Color.parseColor("#FFFE254A"));
        } else {
            //set confirm dialog box
            cardStatus.setStrokeColor(Color.parseColor("#FF4A9F7E"));
            textStatus.setText("Negative");
            imgStatus.setColorFilter(Color.parseColor("#FF439576"));
        }
    }

    public void healthStatus(View view) {
        status = !status;
        setStatus();
    }
}