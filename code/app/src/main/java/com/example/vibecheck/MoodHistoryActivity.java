package com.example.vibecheck;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MoodHistoryActivity extends AppCompatActivity implements MoodFilterFragment.MoodFilterDialogListener{

    private ArrayList<MoodHistoryEntry> dataList;
    private ListView moodEntryList;
    private ArrayAdapter<MoodHistoryEntry> moodHistoryEntryAdapter;
    private ImageButton sortButton;
    private  ImageButton filterButton;

    // true - most recent first, false - oldest first
    private Boolean toggleSort = true;

    private ArrayList<Mood.MoodState> states = new ArrayList<>();
    private MoodHistory history;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_history);

        sortButton = findViewById(R.id.mood_history_sort_button);
        filterButton = findViewById(R.id.mood_history_filter_button);

        dataList = new ArrayList<MoodHistoryEntry>();

        db = FirebaseFirestore.getInstance();
        String username = "TestUser"; //MainActivity.getUsername();
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
                mood.setDocumentId(documentId);

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

        sortButton.setOnClickListener(v -> {
            toggleSort = !toggleSort;
            if(toggleSort){
                history.sortByDate();
            }else{
                history.sortByDateReverse();
            }
            moodHistoryEntryAdapter.clear();
            moodHistoryEntryAdapter.addAll(history.getFilteredMoodList());
            moodHistoryEntryAdapter.notifyDataSetChanged();
        });

        filterButton.setOnClickListener(v -> {
            MoodFilterFragment fragment = MoodFilterFragment.newInstance(states);
            fragment.show(getSupportFragmentManager(), "");
        });

    }

    @Override
    public void filter(ArrayList<Mood.MoodState> states) {
        this.states = states;
        history.filterByMood(states);
        moodHistoryEntryAdapter.clear();
        moodHistoryEntryAdapter.addAll(history.getFilteredMoodList());
        moodHistoryEntryAdapter.notifyDataSetChanged();
    }
}
