package com.react.reactapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter ;
    TabLayout tabIndicator;
    int position = 0 ;
    Animation btnAnim ;
    TextView tvSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_intro);// ini views
        tabIndicator = findViewById(R.id.tab_indicator);

        // fill list screen for the Lay
        final List<ScreenItem> mList = new ArrayList<>();
        switch(getIntent().getIntExtra("position",-1)) {
            case 0:
                //Forgot Password
                mList.add(new ScreenItem("Forgot Password - Intro", "Forgot your password? Don't worry resetting your password is very easy!", R.drawable.help0_0));
                mList.add(new ScreenItem("Forgot Password - 1", "In the log-in screen press the Forgot Password button above the sign in button", R.drawable.help0_1));
                mList.add(new ScreenItem("Forgot Password - 2", "A dialogue box will open asking for your email address", R.drawable.help0_2));
                mList.add(new ScreenItem("Forgot Password - 3", "After entering your email, click on reset", R.drawable.help0_3));
                mList.add(new ScreenItem("Forgot Password - 4", "An email will be sent to your inbox (check your spam as well) containing the link for the password reset request.", R.drawable.help0_4));
                break;
            case 1:
                //View/Edit Profile
                mList.add(new ScreenItem("View/Edit Profile - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help1_0));
                mList.add(new ScreenItem("View/Edit Profile - 2", "Click on the header/card containing your photo and name", R.drawable.help1_1));
                mList.add(new ScreenItem("View/Edit Profile - 3", "You will then be redirected to your profile, you can edit your details by pressing the edit button at the top-right corner of you screen", R.drawable.help1_2));
                mList.add(new ScreenItem("View/Edit Profile - 4", "After editing, you can save all of your information by pressing the save button at the top-right corner", R.drawable.help1_3));
                mList.add(new ScreenItem("View/Edit Profile - End", "Viola! All edited information will be saved in the database ready for viewing", R.drawable.help1_4));
                break;
            case 2:
                //View Covid Cases
                mList.add(new ScreenItem("View Covid Cases - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help2_0));
                mList.add(new ScreenItem("View Covid Cases - 2", "Select the Covid Cases item in the menu", R.drawable.help2_1));
                mList.add(new ScreenItem("View Covid Cases - End", "You will then be redirected to the Covid Cases screen", R.drawable.help2_2));
                break;
            case 3:
                //Set health status
                mList.add(new ScreenItem("Set Health Status - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help3_0));
                mList.add(new ScreenItem("Set Health Status - 2", "Select the health status item in the menu", R.drawable.help3_1));
                mList.add(new ScreenItem("Set Health Status - 3", "Select the health status card", R.drawable.help3_2));
                mList.add(new ScreenItem("Set Health Status - 4", "Upon doing so, you will be asked to confirm your actions", R.drawable.help3_3));
                mList.add(new ScreenItem("Set Health Status - End", "Afterwards, your health status will be toggled", R.drawable.help3_4));
                break;
            case 4:
                //Set vacc status
                mList.add(new ScreenItem("Set Vaccination Status - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help4_0));
                mList.add(new ScreenItem("Set Vaccination Status - 2", "Select the health status item in the menu", R.drawable.help4_1));
                mList.add(new ScreenItem("Set Vaccination Status - 3", "Select the vaccination status card", R.drawable.help4_2));
                mList.add(new ScreenItem("Set Vaccination Status - 4", "You will be redirected to the application screen, you will be asked to enter in your information related to the vaccination.", R.drawable.help4_3));
                mList.add(new ScreenItem("Set Vaccination Status - 5", "A dialogue box will then confirm that the application has been sent", R.drawable.help4_4));
                mList.add(new ScreenItem("Set Vaccination Status - FAQ", "When you are considered \"Not vaccinated\", you can apply for a request to toggle your status. When your status is \"pending\" or \"vaccinated\", pressing on the vaccination status card will do nothing.", R.drawable.ic_questionmark));
                break;
            case 5:
                //QR
                mList.add(new ScreenItem("QR Code - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help5_0));
                mList.add(new ScreenItem("QR Code - 2", "Select the QR Code item in the menu", R.drawable.help5_1));
                mList.add(new ScreenItem("QR Code - End", "You will then be redirected to the QR Code screen, where you can save your QR Code.", R.drawable.help5_2));
                break;
            case 6:
                //History
                mList.add(new ScreenItem("View History - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help6_0));
                mList.add(new ScreenItem("View History - 2", "Select the Location History item in the menu", R.drawable.help6_1));
                mList.add(new ScreenItem("View History - End", "You will then be redirected to the Location History screen", R.drawable.help6_2));
                break;
            case 7:
                //Notifications
                mList.add(new ScreenItem("View Notifications - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help7_0));
                mList.add(new ScreenItem("View Notifications - 2", "Select the Notifications item in the menu", R.drawable.help7_1));
                mList.add(new ScreenItem("View Notifications - End", "You will then be redirected to the Notifications screen", R.drawable.help7_2));
                break;
            case 8:
                //ChangePW
                mList.add(new ScreenItem("Change Password - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help8_0));
                mList.add(new ScreenItem("Change Password - 2", "Select the Settings item in the menu", R.drawable.help8_1));
                mList.add(new ScreenItem("Change Password - 3", "In the settings screen, select change password", R.drawable.help8_2));
                mList.add(new ScreenItem("Change Password - End", "You will be redirected to the Change Password screen", R.drawable.help8_3));
                break;
            case 9:
                //Logout
                mList.add(new ScreenItem("Log out - 1", "In the dashboard, open the navigation/hamburger menu", R.drawable.help9_0));
                mList.add(new ScreenItem("Log out - 2", "Select the Settings item in the menu", R.drawable.help9_1));
                mList.add(new ScreenItem("Log out - 3", "In the settings screen, select log out", R.drawable.help9_2));
                mList.add(new ScreenItem("Log out - 4", "You will be asked if you really wish to log out", R.drawable.help9_3));
                mList.add(new ScreenItem("Log out - End", "After logging out, you will be redirected back to the login page", R.drawable.help9_4));
                break;
            default:
                //ContactUS
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","team.react2021@gmail.com", null));
                email.putExtra(Intent.EXTRA_SUBJECT, "Support - "+mAuth.getUid());
                email.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(email, "Choose an Email client :"));
                finish();
                break;
        }
        mList.add(new ScreenItem("Got more questions?", "END-CONTACT", R.drawable.ic_questionmark));

        // setup viewpager
        screenPager =findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // setup tab-layout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // tab-layout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1) {
                    loaddLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private void loaddLastScreen() {
        tabIndicator.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}