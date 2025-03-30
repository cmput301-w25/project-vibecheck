/**
 * AddMoodEventActivity.java
 *
 * This activity handles adding a new mood event, allowing the user to select a mood,
 * specify an optional trigger, and choose a social situation. The selected mood event
 * is then stored in Firebase Firestore.
 */
package com.example.vibecheck.ui.moodevents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
//import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;



import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.PhotoUtils;
import com.example.vibecheck.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity for adding a new mood event.
 * Allows users to select a mood, enter an optional trigger, and choose a social situation.
 * Saves the data to Firestore upon submission.
 */
public class AddMoodEventActivity extends AppCompatActivity {
    //private EditText inputTrigger;///////////////////////////////////////////////
    private EditText inputDescription;
    private Spinner moodDropdown;
    private Spinner socialDropdown;
    private Button saveMoodButton, addPhotoButton, removePhotoButton;
    private ImageView backButton, imagePreview;
    private TextView moodEmoji;
    private RelativeLayout moodBackground;
    private ToggleButton isPublicButton;
    private Toolbar addMoodToolbar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private String imageData = null;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private String selectedLocation = "";

    /**
     * Initializes the activity, sets up UI components, and populates dropdowns.
     * @param savedInstanceState The saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_mood_event);

        //Top padding
        View root = findViewById(R.id.add_mood_event_root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Initialize UI components
        addMoodToolbar = findViewById(R.id.add_mood_toolbar);
        moodDropdown = findViewById(R.id.dropdown_mood);
        inputDescription = findViewById(R.id.input_description);
        socialDropdown = findViewById(R.id.dropdown_social);
        saveMoodButton = findViewById(R.id.button_save_mood);
        backButton = findViewById(R.id.button_back);
        moodEmoji = findViewById(R.id.mood_emoji);
        moodBackground = findViewById(R.id.mood_background);
        isPublicButton = findViewById(R.id.is_public_button);
        imagePreview = findViewById(R.id.image_preview);
        addPhotoButton = findViewById(R.id.button_add_photo);
        removePhotoButton = findViewById(R.id.button_remove_photo);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        imagePreview.setVisibility(View.GONE);

        // Set image picker launcher
        imagePickerLauncher = PhotoUtils.createImagePickerLauncher(
                this,
                imagePreview,
                removePhotoButton,
                encoded -> imageData = encoded
        );


        // Initialize Places API
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        }

        // Set up the Autocomplete fragment
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
            autocompleteFragment.setHint("Enter location");

            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
                    selectedLocation = place.getName();  // Save location string
                }

                @Override
                public void onError(@NonNull Status status) {
                    Toast.makeText(AddMoodEventActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        // Populate Mood Dropdown
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.mood_options,
                android.R.layout.simple_spinner_item
        );
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodDropdown.setAdapter(moodAdapter);

        // Handle Mood Selection
        moodDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get selected mood, changes string to uppercase
                String selectedMood = parent.getItemAtPosition(position).toString().toUpperCase();
                Mood.MoodState moodState = Mood.MoodState.valueOf(selectedMood);

                // Change background and toolbar colour
                int moodColor = MoodUtils.getMoodColor(AddMoodEventActivity.this, moodState);
                moodBackground.setBackgroundColor(moodColor);
                addMoodToolbar.setBackgroundColor(moodColor);

                // Change emoji
                String emoji = MoodUtils.getEmojiForMood(moodState);
                moodEmoji.setText(emoji);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Populate Social Situation Dropdown
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.social_options,  // Defined in XML
                android.R.layout.simple_spinner_item
        );
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialDropdown.setAdapter(socialAdapter);


        // Handle Add Photo Button Click
        addPhotoButton.setOnClickListener(v -> openImageSelection());

        // Handle Remove Photo Button Click, Hidden Unless Uploaded Photo Is Present
        removePhotoButton.setOnClickListener(v -> {
            imagePreview.setImageResource(R.drawable.add_post_icon);  // Reset to default icon
            imageData = null;                                         // Clear the Base64 image data
            imageUri = null;                                          // Clear the stored URI
            removePhotoButton.setVisibility(View.GONE);               // Hide the remove photo button
            imagePreview.setVisibility(View.GONE);                    // Hide the image preview again
        });

        // Handle Save Button Click
        saveMoodButton.setOnClickListener(v -> SaveMood());

        // Handle Back Button Click
        backButton.setOnClickListener(v -> finish());
    }


    /**
     * Opens the image selection dialog.
     */
    private void openImageSelection() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    /**
     * Builds the mood event, then saves it to Firestore.
     */
    public void SaveMood() {
        //Obtains user inputs
        String selectedMood = moodDropdown.getSelectedItem().toString();
        String descriptionText = inputDescription.getText().toString().trim();
        String selectedSocial = socialDropdown.getSelectedItem().toString();
        boolean isPublic = isPublicButton.isChecked();
        Date moodTimestamp = new Date();

        // Use selectedLocation from autocomplete (if the user hasn't changed it, it remains the loaded value)
        String location = selectedLocation;

        //Converts user inputs to enums
        Mood.SocialSituation socialSituation = Mood.SocialSituation.socialSituationToEnum(selectedSocial);
        Mood.MoodState moodState = Mood.MoodState.moodStateToEnum(selectedMood);

        //Validates user input
        if (selectedMood.isEmpty() || selectedSocial.isEmpty()) {
            Toast.makeText(this, "Please select a mood and social situation", Toast.LENGTH_SHORT).show();
            return;
        }

        //Checks if user is found
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validates description length
        if (descriptionText.length() > 200) {
            Toast.makeText(this, "Description cannot exceed 200 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        //Retrieves user ID
        String uid = currentUser.getUid();

        //Retrieves username from Firestore
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String username = documentSnapshot.getString("username");
                    //If username is null or empty, use email instead, "N/A" if email fails after that
                    if (username == null || username.isEmpty()) {
                        username = currentUser.getEmail() != null ? currentUser.getEmail() : "N/A";
                    }

                    //Creates new mood event
                    Mood newMood = new Mood();
                    newMood.setMoodState(moodState);
                    newMood.setTimestamp(moodTimestamp);
                    if (!descriptionText.isEmpty()) {
                        newMood.setDescription(descriptionText);
                    }
                    newMood.setSocialSituation(socialSituation);
                    newMood.setUsername(username);
                    newMood.setPublic(isPublic);


                    // If there is an image, convert to Base64 and convert to byte array so it can be saved to firestore
                    List<Integer> imageIntList = null;
                    if (imageData != null) {
                        byte[] imageBytes = Base64.decode(imageData, Base64.DEFAULT);

                        // Convert byte[] to List<Integer>
                        imageIntList = new ArrayList<>();
                        for (byte b : imageBytes) {
                            imageIntList.add((int) b & 0xFF); // Convert byte to unsigned int
                        }
                        newMood.setImage(imageIntList);
                    } else {
                        newMood.setImage(null);
                    }

                    //Saves mood event data as a hashmap
                    Map<String, Object> moodData = new HashMap<>();
                    moodData.put("moodState", moodState);
                    moodData.put("timestamp", moodTimestamp);
                    moodData.put("description", descriptionText);
                    moodData.put("socialSituation", socialSituation);
                    moodData.put("username", username);
                    moodData.put("public", isPublic);
                    moodData.put("image", imageIntList);
                    //moodData.put("location", location);

                    //Saves mood event to Firestore, update the moodID to the document ID, and add the mood to the user's mood history
                    db.collection("moods")
                            .add(moodData)
                            .addOnSuccessListener(docRef -> {
                                String moodId = docRef.getId();
                                //docRef.update("moodId", moodId,
                                //        "location", location);
                                docRef.update("moodId", moodId);

                                // Add the mood to the user's mood history
                                newMood.setMoodId(moodId);
                                MoodUtils.addMoodToUserMoodHistory(newMood);
                                Toast.makeText(this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user profile: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void setAuth(FirebaseAuth auth) {
        this.mAuth = auth;
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }
}