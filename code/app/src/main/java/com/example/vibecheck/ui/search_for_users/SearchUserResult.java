package com.example.vibecheck.ui.search_for_users;

import com.example.vibecheck.R;

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

     /*
    public int getUserProfileImageID() {
        return userProfileImageID;
    }

    public void setProfileImageResourceId(int profileImageResourceId) {
        this.userProfileImageID = profileImageResourceId;
    }
    */
}