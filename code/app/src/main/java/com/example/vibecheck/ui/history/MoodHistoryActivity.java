/**
 * Activity for displaying and managing a user's mood history.
 * <p>
 * Loads mood events from Firestore for a given user, displays them in a ListView,
 * and supports sorting (by date) and filtering by mood states. Implements
 * MoodFilterDialogListener to update the list based on selected filters.
 * </p>
 */

package com.example.vibecheck.ui.history;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.ui.moodevents.Mood;
import com.example.vibecheck.R;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.example.vibecheck.ui.moodevents.MyMoodDisplayFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Class for MoodHistoryActivity
 */
public class MoodHistoryActivity extends AppCompatActivity implements MoodFilterFragment.MoodFilterDialogListener{

    private ArrayList<MoodHistoryEntry> dataList;
    private RecyclerView moodEntryList;
    private MoodHistoryEntryAdapter moodHistoryEntryAdapter;
    private ImageButton sortButton;
    private  ImageButton filterButton;
    private ImageButton backButton;

    // true - most recent first, false - oldest first
    private Boolean toggleSort = true;

    private ArrayList<Mood.MoodState> states = new ArrayList<>();
    private MoodHistory history;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private MoodHistory userHistory;
    private Singleton singleton;

    /**
     * Method that is run when Dialog is about to be created
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.mood_history);


        //Top padding
        View root = findViewById(R.id.mood_history_root_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        dataList = new ArrayList<MoodHistoryEntry>();
        db = FirebaseFirestore.getInstance();

        sortButton = findViewById(R.id.mood_history_sort_button);
        filterButton = findViewById(R.id.mood_history_filter_button);
        backButton = findViewById(R.id.navbar_back_button);
        moodEntryList = findViewById(R.id.mood_history_recyclerview);

        // Get current user's mood history
        MoodHistory userMoodHistory = MoodUtils.getUserMoodHistory();

        if(userMoodHistory != null){
            history = userMoodHistory;
            history.sortByDateNewestFirst(); //Displays most recent moods by default
        } else {
            Toast.makeText(this, "No mood history found for the current user.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up mood history adapter
        moodHistoryEntryAdapter = new MoodHistoryEntryAdapter(this, history.getFilteredMoodList(), new MoodHistoryEntryAdapter.OnMoodClickListener() {
            @Override
            public void onMoodClick(Mood mood) {
                if (mood.getMoodId() != null) {
                    openMyMoodDisplayFragment(mood.getMoodId());
                }
            }
        });
        moodEntryList.setLayoutManager(new LinearLayoutManager(this));
        moodEntryList.setAdapter(moodHistoryEntryAdapter);

        // Singleton for passing "states" between activities
        singleton = Singleton.getINSTANCE();
        this.states = singleton.getStates();
        filter(states);

        sortButton.setOnClickListener(v -> {
            toggleSort = !toggleSort;
            if(toggleSort){
                history.sortByDateNewestFirst();
            }else{
                history.sortByDateOldestFirst();
            }
            moodHistoryEntryAdapter.updateData(history.getFilteredMoodList());
        });

        filterButton.setOnClickListener(v -> {
            MoodFilterFragment fragment = MoodFilterFragment.newInstance(states);
            fragment.show(getSupportFragmentManager(), "");
        });

        // Close mood history activity and return to home screen
        backButton.setOnClickListener(v -> {
            finish();
        });

    }

    /**
     * Filters MoodHistory based on states
     * @param states
     *      Mood states to filter the MoodHistory on
     */
    @Override
    public void filter(ArrayList<Mood.MoodState> states) {
        this.states = states;
        singleton.setStates(states);
        history.filterByMood(states);
        moodHistoryEntryAdapter.updateData(history.getFilteredMoodList());
    }


    //I WILL LIKELY REMOVE THIS METHOD IN FAVOUR OF CHANGING THIS ACTIVITY TO A FRAGMENT
    private void openMyMoodDisplayFragment(String moodEventId) {
        Fragment fragment = new MyMoodDisplayFragment();
        Bundle args = new Bundle();
        args.putString("moodEventId", moodEventId);
        fragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.history_fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}