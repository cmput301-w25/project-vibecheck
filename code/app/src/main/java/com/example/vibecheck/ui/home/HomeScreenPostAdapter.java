/**
 * This java class is the adapter for posts on the home screen feed of the app. Populates the recycler view with
 * home screen mood posts, which are themselves populated with data from the homescreen viewmodel.
 * Through the use of the MoodUtils class, emoji and colour-coding are set based on the mood state
 * for each post.
 *
 * This class has no outstanding issues
 */

package com.example.vibecheck.ui.home;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.ui.moodevents.Mood;
import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the home screen recycler view. Displays mood posts.
 */
public class HomeScreenPostAdapter extends RecyclerView.Adapter<HomeScreenPostAdapter.HomeScreenPostViewHolder> {

    private List<Mood> moodPosts = new ArrayList<>();
    private List<Mood> moodPostsCopy = new ArrayList<>(moodPosts);
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
        this.moodPostsCopy = newPosts;
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

        //Set user information, looks for display name, if not found, uses username
        String username = mood.getUsername();
        MoodUtils.getDisplayName(username, displayName -> {
            holder.displayNameText.setText(displayName);
        });

        //Set location text
        holder.locationText.setText(mood.getLocation() != null ? mood.getLocation() : "Location: N/A");

        //Set mood reason text
        holder.moodDescriptionText.setText(mood.getDescription() != null ? mood.getDescription() : "No reason");

        //Change background color based on mood state
        holder.moodPostContainer.setBackgroundColor(MoodUtils.getMoodColor(holder.itemView.getContext(), mood.getMoodState()));

        //Set emoji based on mood state
        holder.moodEmoji.setText(MoodUtils.getEmojiForMood(mood.getMoodState()));

        //Set date text
        holder.dateText.setText(mood.getFormattedTimestamp());

        //Handle click on mood post
        holder.homeScreenMoodPost.setOnClickListener(v -> {
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
     * @return
     *      Returns the total number of items in the data set held by the adapter.
     */
    @Override
    public int getItemCount() {
        return moodPosts.size();
    }

    /**
     * ViewHolder for the home screen recycler view.
     */
    static class HomeScreenPostViewHolder extends RecyclerView.ViewHolder {
        TextView displayNameText, moodDescriptionText, moodEmoji, dateText, locationText;
        RelativeLayout moodPostContainer;
        LinearLayout homeScreenMoodPost;

        /**
         * Constructor for the ViewHolder, initializes the views.
         * @param itemView
         *      The view to be displayed
         */
        public HomeScreenPostViewHolder(@NonNull View itemView) {
            super(itemView);
            homeScreenMoodPost = itemView.findViewById(R.id.home_screen_mood_post);
            displayNameText = itemView.findViewById(R.id.username);
            locationText = itemView.findViewById(R.id.location);
            moodDescriptionText = itemView.findViewById(R.id.moodDescriptionText);
            moodEmoji = itemView.findViewById(R.id.moodEmoji);
            dateText = itemView.findViewById(R.id.mood_date);
            moodPostContainer = itemView.findViewById(R.id.mood_post_container);
        }
    }

    /**
     * Filter the mood posts based on the query.
     * @param query
     *      The query to filter the mood posts by.
     */
    public void filter(String query) {
       //moodPosts.clear();
        List<Mood> filteredMoodPosts = new ArrayList<>();
        if (query.isEmpty()) {
            filteredMoodPosts.addAll(moodPostsCopy);
        } else {
            for (Mood mood : moodPostsCopy) {
                if (mood.getDescription() != null && mood.getDescription().toLowerCase().contains(query.toLowerCase())) {
                    filteredMoodPosts.add(mood);
                }
            }
        }
        this.moodPosts = filteredMoodPosts;
        notifyDataSetChanged();
    }
}