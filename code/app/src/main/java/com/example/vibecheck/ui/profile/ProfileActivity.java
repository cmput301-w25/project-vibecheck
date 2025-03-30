package com.example.vibecheck.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.home.HomeActivity;
import com.example.vibecheck.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private EditText personalInfoEditText;
    private EditText displayNameEditText;
    private ImageView editIcon, backArrow;
    private TextView tvUsername;
    private Button logoutButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    // Store the original display name loaded from Firestore
    private String originalDisplayName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d("ProfileActivity", "onCreate called");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Retrieve views from the layout.
        // Our layout is a ScrollView with a LinearLayout child.
        View rootView = findViewById(android.R.id.content);
        ScrollView scrollView = (ScrollView) ((ViewGroup) rootView).getChildAt(0);
        ViewGroup mainLinearLayout = (ViewGroup) scrollView.getChildAt(0);

        // Profile Header (child index 0 of main layout):
        // Child order:
        // 0: back_arrow, 1: display_name_edit_text, 2: edit_icon, 3: username TextView.
        ViewGroup profileHeader = (ViewGroup) mainLinearLayout.getChildAt(0);
        backArrow = (ImageView) profileHeader.getChildAt(0);
        displayNameEditText = (EditText) profileHeader.getChildAt(1);
        editIcon = (ImageView) profileHeader.getChildAt(2);
        tvUsername = (TextView) profileHeader.getChildAt(3);

        // Personal Info EditText (child index 3 of main layout)
        personalInfoEditText = (EditText) mainLinearLayout.getChildAt(3);
        // Logout Button (child index 6 of main layout)
        logoutButton = (Button) mainLinearLayout.getChildAt(6);

        loadProfileData();

        // When the user taps the edit icon, enable editing of the display name.
        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ProfileActivity", "Edit icon clicked");
                displayNameEditText.setFocusableInTouchMode(true);
                displayNameEditText.setClickable(true);
                displayNameEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.showSoftInput(displayNameEditText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        // When the display name loses focus, check if it has been changed and verify uniqueness.
        displayNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String newDisplayName = displayNameEditText.getText().toString().trim();
                    Log.d("ProfileActivity", "Display name lost focus. New display name: " + newDisplayName);
                    if (!newDisplayName.equals(originalDisplayName)) {
                        checkDisplayNameUnique(newDisplayName);
                    }
                }
            }
        });

        // Back arrow navigates to HomeActivity.
        backArrow.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        // Logout button
        logoutButton.setOnClickListener(view -> {
            MoodUtils.setCurrentUsername(null);
            MoodUtils.clearUserMoodHistory();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Loads profile data from Firestore. The display name is loaded into the editable field
     * and also used to update the username TextView. The original display name is stored.
     */
    private void loadProfileData() {
        Log.d("ProfileActivity", "Loading profile data...");
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String displayName = documentSnapshot.getString("displayName");
                            Log.d("ProfileActivity", "Firestore returned displayName: " + displayName);
                            if (displayName != null && !displayName.isEmpty()) {
                                displayNameEditText.setText(displayName);
                                tvUsername.setText("@" + MoodUtils.getCurrentUsername());
                                originalDisplayName = displayName;
                            } else {
                                displayNameEditText.setText("No Display Name");
                                tvUsername.setText("@NoDisplayName");
                                originalDisplayName = "No Display Name";
                            }

                            String personalInfo = documentSnapshot.getString("personalInfo");
                            if (personalInfo != null) {
                                personalInfoEditText.setText(personalInfo);
                            }
                        } else {
                            displayNameEditText.setText("No Document");
                            tvUsername.setText("@NoDocument");
                            Log.d("ProfileActivity", "No document found for user");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this,
                                "Failed to load profile data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Error loading profile data: " + e.getMessage());
                    });
        } else {
            displayNameEditText.setText("Not Logged In");
            tvUsername.setText("@NotLoggedIn");
            Log.d("ProfileActivity", "User not logged in");
        }
    }

    /**
     * Saves the personal info field in Firestore.
     */
    private void savePersonalInfo() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String personalInfo = personalInfoEditText.getText().toString().trim();
            db.collection("users")
                    .document(uid)
                    .update("personalInfo", personalInfo)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "Personal info saved", Toast.LENGTH_SHORT).show();
                        Log.d("ProfileActivity", "Personal info saved: " + personalInfo);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Failed to save personal info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Error saving personal info: " + e.getMessage());
                    });
        }
    }

    /**
     * Saves the new display name in Firestore.
     */
    private void saveDisplayName(String newDisplayName) {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users")
                    .document(uid)
                    .update("displayName", newDisplayName)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ProfileActivity.this, "Display name updated", Toast.LENGTH_SHORT).show();
                        Log.d("ProfileActivity", "Display name updated in Firestore: " + newDisplayName);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this, "Failed to update display name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("ProfileActivity", "Error updating display name: " + e.getMessage());
                    });
        }
    }

    /**
     * Checks whether the provided display name is unique across all users.
     * If not unique, a Toast is displayed and the field is reverted to the original value.
     * If unique, the new display name is saved.
     */
    private void checkDisplayNameUnique(String newDisplayName) {
        if (currentUser == null) return;
        String uid = currentUser.getUid();
        Log.d("ProfileActivity", "Checking uniqueness for display name: " + newDisplayName);
        db.collection("users")
                .whereEqualTo("displayName", newDisplayName)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean isUnique = true;
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        if (!doc.getId().equals(uid)) {
                            isUnique = false;
                            Log.d("ProfileActivity", "Found duplicate display name: " + doc.getString("displayName"));
                            break;
                        }
                    }
                    if (!isUnique) {
                        Toast.makeText(ProfileActivity.this, "Display name is already taken. Keeping previous name.", Toast.LENGTH_SHORT).show();
                        Log.d("ProfileActivity", "Display name not unique. Reverting to original: " + originalDisplayName);
                        // Revert back to the original display name.
                        displayNameEditText.setText(originalDisplayName);
                    } else {
                        Log.d("ProfileActivity", "Display name is unique. Saving new display name: " + newDisplayName);
                        // Save the new display name in Firestore and update username TextView.
                        saveDisplayName(newDisplayName);
                        originalDisplayName = newDisplayName;
                        tvUsername.setText("@" + newDisplayName);
                    }
                    // Disable editing after finishing.
                    displayNameEditText.setFocusable(false);
                    displayNameEditText.setClickable(false);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error checking display name uniqueness: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProfileActivity", "Error checking display name uniqueness: " + e.getMessage());
                    displayNameEditText.setText(originalDisplayName);
                    displayNameEditText.setFocusable(false);
                    displayNameEditText.setClickable(false);
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Save personal info on any touch event outside of the back arrow.
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            int[] location = new int[2];
            backArrow.getLocationOnScreen(location);
            float x = ev.getRawX();
            float y = ev.getRawY();
            if (!(x >= location[0] && x <= location[0] + backArrow.getWidth() &&
                    y >= location[1] && y <= location[1] + backArrow.getHeight())) {
                savePersonalInfo();
                String newDisplayName = displayNameEditText.getText().toString().trim();
                if (!newDisplayName.equals(originalDisplayName)) {
                    checkDisplayNameUnique(newDisplayName);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
