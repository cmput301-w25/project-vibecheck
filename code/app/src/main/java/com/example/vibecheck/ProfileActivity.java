package com.example.vibecheck;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
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
    private ImageView backArrow;
    private TextView tvDisplayName, tvUsername;
    private TextView followerCountText, followingCountText;
    private EditText personalInfoEditText;
    private Switch notificationsSwitch, publicSwitch;
    private Button logoutButton;

    // Follow requests container
    private LinearLayout followRequestsContainer;
    private TextView followRequestsTitle;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Bind UI elements (make sure these IDs match your XML)
        backArrow = findViewById(R.id.back_arrow);
        tvDisplayName = findViewById(R.id.tvDisplayName);
        tvUsername = findViewById(R.id.tvUsername);
        followerCountText = findViewById(R.id.follower_count);
        followingCountText = findViewById(R.id.following_count);
        personalInfoEditText = findViewById(R.id.personalInfoEditText);
        notificationsSwitch = findViewById(R.id.notifications_switch);
        publicSwitch = findViewById(R.id.public_switch);
        logoutButton = findViewById(R.id.logout_button);

        followRequestsTitle = findViewById(R.id.follow_requests_title);
        followRequestsContainer = findViewById(R.id.follow_requests_container);

        // Load profile data (display name, username, personal info, counts)
        loadProfileData();

        // Load follow requests (if any)
        loadFollowRequests();

        // Handle back arrow
        backArrow.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        // Handle logout
        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    /**
     * Load the user's profile data from Firestore (displayName, username, personalInfo, followerCount, etc).
     */
    private void loadProfileData() {
        if (currentUser == null) {
            tvDisplayName.setText("Not Logged In");
            tvUsername.setText("@NotLoggedIn");
            return;
        }

        String uid = currentUser.getUid();
        db.collection("users").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        tvDisplayName.setText("No Document");
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
                        tvDisplayName.setText(displayName);
                    } else {
                        tvDisplayName.setText("No Display Name");
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
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading profile data", e);
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
                    Toast.makeText(ProfileActivity.this, "Failed to load follow requests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    View requestView = LayoutInflater.from(ProfileActivity.this)
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
     */
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
                                            Toast.makeText(ProfileActivity.this, "Follow request accepted", Toast.LENGTH_SHORT).show();
                                            // Refresh follow requests
                                            refreshFollowRequests();
                                            // Also refresh the profile data so the new followerCount is displayed
                                            loadProfileData();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(ProfileActivity.this, "Error updating requester's following: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            Log.e(TAG, "Error updating requester's following", e);
                                        });
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(ProfileActivity.this, "Error adding to my followers: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error adding to my followers", e);
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error removing from followRequests: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(ProfileActivity.this, "Follow request declined", Toast.LENGTH_SHORT).show();
                    refreshFollowRequests();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error declining request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (currentUser != null) {
                int[] location = new int[2];
                backArrow.getLocationOnScreen(location);
                float x = ev.getRawX();
                float y = ev.getRawY();

                if (!(x >= location[0] && x <= location[0] + backArrow.getWidth() &&
                        y >= location[1] && y <= location[1] + backArrow.getHeight())) {
                    savePersonalInfo();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * Save personal info to Firestore.
     */
    private void savePersonalInfo() {
        if (currentUser == null) return;
        String uid = currentUser.getUid();
        String personalInfo = personalInfoEditText.getText().toString().trim();
        db.collection("users").document(uid)
                .update("personalInfo", personalInfo)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(ProfileActivity.this, "Personal info saved", Toast.LENGTH_SHORT).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(ProfileActivity.this, "Failed to save personal info: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
