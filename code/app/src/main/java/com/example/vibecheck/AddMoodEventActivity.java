package com.example.vibecheck;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Handles adding a new mood event, including selecting mood, optional triggers, and social situations.
 */
public class AddMoodEventActivity extends AppCompatActivity {

    private EditText inputTrigger;
    private Spinner moodDropdown;
    private Spinner socialDropdown;
    private Button saveMoodButton;
    private Button backButton;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mood_event);

        // Initialize UI components
        moodDropdown = findViewById(R.id.dropdown_mood);
        inputTrigger = findViewById(R.id.input_trigger);
        socialDropdown = findViewById(R.id.dropdown_social);
        saveMoodButton = findViewById(R.id.button_save_mood);
        backButton = findViewById(R.id.button_back);

        db = FirebaseFirestore.getInstance();

        // Populate Mood Dropdown
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.mood_options,  // Defined in XML
                android.R.layout.simple_spinner_item
        );
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodDropdown.setAdapter(moodAdapter);

        // Populate Social Situation Dropdown
        ArrayAdapter<CharSequence> socialAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.social_options,  // Defined in XML
                android.R.layout.simple_spinner_item
        );
        socialAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        socialDropdown.setAdapter(socialAdapter);

        // Handle Save Button Click
        saveMoodButton.setOnClickListener(v -> saveMood());

        // Handle Back Button Click
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Saves the mood event to Firestore.
     */
    private void saveMood() {
        String selectedMood = moodDropdown.getSelectedItem().toString();
        String triggerText = inputTrigger.getText().toString().trim();
        String selectedSocial = socialDropdown.getSelectedItem().toString();

        // Ensure a valid mood is selected
        if (selectedMood.isEmpty()) {
            Toast.makeText(this, "Please select a mood.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create mood object
        Mood newMood = new Mood();
        newMood.setMoodState(Mood.MoodState.valueOf(selectedMood.toUpperCase()));
        if (!triggerText.isEmpty()) newMood.setTrigger(triggerText);
        newMood.setSocialSituation(Mood.SocialSituation.valueOf(selectedSocial.toUpperCase().replace(" ", "_")));

        // Save to Firestore
        db.collection("moods")
                .add(newMood)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Mood saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
