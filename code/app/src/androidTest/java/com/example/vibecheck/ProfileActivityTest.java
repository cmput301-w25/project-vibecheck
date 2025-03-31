package com.example.vibecheck;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNull;

import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.vibecheck.ui.profile.ProfileActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ProfileActivityTest {

    // Rule to launch ProfileActivity for each test
    @Rule
    public ActivityScenarioRule<ProfileActivity> activityRule =
            new ActivityScenarioRule<>(ProfileActivity.class);

    // Ensure the user is signed in before each test
    @Before
    public void setUp() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // Sign in with a test user (ensure this user exists in Firebase)
            mAuth.signInWithEmailAndPassword("yo@gmail.com", "abcd12345")
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            throw new RuntimeException("Failed to sign in test user");
                        }
                    });
        }
        // Wait for sign-in to complete (simple delay; consider IdlingResource for production)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Test Case 1: Profile data is correctly displayed
    @Test
    public void testProfileDataDisplay() {
        // Wait for data to load from Firestore
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Assuming the test user has known data in Firestore
        onView(withId(R.id.display_name_edit_text)).check(matches(withText("anhad")));
        onView(withId(R.id.follower_count)).check(matches(withText("1"))); // Adjust based on actual data
        onView(withId(R.id.following_count)).check(matches(withText("0"))); // Adjust based on actual data
    }

    // Test Case 2: Follow requests are displayed
    @Test
    public void testFollowRequestsDisplay() {
        // Assuming there are follow requests for the test user
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if follow requests title is visible
        onView(withId(R.id.follow_requests_title)).check(matches(isDisplayed()));

        // Check if at least one follow request item is displayed
        onView(withId(R.id.follow_requests_container))
                .check(matches(hasDescendant(withId(R.id.request_username))));
    }


    // Test Case 6: Back arrow navigation
    @Test
    public void testBackArrowNavigation() {
        // Click the back arrow
        onView(withId(R.id.back_arrow)).perform(click());

        // Verify that HomeActivity is displayed (adjust ID to a known HomeActivity view)
        onView(withId(R.id.home_screen)).check(matches(isDisplayed()));
    }

    // Test Case 7: Logout functionality
    @Test
    public void testLogout() {
        // Click the logout button
        onView(withId(R.id.logout_button)).perform(click());

        // Verify that LoginActivity is displayed (adjust ID to a known LoginActivity view)
        onView(withId(R.id.login_button)).check(matches(isDisplayed()));

        // Confirm the user is signed out
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }
}

