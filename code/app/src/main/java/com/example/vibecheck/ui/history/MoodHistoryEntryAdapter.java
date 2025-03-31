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
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.history.MoodHistoryEntry;
import com.example.vibecheck.ui.home.HomeScreenPostAdapter;
import com.example.vibecheck.ui.moodevents.Mood;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the history screen recycler view. Displays mood history entries.
 */
public class MoodHistoryEntryAdapter extends RecyclerView.Adapter<MoodHistoryEntryAdapter.ViewHolder> {
    private final List<MoodHistoryEntry> moodHistoryList;
    private final Context context;

    private OnMoodClickListener onMoodClickListener;

    public interface OnMoodClickListener {
        void onMoodClick(Mood mood);
    }

    /**
     * Constructor for the adapter.
     * @param context
     * @param moodHistoryList
     */
    public MoodHistoryEntryAdapter(Context context, List<MoodHistoryEntry> moodHistoryList, OnMoodClickListener listener) {
        this.context = context;
        this.moodHistoryList = moodHistoryList;
        this.onMoodClickListener = listener;
    }

    /**
     * ViewHolder for the history screen recycler view.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView displayNameText, location, description, moodEmoji, dateText;
        LinearLayout moodPostContainer;

        /**
         * Constructor for the ViewHolder
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            displayNameText = itemView.findViewById(R.id.username);
            location = itemView.findViewById(R.id.location);
            description = itemView.findViewById(R.id.moodDescriptionText);
            moodEmoji = itemView.findViewById(R.id.moodEmoji);
            dateText = itemView.findViewById(R.id.mood_date);
            moodPostContainer = itemView.findViewById(R.id.description_background);
        }
    }


    /**
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     */
    @NonNull
    @Override
    public MoodHistoryEntryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.mood_event, parent, false);
        return new ViewHolder(view);
    }


    /**
     * Used to bind mood info to views
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull MoodHistoryEntryAdapter.ViewHolder holder, int position) {
        MoodHistoryEntry entry = moodHistoryList.get(position);
        Mood mood = entry.getMood();

        MoodUtils.getDisplayName(mood.getUsername(), displayName -> {
            holder.displayNameText.setText(displayName);
        });

        holder.dateText.setText(mood.getFormattedTimestamp());

        if (mood.getLocation() == null) {
            holder.location.setText("Location: N/A");
        } else {
            String locationString = mood.getLocation();
            holder.location.setText(locationString);
        }

        holder.description.setText(mood.getDescription());

        int moodColor = MoodUtils.getMoodColor(context, mood.getMoodState());
        holder.moodPostContainer.setBackgroundColor(moodColor);

        holder.moodEmoji.setText(MoodUtils.getEmojiForMood(mood.getMoodState()));

        // Handle click on mood post
        holder.itemView.setOnClickListener(v -> {
            if (mood.getMoodId() == null || mood.getMoodId().isEmpty()) {
                return;
            }

            if (onMoodClickListener != null) {
                onMoodClickListener.onMoodClick(mood);
            }
        });
    }

    public void updateData(List<MoodHistoryEntry> newData) {
        moodHistoryList.clear();
        moodHistoryList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return moodHistoryList.size();
    }
}



/*
public class MoodHistoryEntryAdapter extends ArrayAdapter<MoodHistoryEntry> {

    private TextView displayNameText;
    private TextView location;
    private TextView description;
    private LinearLayout moodPostContainer;
    private TextView moodEmoji;

    private TextView dateText;

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

        displayNameText = view.findViewById(R.id.username);
        location = view.findViewById(R.id.location);
        description = view.findViewById(R.id.moodDescriptionText);
        moodPostContainer = view.findViewById(R.id.description_background);
        moodEmoji = view.findViewById(R.id.moodEmoji);
        dateText = view.findViewById(R.id.mood_date);

        String username = entry.getMood().getUsername();
        MoodUtils.getDisplayName(username, displayName -> {
            displayNameText.setText(displayName);
        });

        dateText.setText(entry.getMood().getFormattedTimestamp());

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

 */
