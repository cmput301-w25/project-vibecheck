package com.example.vibecheck.ui.viewmoodevents;

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
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
        String moodEventId = getArguments().getString("moodEventId");

        //Load user's own mood event
        loadMoodEvent(moodEventId);

        //Handle back button click
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
        backButton.setOnClickListener(v -> navController.popBackStack());

        /*
        WILL NEED TO IMPLEMENT EDIT BUTTON LATER, COMMENTED OUT FOR NOW
        NEED TO KEEP IN MIND VALIDATION OF WETHER THE MOOD ON DISPLAY BELONGS TO THE
        USER LOGGED IN OR IF ITS AN EXTERNAL USER.
        MAYBE SEE ABOUT VALIDATING THAT WHENEVER THE LOGGED IN USER TAPS ON A MOOD EVENT TO
        VIEW IT, POSSIBLY MAKE THAT A MoodUtil OR JUST CHECK AND VALIDATE IN WHATEVER FRAGMENT THEY CAN TAP ON
        A MOOD EVENT IN

        //Handle edit button click
        editButton.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("moodEventId", moodEventId);
            navController.navigate(R.id.((((NAV NAME TO PUT HERE LATER)))), args);
        });
        */

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