/**
 * This java object class represents a mood event recorded by the user, including details such as
 * emotional state, social situation, timestamp, optional reason (description), optional location,
 * optional image, and whether the event is public or private.
 * It is designed to be stored in Firestore and supports serialization.
 *
 * This class has no outstanding issues.
 */

package com.example.vibecheck.ui.moodevents;

import android.util.Log;
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
    private String location;
    private String username; // The username of the logged user who created the mood event.
    private String moodId;
    private boolean isPublic; //if the mood is public or not

    /**
     * No-argument constructor required by Firestore for serialization.
     */
    public Mood() {
        // Default constructor needed for Firebase auto-mapping
    }

    /**
     * Constructs a mood entry with a specified mood state, sets the timestamp to the current time.
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
     * Constructs a mood entry with a specified mood state and the logged user's username.
     * Use this constructor to ensure the mood event records the username of the user who added it.
     *
     * @param moodState The emotional state of the user.
     * @param username The username (or display name) of the logged-in user.

    public Mood(MoodState moodState, String username) {
        this.timestamp = new Date();
        this.moodState = moodState;
        this.username = username;
    }*/

    /**
     * Constructs a mood entry with a pre existing timestamp and mood state.
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
     * Sets the description of the mood entry. 200 character length constraint handled during input validation.
     * @param description
     */
    public void setDescription(String description) {
        // Removed the strict constraint to avoid crashes during Firestore deserialization.
        // Description length constraint now handled during input validation.
        this.description = description;
    }

    /**
     * Retrieves the mood image, stored as a list of integers in firestore, and converts it to a byte array.
     * @return
     *      Returns a byte array representing the mood image.
     */
    public byte[] getImageByteArr() {
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

    public List<Integer> getImage() {
        return image;
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
     * Assigns latitude and longitude to the mood event.
     * @param latitude The latitude coordinate.
     * @param longitude The longitude coordinate.
     */
    public void setLatAndLong(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}