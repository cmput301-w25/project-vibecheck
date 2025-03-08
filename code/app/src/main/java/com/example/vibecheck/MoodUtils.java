package com.example.vibecheck;

import android.content.Context;
import androidx.core.content.ContextCompat;
import com.example.vibecheck.Mood;

/**
 * Utility class for colour-coding moods and emojis.
 */
public class MoodUtils {

    /**
     * Returns the color resource ID for a given MoodState.
     */
    private static int getMoodColourResourceID(Mood.MoodState moodState) {
        if (moodState == null) return R.color.white; // Default fallback color
        switch (moodState) {
            case ANGER: return R.color.anger;
            case CONFUSION: return R.color.confusion;
            case DISGUST: return R.color.disgust;
            case FEAR: return R.color.fear;
            case HAPPINESS: return R.color.happiness;
            case SADNESS: return R.color.sadness;
            case SHAME: return R.color.shame;
            case SURPRISE: return R.color.surprise;
            case BOREDOM: return R.color.boredom;
            default: return R.color.white;
        }
    }


    /**
     * Returns the actual color (int) for a given MoodState using ContextCompat.
     */
    public static int getMoodColor(Context context, Mood.MoodState moodState) {
        return ContextCompat.getColor(context, getMoodColourResourceID(moodState));
    }


    /**
     * Returns the emoji associated with a given MoodState.
     */
    public static String getEmojiForMood(Mood.MoodState moodState) {
        if (moodState == null) return "🙂"; // Default emoji
        switch (moodState) {
            case ANGER: return "😡";
            case CONFUSION: return "😕";
            case DISGUST: return "🤢";
            case FEAR: return "😨";
            case HAPPINESS: return "😃";
            case SADNESS: return "😢";
            case SHAME: return "😳";
            case SURPRISE: return "😲";
            case BOREDOM: return "😴";
            default: return "🙂";
        }
    }
}
