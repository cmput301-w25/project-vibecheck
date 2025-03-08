package com.example.vibecheck;

import com.google.android.material.bottomnavigation.BottomNavigationView;


import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.vibecheck.NetworkStatusChecker;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.vibecheck.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private NetworkStatusChecker networkChecker;
    private ActivityMainBinding binding;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        //Bottom navigation
        BottomNavigationView navView = findViewById(R.id.nav_view);

        //Navigation destinations / menu ID's
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_history,
                R.id.navigation_post,
                R.id.navigation_map,
                R.id.navigation_profile
        ).build();

        //Navigation controller
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        //Initialize the NetworkStatusChecker
        networkChecker = new NetworkStatusChecker(this);
        networkChecker.startChecking();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkChecker != null) {
            networkChecker.stopChecking();

            //Prevent memory leaks
            networkChecker = null;
        }
    }
}