package com.example.vibecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.widget.Button;
import android.widget.EditText;

import com.example.vibecheck.ui.signup.SignUpActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowToast;

/**
 * Unit tests for SignUpActivity.
 */
@RunWith(RobolectricTestRunner.class)
public class SignUpActivityTest {

    private SignUpActivity signUpActivity;

    /**
     * Initializes the SignUpActivity with Robolectric before each test.
     */
    @Before
    public void setUp() {
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
     * <p>
     * Note: This doesn't verify Firebase sign-up success, just that
     * the empty-fields check is bypassed.
     * </p>
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
