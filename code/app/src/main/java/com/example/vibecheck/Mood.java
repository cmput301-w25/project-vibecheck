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

import java.util.Date;

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
        ALONE, ONE_TO_ONE, SMALL_GROUP, LARGE_AUDIENCE
    }

    private Date timestamp;
    private MoodState moodState;
    private String trigger;
    private SocialSituation socialSituation;
    private String description;
    private byte[] image;
    private static final int MAX_IMAGE_SIZE = 65536;
    private Double latitude;
    private Double longitude;
    private String username;
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
     * Assigns a description to the mood while ensuring it follows length constraints.
     * @param description A brief description of the mood event (max 20 characters, 3 words).
     * @throws IllegalArgumentException if the description exceeds constraints.
     */
    public void setDescription(String description) {
        if (description != null && (description.length() > 20 || description.split("\\s+").length > 3)) {
            throw new IllegalArgumentException("Description must not exceed 20 characters or 3 words.");
        }
        this.description = description;
    }

    public byte[] getImage() {
        return image;
    }

    /**
     * Sets the mood image while ensuring it adheres to the size limit.
     * @param image A byte array representing the image.
     * @throws IllegalArgumentException if the image size exceeds the allowed limit.
     */
    public void setImage(byte[] image) {
        if (image != null && image.length > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Image must be under 65536 bytes.");
        }
        this.image = image;
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
                ", image=" + (image != null ? "attached" : "none") +
                ", location=" + (latitude != null && longitude != null ? "(" + latitude + ", " + longitude + ")" : "none") +
                '}';
    }
}
