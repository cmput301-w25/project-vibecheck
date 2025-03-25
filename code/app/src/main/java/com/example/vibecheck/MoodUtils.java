/*
This is a small utility class to provide helper functions useable by any class they may be needed in.
It holds a static global variable to store the current username of the user who is logged in, and handles
matching the username of mood events with the current user's username.

It also handles navigation to the appropriate fragment for viewing a mood event as a result.

Finally this class also handles consistent colour-coding of moods and consistent emoji-coding as well.
 */

package com.example.vibecheck;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;

import com.example.vibecheck.ui.moodevents.Mood;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

/**
 * Utility class for colour-coding moods and emojis, determining if a mood event is owned by the current user,
 * handling navigation to the appropriate fragment for viewing a mood event, obtaining the time since a post was made,
 * and holds the current username of the user who is logged in.
 */
public class MoodUtils {

    //Static variable to store current username of whoever is logged in
    private static String currentUsername = null;

    //Interface for getting display name
    public interface OnDisplayNameFetchedListener {
        void onFetched(String displayName);
    }


    //Getter and setter for current username
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Identifies if the current user owns a given mood event.
     *
     * @param mood
     * @return Returns a boolean to confirm or deny current user mood event ownership
     */
    public static boolean isMoodOwnedByCurrentUser(Mood mood) {
        if (mood == null || currentUsername == null) return false;
        return currentUsername.equals(mood.getUsername());
    }

    /**
     * Navigates to the appropriate fragment for viewing a mood event.
     *
     * @param navController
     * @param mood
     */
    public static void navigateToViewMoodEvent(NavController navController, Mood mood) {
        if (navController == null || mood == null || mood.getMoodId() == null || mood.getMoodId().isEmpty()) {
            Log.e("MoodUtils", "Invalid mood or NavController");
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("moodEventId", mood.getMoodId());

        if (isMoodOwnedByCurrentUser(mood)) {
            Log.d("MoodUtils", "Navigating to MyMoodDisplayFragment");
            navController.navigate(R.id.action_navigation_home_to_myMoodDisplayFragment, bundle);
        } else {
            Log.d("MoodUtils", "Navigating to UserMoodDisplayFragment");
            navController.navigate(R.id.action_navigation_home_to_userMoodDisplayFragment, bundle);
        }
    }


    /**
     * Returns the color resource ID for a given MoodState.
     */
    private static int getMoodColourResourceID(Mood.MoodState moodState) {
        if (moodState == null) return R.color.white; //Default fallback color
        switch (moodState) {
            case ANGER:
                return R.color.anger;
            case CONFUSION:
                return R.color.confusion;
            case DISGUST:
                return R.color.disgust;
            case FEAR:
                return R.color.fear;
            case HAPPINESS:
                return R.color.happiness;
            case SADNESS:
                return R.color.sadness;
            case SHAME:
                return R.color.shame;
            case SURPRISE:
                return R.color.surprise;
            case BOREDOM:
                return R.color.boredom;
            default:
                return R.color.white;
        }
    }


    /**
     * Returns the actual color (int) for a given MoodState using ContextCompat.
     */
    public static int getMoodColor(Context context, Mood.MoodState moodState) {
        return ContextCompat.getColor(context, getMoodColourResourceID(moodState));
    }

    /**
     * Returns the emoji associated with a given MoodState.
     */
    public static String getEmojiForMood(Mood.MoodState moodState) {
        if (moodState == null) return "🙂"; // Default emoji
        switch (moodState) {
            case ANGER:
                return "😡";
            case CONFUSION:
                return "😕";
            case DISGUST:
                return "🤢";
            case FEAR:
                return "😨";
            case HAPPINESS:
                return "😃";
            case SADNESS:
                return "😢";
            case SHAME:
                return "😳";
            case SURPRISE:
                return "😲";
            case BOREDOM:
                return "😴";
            default:
                return "🙂";
        }
    }


    /**
     * Subtracts the time when a post was made from the current time
     * to determine the time since the post was made.
     * For visual display purposes.
     *
     * @param postDate
     * @return Returns a string representing the time since a given post was made.
     */
    public static String timeSincePosting(Date postDate) {
        long diff = new Date().getTime() - postDate.getTime();
        long minutes = diff / (60 * 1000);
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + " minutes ago";
        long hours = minutes / 60;
        if (hours < 24) return hours + " hours ago";
        long days = hours / 24;
        return days + " days ago";
    }


    /**
     * Fetches the current display name for a given username by querying the Firestore database based on the unique username
     * of a user that is provided for the query. Necessary as display names can be changed by the user anytime.
     * @param username
     * @param listener
     */
    public static void getDisplayName(String username, OnDisplayNameFetchedListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("username", username)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String displayName = querySnapshot.getDocuments().get(0).getString("displayName");
                        if (displayName != null && !displayName.isEmpty()) {
                            listener.onFetched(displayName);
                        } else {
                            // Fallback to username if displayName is missing
                            listener.onFetched(username);
                        }
                    } else {
                        // If no user found, return the username
                        listener.onFetched(username);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("MoodUtils", "Error fetching displayName: " + e.getMessage());
                    listener.onFetched(username); // Return username on failure
                });
    }

}
