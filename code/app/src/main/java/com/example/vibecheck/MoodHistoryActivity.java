package com.example.vibecheck;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;

public class MoodHistoryActivity extends AppCompatActivity {

    private ArrayList<MoodHistoryEntry> dataList;
    private ListView moodEntryList;
    private ArrayAdapter<MoodHistoryEntry> moodHistoryEntryAdapter;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mood_history);

        Mood test1 = new Mood(Mood.MoodState.ANGER);
        test1.setDescription("Angry");

        Mood test2 = new Mood(Mood.MoodState.FEAR);
        test2.setDescription("Fearful");

        dataList.add(new MoodHistoryEntry(test1));
        dataList.add(new MoodHistoryEntry(test2));

        moodEntryList = findViewById(R.id.mood_history_list);
        moodHistoryEntryAdapter = new MoodHistoryEntryAdapter(this, dataList);
        moodEntryList.setAdapter(moodHistoryEntryAdapter);
    }
}
