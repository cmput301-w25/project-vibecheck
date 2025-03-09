package com.example.vibecheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    // Declare UI elements
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView iconSearch;
    private ImageView iconFilter;
    private ImageView iconNotifications;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout to activity_home.xml
        setContentView(R.layout.activity_home);

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Hide the default title to use our custom TextView
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize Toolbar components
        toolbarTitle = findViewById(R.id.title);
        iconSearch = findViewById(R.id.icon_search);
        iconFilter = findViewById(R.id.icon_filter);
        iconNotifications = findViewById(R.id.icon_notifications);

        // Set click listeners for toolbar icons
        iconSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Search clicked", Toast.LENGTH_SHORT).show();
                // Replace this with actual search functionality (e.g., start a search activity)
            }
        });

        iconFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Filter clicked", Toast.LENGTH_SHORT).show();
                // Replace this with actual filter functionality (e.g., show a filter dialog)
            }
        });

        iconNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "Notifications clicked", Toast.LENGTH_SHORT).show();
                // Replace this with actual notifications functionality (e.g., open notifications screen)
            }
        });

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_nav);

        // Set up the navigation listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                Toast.makeText(this, "Home selected", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
    }
}