/*
This java class is the adapter for posts on the home screen feed of the app. Populates the recycler view with
home screen mood posts, which are themselves populated with data from the homescreen viewmodel.
Through the use of the MoodUtils class, emoji and colour-coding are set based on the mood state
for each post.

Outstanding issues: Recognizing logged in users to select my mood display or user mood display,
issues and crashing occurs when certain parts of mood info are null
 */

package com.example.vibecheck.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.ui.moodevents.Mood;
import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the home screen recycler view. Displays mood posts.
 */
public class HomeScreenPostAdapter extends RecyclerView.Adapter<HomeScreenPostAdapter.HomeScreenPostViewHolder> {

    private List<Mood> moodPosts = new ArrayList<>();
    private OnMoodClickListener onMoodClickListener;

    /**
     * Click listener for mood posts.
     */
    public interface OnMoodClickListener {
        void onMoodClick(Mood mood);
    }

    /**
     * Constructor for the adapter.
     * @param listener
     */
    public HomeScreenPostAdapter(OnMoodClickListener listener) {
        this.onMoodClickListener = listener;
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
        holder.displayNameText.setText(mood.getUsername());

        //Set mood trigger text
        holder.moodTriggerText.setText(mood.getTrigger() != null ? mood.getTrigger() : "No trigger");

        //Set mood description text
        holder.moodDescriptionText.setText(mood.getDescription() != null ? mood.getDescription() : "No description");

        //Change background color based on mood state
        holder.moodPostContainer.setBackgroundColor(MoodUtils.getMoodColor(holder.itemView.getContext(), mood.getMoodState()));

        //Set emoji based on mood state
        holder.moodEmoji.setText(MoodUtils.getEmojiForMood(mood.getMoodState()));

        //Handle click on mood post
        holder.moodPostContainer.setOnClickListener(v -> {
            Log.d("HomeScreenPostAdapter", "Mood Post Clicked. MoodEventId: " + mood.getMoodId());

            if (mood.getMoodId() == null || mood.getMoodId().isEmpty()) {
                Log.e("HomeScreenPostAdapter", "ERROR: mood.getDocumentId() is NULL or EMPTY!");
                return;
            }

            if (onMoodClickListener != null) {
                onMoodClickListener.onMoodClick(mood);
            } else {
                Log.e("HomeScreenPostAdapter", "ERROR: onMoodClickListener is NULL!");
            }
        });
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
        TextView displayNameText, moodTriggerText, moodDescriptionText, moodEmoji;
        RelativeLayout moodPostContainer;

        /**
         * Constructor for the ViewHolder
         * @param itemView
         *      The view to be displayed
         */
        public HomeScreenPostViewHolder(@NonNull View itemView) {
            super(itemView);
            displayNameText = itemView.findViewById(R.id.username);
            moodTriggerText = itemView.findViewById(R.id.moodReasonText);
            moodDescriptionText = itemView.findViewById(R.id.moodDescriptionText);
            moodEmoji = itemView.findViewById(R.id.moodEmoji);
            moodPostContainer = itemView.findViewById(R.id.mood_post_container);
        }
    }
}