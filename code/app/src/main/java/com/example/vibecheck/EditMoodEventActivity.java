package com.example.vibecheck;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditMoodEventActivity extends AppCompatActivity {

    // UI elements
    private ImageView cancelButton, saveButton, deleteButton, addImageButton, currentLocationButton;
    private TextView moodDate;
    private Spinner moodTypeSpinner, socialSituationSpinner;
    private EditText moodTriggerInput, moodDescriptionInput;
    // Remove the old location EditText if it exists; now we use the Autocomplete fragment.

    // Firebase Firestore
    private FirebaseFirestore db;
    private String moodEventId;

    // For current location access
    private FusedLocationProviderClient fusedLocationClient;

    // Arrays for spinner data using the enums from Mood class
    private Mood.MoodState[] moodStates = Mood.MoodState.values();
    private Mood.SocialSituation[] socialSituations = Mood.SocialSituation.values();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_mood_event);

        // Get moodEventId from intent extras
        moodEventId = getIntent().getStringExtra("moodEventId");
        if (moodEventId == null) {
            Toast.makeText(this, "Invalid mood event", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Bind UI elements (IDs must match your XML)
        cancelButton = findViewById(R.id.cancel_button);
        saveButton = findViewById(R.id.save_button);
        deleteButton = findViewById(R.id.delete_button);
        moodDate = findViewById(R.id.mood_date);
        moodTypeSpinner = findViewById(R.id.mood_type_spinner);
        moodTriggerInput = findViewById(R.id.mood_trigger_input);
        moodDescriptionInput = findViewById(R.id.mood_description_input);
        socialSituationSpinner = findViewById(R.id.social_situation_spinner);
        addImageButton = findViewById(R.id.add_image_button);
//        currentLocationButton = findViewById(R.id.current_location_button);

        db = FirebaseFirestore.getInstance();

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

        // Populate spinners using ArrayAdapter (existing code)
        ArrayAdapter<Mood.MoodState> moodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moodStates);
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodTypeSpinner.setAdapter(moodAdapter);

        ArrayAdapter<Mood.SocialSituation> socialAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, socialSituations);
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialSituationSpinner.setAdapter(socialAdapter);

        // Load mood event data from Firestore
        loadMoodEventData();

        // Set Cancel button click: finish activity without saving changes.
        cancelButton.setOnClickListener(view -> finish());

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

        // Set up current location button click
//        currentLocationButton.setOnClickListener(view -> fetchCurrentLocation());
    }

    // Variable to store the selected location from autocomplete
    private String selectedLocation = "";

    /**
     * Loads the mood event data from Firestore and updates the UI.
     */
    private void loadMoodEventData() {
        DocumentReference docRef = db.collection("moods").document(moodEventId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Extract fields from the document.
                String moodStateStr = documentSnapshot.getString("moodState");
                String trigger = documentSnapshot.getString("trigger");
                String description = documentSnapshot.getString("description");
                Date timestamp = documentSnapshot.getDate("timestamp");
                String socialSituationStr = documentSnapshot.getString("socialSituation");
                // Get location field.
                String location = documentSnapshot.getString("location");
                Log.d("location", "location: " + location);
                // Set trigger and description.
                if (trigger != null) {
                    moodTriggerInput.setText(trigger);
                }
                if (description != null) {
                    moodDescriptionInput.setText(description);
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
        String trigger = moodTriggerInput.getText().toString().trim();
        String description = moodDescriptionInput.getText().toString().trim();
        String moodStateStr = moodTypeSpinner.getSelectedItem().toString();
        String socialSituationStr = socialSituationSpinner.getSelectedItem().toString();
        // Use selectedLocation from autocomplete (if the user hasn't changed it, it remains the loaded value)
        String location = selectedLocation;

        db.collection("moods").document(moodEventId)
                .update(
                        "trigger", trigger,
                        "description", description,
                        "moodState", moodStateStr,
                        "socialSituation", socialSituationStr,
                        "location", location
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
     * Deletes the mood event from Firestore.
     */
    private void deleteMoodEvent() {
        db.collection("moods").document(moodEventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditMoodEventActivity.this, "Mood event deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditMoodEventActivity.this, "Error deleting mood event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Uses FusedLocationProviderClient to fetch the current location and update the location variable.
     */
    private void fetchCurrentLocation() {
        // Check location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show();
            // You might want to request permission here.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        // Use Geocoder to get a human-readable address from the coordinates.
                        Geocoder geocoder = new Geocoder(EditMoodEventActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                String currentAddress = addresses.get(0).getAddressLine(0);
                                selectedLocation = currentAddress;
                                Toast.makeText(EditMoodEventActivity.this, "Current location set", Toast.LENGTH_SHORT).show();
                                // Optionally, update the Autocomplete fragment hint (not straightforward)
                            } else {
                                Toast.makeText(EditMoodEventActivity.this, "Unable to retrieve address", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(EditMoodEventActivity.this, "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EditMoodEventActivity.this, "Location not available", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
