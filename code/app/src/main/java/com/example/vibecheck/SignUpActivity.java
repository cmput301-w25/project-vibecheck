package com.example.vibecheck;

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

/**
 * SignUpActivity handles user registration by creating a new account using Firebase Authentication.
 * <p>
 * Upon successful sign up, the user's information is stored in Firestore under the "users" collection,
 * and the user is redirected to the LoginActivity.
 * </p>
 */
public class SignUpActivity extends AppCompatActivity {
    private EditText etEmail, etUsername, etPassword;
    private Button btnSignUp;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    /**
     * Called when the activity is first created.
     * <p>
     * This method initializes Firebase instances, binds the UI elements, and sets up the click listener
     * for the sign-up button.
     * </p>
     *
     * @param savedInstanceState the previously saved state of the activity, if any
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.edit_text_signup_username);
        etUsername = findViewById(R.id.edit_text_username);
        etPassword = findViewById(R.id.edit_text_confirm_password);
        btnSignUp = findViewById(R.id.signup_button);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            /**
             * Handles the click event for the sign-up button.
             * <p>
             * It retrieves input from the user, validates that all fields are filled, and first checks whether
             * the chosen username is unique by querying Firestore. If the username is unique, it attempts to create a new user with Firebase Authentication.
             * </p>
             *
             * @param v the view that was clicked
             */
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if the username is unique before creating the user
                db.collection("users")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().isEmpty()) {
                                    // Username is unique, proceed with user creation
                                    mAuth.createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(SignUpActivity.this, authTask -> {
                                                if (authTask.isSuccessful()) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    if (user != null) {
                                                        Map<String, Object> userData = new HashMap<>();
                                                        userData.put("email", email);
                                                        userData.put("username", username);
                                                        userData.put("displayName", username);
                                                        userData.put("uid", user.getUid());

                                                        db.collection("users").document(user.getUid())
                                                                .set(userData)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    Toast.makeText(SignUpActivity.this,
                                                                            "Sign up successful", Toast.LENGTH_SHORT).show();
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
                                                    Toast.makeText(SignUpActivity.this,
                                                            "Sign up failed: " + authTask.getException().getMessage(),
                                                            Toast.LENGTH_LONG).show();
                                                }
                                            });
                                } else {
                                    // Username already exists
                                    Toast.makeText(SignUpActivity.this,
                                            "Username is already taken. Please choose another username.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(SignUpActivity.this,
                                        "Error checking username uniqueness: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}
