package com.example.vibecheck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class MoodHistory implements Serializable {

    private ArrayList<MoodHistoryEntry> moodList = new ArrayList<>();
    private ArrayList<MoodHistoryEntry> filteredMoodList = new ArrayList<>();
    private User profile;

    public void addMoodEvent(Mood moodEvent){
        MoodHistoryEntry entry = new MoodHistoryEntry(moodEvent);
        moodList.add(entry);
    }

    public void removeMoodEvent(Mood moodEvent){
        MoodHistoryEntry entry = new MoodHistoryEntry(moodEvent);
        moodList.remove(entry);
    }

    public MoodHistory(User profile, ArrayList<MoodHistoryEntry> moodList){
        this.profile = profile;
        this.moodList = moodList;
        filteredMoodList = moodList;
    }

    public ArrayList<MoodHistoryEntry>  getMoodList() {
        return moodList;
    }

    public ArrayList<MoodHistoryEntry> getFilteredMoodList(){
        filteredMoodList = new ArrayList<MoodHistoryEntry>();
        for (int i = 0; i < moodList.size(); i++){
            if(moodList.get(i).getVisiblity()){
                filteredMoodList.add(moodList.get(i));
            }
        }
        return filteredMoodList;
    }

    public void setMoodList(ArrayList<MoodHistoryEntry>  moodList) {
        this.moodList = moodList;
    }

    public User getProfile() {
        return profile;
    }

    public void setProfile(User profile) {
        this.profile = profile;
    }

    //TODO: delete if not used
    public void changeVisibility(Mood moodEvent, Boolean visibility){
        for (int i = 0; i < moodList.size(); i++){
            if(moodList.get(i).getEntry().getKey() == moodEvent){
                moodList.get(i).setVisiblity(visibility);
            }
        }
    }

    public void sortByDateReverse(){
        Collections.reverse(moodList);
    }

    public void sortByDate(){
        Collections.sort(moodList);
    }

    public void filterByMood(ArrayList<Mood.MoodState> states){
        if(!states.isEmpty()) {
            for (int i = 0; i < moodList.size(); i++) {
                moodList.get(i).setVisiblity(states.contains(moodList.get(i).getEntry().getKey().getMoodState()));
            }
        }else{
            reset();
        }
    }


    public void reset(){
        for (int i = 0; i < moodList.size(); i++){
            moodList.get(i).setVisiblity(true);
        }
    }

}
