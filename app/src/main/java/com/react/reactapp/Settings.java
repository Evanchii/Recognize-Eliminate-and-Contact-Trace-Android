package com.react.reactapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Settings");
        setContentView(R.layout.settings);

        mAuth = FirebaseAuth.getInstance();

        new CommonFunctions().fetchHamburgerDetails((NavigationView) findViewById(R.id.navigation_view));
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerButton);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(4).setChecked(true);

        View headerView = navigationView.getHeaderView(0);
        CardView headerCard = (CardView) headerView.findViewById(R.id.header_cardMain);
        headerCard.setOnClickListener(v -> {
            startActivity(new Intent(this, Profile.class));
        });

        Button password = (Button) findViewById(R.id.settings_btnPassword),
                about = (Button) findViewById(R.id.settings_btnAbout),
                privacy = (Button) findViewById(R.id.settings_btnPrivacy),
                logout = (Button) findViewById(R.id.settings_btnLogOut);
        password.setOnClickListener(v -> {
            startActivity(new Intent(Settings.this, Password.class));
        });
        about.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://react-app.ga/pages/aboutus.php/"))));
        privacy.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://react-app.ga/pages/privacy.php/"))));
        logout.setOnClickListener(v -> {
            AlertDialog.Builder confirm = new AlertDialog.Builder(Settings.this)
                    .setTitle("Log Out?")
                    .setMessage("Do you wish to log out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        mAuth.signOut();
                        dialog.dismiss();
                        finish();
                        startActivity(new Intent(Settings.this, Login.class));
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss();
                    });
            confirm.show();
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
        if(CommonFunctions.menu(this, item, "Settings"))
            finish();
        return true;
    }
}