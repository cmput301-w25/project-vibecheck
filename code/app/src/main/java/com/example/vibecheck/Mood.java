package com.example.vibecheck;

import java.util.Date;

public class Mood {

    public enum MoodType {
        // Represents various emotional states a user can have
        ANGER, CONFUSION, DISGUST, FEAR, HAPPINESS, SADNESS, SHAME, SURPRISE, BOREDOM
    }

    public enum SocialContext {
        // Describes the social setting when the mood was recorded
        ALONE, ONE_TO_ONE, SMALL_GROUP, LARGE_AUDIENCE
    }

    private Date timestamp;
    private MoodType moodType;
    private String moodTrigger;
    private SocialContext socialContext;
    private String moodDescription;
    private byte[] image;

    private int maxImageSize = 65536;
    private Double latitude;
    private Double longitude;
    private String userHandle;
    private String documentId;

    /**
     * No-argument constructor required by Firestore for serialization.
     */
    public Mood() {
        // Default constructor needed for Firebase auto-mapping
    }

    public Mood(MoodType moodType) {
        this.timestamp = new Date();
        this.moodType = moodType;
    }

    public Mood(Date timestamp, MoodType moodType) {
        this.timestamp = timestamp;
        this.moodType = moodType;
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

    public MoodType getMoodType() {
        return moodType;
    }

    public void setMoodType(MoodType moodType) {
        this.moodType = moodType;
    }

    public String getMoodTrigger() {
        return moodTrigger;
    }

    public void setMoodTrigger(String moodTrigger) {
        this.moodTrigger = moodTrigger;
    }

    public SocialContext getSocialContext() {
        return socialContext;
    }

    public void setSocialContext(SocialContext socialContext) {
        this.socialContext = socialContext;
    }

    public String getMoodDescription() {
        return moodDescription;
    }

    /**
     * Assigns a description to the mood while ensuring it follows length constraints.
     */
    public void setMoodDescription(String moodDescription) {
        if (moodDescription != null && (moodDescription.length() > 20 || moodDescription.split("\\s+").length > 3)) {
            throw new IllegalArgumentException("Description must not exceed 20 characters or 3 words.");
        }
        this.moodDescription = moodDescription;
    }

    public int getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(int maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public byte[] getImage() {
        return image;
    }

    /**
     * Sets the mood image while ensuring it adheres to the size limit.
     */
    public void setImage(byte[] image) {
        if (image != null && image.length > maxImageSize) {
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
     */
    public void setLocation(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserHandle() {
        return userHandle;
    }

    public void setUserHandle(String userHandle) {
        this.userHandle = userHandle;
    }

    @Override
    public String toString() {
        return "Mood{" +
                "timestamp=" + timestamp +
                ", moodType=" + moodType +
                ", moodTrigger='" + moodTrigger + '\'' +
                ", socialContext=" + socialContext +
                ", moodDescription='" + moodDescription + '\'' +
                ", image=" + (image != null ? "attached" : "none") +
                ", location=" + (latitude != null && longitude != null ? "(" + latitude + ", " + longitude + ")" : "none") +
                '}';
    }
}
