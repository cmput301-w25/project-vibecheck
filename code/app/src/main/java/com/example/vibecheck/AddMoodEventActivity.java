/**
 * AddMoodEventActivity.java
 *
 * This activity handles adding a new mood event, allowing the user to select a mood,
 * specify an optional trigger, and choose a social situation. The selected mood event
 * is then stored in Firebase Firestore.
 *
 * Outstanding Issues:
 * - Need to add emojis for the dropdown menu
 */
package com.example.vibecheck;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Activity for adding a new mood event.
 * Allows users to select a mood, enter an optional trigger, and choose a social situation.
 * Saves the data to Firestore upon submission.
 */
public class AddMoodEventActivity extends AppCompatActivity {
    private EditText inputTrigger;
    private Spinner moodDropdown;
    private Spinner socialDropdown;
    private Button saveMoodButton;
    private Button backButton;
    private TextView moodEmoji;
    private RelativeLayout moodBackground;

    private FirebaseFirestore db;

    /**
     * Initializes the activity, sets up UI components, and populates dropdowns.
     * @param savedInstanceState The saved instance state.
     */
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
        moodEmoji = findViewById(R.id.mood_emoji);
        moodBackground = findViewById(R.id.mood_background);
      
        db = FirebaseFirestore.getInstance();

        // Populate Mood Dropdown
        ArrayAdapter<CharSequence> moodAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.mood_options,  // Defined in XML
                android.R.layout.simple_spinner_item
        );
        moodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        moodDropdown.setAdapter(moodAdapter);

        // Handle Mood Selection
        moodDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMood = parent.getItemAtPosition(position).toString().toUpperCase();
                Mood.MoodState moodState = Mood.MoodState.valueOf(selectedMood);

                // Change Background Color
                int moodColor = MoodUtils.getMoodColor(AddMoodEventActivity.this, moodState);
                moodBackground.setBackgroundColor(moodColor);

                // Change Emoji
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

        // Handle Save Button Click
        saveMoodButton.setOnClickListener(v -> saveMood());

        // Handle Back Button Click
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Saves the mood event to Firestore after validating user input.
     * Displays a success or failure message.
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
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving mood: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
