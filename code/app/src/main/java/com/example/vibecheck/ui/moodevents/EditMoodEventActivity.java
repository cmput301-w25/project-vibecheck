package com.example.vibecheck.ui.moodevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.home.HomeActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

/**
 * Activity for editing or deleting a mood event.
 * <p>
 * This activity loads the mood event data from Firestore, allows the user to update
 * fields (trigger, description, mood state, social situation), and provides options
 * to save the changes or delete the event.
 * </p>
 */

public class EditMoodEventActivity extends AppCompatActivity {

    // UI elements – note cancelButton is now an ImageView to match XML
    private ImageView cancelButton, saveButton, deleteButton;
    private TextView moodDate, moodEmoji;
    private Spinner moodTypeSpinner;
    private EditText moodReasonInput;
    private Spinner socialSituationSpinner;
    private RelativeLayout moodBackground, editMoodTopbar;
    private ToggleButton isPublicButton;
    private ImageView addImagePreview;
    private Button addImage, removeImage;
    private String imageData = null;
    private Uri imageUri;

    // Firebase Firestore
    private FirebaseFirestore db;
    private String moodEventId;

    // Arrays for spinner data using the enums from Mood class
    private Mood.MoodState[] moodStates = Mood.MoodState.values();
    private Mood.SocialSituation[] socialSituations = Mood.SocialSituation.values();
    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied; otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_mood_event);

        //Top padding
        View root = findViewById(R.id.edit_mood_event_root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Get moodEventId from intent extras
        moodEventId = getIntent().getStringExtra("moodEventId");
        if (moodEventId == null) {
            Toast.makeText(this, "Invalid mood event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        // Bind UI elements (IDs must match your XML)
        editMoodTopbar = findViewById(R.id.edit_mood_topbar);
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        moodDate = findViewById(R.id.mood_date);
        moodTypeSpinner = findViewById(R.id.mood_type_spinner);
        moodReasonInput = findViewById(R.id.mood_reason_input);
        socialSituationSpinner = findViewById(R.id.social_situation_spinner);
        addImagePreview = findViewById(R.id.add_image_preview);
        moodBackground = findViewById(R.id.mood_background);
        moodEmoji = findViewById(R.id.mood_emoji);
        isPublicButton = findViewById(R.id.is_public_button);
        addImage = findViewById(R.id.button_add_photo);
        removeImage = findViewById(R.id.button_remove_photo);

        //Set image preview to invisible initially
        addImagePreview.setVisibility(View.GONE);

        // Populate spinners using ArrayAdapter, options defined in strings.xml
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.mood_options,
                android.R.layout.simple_spinner_item
        );
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodTypeSpinner.setAdapter(moodAdapter);

        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.social_options,
                android.R.layout.simple_spinner_item
        );
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Handle Mood Selection
        moodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMood = parent.getItemAtPosition(position).toString().toUpperCase();
                Mood.MoodState moodState = Mood.MoodState.valueOf(selectedMood);

                // Change Background Color
                int moodColor = MoodUtils.getMoodColor(EditMoodEventActivity.this, moodState);
                moodBackground.setBackgroundColor(moodColor);
                editMoodTopbar.setBackgroundColor(moodColor);

                // Change Emoji
                String emoji = MoodUtils.getEmojiForMood(moodState);
                moodEmoji.setText(emoji);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



        // Load mood event data from Firestore
        loadMoodEventData();

        // Set Cancel button click: finish activity without saving changes.
        cancelButton.setOnClickListener(view -> finish());

        // Set Add Image button click: open image picker.
        //IMPLEMENT IMAGE PICKER HERE

        // Set Remove Image button click: remove image.
        removeImage.setOnClickListener(v -> {
            addImagePreview.setImageResource(R.drawable.add_post_icon);  // Reset to default icon
            imageData = null;                                            // Clear the Base64 image data
            imageUri = null;                                             // Clear the stored URI
            removeImage.setVisibility(View.GONE);                        // Hide the remove photo button
            addImagePreview.setVisibility(View.GONE);                    // Hide the image preview again
        });

        // Set Save button click: update mood event in Firestore.
        saveButton.setOnClickListener(view -> saveMoodEvent());

        // Set Delete button click: confirm and then delete the mood event.
        deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(EditMoodEventActivity.this)
                    .setTitle("Delete Mood Event")
                    .setMessage("Are you sure you want to delete this mood event?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteMoodEvent();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    /**
     * Loads the mood event data from Firestore and updates the UI.
     */
    private void loadMoodEventData() {
        DocumentReference docRef = db.collection("moods").document(moodEventId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {

                //Get the mood object from the document, if it exists, for updating the image later in this method
                Mood foundMood = documentSnapshot.toObject(Mood.class);
                if (foundMood == null) {
                    Toast.makeText(EditMoodEventActivity.this, "Error loading mood event", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Extract fields from the document.
                String moodStateStr = documentSnapshot.getString("moodState");
                String description = documentSnapshot.getString("description");
                Date timestamp = documentSnapshot.getDate("timestamp");
                String socialSituationStr = documentSnapshot.getString("socialSituation");

                // Set public/private mood event toggle.
                boolean isPublic = documentSnapshot.getBoolean("public");
                isPublicButton.setChecked(Boolean.TRUE.equals(isPublic));

                // Set mood reason if it exists.
                if (description != null) {
                    moodReasonInput.setText(description);
                }

                // Format and display the timestamp.
                if (timestamp != null) {
                    String dateStr = DateFormat.format("MMM dd, yyyy hh:mm a", timestamp).toString();
                    moodDate.setText(dateStr);
                } else {
                    moodDate.setText("N/A");
                }

                // Set spinner selection for mood type.
                if (moodStateStr != null) {
                    try {
                        Mood.MoodState moodState = Mood.MoodState.valueOf(moodStateStr);
                        for (int i = 0; i < moodStates.length; i++) {
                            if (moodStates[i] == moodState) {
                                moodTypeSpinner.setSelection(i);
                                break;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        // Do nothing if the value isn't found.
                    }
                }

                // Set spinner selection for social situation.
                if (socialSituationStr != null) {
                    try {
                        Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf(socialSituationStr);
                        for (int i = 0; i < socialSituations.length; i++) {
                            if (socialSituations[i] == socialSituation) {
                                socialSituationSpinner.setSelection(i);
                                break;
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        // Do nothing if the value isn't found.
                    }


                    //LOCATION GOES HERE FROM SEERAT BRANCH I THINK


                    //set the image if there is one
                    if (foundMood.getImage() != null) {
                        byte[] imageBytes = foundMood.getImage();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        addImagePreview.setImageBitmap(bitmap);
                        addImagePreview.setVisibility(View.VISIBLE);
                    } else {
                        removeImage.setVisibility(View.GONE);
                    }

                }
            } else {
                Toast.makeText(EditMoodEventActivity.this, "Mood event not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(EditMoodEventActivity.this, "Error loading mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    /**
     * Saves the updated mood event data back to Firestore.
     */
    private void saveMoodEvent() {
        String description = moodReasonInput.getText().toString().trim();
        String moodStateStr = moodTypeSpinner.getSelectedItem().toString();
        String socialSituationStr = socialSituationSpinner.getSelectedItem().toString();
        boolean isPublic = isPublicButton.isChecked();

        Mood.SocialSituation socialSituation = Mood.SocialSituation.socialSituationToEnum(socialSituationStr);
        Mood.MoodState moodState = Mood.MoodState.moodStateToEnum(moodStateStr);

        // Validates description length
        if (description.length() > 200) {
            Toast.makeText(this, "Description cannot exceed 200 characters", Toast.LENGTH_SHORT).show();
            return;
        }


        db.collection("moods").document(moodEventId)
                .update(
                        "description", description,
                        "moodState", moodState,
                        "socialSituation", socialSituation,
                        "public", isPublic
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditMoodEventActivity.this, "Mood event updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditMoodEventActivity.this, "Error updating mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Deletes all the comments associated with a mood event from the Firestore database
     * Upon success of deleting the comments, the mood event is then deleted
     * from Firestore as well.
     */
    private void deleteMoodEvent() {
        // First we have to delete all comments attached to this mood event
        db.collection("comments")
                .whereEqualTo("moodEventId", moodEventId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Loop through and delete each comment
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().delete();
                    }


                    // Once all comments are deleted, we can safely delete the mood event
                    db.collection("moods").document(moodEventId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                MoodUtils.removeMoodFromUserMoodHistory(moodEventId);
                                Toast.makeText(EditMoodEventActivity.this, "Mood event and comments deleted", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditMoodEventActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(EditMoodEventActivity.this, "Error deleting mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditMoodEventActivity.this, "Failed to delete comments: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


       /*
       db.collection("moods").document(moodEventId)
               .delete()
               .addOnSuccessListener(aVoid -> {
                   Toast.makeText(EditMoodEventActivity.this, "Mood event deleted", Toast.LENGTH_SHORT).show();


                   Intent intent = new Intent(EditMoodEventActivity.this, HomeActivity.class);
                   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   finish();
               })
               .addOnFailureListener(e -> {
                   Toast.makeText(EditMoodEventActivity.this, "Error deleting mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
               });


        */
    }
}
