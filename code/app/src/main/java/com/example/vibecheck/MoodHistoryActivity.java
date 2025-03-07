package com.example.vibecheck;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    private Mood.MoodState[] states;

    private MoodHistory history;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_history);

        sortButton = findViewById(R.id.mood_history_sort_button);
        filterButton = findViewById(R.id.mood_history_filter_button);

        dataList = new ArrayList<MoodHistoryEntry>();

        history = new MoodHistory("placeholder",dataList);



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
            new MoodFilterFragment().show(getSupportFragmentManager(), "");
        });

    }

    @Override
    public void filter(ArrayList<Mood.MoodState> states) {
        history.filterByMood(states);
        moodHistoryEntryAdapter.clear();
        moodHistoryEntryAdapter.addAll(history.getFilteredMoodList());
        moodHistoryEntryAdapter.notifyDataSetChanged();
    }
}
