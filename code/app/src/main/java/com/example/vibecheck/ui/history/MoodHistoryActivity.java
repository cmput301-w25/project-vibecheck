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
import com.example.vibecheck.ui.moodevents.MyMoodDisplayFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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


        /*
        //String username = "TestUser"; //MainActivity.getUsername();
        Task<QuerySnapshot> collection = db.collection("users/"+username+"/MoodHistory").get();
        collection.addOnSuccessListener(querySnapshot -> {
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                Date timestamp = document.getTimestamp("timestamp").toDate();
                Mood.MoodState moodState = Mood.MoodState.valueOf((String) document.get("moodState"));
                String trigger =(String) document.get("trigger");
                Mood.SocialSituation socialSituation = Mood.SocialSituation.valueOf((String) document.get("socialSituation"));
                String description = (String) document.get("description");
                Double latitude = document.getDouble("latitude");
                Double longitude = document.getDouble("longitude");
                String documentId = document.getId();

                Mood mood = new Mood(timestamp,moodState);
                mood.setTrigger(trigger);
                mood.setSocialSituation(socialSituation);
                mood.setDescription(description);
                mood.setLocation(latitude, longitude);
                mood.setMoodId(documentId);

                // Ensure Mood Entries Load with Colors and Emojis
                moodHistoryEntryAdapter.add(new MoodHistoryEntry(mood));
                dataList.add(new MoodHistoryEntry(mood));

                // Need to add mood to adapter else mood history not displayed
                //until after sorting or filtering.
                // Need to add mood to data list as well else, the list will be cleared
                // after soring or filtering
                moodHistoryEntryAdapter.add(new MoodHistoryEntry(mood));
                dataList.add(new MoodHistoryEntry(mood));
            }
            moodHistoryEntryAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            System.err.println("Error retrieving mood history" + e.getMessage());
        });

        history = new MoodHistory(username,dataList);
        history.sortByDate(); //Displays most recent moods by default
        moodEntryList = findViewById(R.id.mood_history_list);
        moodHistoryEntryAdapter = new MoodHistoryEntryAdapter(this, history.getFilteredMoodList());
        moodEntryList.setAdapter(moodHistoryEntryAdapter);
        */

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

    @Override
    public void filter(ArrayList<Mood.MoodState> states) {
        this.states = states;
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
