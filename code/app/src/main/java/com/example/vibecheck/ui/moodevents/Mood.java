/**
 * Mood.java
 *
 * This class represents a mood event recorded by the user, including details such as
 * emotional state, social situation, timestamp, optional reason (description), location,
 * and an optional image. It is designed to be stored in Firestore and supports serialization.
 *
 * Outstanding Issues:
 */

package com.example.vibecheck.ui.moodevents;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        NOINPUT, ALONE, ONE_TO_ONE, SMALL_GROUP, LARGE_AUDIENCE;

        public String socialSituationToString() {
            switch (this) {
                case NOINPUT: return "N/A";
                case ALONE: return "Alone";
                case ONE_TO_ONE: return "One-to-One";
                case SMALL_GROUP: return "Small Group";
                case LARGE_AUDIENCE: return "Large Audience";
                default: return null;
            }
        }

        public static SocialSituation socialSituationToEnum(String socialSituationString) {
            switch (socialSituationString) {
                case "N/A": return NOINPUT;
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
    private SocialSituation socialSituation;
    private String description; //This is the mood Reason
    private List<Integer> image;
    private static final int MAX_IMAGE_SIZE = 65536;
    private Double latitude;
    private Double longitude;
    private String username;
    private String moodId;
    private boolean isPublic; //if the mood is public or not

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

    /**
     * Retrieves the mood image, stored as a list of integers in firestore, and converts it to a byte array.
     * @return
     *      Returns a byte array representing the mood image.
     */
    public byte[] getImage() {
        // If the mood doesn't have an image, return null
        if (image == null) {
            return null;
        }

        // Convert the List<Integer> to a byte array
        byte[] byteArray = new byte[image.size()];
        // Convert each Integer in the List to a byte and store in the byte array
        for (int i = 0; i < image.size(); i++) {
            byteArray[i] = (byte) (image.get(i) & 0xFF);
        }
        return byteArray;
    }

    /**
     * Sets the mood image while ensuring it adheres to the size limit.
     * @param image A list of integers representing the image.
     * @throws IllegalArgumentException if the image size exceeds the allowed limit.
     */
    public void setImage(List<Integer> image) {
        if (image != null && image.size() > MAX_IMAGE_SIZE) {
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

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
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
