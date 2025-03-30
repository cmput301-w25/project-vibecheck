/*
This class is the fragment that operates to display a user's mood event as a fragment on the home activity.
Simple functionality, populates the fields with the mood event's information, colour-codes the mood type and description,
emoji-codes the mood type, and handles the back button click.

Outstanding issues: Top needs padding so the back button is pressable (on certain devices). Bottom navigation still works
to change page is back button inaccessable
 */

package com.example.vibecheck.ui.moodevents;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.ui.comments.Comment;
import com.example.vibecheck.ui.comments.CommentAdapter;
import com.example.vibecheck.R;
import com.example.vibecheck.MoodUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Fragment to display a user's mood event.
 */
public class UserMoodDisplayFragment extends Fragment{

    //TextViews for labels
    private TextView moodReasonLabel, socialSituationLabel, locationLabel;
    //TextViews for mood event data
    private TextView usernameText, moodDate, moodType, moodDescription, socialSituation, commentsLabel;
    private ImageView backButton, moodImage;
    private RelativeLayout topBar;
    private ListenerRegistration moodListener;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private RecyclerView recyclerView;
    private EditText commentInput;
    private ImageButton sendButton;

    private androidx.cardview.widget.CardView moodTypeCard, moodDescriptionCard;

    private CommentAdapter commentAdapter;
    private List<Comment> commentList = new ArrayList<>();
    private String moodEventUsername;

