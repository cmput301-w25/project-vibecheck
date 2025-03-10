package com.example.vibecheck; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etUsername, etPassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind UI elements from the existing XML layout:
        // Note: the IDs are taken directly from your XML file.
        etEmail = findViewById(R.id.edit_text_signup_username); // Email input (despite the id name)
        etUsername = findViewById(R.id.edit_text_username);         // Username input
        etPassword = findViewById(R.id.edit_text_confirm_password);   // Password input

        btnSignUp = findViewById(R.id.signup_button);

        // Set click listener for the Sign Up button
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve and trim input values
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Create a new user with email and password using Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Prepare user data for Firestore
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("email", email);
                                    userData.put("username", username);
                                    userData.put("displayName", username);  // Added field: displayName same as username
                                    userData.put("uid", user.getUid());

                                    // Add user data to Firestore
                                    db.collection("users").document(user.getUid())
                                            .set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(SignUpActivity.this,
                                                        "Sign up successful", Toast.LENGTH_SHORT).show();
                                                // Navigate to LoginActivity
                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(SignUpActivity.this,
                                                        "Failed to add user to Firestore: " + e.getMessage(),
                                                        Toast.LENGTH_LONG).show();
                                            });
                                }
                            } else {
                                // Handle authentication failure
                                Toast.makeText(SignUpActivity.this,
                                        "Sign up failed: " + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
