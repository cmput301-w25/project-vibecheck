package com.example.vibecheck;

import java.util.AbstractMap;
import java.util.Map;

/**
 * This is a class that represent entries in the mood history comprising of
 * the actual mood event and a boolean value indicating whether the event should
 * be visible
 */
public class MoodHistoryEntry implements Comparable<MoodHistoryEntry>{

    private Map.Entry<Mood, Boolean> entry;

    /**
     * Constructor for MoodHistoryEntry. Entries are visible by default
     * @param moodEvent
     *      Mood event to add to history
     */
    public MoodHistoryEntry(Mood moodEvent){
        this.entry = new AbstractMap.SimpleEntry<>(moodEvent, true);
    }

    /**
     * Getter method for entry
     * @return
     *      Return the entry
     */
    public Map.Entry<Mood, Boolean> getEntry() {
        return entry;
    }

    /**
     * Setter method for visibility
     * @param visibility
     *      The visibility of the entry
     */
    public void setVisibility(Boolean visibility){
        this.entry.setValue(visibility);
    }

    /**
     * Getter method for visibility
     * @return
     *      The visibility of the entry
     */
    public Boolean getVisibility(){
        return entry.getValue();
    }

    @Override
    public int compareTo(MoodHistoryEntry entry) {
        return this.getEntry().getKey().getTimestamp().compareTo(entry.getEntry().getKey().getTimestamp());
    }

    /**
     * Getter method for mood
     * @return
     *      The mood of the entry
     */
    public Mood getMood(){
        return this.getEntry().getKey();
    }



}
