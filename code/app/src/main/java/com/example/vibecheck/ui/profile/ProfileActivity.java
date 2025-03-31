package com.example.vibecheck.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.home.HomeActivity;
import com.example.vibecheck.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * ProfileActivity displays the logged-in user's profile info, including:
 * - Display name, username
 * - Follower/following counts
 * - Personal info
 * - Preferences (e.g. notifications, public)
 * - A list of follow requests that can be accepted or declined
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // UI elements
    private EditText displayNameEditText, personalInfoEditText;
    private ImageView editIcon, backArrow;
    private TextView tvUsername;
    private TextView followerCountText, followingCountText;
    private Switch notificationsSwitch, publicSwitch;
    private Button logoutButton;

    // Follow requests container
    private LinearLayout followRequestsContainer;
    private TextView followRequestsTitle;

    // Firebase
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

        //Top padding
        View root = findViewById(R.id.activity_profile_root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Initialize Firebase
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
        backArrow = findViewById(R.id.back_arrow);
        displayNameEditText = findViewById(R.id.display_name_edit_text); //(EditText) profileHeader.getChildAt(1);
        editIcon = findViewById(R.id.edit_icon);
        tvUsername = findViewById(R.id.tvUsername);
        followerCountText = findViewById(R.id.follower_count);
        followingCountText = findViewById(R.id.following_count);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        publicSwitch = findViewById(R.id.public_switch);

        followRequestsTitle = findViewById(R.id.follow_requests_title);
        followRequestsContainer = findViewById(R.id.follow_requests_container);

        // Personal Info EditText (child index 3 of main layout)
        personalInfoEditText = findViewById(R.id.personalInfoEditText);
        // Logout Button (child index 6 of main layout)
        logoutButton = findViewById(R.id.logout_button);

        // Load profile data
        loadProfileData();

        // Load follow requests (if any)
        loadFollowRequests();

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
        if (currentUser == null) {
            displayNameEditText.setText("Not Logged In");
            tvUsername.setText("@NotLoggedIn");
            Log.d("ProfileActivity", "User not logged in");
            return;
        }



        String uid = currentUser.getUid();
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        displayNameEditText.setText("No Document");
                        tvUsername.setText("@NoDocument");
                        return;
                    }
                    // Retrieve fields
                    String displayName = documentSnapshot.getString("displayName");
                    String username = documentSnapshot.getString("username");
                    String personalInfo = documentSnapshot.getString("personalInfo");
                    Long followerCount = documentSnapshot.getLong("followerCount");
                    Long followingCount = documentSnapshot.getLong("followingCount");
                    // Optionally, you might store notifications/public settings in Firestore as well
                    // e.g., boolean notificationsEnabled = documentSnapshot.getBoolean("notificationsEnabled");

                    // Update UI
                    if (displayName != null && !displayName.isEmpty()) {
                        displayNameEditText.setText(displayName);
                    } else {
                        displayNameEditText.setText("No Display Name");
                    }

                    if (username != null && !username.isEmpty()) {
                        tvUsername.setText("@" + username);
                    } else {
                        tvUsername.setText("@NoUsername");
                    }

                    if (personalInfo != null) {
                        personalInfoEditText.setText(personalInfo);
                    }

                    if (followerCount != null) {
                        followerCountText.setText(String.valueOf(followerCount));
                    }
                    if (followingCount != null) {
                        followingCountText.setText(String.valueOf(followingCount));
                    }

                    /*
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

                    */
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this,
                            "Failed to load profile data: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("ProfileActivity", "Error loading profile data: " + e.getMessage());
                });
    }

    /**
     * Saves the personal info field in Firestore.
     */
    private void savePersonalInfo() {
        if (currentUser == null) return;
        String uid = currentUser.getUid();
        String personalInfo = personalInfoEditText.getText().toString().trim();
        db.collection("users")
                .document(uid)
                .update("personalInfo", personalInfo)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ProfileActivity.this, "Personal info saved", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Failed to save personal info: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
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


    /**
     * Load the list of follow requests from the user's Firestore document
     * and display them in the followRequestsContainer.
     */
    private void loadFollowRequests() {
        if (currentUser == null) {
            return;
        }
        String uid = currentUser.getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // No doc => no follow requests
                        return;
                    }
                    // Retrieve the followRequests array
                    List<String> followRequests = (List<String>) documentSnapshot.get("followRequests");
                    if (followRequests == null || followRequests.isEmpty()) {
                        // No pending requests
                        // Optionally hide the follow requests title
                        followRequestsTitle.setVisibility(View.GONE);
                        return;
                    }

                    // If there are requests, show the title
                    followRequestsTitle.setVisibility(View.VISIBLE);

                    // Clear container first (in case we're reloading)
                    followRequestsContainer.removeAllViews();

                    // For each requesterId, load that user's doc to display name, username, etc
                    for (String requesterId : followRequests) {
                        loadRequesterInfoAndAddItem(requesterId);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Failed to load follow requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading follow requests", e);
                });
    }

    /**
     * Given a requesterId, load that user's info and inflate a follow_request_item to display.
     */
    private void loadRequesterInfoAndAddItem(String requesterId) {
        db.collection("users").document(requesterId)
                .get()
                .addOnSuccessListener(requesterDoc -> {
                    if (!requesterDoc.exists()) {
                        return;
                    }
                    String requesterDisplayName = requesterDoc.getString("displayName");
                    String requesterUsername = requesterDoc.getString("username");

                    // Now inflate the follow_request_item.xml and add it to the container
                    View requestView = LayoutInflater.from(com.example.vibecheck.ui.profile.ProfileActivity.this)
                            .inflate(R.layout.follow_request_item, followRequestsContainer, false);

                    ImageView requestProfileImage = requestView.findViewById(R.id.request_profile_image);
                    TextView requestUsernameText = requestView.findViewById(R.id.request_username);
                    Button acceptButton = requestView.findViewById(R.id.accept_button);
                    Button declineButton = requestView.findViewById(R.id.decline_button);

                    // For simplicity, we won't load an actual profile image. (Use a library like Glide if you store URLs.)
                    requestUsernameText.setText("@" + (requesterUsername != null ? requesterUsername : "unknown"));

                    acceptButton.setOnClickListener(v -> acceptFollowRequest(requesterId));
                    declineButton.setOnClickListener(v -> declineFollowRequest(requesterId));

                    followRequestsContainer.addView(requestView);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error loading requester info", e);
                });
    }

    /**
     * Accept the follow request: remove from followRequests, add to followers, increment followerCount.
     * Also update the requester's document to increment their following count and add the current user to their following array.
     */
    private void acceptFollowRequest(String requesterId) {
        if (currentUser == null) return;
        String currentUserId = currentUser.getUid();

        // 1) Remove the requester from my (the current user's) followRequests array
        db.collection("users").document(currentUserId)
                .update("followRequests", FieldValue.arrayRemove(requesterId))
                .addOnSuccessListener(aVoid -> {
                    // 2) Add requester to my 'followers' and increment my 'followerCount'
                    db.collection("users").document(currentUserId)
                            .update("followers", FieldValue.arrayUnion(requesterId),
                                    "followerCount", FieldValue.increment(1))
                            .addOnSuccessListener(aVoid2 -> {
                                // 3) Now update the requester's doc to add me to their 'following' and increment their 'followingCount'
                                db.collection("users").document(requesterId)
                                        .update("following", FieldValue.arrayUnion(currentUserId),
                                                "followingCount", FieldValue.increment(1))
                                        .addOnSuccessListener(aVoid3 -> {
                                            Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Follow request accepted", Toast.LENGTH_SHORT).show();
                                            // Refresh follow requests
                                            refreshFollowRequests();
                                            // Also refresh the profile data so the new followerCount is displayed
                                            loadProfileData();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Error updating requester's following: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error updating requester's following", e);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Error adding to my followers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error adding to my followers", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Error removing from followRequests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error removing from followRequests", e);
                });
    }


    /**
     * Decline the follow request: just remove from followRequests array.
     */
    private void declineFollowRequest(String requesterId) {
        if (currentUser == null) return;
        String currentUserId = currentUser.getUid();

        db.collection("users").document(currentUserId)
                .update("followRequests", FieldValue.arrayRemove(requesterId))
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Follow request declined", Toast.LENGTH_SHORT).show();
                    refreshFollowRequests();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(com.example.vibecheck.ui.profile.ProfileActivity.this, "Error declining request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error removing request from followRequests", e);
                });
    }

    /**
     * Refresh the follow requests section by clearing and re-loading them.
     */
    private void refreshFollowRequests() {
        followRequestsContainer.removeAllViews();
        loadFollowRequests();
    }

    /**
     * Save personal info if user taps outside the back arrow.
     */
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