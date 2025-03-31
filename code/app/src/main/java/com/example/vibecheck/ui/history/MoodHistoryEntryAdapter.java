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

/**
 * Adapter for the MoodHistory entries
 */
public class MoodHistoryEntryAdapter extends ArrayAdapter<MoodHistoryEntry> {

    private TextView username;
    private TextView location;
    private TextView description;
    private LinearLayout moodPostContainer;
    private TextView moodEmoji;

    /**
     * Constructor for MoodHistoryEntryAdapter
     * @param context
     *      The context in which the adapter is running
     * @param moodHistory
     *      Mood History that is the basis of the array
     */
    public MoodHistoryEntryAdapter(Context context, ArrayList<MoodHistoryEntry> moodHistory) {
        super(context, 0, moodHistory);
    }

    /**
     * Get a View that displays the data at the specified position in the data set.
     * @param position The position of the item within the adapter's data set of the item whose view
     *        we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *        is non-null and of an appropriate type before using. If it is not possible to convert
     *        this view to display the correct data, this method can create a new view.
     *        Heterogeneous lists can specify their number of view types, so that this View is
     *        always of the right type (see {@link #getViewTypeCount()} and
     *        {@link #getItemViewType(int)}).
     * @param parent The parent that this view will eventually be attached to
     * @return
     *      Returns this view
     */
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
