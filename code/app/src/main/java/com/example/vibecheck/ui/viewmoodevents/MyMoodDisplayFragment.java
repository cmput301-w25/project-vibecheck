/*
This fragment class is to display the logged-in user's mood event. It is distinct from the
UserMoodDisplayFragment because it provides functionality to navigate to the edit mood event.
Populates the fields with the mood event's information, colour-codes the mood type and description,
emoji-codes the mood type, and handles the back button click.

Outstanding issues: Currently not in use as the functionality to recognize if a selected mood event post
belongs to the logged in user is not implemented. We need to be able to compare the user id of the logged in user
to the user id of the selected post, and Top needs padding to access back button on certain devices
 */

//user oliverrlmoore@gmail.com
//pass MysteryChimp


/*
QUICK LOGIN INFO
oliverrlmoore@gmail.com
MysteryChimp
 */


package com.example.vibecheck.ui.viewmoodevents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.vibecheck.EditMoodEventActivity;
import com.example.vibecheck.R;
import com.example.vibecheck.Mood;
import com.example.vibecheck.User;
import com.example.vibecheck.MoodUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Fragment to display the logged-in user's mood event.
 */
public class MyMoodDisplayFragment extends Fragment {

    private TextView moodDate, moodType, moodTrigger, moodDescription, socialSituation;
    private ImageView backButton, editButton;
    private ListenerRegistration moodListener;
    private androidx.cardview.widget.CardView moodTypeCard, moodDescriptionCard;
    private String moodEventId;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();


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
        View view = inflater.inflate(R.layout.my_mood_display_view, container, false);

        //Initialize UI elements
        moodDate = view.findViewById(R.id.mood_date);
        moodType = view.findViewById(R.id.mood_type);
        moodTrigger = view.findViewById(R.id.mood_trigger);
        moodDescription = view.findViewById(R.id.mood_description);
        socialSituation = view.findViewById(R.id.social_situation);
        backButton = view.findViewById(R.id.back_button);
        editButton = view.findViewById(R.id.edit_button);
        moodTypeCard = view.findViewById(R.id.mood_type_card);
        moodDescriptionCard = view.findViewById(R.id.mood_description_card);

        //Get mood event ID from arguments
        moodEventId = getArguments().getString("moodEventId");

        //Load user's own mood event
        loadMoodEvent(moodEventId);

        //Handle back button click
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
        backButton.setOnClickListener(v -> navController.popBackStack());

        //Handle edit button click
        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditMoodEventActivity.class);
            intent.putExtra("moodEventId", moodEventId);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Loads a mood event from Firestore and updates the UI accordingly.
     * @param moodEventId
     */
    public void loadMoodEvent(String moodEventId) {
        DocumentReference moodRef = db.collection("moods").document(moodEventId);
        moodListener = moodRef.addSnapshotListener((snapshot, error) -> {
            if (snapshot != null && snapshot.exists()) {
                Mood mood = snapshot.toObject(Mood.class);

                if (mood != null) {
                    moodDate.setText(mood.getFormattedTimestamp());
                    moodType.setText(MoodUtils.getEmojiForMood(mood.getMoodState()) + " " + mood.moodStateToString());
                    moodDescription.setText(mood.getDescription());

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
     *  Called when the fragment is visible to the user and actively running, refreshes fragment upon return
     */
    @Override
    public void onResume() {
        super.onResume();
        if (moodEventId != null) {
            loadMoodEvent(moodEventId);
        }
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
