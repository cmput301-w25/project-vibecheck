package com.example.vibecheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.vibecheck.ui.history.MoodFilterFragment;
import com.example.vibecheck.ui.history.MoodHistory;
import com.example.vibecheck.ui.history.MoodHistoryEntry;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class MoodHistoryTest {
    private MoodHistory mockHistory(){
        ArrayList<MoodHistoryEntry> moodList = new ArrayList<>();
        moodList.add(new MoodHistoryEntry(new Mood(new Date(1000L), Mood.MoodState.ANGER)));
        moodList.add(new MoodHistoryEntry(new Mood(new Date(2000L), Mood.MoodState.SADNESS)));
        moodList.add(new MoodHistoryEntry(new Mood(new Date(3000L), Mood.MoodState.ANGER)));
        moodList.add(new MoodHistoryEntry(new Mood(new Date(2500L), Mood.MoodState.CONFUSION)));

        return new MoodHistory("mockUser",moodList);
    }

    @Test
    public void testAddMoodEvent(){
        MoodHistory history = mockHistory();
        Mood event = new Mood(new Date(5000L), Mood.MoodState.CONFUSION);
        assertEquals(4,history.getMoodList().size());
        history.addMoodEvent(event);
        assertEquals(5,history.getMoodList().size());
    }

    @Test
    public void testRemoveMoodEvent(){
        MoodHistory history = mockHistory();
        assertEquals(4,history.getMoodList().size());
        Mood event = new Mood(new Date(5000L), Mood.MoodState.CONFUSION);
        history.addMoodEvent(event);
        history.removeMoodEvent(event);
        assertEquals(4,history.getMoodList().size());
    }

    @Test
    public void testFilterByMood(){
        MoodHistory history = mockHistory();
        ArrayList<Mood.MoodState> states = new ArrayList<>();

        // Filtering for angry moods
        states.add(Mood.MoodState.ANGER);
        history.filterByMood(states);
        int numVisibleStates = 0;
        for(MoodHistoryEntry entry:history.getMoodList()){
            if(entry.getVisibility()){
                ++numVisibleStates;
            }
        }
        assertEquals(2,numVisibleStates);

        // Filtering for angry and confused moods
        states.add(Mood.MoodState.CONFUSION);
        history.filterByMood(states);
        numVisibleStates = 0;
        for(MoodHistoryEntry entry:history.getMoodList()){
            if(entry.getVisibility()){
                ++numVisibleStates;
            }
        }
        assertEquals(3,numVisibleStates);

        //Clearing filters
        states.clear();
        history.filterByMood(states);
        numVisibleStates = 0;
        for(MoodHistoryEntry entry:history.getMoodList()){
            if(entry.getVisibility()){
                ++numVisibleStates;
            }
        }
        assertEquals(4,numVisibleStates);
    }

    @Test
    public void testGetFilteredMoodList(){
        MoodHistory history = mockHistory();
        ArrayList<Mood.MoodState> states = new ArrayList<>();

        // Filtering for angry moods
        states.add(Mood.MoodState.ANGER);
        history.filterByMood(states);
        assertEquals(2,history.getFilteredMoodList().size());

        // Filtering for angry and confused moods
        states.add(Mood.MoodState.CONFUSION);
        history.filterByMood(states);
        assertEquals(3,history.getFilteredMoodList().size());

        // Clearing filters
        states.clear();
        history.filterByMood(states);
        assertEquals(4,history.getFilteredMoodList().size());
    }

    @Test
    public void testCompareTo(){
         Date earlierDate = new Date(1000);
         Date laterDate = new Date(2000);

         assertTrue(earlierDate.compareTo(laterDate) < 0);
         assertTrue(laterDate.compareTo(earlierDate) > 0);
    }

    @Test
    public void testSortByDate(){
        MoodHistory history = mockHistory();
        history.addMoodEvent(new Mood(new Date(1500),Mood.MoodState.SADNESS));
        history.sortByDate();
        Date currentDate = new Date(0);
        boolean isSorted = true;

        //Checking if the dates are ascending
        for(MoodHistoryEntry entry: history.getMoodList()){
            if(entry.getMood().getTimestamp().compareTo(currentDate) < 0){
                isSorted = false;
                break;
            }else{
                currentDate = entry.getMood().getTimestamp();
            }
        }
        assertTrue(isSorted);
    }

    @Test
    public void testSortByDateReverse(){
        MoodHistory history = mockHistory();
        history.addMoodEvent(new Mood(new Date(1500),Mood.MoodState.SADNESS));
        history.sortByDateReverse();
        Date currentDate = new Date(7000);
        boolean isSorted = true;

        //Checking if the dates are descending
        for(MoodHistoryEntry entry: history.getMoodList()){
            if(entry.getMood().getTimestamp().compareTo(currentDate) > 0){
                isSorted = false;
                break;
            }else{
                currentDate = entry.getMood().getTimestamp();
            }
        }
        assertTrue(isSorted);
    }

    @Test
    public void testReset(){
        MoodHistory history = mockHistory();
        ArrayList<Mood.MoodState> states = new ArrayList<>();
        states.add(Mood.MoodState.ANGER);
        history.filterByMood(states);
        history.reset();
        for(MoodHistoryEntry entry: history.getMoodList()){
            assertTrue(entry.getVisibility());
        }
        assertEquals(history.getMoodList(),history.getFilteredMoodList());
    }

    @Test
    public void testToStringList(){
        ArrayList<Mood.MoodState> states = new ArrayList<>();
        states.add(Mood.MoodState.ANGER);
        states.add(Mood.MoodState.SURPRISE);
        ArrayList<String> stateStrings = new ArrayList<>();
        stateStrings.add("ANGER");
        stateStrings.add("SURPRISE");
        assertEquals(stateStrings, MoodFilterFragment.toStringList(stateStrings));
    }


}
