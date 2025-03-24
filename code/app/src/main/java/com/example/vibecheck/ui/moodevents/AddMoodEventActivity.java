/**
 * AddMoodEventActivity.java
 *
 * This activity handles adding a new mood event, allowing the user to select a mood,
 * specify an optional trigger, and choose a social situation. The selected mood event
 * is then stored in Firebase Firestore.
 */
package com.example.vibecheck.ui.moodevents;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

/**
 * Activity for adding a new mood event.
 * Allows users to select a mood, enter an optional trigger, and choose a social situation.
 * Saves the data to Firestore upon submission.
 */
public class AddMoodEventActivity extends AppCompatActivity {
    private EditText inputTrigger;
    private EditText inputDescription;
    private Spinner moodDropdown;
    private Spinner socialDropdown;
    private Button saveMoodButton;
    private Button backButton;
    private TextView moodEmoji;
    private RelativeLayout moodBackground;
    private ToggleButton isPublicButton;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

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
        moodDropdown = findViewById(R.id.dropdown_mood);
        inputTrigger = findViewById(R.id.input_trigger);
        inputDescription = findViewById(R.id.input_description);
        socialDropdown = findViewById(R.id.dropdown_social);
        saveMoodButton = findViewById(R.id.button_save_mood);
        backButton = findViewById(R.id.button_back);
        moodEmoji = findViewById(R.id.mood_emoji);
        moodBackground = findViewById(R.id.mood_background);
        isPublicButton = findViewById(R.id.is_public_button);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

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
        saveMoodButton.setOnClickListener(v -> SaveMood());

        // Handle Back Button Click
        backButton.setOnClickListener(v -> finish());
    }

    /**
     * Builds the mood event, then saves it to Firestore.
     */
    public void SaveMood() {
        //Obtains user inputs
        String selectedMood = moodDropdown.getSelectedItem().toString();
        String triggerText = inputTrigger.getText().toString().trim();
        String descriptionText = inputDescription.getText().toString().trim();
        String selectedSocial = socialDropdown.getSelectedItem().toString();
        boolean isPublic = isPublicButton.isChecked();

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
                    newMood.setTimestamp(new Date());
                    if (!triggerText.isEmpty()) {
                        newMood.setTrigger(triggerText);
                    }
                    if (!descriptionText.isEmpty()) {
                        newMood.setDescription(descriptionText);
                    }
                    newMood.setSocialSituation(socialSituation);
                    newMood.setUsername(username);
                    newMood.setPublic(isPublic);

                    //Saves mood event to Firestore
                    db.collection("moods")
                            .add(newMood)
                            .addOnSuccessListener(docRef -> {
                                String moodId = docRef.getId();
                                docRef.update("moodId", moodId);
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


