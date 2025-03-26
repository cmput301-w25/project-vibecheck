package com.example.vibecheck.ui.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;

import java.util.ArrayList;

public class MoodHistoryEntryAdapter extends ArrayAdapter<MoodHistoryEntry> {

    private TextView username;
    private TextView location;
    private TextView description;
    private LinearLayout moodPostContainer;
    private TextView moodEmoji;

    public MoodHistoryEntryAdapter(Context context, ArrayList<MoodHistoryEntry> moodHistory) {
        super(context, 0, moodHistory);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.mood_event,
                    parent, false);
        } else {
            view = convertView;
        }

        MoodHistoryEntry entry = getItem(position);

        username = view.findViewById(R.id.username);
        location = view.findViewById(R.id.location);
        description = view.findViewById(R.id.moodDescriptionText);
        moodPostContainer = view.findViewById(R.id.description_background);
        moodEmoji = view.findViewById(R.id.moodEmoji);

        username.setText(entry.getMood().getUsername());

        if(entry.getMood().getLatitude() == null || entry.getMood().getLongitude() == null){
            location.setText("");
        }else{
            String latitude = entry.getMood().getLatitude().toString();
            String longitude = entry.getMood().getLongitude().toString();
            String locationString = "(" + latitude + ", " + longitude + ")";
            location.setText(locationString);
        }

        description.setText(entry.getMood().getDescription());

        // Change Background Color Based on Mood
        int moodColor = MoodUtils.getMoodColor(getContext(), entry.getMood().getMoodState());
        moodPostContainer.setBackgroundColor(moodColor);

        // Change Emoji Based on Mood
        String emoji = MoodUtils.getEmojiForMood(entry.getMood().getMoodState());
        moodEmoji.setText(emoji);

        return view;
    }

}
