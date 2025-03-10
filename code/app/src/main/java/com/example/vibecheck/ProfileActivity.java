package com.example.vibecheck;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    private Button logoutButton;
    private Switch notificationsSwitch;
    private Switch publicSwitch;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile); // Ensure XML is named activity_profile.xml

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        logoutButton = findViewById(R.id.logout_button);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        publicSwitch = findViewById(R.id.public_switch);

        // Set click listener for logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sign out from Firebase
                mAuth.signOut();

                // Navigate to LoginActivity
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // Close ProfileActivity
            }
        });

        // Set listeners for switches (optional demonstration)
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Notifications: " + (isChecked ? "On" : "Off"), Toast.LENGTH_SHORT).show();
        });

        publicSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(this, "Public: " + (isChecked ? "On" : "Off"), Toast.LENGTH_SHORT).show();
        });
    }
}