package com.example.vibecheck;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.vibecheck.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginActivityTest {

    // Rule to launch LoginActivity for each test
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    // Ensure no user is signed in before each test
    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
        // Verify that no user is signed in initially
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    // Test Case 1: Successful login with valid credentials
    @Test
    public void testSuccessfulLogin() {
        // Enter valid email and password (ensure this test user exists in Firebase)
        onView(withId(R.id.edit_text_login_username))
                .perform(typeText("yo@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_login_password))
                .perform(typeText("abcd12345"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.login_button)).perform(click());

        // Wait for Firebase Authentication to complete (not ideal, see notes below)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that HomeActivity is displayed by checking a unique view
        // Replace R.id.home_view with an actual view ID from HomeActivity
        onView(withId(R.id.home_screen)).check(matches(isDisplayed()));

        // Optional: Confirm the user is signed in
        assertNotNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    // Test Case 2: Login attempt with empty fields
    @Test
    public void testLoginWithEmptyFields() {
        // Click login button without entering any credentials
        onView(withId(R.id.login_button)).perform(click());

        // Verify that LoginActivity is still displayed
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

        // Optional: Confirm no user is signed in
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    // Test Case 3: Login attempt with invalid credentials
    @Test
    public void testLoginWithInvalidCredentials() {
        // Enter invalid email and password
        onView(withId(R.id.edit_text_login_username))
                .perform(typeText("invalid@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edit_text_login_password))
                .perform(typeText("wrongpassword"), closeSoftKeyboard());

        // Click the login button
        onView(withId(R.id.login_button)).perform(click());

        // Wait for authentication attempt to fail
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that LoginActivity is still displayed
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

        // Optional: Confirm no user is signed in
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    // Test Case 4: Navigation to SignUpActivity via sign-up link
    @Test
    public void testNavigateToSignUp() {
        // Click the sign-up link
        onView(withId(R.id.link_to_signup)).perform(click());

        // Verify that SignUpActivity is displayed by checking a unique view
        // Replace R.id.signup_view with an actual view ID from SignUpActivity
        onView(withId(R.id.signup_view)).check(matches(isDisplayed()));
    }
}