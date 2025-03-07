package com.example.vibecheck.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.Mood;
import com.example.vibecheck.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the home screen recycler view. Displays mood posts.
 */
public class HomeScreenPostAdapter extends RecyclerView.Adapter<HomeScreenPostAdapter.HomeScreenPostViewHolder> {

    private List<Mood> moodPosts = new ArrayList<>();

    //Mood state to color mapping
    private static final Map<Mood.MoodState, String> moodColors = new HashMap<>();
    private static final Map<Mood.MoodState, String> moodEmojis = new HashMap<>();

    /**
     * Initialize mood state emoji mapping
     */
    static {
        moodEmojis.put(Mood.MoodState.ANGER, "😡");
        moodEmojis.put(Mood.MoodState.CONFUSION, "😕");
        moodEmojis.put(Mood.MoodState.DISGUST, "🤢");
        moodEmojis.put(Mood.MoodState.FEAR, "😨");
        moodEmojis.put(Mood.MoodState.HAPPINESS, "😃");
        moodEmojis.put(Mood.MoodState.SADNESS, "😢");
        moodEmojis.put(Mood.MoodState.SHAME, "😳");
        moodEmojis.put(Mood.MoodState.SURPRISE, "😲");
        moodEmojis.put(Mood.MoodState.BOREDOM, "😴");
    }

    /**
     * Set the mood posts in the adapter.
     * @param newPosts
     *      These are the new posts to add to the adapter.
     */
    public void setMoodPosts(List<Mood> newPosts) {
        this.moodPosts = newPosts;
        notifyDataSetChanged();
    }

    /**
     * Called when RecyclerView needs a new {@link HomeScreenPostViewHolder} of the given type to represent
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return
     *      returns a new {@link HomeScreenPostViewHolder}
     */
    @NonNull
    @Override
    public HomeScreenPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_post, parent, false);
        return new HomeScreenPostViewHolder(view);
    }

    /**
     * Used to bind mood info to views
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HomeScreenPostViewHolder holder, int position) {
        Mood mood = moodPosts.get(position);

        //Set user information
        holder.usernameText.setText(mood.getUsername());

        //Set mood trigger text
        holder.moodTriggerText.setText(mood.getTrigger() != null ? mood.getTrigger() : "No trigger");

        //Set mood description text
        holder.moodDescriptionText.setText(mood.getDescription() != null ? mood.getDescription() : "No description");

        //Change background color based on mood state
        int colorResId = getMoodColourResourceID(mood.getMoodState());
        holder.moodPostContainer.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), colorResId));

        //Set emoji based on mood state
        holder.moodEmoji.setText(moodEmojis.getOrDefault(mood.getMoodState(), "🙂"));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return
     */
    @Override
    public int getItemCount() {
        return moodPosts.size();
    }

    /**
     * ViewHolder for the home screen recycler view.
     */
    static class HomeScreenPostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, moodTriggerText, moodDescriptionText, moodEmoji;
        RelativeLayout moodPostContainer;

        /**
         * Constructor for the ViewHolder
         * @param itemView
         *      The view to be displayed
         */
        public HomeScreenPostViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.username);
            moodTriggerText = itemView.findViewById(R.id.moodTriggerText);
            moodDescriptionText = itemView.findViewById(R.id.moodDescriptionText);
            moodEmoji = itemView.findViewById(R.id.moodEmoji);
            moodPostContainer = itemView.findViewById(R.id.mood_post_container);
        }
    }

    private int getMoodColourResourceID(Mood.MoodState moodState) {
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
}