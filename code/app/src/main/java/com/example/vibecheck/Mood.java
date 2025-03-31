/**
 * Mood.java
 *
 * This class represents a mood event recorded by the user, including details such as
 * emotional state, social situation, timestamp, optional trigger, description, location,
 * and an optional image. It is designed to be stored in Firestore and supports serialization.
 *
 * Outstanding Issues:
 */

package com.example.vibecheck;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Represents a recorded mood entry, capturing emotional state, context, and metadata.
 */
public class Mood {

    /**
     * Represents various emotional states a user can have.
     */
    public enum MoodState {
        ANGER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE, BOREDOM
    }

    /**
     * Describes the social setting when the mood was recorded.
     */
    public enum SocialSituation {
        ALONE, ONE_TO_ONE, SMALL_GROUP, LARGE_AUDIENCE, NOINPUT
    }

    private Date timestamp;
    private MoodState moodState;
    private String trigger;
    private SocialSituation socialSituation;
    private String description;
    // The image field remains commented out; if you use image storage, consider using Blob.
    // private byte[] image;
    private static final int MAX_IMAGE_SIZE = 65536;
    private Double latitude;
    private Double longitude;
    private String username;  // The username of the logged user who created the mood event.
    private String documentId;

    /**
     * No-argument constructor required by Firestore for serialization.
     */
    public Mood() {
        // Default constructor needed for Firebase auto-mapping
    }

    /**
     * Constructs a mood entry with a specified mood state.
     * @param moodState The emotional state of the user.
     */
    public Mood(MoodState moodState) {
        this.timestamp = new Date();
        this.moodState = moodState;
    }

    /**
     * Constructs a mood entry with a specified mood state and the logged user's username.
     * Use this constructor to ensure the mood event records the username of the user who added it.
     *
     * @param moodState The emotional state of the user.
     * @param username The username (or display name) of the logged-in user.
     */
    public Mood(MoodState moodState, String username) {
        this.timestamp = new Date();
        this.moodState = moodState;
        this.username = username;
    }

    /**
     * Constructs a mood entry with a timestamp and mood state.
     * @param timestamp The date and time the mood was recorded.
     * @param moodState The emotional state of the user.
     */
    public Mood(Date timestamp, MoodState moodState) {
        this.timestamp = timestamp;
        this.moodState = moodState;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public MoodState getMoodState() {
        return moodState;
    }

    public void setMoodState(MoodState moodState) {
        this.moodState = moodState;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public SocialSituation getSocialSituation() {
        return socialSituation;
    }

    public void setSocialSituation(SocialSituation socialSituation) {
        this.socialSituation = socialSituation;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Assigns a description to the mood while checking constraints.
     * If the description is too long (more than 20 characters or more than 3 words), it logs a warning
     * and truncates the description.
     *
     * @param description A brief description of the mood event.
     */
    public void setDescription(String description) {
        if (description == null) {
            this.description = "";
            return;
        }
        if (description.length() > 20 || description.trim().split("\\s+").length > 3) {
            Log.w("Mood", "Description exceeds limits: " + description);
            // Truncate to first 20 characters and the first word (example strategy)
            this.description = description.substring(0, Math.min(20, description.length())).split("\\s+")[0];
        } else {
            this.description = description;
        }
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    /**
     * Assigns a geographic location to the mood entry.
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     */
    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user who created the mood event.
     * This should be the logged user's username or display name.
     * @param username The username of the user.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "timestamp=" + timestamp +
                ", moodState=" + moodState +
                ", trigger='" + trigger + '\'' +
                ", socialSituation=" + socialSituation +
                ", description='" + description + '\'' +
                ", location=" + (latitude != null && longitude != null ? "(" + latitude + ", " + longitude + ")" : "none") +
                ", username='" + username + '\'' +
                '}';
    }

    /**
     * Formats the timestamp for display.
     * @return Returns a formatted string representation of the timestamp in the format "MMM dd, yyyy - hh:mm a".
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        return sdf.format(timestamp);
    }

    /**
     * Converts the social situation to a string representation.
     * @return Returns a string representation of the social situation.
     */
    public String socialSituationToString() {
        return socialSituation.name().charAt(0) + socialSituation.name().substring(1).toLowerCase();
    }

    /**
     * Converts the mood state to a string representation.
     * @return Returns a string representation of the mood state.
     */
    public String moodStateToString() {
        return moodState.name().charAt(0) + moodState.name().substring(1).toLowerCase();
    }
}
