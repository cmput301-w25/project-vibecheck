/**
 * This Java class is the activity for the home screen which doubles as the main activity for the app once a user is logged in.
 * It holds the fragments to display above the bottom navigation bar, and handles navigation to other activities when an icon
 * in the bottom navigation bar is clicked.
 *
 * This class itself has no outstanding issues, though currently there are problems in other activities this class navigates to.
 */

package com.example.vibecheck.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.vibecheck.ui.map.MapActivity;
import com.example.vibecheck.ui.history.Singleton;
import com.example.vibecheck.ui.moodevents.AddMoodEventActivity;
import com.example.vibecheck.ui.history.MoodHistoryActivity;
import com.example.vibecheck.ui.profile.ProfileActivity;
import com.example.vibecheck.R;
import com.example.vibecheck.databinding.ActivityHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ActivityHomeBinding binding;
    private NavController navController;
    private Singleton singleton;

    /**
     * The onCreate function initializes the ui elements, binding, and navigation through the bottom nav menu.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Singleton for passing "states" between activities
        singleton = Singleton.getINSTANCE();


        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        // Top padding
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

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
                Intent intent = new Intent(HomeActivity.this, MapActivity.class);
                startActivity(intent);
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