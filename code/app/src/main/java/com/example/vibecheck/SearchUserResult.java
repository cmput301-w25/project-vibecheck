package com.example.vibecheck;

import android.graphics.drawable.Icon;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/*
 * Represents a search result in a user search fragment.
 */
public class SearchUserResult {
    private int userProfileImageID;
    private String username;
    private Integer numberOfFollowers;

    public SearchUserResult(int profileImageId, String username, int numberOfFollowers) {
        this.userProfileImageID = profileImageId;
        this.username = username;
        this.numberOfFollowers = (Integer) numberOfFollowers;
    }

    public SearchUserResult(String username, int numberOfFollowers) {
        this.userProfileImageID = R.id.search_user_profile_image;
        this.username = username;
        this.numberOfFollowers = (Integer) numberOfFollowers;
    }

    // Getters and Setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(int numberOfFollowers) {
        this.numberOfFollowers = (Integer) numberOfFollowers;
    }

    public int getUserProfileImageID() {
        return userProfileImageID;
    }

    public void setProfileImageResourceId(int profileImageResourceId) {
        this.userProfileImageID = profileImageResourceId;
    }
}