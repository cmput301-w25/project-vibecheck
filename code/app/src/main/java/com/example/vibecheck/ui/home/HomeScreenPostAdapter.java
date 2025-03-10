/*
This java class is the adapter for posts on the home screen feed of the app. Populates the recycler view with
home screen mood posts, which are themselves populated with data from the homescreen viewmodel.
Through the use of the MoodUtils class, emoji and colour-coding are set based on the mood state
for each post.

Outstanding issues: Recognizing logged in users to select my mood display or user mood display,
issues and crashing occurs when certain parts of mood info are null
 */

package com.example.vibecheck.ui.home;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.Mood;
import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter for the home screen recycler view. Displays mood posts.
 */
public class HomeScreenPostAdapter extends RecyclerView.Adapter<HomeScreenPostAdapter.HomeScreenPostViewHolder> {

    private List<Mood> moodPosts = new ArrayList<>();
    private OnMoodClickListener onMoodClickListener;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    /**
     * Click listener for mood posts.
     */
    public interface OnMoodClickListener {
        void onMoodClick(String moodEventId);
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

        // Get current user from firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        //Set user information
        holder.usernameText.setText(mood.getUsername());

        //Set mood trigger text
        holder.moodTriggerText.setText(mood.getTrigger() != null ? mood.getTrigger() : "No trigger");

        //Set mood description text
        holder.moodDescriptionText.setText(mood.getDescription() != null ? mood.getDescription() : "No description");

        //Change background color based on mood state
        holder.moodPostContainer.setBackgroundColor(MoodUtils.getMoodColor(holder.itemView.getContext(), mood.getMoodState()));

        //Set emoji based on mood state
        holder.moodEmoji.setText(MoodUtils.getEmojiForMood(mood.getMoodState()));

        //Check if the mood post is from the logged in user
        String moodUsername = mood.getUsername();
        String currentEmail = (currentUser != null) ? currentUser.getEmail() : null;
        boolean isLoggedUserPost = moodUsername != null && currentEmail != null && moodUsername.equals(currentEmail);

        //Handle click on mood post
        holder.moodPostContainer.setOnClickListener(v -> {
            Log.d("HomeScreenPostAdapter", "Mood Post Clicked. MoodEventId: " + mood.getDocumentId());

            if (mood.getDocumentId() == null || mood.getDocumentId().isEmpty()) {
                Log.e("HomeScreenPostAdapter", "ERROR: mood.getDocumentId() is NULL or EMPTY!");
                return;
            }

            if (onMoodClickListener != null) {
                onMoodClickListener.onMoodClick(mood.getDocumentId());
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
}