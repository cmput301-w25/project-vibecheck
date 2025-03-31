package com.example.vibecheck;

import android.os.SystemClock;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AddMoodEventActivityTest {

    @Rule
    public ActivityScenarioRule<AddMoodEventActivity> activityRule =
            new ActivityScenarioRule<>(AddMoodEventActivity.class);

    @Before
    public void setUp() {
        // Sign in anonymously so currentUser != null
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInAnonymously();
            SystemClock.sleep(1500);
        }
    }

    /**
     * Test Case 1: Verify that clicking the back button finishes the activity.
     */
    @Test
    public void testBackButtonFinishesActivity() {
        ActivityScenario<AddMoodEventActivity> scenario = activityRule.getScenario();
        onView(withId(R.id.button_back)).perform(click());
        scenario.onActivity(activity -> {
            assertTrue("Activity should be finishing after back button click", activity.isFinishing());
        });
    }
    /**
     * Test Case 2: Verify that selecting a mood updates UI elements (emoji).
     */
    @Test
    public void testMoodSelectionUpdatesUI() {
        // Use the exact text from your R.array.mood_options (e.g., "Happiness")
        onView(withId(R.id.dropdown_mood)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Happiness"))).perform(click());

        // Check that the emoji TextView is not empty
        onView(withId(R.id.mood_emoji)).check(matches(not(withText(""))));
    }

    /**
     * Test Case 3: Verify that saving a mood with valid data finishes the activity.
     */
    @Test
    public void testSaveMoodWithValidData() throws InterruptedException {
        // 1) Select "Happiness"
        onView(withId(R.id.dropdown_mood)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Happiness"))).perform(click());

        // 2) Enter trigger text
        onView(withId(R.id.input_trigger)).perform(typeText("Feeling great"), closeSoftKeyboard());

        // 3) Select social situation "Alone"
        onView(withId(R.id.dropdown_social)).perform(click());
        onData(allOf(is(instanceOf(String.class)), is("Alone"))).perform(click());

        // 4) Click the save mood button
        onView(withId(R.id.button_save_mood)).perform(click());

        // 5) Wait for the asynchronous Firestore operation to complete and activity to finish
        SystemClock.sleep(3000);

        // 6) Verify that the activity has been destroyed
        ActivityScenario<AddMoodEventActivity> scenario = activityRule.getScenario();
        assertTrue("Activity should be destroyed after saving mood",
                scenario.getState() == Lifecycle.State.DESTROYED);
    }
}
