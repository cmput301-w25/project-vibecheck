package com.example.vibecheck;

import java.util.AbstractMap;
import java.util.Map;

public class MoodHistoryEntry implements Comparable<MoodHistoryEntry>{

    private Map.Entry<Mood, Boolean> entry;

    public MoodHistoryEntry(Mood moodEvent){
        this.entry = new AbstractMap.SimpleEntry<>(moodEvent, true);
    }

    public Map.Entry<Mood, Boolean> getEntry() {
        return entry;
    }

    public void setVisiblity(Boolean visiblity){
        this.entry.setValue(visiblity);
    }

    @Override
    public int compareTo(MoodHistoryEntry entry) {
        return this.getEntry().getKey().getTimestamp().compareTo(entry.getEntry().getKey().getTimestamp());
    }

    public Mood getMood(){
        return this.getEntry().getKey();
    }



}
