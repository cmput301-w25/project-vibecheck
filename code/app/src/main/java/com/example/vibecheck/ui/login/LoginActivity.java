package com.example.vibecheck.ui.login; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.home.HomeActivity;
import com.example.vibecheck.ui.signup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;

/**
 * LoginActivity handles user login functionality using Firebase Authentication.
 * <p>
 * It provides an interface for users to input their email (or account) and password,
 * and offers navigation to the Sign Up screen if needed.
 * </p>
 */

public class LoginActivity extends AppCompatActivity {
    /**
     * EditText for user email input.
     */
    /**
     * EditText for user password input.
     */
    private EditText etEmail, etPassword;
    /**
     * Button to initiate login process.
     */
    private Button btnLogin;
    /**
     * TextView that acts as a link to the Sign Up activity.
     */
    private TextView tvSignUp;
    /**
     * Instance of Firebase Authentication.
     */
    private FirebaseAuth mAuth;
    /**
     * Called when the activity is first created.
     * <p>
     * This method initializes the Firebase Authentication instance, binds UI elements,
     * and sets up click listeners for login and sign up actions.
     * </p>
     *
     * @param savedInstanceState the saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Matches the provided XML layout

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Bind UI elements to variables using IDs from activity_login.xml
        etEmail = findViewById(R.id.edit_text_login_username); // Using "Account" as email
        etPassword = findViewById(R.id.edit_text_login_password);
        btnLogin = findViewById(R.id.login_button);
        tvSignUp = findViewById(R.id.link_to_signup);

        // Set click listener for the Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the login button.
             * <p>
             * Validates user input and attempts to sign in using Firebase Authentication.
             * If login is successful, a success message is shown. Otherwise, an error message is displayed.
             * </p>
             *
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                // Get input values and remove leading/trailing whitespace
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Sign in user with Firebase Authentication
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<com.google.firebase.auth.AuthResult>() {
                            /**
                             * Called when the sign-in task is complete.
                             *
                             * @param task the task containing the sign-in result
                             */
                            @Override
                            public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Login successful
                                    Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                                    //Get the current user's ID
                                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    // Obtain the user's username from Firestore to store globally for easy access
                                    FirebaseFirestore.getInstance().collection("users")
                                            .document(uid)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                String username = documentSnapshot.getString("username");
                                                if (username != null) {
                                                    MoodUtils.setCurrentUsername(username);
                                                    MoodUtils.populateUserMoodHistory();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(LoginActivity.this, "Failed to load user profile", Toast.LENGTH_SHORT).show();
                                            });

                                    // Navigate to home activity
                                     startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                     finish();
                                } else {
                                    // Login failed
                                    Toast.makeText(LoginActivity.this,
                                            "Login failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        // Set click listener for the Sign Up link
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the sign-up link.
             * <p>
             * Navigates the user to the SignUpActivity to create a new account.
             * </p>
             *
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                // Navigate to SignUpActivity
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }
}
