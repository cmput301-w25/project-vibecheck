package com.example.vibecheck.ui.history;

import com.example.vibecheck.ui.moodevents.Mood;

import java.util.ArrayList;

/**
 * Singleton class for storing states to be reused between activities
 */
public class Singleton {

    private static Singleton INSTANCE;
    private ArrayList<Mood.MoodState> states = new ArrayList<>();

    /**
     * Private Constructor to prevent more than one instance of this class
     * being made
     */
    private Singleton(){};

    /**
     * Returns Singleton instance
     * @return
     *      Singleton instance
     */
    public static Singleton getINSTANCE() {
       if(INSTANCE == null){
           INSTANCE = new Singleton();
       }
       return INSTANCE;
    }

    /**
     * Getter method for states
     * @return
     *      Mood states
     */
    public ArrayList<Mood.MoodState> getStates() {
        return states;
    }

    /**
     * Setter method for states
     * @param states
     *      Mood states for filters
     */
    public void setStates(ArrayList<Mood.MoodState> states) {
        this.states = states;
    }


}
