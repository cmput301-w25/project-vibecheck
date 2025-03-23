/**
 * Mood.java
 *
 * This class represents a mood event recorded by the user, including details such as
 * emotional state, social situation, timestamp, optional trigger, description, location,
 * and an optional image. It is designed to be stored in Firestore and supports serialization.
 *
 * Outstanding Issues:
 */

package com.example.vibecheck.ui.moodevents;

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
        ANGER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE, BOREDOM;

        public String moodStateToString() {
            return this.name().charAt(0) + name().substring(1).toLowerCase();
        }

        public static MoodState moodStateToEnum(String moodStateString) {
            return MoodState.valueOf(moodStateString.toUpperCase().trim());
        }
    }

    /**
     * Describes the social setting when the mood was recorded.
     */
    public enum SocialSituation {
        ALONE, ONE_TO_ONE, SMALL_GROUP, LARGE_AUDIENCE, LARGE_GROUP;

        public String socialSituationToString() {
            switch (this) {
                case ALONE: return "Alone";
                case ONE_TO_ONE: return "One-to-One";
                case SMALL_GROUP: return "Small Group";
                case LARGE_AUDIENCE: return "Large Audience";
                default: return null;
            }
        }

        public static SocialSituation socialSituationToEnum(String socialSituationString) {
            switch (socialSituationString) {
                case "Alone": return ALONE;
                case "One-to-One": return ONE_TO_ONE;
                case "Small Group": return SMALL_GROUP;
                case "Large Audience": return LARGE_AUDIENCE;
                default: return null;
            }
        }
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
    private String moodId;
    private boolean isPublic; //Use in implementation of public/private mood events later

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

    // Getters and setters
    public String getMoodId() {
        return moodId;
    }

    public void setMoodId(String moodId) {
        this.moodId = moodId;
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
        // Check for null or empty description
        if (description == null || description.trim().isEmpty()) {
            this.description = null;
            return;
        }
        // Check for description length
        if (description.length() > 200) {
            throw new IllegalArgumentException("Description must not exceed 200 characters.");
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

    /**
     * @return
     *      Returns a string representation of the mood entry.
     */
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

    /**
     * Formats the timestamp for display.
     * @return
     *      Returns a formatted string representation of the timestamp in the format "MMM dd, yyyy - hour:min am/pm".
     */
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault());
        return sdf.format(timestamp);
    }
}
