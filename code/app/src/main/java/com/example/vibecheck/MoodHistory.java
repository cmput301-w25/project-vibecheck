package com.example.vibecheck;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This is a class that keeps track of a list of mood events and a list of mood event
 * that are visible given a filter
 */
public class MoodHistory implements Serializable {

    private ArrayList<MoodHistoryEntry> moodList = new ArrayList<>();
    private ArrayList<MoodHistoryEntry> filteredMoodList = new ArrayList<>();
    private String username;

    /**
     * This adds a moodEvent to the mood history
     * @param moodEvent
     *      This is the mood event to be added
     */
    public void addMoodEvent(Mood moodEvent){
        MoodHistoryEntry entry = new MoodHistoryEntry(moodEvent);
        moodList.add(entry);
    }

    /**
     * This removes a moodEvent to the mood history
     * @param moodEvent
     *      This is the mood event to be removed
     */
    public void removeMoodEvent(Mood moodEvent){
        for(MoodHistoryEntry entry: moodList){
            if(entry.getMood().equals(moodEvent)){
                moodList.remove(entry);
                break;
            }
        }
    }

    /**
     * Constructor for MoodHistory
     * @param moodList
     *      The list of mood events
     * @param username
     *      Username of the person who is associated with this history
     */
    public MoodHistory(String username, ArrayList<MoodHistoryEntry> moodList){
        this.username = username;
        this.moodList = moodList;
        filteredMoodList = moodList;
    }

    /**
     * Getter method for moodList
     * @return
     *      Return the list
     */
    public ArrayList<MoodHistoryEntry>  getMoodList() {
        return moodList;
    }

    /**
     * This returns a filtered list of mood events
     * @return
     *      Return the filtered list
     */
    public ArrayList<MoodHistoryEntry> getFilteredMoodList(){
        filteredMoodList = new ArrayList<MoodHistoryEntry>();
        for (int i = 0; i < moodList.size(); i++){
            if(moodList.get(i).getVisibility()){
                filteredMoodList.add(moodList.get(i));
            }
        }
        return filteredMoodList;
    }

    /**
     * Setter method for moodList
     * @param moodList
     *      Mood list of the mood history
     */
    public void setMoodList(ArrayList<MoodHistoryEntry>  moodList) {
        this.moodList = moodList;
    }

    /**
     * Getter method for username
     * @return
     *  Return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Setter method for username
     * @param username
     *      Username associated with the mood history
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sorts the mood list in descending order
     *
     */
    public void sortByDateReverse(){
        Collections.sort(moodList);
        Collections.reverse(moodList);
    }

    /**
     * Sorts the mood list in ascending order
     *
     */
    public void sortByDate(){
        Collections.sort(moodList);
    }

    /**
     * Filters the mood list
     * @param states
     *      The list of mood states that are visible.If empty all mood states are visible
     *
     */
    public void filterByMood(ArrayList<Mood.MoodState> states){
        if(!states.isEmpty()) {
            for (int i = 0; i < moodList.size(); i++) {
                moodList.get(i).setVisibility(states.contains(moodList.get(i).getEntry().getKey().getMoodState()));
            }
        }else{
            reset();
        }
    }

    /**
     * Resets the filtering of the mood list
     *
     */
    public void reset(){
        for (int i = 0; i < moodList.size(); i++){
            moodList.get(i).setVisibility(true);
        }
    }

}
