/**
 * This activity class handles editing a mood event, allowing the user to update all inputs to the mood event.
 * The mood event is updated in the Firestore database upon saving, or the user can press the back button to cancel.
 * The user can also delete the mood event, deleting all comments attached to the event along with it.
 *
 * Outstanding issues: Does not yet update a mood event in the user's mood history, only updates the mood in the firebase right now
 */

package com.example.vibecheck.ui.moodevents;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
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

import androidx.annotation.NonNull;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.PhotoUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.home.HomeActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.Arrays;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Activity for editing or deleting a mood event.
 * <p>
 * This activity loads the mood event data from Firestore, allows the user to update
 * fields (reason, mood state, social situation, location, photo, privacy), and provides options
 * to save the changes, cancel the changes, or delete the event.
 * </p>
 */
public class EditMoodEventActivity extends AppCompatActivity {

    // UI elements
    private ImageView cancelButton, saveButton, deleteButton, addImagePreview;
    private TextView moodDate, moodEmoji;
    private Spinner moodTypeSpinner, socialSituationSpinner;
    private EditText moodReasonInput;
    private RelativeLayout moodBackground, editMoodTopbar;
    private ToggleButton isPublicButton;
    private Button addImageButton, removeImageButton;

    // Image data, URI, and image picker launcher
    private String imageData = null;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // Firebase Firestore
    private FirebaseFirestore db;
    private String moodEventId;
    private Mood moodToEdit;

    // For current location access
    private FusedLocationProviderClient fusedLocationClient;

    // Arrays for spinner data using the enums from Mood class
    private Mood.MoodState[] moodStates = Mood.MoodState.values();
    private Mood.SocialSituation[] socialSituations = Mood.SocialSituation.values();

    // Variable to store the selected location from autocomplete
    private String selectedLocation = "";


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
        addImageButton = findViewById(R.id.button_add_photo);
        removeImageButton = findViewById(R.id.button_remove_photo);

        //Set image preview to invisible initially, only visible after image is confirmed present
        addImagePreview.setVisibility(View.GONE);


        // Initialize Places API (ensure you have added your API key in strings.xml)
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Set up the AutocompleteSupportFragment for location suggestions.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragment.setHint("Enter location");
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    // You can save the selected place name in a field or update the UI.
                    // For example, you might store it in a member variable:
                    selectedLocation = place.getName();
                }

                @Override
                public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                    Toast.makeText(EditMoodEventActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }

        // Initialize FusedLocationProviderClient for current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        // Set image picker launcher
        imagePickerLauncher = PhotoUtils.createImagePickerLauncher(
                this,
                addImagePreview,
                removeImageButton,
                encoded -> imageData = encoded
        );

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

                // Change Background And Topbar Colour
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
        addImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Set Remove Image button click: remove image.
        removeImageButton.setOnClickListener(v -> {
            addImagePreview.setImageResource(R.drawable.add_post_icon);  // Reset to default icon
            imageData = null;                                            // Clear the Base64 image data
            imageUri = null;                                             // Clear the stored URI
            removeImageButton.setVisibility(View.GONE);                  // Hide the remove photo button
            addImagePreview.setVisibility(View.GONE);                    // Hide the image preview again
        });

        // Set Save button click: update mood event in Firestore.
        saveButton.setOnClickListener(view -> saveMoodEvent());

        // Set Delete button click: confirm and then delete the mood event.
        deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(EditMoodEventActivity.this)
                    .setTitle("Delete Mood Event")
                    .setMessage("Are you sure you want to delete this mood event?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteMoodEvent())
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
                moodToEdit = documentSnapshot.toObject(Mood.class);
                if (moodToEdit == null) {
                    Toast.makeText(EditMoodEventActivity.this, "Error loading mood event", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                // Extract fields from the document.
                String moodStateStr = documentSnapshot.getString("moodState");
                String description = documentSnapshot.getString("description");
                Date timestamp = documentSnapshot.getDate("timestamp");
                String socialSituationStr = documentSnapshot.getString("socialSituation");
                // Get location field.
                String location = documentSnapshot.getString("location");
                Log.d("location", "location: " + location);

                // Set public/private mood event toggle.
                boolean isPublic = documentSnapshot.getBoolean("public");
                isPublicButton.setChecked(Boolean.TRUE.equals(isPublic));

                // Set mood reason if it exists.
                if (description != null) {
                    moodReasonInput.setText(description);
                }

                // Set location in the Autocomplete fragment if available.
                if (location != null) {
                    selectedLocation = location;
                    // Optionally, update the Autocomplete fragment's text.
                    // Note: The AutocompleteSupportFragment does not provide a direct API to set text.
                    // You might need a custom solution if you require that.
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

                    //set the image if there is one
                    if (moodToEdit.getImageByteArr() != null) {
                        byte[] imageBytes = moodToEdit.getImageByteArr();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        imageData = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
                        addImagePreview.setImageBitmap(bitmap);
                        addImagePreview.setVisibility(View.VISIBLE);
                    } else {
                        removeImageButton.setVisibility(View.GONE);
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
        // Use selectedLocation from autocomplete (if the user hasn't changed it, it remains the loaded value)
        String location = selectedLocation;
        boolean isPublic = isPublicButton.isChecked();

        Mood.SocialSituation socialSituation = Mood.SocialSituation.socialSituationToEnum(socialSituationStr);
        Mood.MoodState moodState = Mood.MoodState.moodStateToEnum(moodStateStr);

        // Validates description length
        if (description.length() > 200) {
            Toast.makeText(this, "Description cannot exceed 200 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mood event is updated differently based on if there is an image or not
        if (imageData != null) {
            byte[] imageBytes = Base64.decode(imageData, Base64.DEFAULT);
            // Convert byte[] to List<Integer>
            List<Integer> imageIntList = new ArrayList<>();
            for (byte b : imageBytes) {
                imageIntList.add((int) b & 0xFF); // Convert byte to unsigned int
            }

            db.collection("moods").document(moodEventId)
                    .update(
                            "description", description,
                            "moodState", moodState,
                            "socialSituation", socialSituation,
                            "image", imageIntList,
                            "location", location,
                            "public", isPublic
                    )
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(EditMoodEventActivity.this, "Mood event updated", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(EditMoodEventActivity.this, "Error updating mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            db.collection("moods").document(moodEventId)
                    .update(
                            "description", description,
                            "moodState", moodState,
                            "socialSituation", socialSituation,
                            "image", null,
                            "location", location,
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
    }

    /**
     * Deletes all the comments associated with a mood event from the Firestore database
     * Upon success of deleting the comments, the mood event is then deleted
     * from Firestore and the user's mood history as well.
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

                    // Once all comments are deleted, we can safely delete the mood event, remove it from the user's mood history, and finish the activity
                    db.collection("moods").document(moodEventId)
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(EditMoodEventActivity.this, "Mood event and comments deleted", Toast.LENGTH_SHORT).show();
                                MoodUtils.removeMoodFromUserMoodHistory(moodToEdit);
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
    }
}