    private CardView moodImageCard;


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
        return inflater.inflate(R.layout.user_mood_display_view, container, false);
        //View view = inflater.inflate(R.layout.user_mood_display_view, container, false);
        //return view;
    }


    /**
     * Initializes ui elements, loads the mood event from Firestore, dynamically fetches the display name of the mood post's creator.
     * Loads comments from Firestore. Handles post comment and back button presses.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //Get mood event ID from navigation arguments
        String moodEventId = getArguments() != null ? getArguments().getString("moodEventId") : null;
        if (moodEventId == null || moodEventId.isEmpty()) {
            Log.e("UserMoodDisplay", "Error: moodEventId is null or empty");
            Toast.makeText(getContext(), "Error loading mood event.", Toast.LENGTH_SHORT).show();
            return;
        }
        loadMoodEvent(moodEventId);

        MoodUtils.getDisplayName(moodEventUsername, displayName -> {
            if (displayName != null) {
                usernameText.setText(displayName + "'s Mood");
            }
        });

        //Initialize UI elements
        usernameText = view.findViewById(R.id.username_mood_title);
        moodReasonLabel = view.findViewById(R.id.mood_reason_label);
        socialSituationLabel = view.findViewById(R.id.social_situation_label);
        //locationLabel = view.findViewById(R.id.location_label); COMMENTED OUT UNTIL LOCATION ADDED TO VIEW MOOD EVENT
        moodDate = view.findViewById(R.id.mood_date);
        moodType = view.findViewById(R.id.mood_type);
        //moodTrigger = view.findViewById(R.id.mood_trigger); I MAY BE OKAY TO JUST REMOVE THIS
        moodDescription = view.findViewById(R.id.mood_description);
        socialSituation = view.findViewById(R.id.social_situation);
        backButton = view.findViewById(R.id.back_button);
        moodTypeCard = view.findViewById(R.id.mood_type_card);
        moodDescriptionCard = view.findViewById(R.id.mood_description_card);
        topBar = view.findViewById(R.id.view_mood_topbar);
        commentsLabel = view.findViewById(R.id.comments_label);
        moodImageCard = view.findViewById(R.id.mood_image_card);
        moodImage = view.findViewById(R.id.mood_image);
        recyclerView = view.findViewById(R.id.comment_list);
        commentInput = view.findViewById(R.id.comment_input);
        sendButton = view.findViewById(R.id.send_comment_button);

        //Initialize comment adapter
        commentAdapter = new CommentAdapter(commentList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(commentAdapter);

        //Set optional attribute labels as invisible initially, then make them visible when their elements are present in the mood event
        moodReasonLabel.setVisibility(View.GONE);
        socialSituationLabel.setVisibility(View.GONE);
        //locationLabel.setVisibility(View.GONE); COMMENTED OUT UNTIL LOCATION ADDED TO VIEW MOOD EVENT

        //Set optional attributes as invisible initially, then make them visible when they are not null or empty
        moodDescription.setVisibility(View.GONE);
        socialSituation.setVisibility(View.GONE);
        moodImageCard.setVisibility(View.GONE);
        moodImage.setVisibility(View.GONE);

        //Handle send button click
        sendButton.setOnClickListener(v -> saveComment());

        //Load comments
        loadComments();
        if (commentList.isEmpty()) {
            commentsLabel.setText("Comments (No Comments Yet)");
        } else {
            commentsLabel.setText("Comments");
        }

        //Handle back button click
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
        backButton.setOnClickListener(v -> navController.popBackStack());
    }


    //I MIGHT CONSIDER DOING A FALLBACK PLAN FOR THIS
    /**
     * Loads a mood event from Firestore and updates the UI accordingly.
     * @param moodEventId
     */
    private void loadMoodEvent(String moodEventId) {
        DocumentReference moodRef = db.collection("moods").document(moodEventId);
        moodListener = moodRef.addSnapshotListener((snapshot, error) -> {
            if (snapshot != null && snapshot.exists()) {
                //Obtain the mood from the snapshot
                Mood mood = snapshot.toObject(Mood.class);

                //Check if mood is null
                if (mood != null) {
                    //Obtain the username from the mood
                    String username = mood.getUsername();

                    //If username is null or empty, set it to "N/A"
                    if (username == null || username.isEmpty()) {
                        username = "N/A";
                    }
                    usernameText.setText(username + "'s Mood");
                    moodEventUsername = username;

                    //Attempts to find and set the display name of the user associated with the mood
                    db.collection("users")
                            .whereEqualTo("username", username)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                if (!querySnapshot.isEmpty()) {
                                    String displayName = querySnapshot.getDocuments().get(0).getString("displayName");
                                    if (displayName != null && !displayName.isEmpty()) {
                                        usernameText.setText(displayName + "'s Mood");
                                    }
                                }
                            });

                    //Obtains mood event details
                    Mood.MoodState foundMoodState = mood.getMoodState();
                    moodType.setText(MoodUtils.getEmojiForMood(foundMoodState) + " " + foundMoodState.moodStateToString());

                    //If the timestamp is null, set it to the current time
                    if (mood.getTimestamp() == null) {
                        Log.e("UserMoodDisplayFragment", "ERROR: Mood timestamp is NULL in loadMoodEvent!");
                        mood.setTimestamp(new Date()); // Prevents crash
                    }
                    moodDate.setText(mood.getFormattedTimestamp());

                    //Set mood type card and description card colors based on mood state
                    int moodColor = MoodUtils.getMoodColor(requireContext(), mood.getMoodState());
                    moodTypeCard.setCardBackgroundColor(moodColor);
                    topBar.setBackgroundColor(moodColor);
                    moodDescriptionCard.setCardBackgroundColor(moodColor);

                    //Only set the reason, social situation, location if they are not null or empty, if any are their views become visible
                    if (mood.getDescription() != null && !mood.getDescription().trim().isEmpty()) {
                        moodDescription.setText(mood.getDescription());
                        moodReasonLabel.setVisibility(View.VISIBLE);
                        moodDescription.setVisibility(View.VISIBLE);
                    } else {
                        moodDescription.setText("N/A");
                    }

                    Mood.SocialSituation foundSocialSituation = mood.getSocialSituation();
                    if (mood.getSocialSituation() != null &&
                            !foundSocialSituation.socialSituationToString().trim().isEmpty() &&
                            !foundSocialSituation.equals(Mood.SocialSituation.NOINPUT)) {
                        socialSituation.setText(foundSocialSituation.socialSituationToString());
                        socialSituationLabel.setVisibility(View.VISIBLE);
                        socialSituation.setVisibility(View.VISIBLE);
                    } else {
                        socialSituation.setText("N/A");
                    }

                    //DO LOCATION HERE

                    //Set mood image if it exists
                    if (mood.getImage() != null) {
                        byte[] imageBytes = mood.getImage();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        moodImage.setImageBitmap(bitmap);
                        moodImageCard.setVisibility(View.VISIBLE);
                        moodImage.setVisibility(View.VISIBLE);
                    }

                }
            }
        });
    }


    /**
     * Called to load comments from Firestore. Obtains the mood ID from the navigation arguments,
     * queries the database for comments associated with that mood ID, and updates the UI accordingly.
     */
    private void loadComments() {
        String moodId = getArguments().getString("moodEventId");
        if (moodId == null || moodId.isEmpty()) {
            Log.e("Comments", "Error: moodId is null or empty");
            return;
        }

        //Query the database for comments associated with the mood ID
        db.collection("comments")
                .whereEqualTo("moodEventId", moodId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Log.e("Comments", "Listen failed.", error);
                        return;
                    }

                    //Clear the comment list and add the new comments
                    commentList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        try {
                            Comment comment = doc.toObject(Comment.class);
                            if (comment.getCommentText() != null && !comment.getCommentText().trim().isEmpty()) {
                                commentList.add(comment);
                            }
                        } catch (Exception e) {
                            Log.e("Comments", "Failed to parse comment document: " + doc.getId(), e);
                        }
                    }
                    //Update the UI
                    commentAdapter.notifyDataSetChanged();
                    if (commentList.isEmpty()) {
                        commentsLabel.setText("Comments (No Comments Yet)");
                    } else {
                        commentsLabel.setText("Comments");
                    }
                });
    }


    /**
     * Creates a new comment then saves it to Firestore, verifies user inputs, and updates the UI accordingly.
     */
    private void saveComment() {

        // Obtains and validates user input
        String commentText = commentInput.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(getContext(), "Comment is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (commentText.length() > 200) {
            Toast.makeText(getContext(), "Comment too long (max 200 characters)", Toast.LENGTH_SHORT).show();
            return;
        }

        //Obtains and validates user ID
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        //Obtains mood ID from navigation arguments
        String moodId = getArguments().getString("moodEventId");
        String userId = user.getUid();

        //Gets the current user's username
        String username = MoodUtils.getCurrentUsername();
        if (username == null || username.isEmpty()) {
            username = user.getEmail();
        }

        //Creates a new comment and saves it to Firestore
        Comment newComment = new Comment(moodId, userId, username, commentText);

        db.collection("comments")
                .add(newComment)
                .addOnSuccessListener(docRef -> {
                    String commentID = docRef.getId();
                    docRef.update("commentID", commentID)
                            .addOnSuccessListener(aVoid -> {
                                //Clears the input field and refreshes the comments
                                commentInput.setText("");
                                loadComments();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Error updating comment ID", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to post comment", Toast.LENGTH_SHORT).show()
                );
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
