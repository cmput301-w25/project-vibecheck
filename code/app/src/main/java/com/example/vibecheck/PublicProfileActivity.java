/**
 * PublicProfileActivity displays a user's public profile.
 * <p>
 * It loads the user's display name, username, and follower/following counts from Firestore
 * based on an identifier (email) passed via intent. It also allows sending a follow request
 * by updating the target user's followRequests field. A back arrow enables navigation back.
 * </p>
 */

package com.example.vibecheck;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class PublicProfileActivity extends AppCompatActivity {

    private static final String TAG = "PublicProfileActivity";

    private ImageView backArrow;
    private TextView displayNameText;
    private TextView usernameText;
    private TextView followerCountText;
    private TextView followingCountText;
    private Button followRequestButton;

    private FirebaseFirestore db;
    // In this implementation, we are receiving the email as the identifier.
    private String userIdentifier; // email passed as "userId"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_profile);

        db = FirebaseFirestore.getInstance();

        // Bind UI elements – ensure these IDs match your XML.
        backArrow = findViewById(R.id.public_profile_back_arrow);
        displayNameText = findViewById(R.id.public_profile_username);
        usernameText = findViewById(R.id.public_profile_username);
        followerCountText = findViewById(R.id.follower_count);
        followingCountText = findViewById(R.id.following_count);
        followRequestButton = findViewById(R.id.follow_request_status_button);

        // Retrieve the user identifier (email) passed from the previous screen.
        userIdentifier = getIntent().getStringExtra("userId");
        Log.d(TAG, "User ID received: " + userIdentifier);
        if (userIdentifier == null || userIdentifier.isEmpty()) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load the user's profile data using a query on the "email" field.
        loadUserProfile();

        // Set back arrow click listener.
        backArrow.setOnClickListener(view -> finish());

        // Set follow request button functionality.
        followRequestButton.setOnClickListener(view -> sendFollowRequest());
    }

    private void loadUserProfile() {
        db.collection("users")
                .whereEqualTo("email", userIdentifier)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        String displayName = documentSnapshot.getString("displayName");
                        String username = documentSnapshot.getString("username");
                        // Retrieve follower and following counts (assuming they are stored as numbers)
                        Long followerCount = documentSnapshot.getLong("followerCount");
                        Long followingCount = documentSnapshot.getLong("followingCount");

                        if (displayName != null && !displayName.isEmpty()) {
                            displayNameText.setText(displayName);
                        } else {
                            displayNameText.setText("No Display Name");
                        }

                        if (username != null && !username.isEmpty()) {
                            usernameText.setText("@" + username);
                        } else {
                            usernameText.setText("@unknown");
                        }

                        followerCountText.setText(followerCount != null ? followerCount.toString() : "0");
                        followingCountText.setText(followingCount != null ? followingCount.toString() : "0");
                    } else {
                        Toast.makeText(PublicProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PublicProfileActivity.this, "Error loading user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error loading user profile", e);
                    finish();
                });
    }

    private void sendFollowRequest() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Suppose userIdentifier is the target user’s email or doc ID
        db.collection("users")
                .whereEqualTo("email", userIdentifier)  // or .document(userId) if you have the doc ID
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String targetDocId = querySnapshot.getDocuments().get(0).getId();

                        // Instead of updating "followers", update "followRequests"
                        db.collection("users").document(targetDocId)
                                .update("followRequests", FieldValue.arrayUnion(currentUserId))
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(PublicProfileActivity.this, "Follow request sent", Toast.LENGTH_SHORT).show();
                                    // Optionally disable the follow button or show "Pending"
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PublicProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Error sending follow request", e);
                                });
                    } else {
                        Toast.makeText(PublicProfileActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PublicProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error finding user for follow request", e);
                });
    }

}
