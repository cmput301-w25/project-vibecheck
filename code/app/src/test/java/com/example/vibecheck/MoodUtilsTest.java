package com.example.vibecheck;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;

import com.example.vibecheck.ui.moodevents.Mood;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Unit tests for MoodUtils.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30)
public class MoodUtilsTest {

    @Test
    public void testGetEmojiForMood() {
        // Verify emoji for each MoodState
        assertEquals("😡", MoodUtils.getEmojiForMood(Mood.MoodState.ANGER));
        assertEquals("😕", MoodUtils.getEmojiForMood(Mood.MoodState.CONFUSION));
        assertEquals("🤢", MoodUtils.getEmojiForMood(Mood.MoodState.DISGUST));
        assertEquals("😨", MoodUtils.getEmojiForMood(Mood.MoodState.FEAR));
        assertEquals("😃", MoodUtils.getEmojiForMood(Mood.MoodState.HAPPINESS));
        assertEquals("😢", MoodUtils.getEmojiForMood(Mood.MoodState.SADNESS));
        assertEquals("😳", MoodUtils.getEmojiForMood(Mood.MoodState.SHAME));
        assertEquals("😲", MoodUtils.getEmojiForMood(Mood.MoodState.SURPRISE));
        assertEquals("😴", MoodUtils.getEmojiForMood(Mood.MoodState.BOREDOM));

        // Verify default emoji for null mood
        assertEquals("🙂", MoodUtils.getEmojiForMood(null));
    }

    @Test
    public void testGetMoodColor() {
        Context context = ApplicationProvider.getApplicationContext();

        // For null mood, should fallback to white
        int expectedWhite = ContextCompat.getColor(context, R.color.white);
        assertEquals(expectedWhite, MoodUtils.getMoodColor(context, null));

        // Test for a specific mood, e.g. ANGER
        int expectedAngerColor = ContextCompat.getColor(context, R.color.anger);
        assertEquals(expectedAngerColor, MoodUtils.getMoodColor(context, Mood.MoodState.ANGER));

        // Test for another mood, e.g. HAPPINESS
        int expectedHappinessColor = ContextCompat.getColor(context, R.color.happiness);
        assertEquals(expectedHappinessColor, MoodUtils.getMoodColor(context, Mood.MoodState.HAPPINESS));
    }
}
