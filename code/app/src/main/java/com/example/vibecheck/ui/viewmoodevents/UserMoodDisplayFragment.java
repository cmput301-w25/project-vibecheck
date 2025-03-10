/*
This class is the fragment that operates to display a user's mood event as a fragment on the home activity.
Simple functionality, populates the fields with the mood event's information, colour-codes the mood type and description,
emoji-codes the mood type, and handles the back button click.

Outstanding issues: Top needs padding so the back button is pressable (on certain devices). Bottom navigation still works
to change page is back button inaccessable
 */

package com.example.vibecheck.ui.viewmoodevents;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vibecheck.R;
import com.example.vibecheck.Mood;
import com.example.vibecheck.User;
import com.example.vibecheck.MoodUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Date;

/**
 * Fragment to display a user's mood event.
 */
public class UserMoodDisplayFragment extends Fragment{
    private TextView usernameText, moodDate, moodType, moodTrigger, moodDescription, socialSituation;
    private ImageView backButton;
    private ListenerRegistration moodListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private androidx.cardview.widget.CardView moodTypeCard, moodDescriptionCard;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *      Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.user_mood_display_view, container, false);

        //Get mood event ID from navigation arguments
        String moodEventId = getArguments() != null ? getArguments().getString("moodEventId") : null;
        if (moodEventId == null || moodEventId.isEmpty()) {
            Log.e("UserMoodDisplay", "Error: moodEventId is null or empty");
            Toast.makeText(getContext(), "Error loading mood event.", Toast.LENGTH_SHORT).show();
            return view; // Prevents crash by stopping execution
        }

        //Initialize UI elements
        usernameText = view.findViewById(R.id.username_mood_title);
        moodDate = view.findViewById(R.id.mood_date);
        moodType = view.findViewById(R.id.mood_type);
        moodTrigger = view.findViewById(R.id.mood_trigger);
        moodDescription = view.findViewById(R.id.mood_description);
        socialSituation = view.findViewById(R.id.social_situation);
        backButton = view.findViewById(R.id.back_button);
        moodTypeCard = view.findViewById(R.id.mood_type_card);
        moodDescriptionCard = view.findViewById(R.id.mood_description_card);

        //Load the mood event from Firestore
        loadMoodEvent(moodEventId);

        //Handle back button click
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
        backButton.setOnClickListener(v -> navController.popBackStack());

        return view;
    }


    /**
     * Loads a mood event from Firestore and updates the UI accordingly.
     * @param moodEventId
     */
    private void loadMoodEvent(String moodEventId) {
        DocumentReference moodRef = db.collection("moods").document(moodEventId);
        moodListener = moodRef.addSnapshotListener((snapshot, error) -> {
            if (snapshot != null && snapshot.exists()) {
                Mood mood = snapshot.toObject(Mood.class);

                if (mood != null) {
                    usernameText.setText(user.getUsername() + "'s Mood");
                    moodType.setText(MoodUtils.getEmojiForMood(mood.getMoodState()) + " " + mood.moodStateToString());
                    moodDescription.setText(mood.getDescription());

                    if (mood.getTimestamp() == null) {
                        Log.e("UserMoodDisplayFragment", "ERROR: Mood timestamp is NULL in loadMoodEvent!");
                        mood.setTimestamp(new Date()); // Prevents crash
                    }
                    moodDate.setText(mood.getFormattedTimestamp());

                    //Set mood type card and description card colors based on mood state
                    int moodColor = MoodUtils.getMoodColor(requireContext(), mood.getMoodState());
                    moodTypeCard.setCardBackgroundColor(moodColor);
                    moodDescriptionCard.setCardBackgroundColor(moodColor);

                    //Only update trigger and social situation if they are not null or empty
                    if (mood.getTrigger() != null && !mood.getTrigger().trim().isEmpty()) {
                        moodTrigger.setText(mood.getTrigger());
                    }
                    if (mood.getSocialSituation() != null && !mood.socialSituationToString().trim().isEmpty()) {
                        socialSituation.setText(mood.socialSituationToString());
                    }
                }
            }
        });
    }

    /**
     * Called when the fragment is no longer in use.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (moodListener != null) {
            moodListener.remove();
        }
    }
}
