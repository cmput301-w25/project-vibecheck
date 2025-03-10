package com.example.vibecheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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
        // Set the layout to activity_home.xml
        setContentView(R.layout.activity_home);


        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //Navigation destinations / menu ID's
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_history,
                R.id.navigation_post,
                R.id.navigation_map,
                R.id.navigation_profile
        ).build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // Set up the navigation listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                Toast.makeText(this, "You are already on this page.", Toast.LENGTH_SHORT).show();
                return true;

            } else if (itemId == R.id.navigation_history) {
                //Navigate to the History Fragment
                navController.navigate(R.id.navigation_history);
                return true;

            } else if (itemId == R.id.navigation_post) {
                //Open the Post Mood Event Activity
                Intent intent = new Intent(HomeActivity.this, AddMoodEventActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_map) {
                //Navigate to Map Fragment
                navController.navigate(R.id.navigation_map);
                return true;

            } else if (itemId == R.id.navigation_profile) {
                //Navigate to Profile Activity
                //navController.navigate(R.id.navigation_profile);
                return true;
            } else {
                return false;
            }
        });
    }
}