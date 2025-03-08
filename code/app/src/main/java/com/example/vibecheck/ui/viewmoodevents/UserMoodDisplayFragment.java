package com.example.vibecheck.ui.viewmoodevents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

/**
 * Fragment to display a user's mood event.
 */
public class UserMoodDisplayFragment extends Fragment{
    private TextView usernameText, moodDate, moodType, moodTrigger, moodDescription, socialSituation;
    private ImageView backButton;
    private ListenerRegistration moodListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        //Initialize UI elements
        usernameText = view.findViewById(R.id.username_mood_title);
        moodDate = view.findViewById(R.id.mood_date);
        moodType = view.findViewById(R.id.mood_type);
        moodTrigger = view.findViewById(R.id.mood_trigger);
        moodDescription = view.findViewById(R.id.mood_description);
        socialSituation = view.findViewById(R.id.social_situation);
        backButton = view.findViewById(R.id.back_button);

        //Get mood event ID from navigation arguments
        String moodEventId = getArguments().getString("moodEventId");

        //Load the mood event from Firestore
        loadMoodEvent(moodEventId);

        //Handle back button click
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
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
                User user = snapshot.toObject(User.class);

                if (mood != null) {
                    usernameText.setText(user.getUsername() + "'s Mood");
                    moodDate.setText(mood.getFormattedTimestamp());
                    moodType.setText(MoodUtils.getEmojiForMood(mood.getMoodState()) + " " + mood.moodStateToString());
                    moodTrigger.setText(mood.getTrigger());
                    moodDescription.setText(mood.getDescription());
                    socialSituation.setText(mood.socialSituationToString());
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
