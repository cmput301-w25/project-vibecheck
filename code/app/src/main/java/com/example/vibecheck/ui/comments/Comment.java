/**
 * This java object class represents a comment on a mood event. It constructs a comment object containing the comment text, comment ID,
 * the mood event ID of the mood event it belongs to, the Firebase UID and username of the commenter, and the time of creation as a timestamp.
 *
 * No outstanding issues with this class.
 */

package com.example.vibecheck.ui.comments;

import java.util.Date;

public class Comment {

    private String commentID;;      // ID of the comment
    private String moodEventId;    // The ID of the mood this comment is attached to
    private String userId;         // Firebase UID of the commenting user
    private String username;       // The commenter's chosen unique username
    private String commentText;    // The actual comment
    private  Date timestamp;       // Time of comment creation


    /**
     * No-argument constructor required by Firestore for serialization.
     */
    public Comment() {}

    /**
     * Constructor for a comment.
     * @param moodEventId
     * @param userId
     * @param username
     * @param commentText
     */
    public Comment(String moodEventId, String userId, String username, String commentText) {
        this.moodEventId = moodEventId;
        this.userId = userId;
        this.username = username;
        this.commentText = commentText;
        this.timestamp = new Date();
    }

    // Getters and setters
    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getMoodEventId() {
        return moodEventId;
    }

    public void setMoodEventId(String moodEventId) {
        this.moodEventId = moodEventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommentText() {
        return commentText;
    }



    /**
     * Sets the comment text while ensuring it follows length constraints.
     * @param commentText
     */
    public void setCommentText(String commentText) {
        if (commentText == null || commentText.trim().isEmpty()) {
            this.commentText = null;
            return;
        }
        if (commentText.length() > 200) {
            throw new IllegalArgumentException("Comment must not exceed 200 characters.");
        }

        this.commentText = commentText;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}