package com.example.vibecheck.ui.history;

import com.example.vibecheck.ui.moodevents.Mood;

import java.util.ArrayList;

public class Singleton {

    private static Singleton INSTANCE;
    private ArrayList<Mood.MoodState> states = new ArrayList<>();

    private Singleton(){};

    public static Singleton getINSTANCE() {
       if(INSTANCE == null){
           INSTANCE = new Singleton();
       }
       return INSTANCE;
    }

    public ArrayList<Mood.MoodState> getStates() {
        return states;
    }

    public void setStates(ArrayList<Mood.MoodState> states) {
        this.states = states;
    }


}
