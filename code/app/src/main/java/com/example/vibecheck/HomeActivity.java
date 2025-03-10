/*
This Java file operates most of the front end of the app after logging in, mainly to display the home screen
feed, user posts, and logged in user posts. Will eventually have the map fragment implemented to match
our storyboards.

Outstanding issues: Distinguishing user posts from the logged in user posts, only separate user mood event viewing functionality currently,
map functionality not implemented.
 */


package com.example.vibecheck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vibecheck.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ActivityHomeBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtains the navController from the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            Log.d("HomeActivity", "NavController found successfully.");
        }

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) {
            Log.e("HomeActivity", "ERROR: BottomNavigationView is NULL! Check activity_home.xml.");
        } else {
            Log.d("HomeActivity", "BottomNavigationView found successfully.");
        }

        // Set up the navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.d("HomeActivity", "Bottom Navigation Clicked: " + item.getItemId());

            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                if (navController.getCurrentDestination().getId() == R.id.navigation_home) {
                    Toast.makeText(this, "You are already on this page.", Toast.LENGTH_SHORT).show();
                } else {
                    navController.navigate(R.id.navigation_home);
                }
                return true;

            } else if (itemId == R.id.navigation_history) {
                Log.d("HomeActivity", "Opening MoodHistoryActivity...");
                Intent intent = new Intent(HomeActivity.this, MoodHistoryActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_post) {
                Log.d("HomeActivity", "Opening AddMoodEventActivity...");
                Intent intent = new Intent(HomeActivity.this, AddMoodEventActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_map) {
                Log.d("HomeActivity", "Opening MapActivity...");
                //Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                //startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_profile) {
                Log.d("HomeActivity", "Opening ProfileActivity...");
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });
    }
}