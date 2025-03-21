package com.example.vibecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vibecheck.ui.login.LoginActivity;
import com.example.vibecheck.ui.signup.SignUpActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowToast;

/**
 * Unit tests for LoginActivity.
 */
@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    private LoginActivity loginActivity;

    /**
     * Sets up the LoginActivity instance before each test.
     */
    @Before
    public void setUp() {
        loginActivity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

    /**
     * Tests that clicking the login button with empty email and password fields
     * shows the toast "Please fill all fields".
     */
    @Test
    public void testEmptyFieldsShowsToast() {
        EditText etEmail = loginActivity.findViewById(R.id.edit_text_login_username);
        EditText etPassword = loginActivity.findViewById(R.id.edit_text_login_password);
        Button btnLogin = loginActivity.findViewById(R.id.login_button);

        // Set both fields to empty strings
        etEmail.setText("");
        etPassword.setText("");

        // Simulate button click
        btnLogin.performClick();

        // Verify that the toast message is as expected
        String toastMessage = ShadowToast.getTextOfLatestToast();
        assertEquals("Please fill all fields", toastMessage);
    }

    /**
     * Tests that when valid non‑empty email and password fields are provided,
     * the toast message is not the "Please fill all fields" error.
     * <p>
     * Note: Since Firebase sign‑in is asynchronous and external, this test only
     * verifies that the empty-fields error toast is not shown.
     * </p>
     */
    @Test
    public void testValidFieldsDoNotShowEmptyFieldsToast() {
        EditText etEmail = loginActivity.findViewById(R.id.edit_text_login_username);
        EditText etPassword = loginActivity.findViewById(R.id.edit_text_login_password);
        Button btnLogin = loginActivity.findViewById(R.id.login_button);

        // Set valid email and password
        etEmail.setText("user@example.com");
        etPassword.setText("password123");

        // Simulate button click
        btnLogin.performClick();

        // Get the latest toast text
        String toastMessage = ShadowToast.getTextOfLatestToast();
        // Verify that the empty fields error toast is not shown.
        assertTrue(!"Please fill all fields".equals(toastMessage));
    }

    /**
     * Tests that clicking the sign‑up link navigates to SignUpActivity.
     */
    @Test
    public void testSignUpLinkNavigatesToSignUpActivity() {
        TextView tvSignUp = loginActivity.findViewById(R.id.link_to_signup);

        // Simulate clicking the sign‑up TextView
        tvSignUp.performClick();

        // Verify that an intent to start SignUpActivity was launched
        Intent expectedIntent = new Intent(loginActivity, SignUpActivity.class);
        Intent actualIntent = Shadows.shadowOf(loginActivity).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actualIntent.getComponent());
    }
}
