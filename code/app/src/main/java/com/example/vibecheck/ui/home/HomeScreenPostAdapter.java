package com.example.vibecheck.ui.home;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.Mood;
import com.example.vibecheck.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeScreenPostAdapter extends RecyclerView.Adapter<HomeScreenPostAdapter.HomeScreenPostViewHolder> {

    private List<Mood> moodPosts = new ArrayList<>();

    //Mood state to color mapping
    private static final Map<Mood.MoodState, String> moodColors = new HashMap<>();
    private static final Map<Mood.MoodState, String> moodEmojis = new HashMap<>();

    static {
        moodColors.put(Mood.MoodState.CONFUSION, "@colors/confusion");//Gray
        moodColors.put(Mood.MoodState.ANGER, "@colors/anger");    //Red
        moodColors.put(Mood.MoodState.DISGUST, "@colors/disgust");  //Green
        moodColors.put(Mood.MoodState.FEAR, "@colors/fear");     //Purple
        moodColors.put(Mood.MoodState.HAPPINESS, "@colors/happiness");//Yellow
        moodColors.put(Mood.MoodState.SADNESS, "@colors/sadness");  //Blue
        moodColors.put(Mood.MoodState.SHAME, "@colors/shame");    //Pink
        moodColors.put(Mood.MoodState.SURPRISE, "@colors/surprise"); //Orange
        moodColors.put(Mood.MoodState.BOREDOM, "@colors/boredom");  // Gray

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

    //Update mood post data in the adapter
    public void setMoodPosts(List<Mood> newPosts) {
        this.moodPosts = newPosts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HomeScreenPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_post, parent, false);
        return new HomeScreenPostViewHolder(view);
    }

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
        String color = moodColors.getOrDefault(mood.getMoodState(), "#FFFFFF");//Default White
        holder.moodPostContainer.setBackgroundColor(Color.parseColor(color));

        //Set emoji based on mood state
        holder.moodEmoji.setText(moodEmojis.getOrDefault(mood.getMoodState(), "🙂"));
    }

    @Override
    public int getItemCount() {
        return moodPosts.size();
    }

    static class HomeScreenPostViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText, moodTriggerText, moodDescriptionText, moodEmoji;
        RelativeLayout moodPostContainer;

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