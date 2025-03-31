package com.example.vibecheck;

import static androidx.test.InstrumentationRegistry.getContext;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.anything;

import android.widget.ListView;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.vibecheck.ui.history.MoodHistoryActivity;
import com.example.vibecheck.ui.moodevents.Mood;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MoodHistoryActivityTest {
    private static FirebaseAuth mAuth;

    @Rule
    public ActivityScenarioRule<MoodHistoryActivity> scenario = new
            ActivityScenarioRule<>(MoodHistoryActivity.class);

    @BeforeClass
    public static void startup(){
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("jo@gmail.com","joel02");
    }

    @Test
    public void moodHistoryFilterTest(){
        //Filter : Only Sadness mood events
        onView(withId(R.id.mood_history_filter_button)).perform(click());
        onView(withId(R.id.sadness_box)).perform(click());
        onView(withText("Confirm")).perform(click());
    }
}
