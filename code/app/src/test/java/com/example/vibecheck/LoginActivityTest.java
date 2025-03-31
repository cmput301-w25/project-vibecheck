package com.example.vibecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowToast;

import com.example.vibecheck.ui.login.LoginActivity;
import com.example.vibecheck.ui.signup.SignUpActivity;
import com.google.firebase.FirebaseApp;
import androidx.test.core.app.ApplicationProvider;


/**
 * Unit tests for LoginActivity using Robolectric.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)  // or whichever SDK version you want
public class LoginActivityTest {

    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext());
        loginActivity = Robolectric.buildActivity(LoginActivity.class)
                .create()
                .start()
                .resume()
                .get();
    }

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

    @Test
    public void testValidFieldsDoNotShowEmptyFieldsToast() {
        EditText etEmail = loginActivity.findViewById(R.id.edit_text_login_username);
        EditText etPassword = loginActivity.findViewById(R.id.edit_text_login_password);
        Button btnLogin = loginActivity.findViewById(R.id.login_button);

        // Set valid email and password
        etEmail.setText("swayam@ualberta.ca");
        etPassword.setText("Seth@2460");

        // Simulate button click
        btnLogin.performClick();

        // Get the latest toast text
        String toastMessage = ShadowToast.getTextOfLatestToast();
        // Verify that the empty fields error toast is not shown.
        assertTrue(!"Please fill all fields".equals(toastMessage));
    }

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
