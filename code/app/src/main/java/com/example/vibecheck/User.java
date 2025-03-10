package com.example.vibecheck;

/**
 * Represents a user in the system, containing their username and password.
 * This class is used for authentication and storing user-related data in Firestore.
 *
 * Outstanding Issues:
 * - Ensure password storage follows security best practices (e.g., hashing instead of plain text).
 * - Consider adding additional fields like email or profile information.
 * - Implement validation for username and password constraints.
 */
public class User {
    private String username;
    private String password;

    /**
     * Default constructor required for Firestore deserialization.
     */
    public User() {
        // Empty constructor needed for Firebase to map data
    }

    /**
     * Constructs a new user with a given username and password.
     *
     * @param username The unique identifier for the user.
     * @param password The secret key used for authentication.
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Retrieves the user's username.
     *
     * @return The stored username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Updates the username for this user.
     *
     * @param username The new username to be set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Retrieves the user's password.
     *
     * @return The stored password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Updates the user's password.
     *
     *
     * @param password The new password to be assigned.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
