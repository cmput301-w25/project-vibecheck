package com.example.vibecheck.ui.history;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.vibecheck.Mood;
import com.example.vibecheck.R;

import java.util.ArrayList;

public class MoodFilterFragment extends DialogFragment {

    //Code for newInstance derived from here:
    //https://stackoverflow.com/questions/9245408/best-practice-for-instantiating-a-new-android-fragment
    public static MoodFilterFragment newInstance(ArrayList<Mood.MoodState> states) {
        MoodFilterFragment fragment = new MoodFilterFragment();

        Bundle args = new Bundle();
        ArrayList<String> stateStrings = toStringList(states);
        args.putStringArrayList("states", stateStrings);
        fragment.setArguments(args);

        return fragment;
    }

    //Code for this method adapted from code from here:
    //https://stackoverflow.com/questions/52862900/converting-arraylist-of-enums-to-string
    public static ArrayList<String> toStringList(Iterable<?> iterable) {
        ArrayList<String> strings = new ArrayList<>();
        for (Object value : iterable)
            strings.add(String.valueOf(value));
        return strings;
    }

    interface MoodFilterDialogListener {
        void filter(ArrayList<Mood.MoodState> states);
    }

    private MoodFilterDialogListener listener;

    private ArrayList<Mood.MoodState> states = new ArrayList<>();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MoodFilterDialogListener) {
            listener = (MoodFilterDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement MoodFilterDialogListener");
        }
    }

    public void checkState(CheckBox box, Mood.MoodState state){
        if(box.isChecked()){
            states.add(state);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        super.onCreateDialog(savedInstanceState);
        ArrayList<String> currentlyChecked = new ArrayList<>();
       if (getArguments() != null) {
           currentlyChecked = getArguments().getStringArrayList("states");
       }

        View view = LayoutInflater.from(getContext()).inflate(R.layout.checkbox_dialog, null);
        CheckBox angerBox = view.findViewById(R.id.anger_box);
        CheckBox confusionBox = view.findViewById(R.id.confusion_box);
        CheckBox disgustBox = view.findViewById(R.id.disgust_box);
        CheckBox fearBox = view.findViewById(R.id.fear_box);
        CheckBox happinessBox = view.findViewById(R.id.happines_box);
        CheckBox sadnessBox = view.findViewById(R.id.sadness_box);
        CheckBox shameBox = view.findViewById(R.id.shame_box);
        CheckBox surpriseBox = view.findViewById(R.id.surprise_box);
        CheckBox boredomBox = view.findViewById(R.id.boredom_box);

        for(String s: currentlyChecked){
            switch(s){
                case "ANGER":
                    angerBox.setChecked(true);
                case "CONFUSION":
                    confusionBox.setChecked(true);
                    break;
                case "DISGUST":
                    disgustBox.setChecked(true);
                    break;
                case "FEAR":
                    fearBox.setChecked(true);
                    break;
                case "HAPPINESS":
                    happinessBox.setChecked(true);
                    break;
                case "SADNESS":
                    sadnessBox.setChecked(true);
                    break;
                case "SHAME":
                    shameBox.setChecked(true);
                    break;
                case "SURPRISE":
                    surpriseBox.setChecked(true);
                    break;
                case "BOREDOM":
                    boredomBox.setChecked(true);
                    break;
                default:
                    break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder
                .setView(view)
                .setPositiveButton("Confirm", (dialog, which) -> {

                    checkState(angerBox, Mood.MoodState.ANGER);
                    checkState(confusionBox, Mood.MoodState.CONFUSION);
                    checkState(disgustBox, Mood.MoodState.DISGUST);
                    checkState(fearBox, Mood.MoodState.FEAR);
                    checkState(happinessBox, Mood.MoodState.HAPPINESS);
                    checkState(sadnessBox, Mood.MoodState.SADNESS);
                    checkState(shameBox, Mood.MoodState.SHAME);
                    checkState(surpriseBox, Mood.MoodState.SURPRISE);
                    checkState(boredomBox, Mood.MoodState.BOREDOM);

                    listener.filter(states);
                })
                .create();
    }
}
