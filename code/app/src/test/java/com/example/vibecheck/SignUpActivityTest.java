package com.example.vibecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;

/**
 * Unit tests for SignUpActivity using Robolectric.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30) // or whichever SDK you want to simulate
public class SignUpActivityTest {

    private SignUpActivity signUpActivity;

    /**
     * Initializes Firebase and the SignUpActivity before each test.
     */
    @Before
    public void setUp() {
        // 1) Initialize Firebase in the Robolectric environment
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());

        // 2) Build and start the activity
        signUpActivity = Robolectric.buildActivity(SignUpActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    /**
     * Tests that when all fields (email, username, password) are empty,
     * the toast message "Please fill all fields" is displayed.
     */
    @Test
    public void testEmptyFieldsShowsToast() {
        EditText etEmail = signUpActivity.findViewById(R.id.edit_text_signup_username);
        EditText etUsername = signUpActivity.findViewById(R.id.edit_text_username);
        EditText etPassword = signUpActivity.findViewById(R.id.edit_text_confirm_password);
        Button btnSignUp = signUpActivity.findViewById(R.id.signup_button);

        // Set all fields to empty
        etEmail.setText("");
        etUsername.setText("");
        etPassword.setText("");

        // Click the sign-up button
        btnSignUp.performClick();

        // Get the latest toast text
        String latestToast = ShadowToast.getTextOfLatestToast();
        assertEquals("Please fill all fields", latestToast);
    }

    /**
     * Tests that if the fields are not empty, we do NOT see the
     * "Please fill all fields" toast.
     */
    @Test
    public void testNonEmptyFieldsDoNotShowEmptyToast() {
        EditText etEmail = signUpActivity.findViewById(R.id.edit_text_signup_username);
        EditText etUsername = signUpActivity.findViewById(R.id.edit_text_username);
        EditText etPassword = signUpActivity.findViewById(R.id.edit_text_confirm_password);
        Button btnSignUp = signUpActivity.findViewById(R.id.signup_button);

        // Provide non-empty inputs
        etEmail.setText("test@example.com");
        etUsername.setText("TestUser");
        etPassword.setText("password123");

        // Click the sign-up button
        btnSignUp.performClick();

        // Robolectric captures the latest toast (if any)
        String latestToast = ShadowToast.getTextOfLatestToast();

        // Verify that it's not the "Please fill all fields" error
        assertNotEquals("Please fill all fields", latestToast);
    }
}
