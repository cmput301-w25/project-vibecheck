/**
 * This class is the view model for the home screen. Obtains and handles the mood event data from the firestore database.
 * Also handles real time updates from the firestore database.
 *
 * This class has no outstanding issues
 */

package com.example.vibecheck.ui.home;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vibecheck.ui.moodevents.Mood;
//import com.google.firebase.Firebase;
//import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * View model for the home screen.
 */
public class HomeScreenViewModel extends ViewModel {

    private final MutableLiveData<List<Mood>> moodPosts;
    private final FirebaseFirestore db;
    private final CollectionReference moodsCollection;

    /**
     * Constructor for the view model, obtains the mood posts to display as MutableLiveData.
     */
    public HomeScreenViewModel() {
        moodPosts = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        moodsCollection = db.collection("moods");

        //Listen for real time updates from firestore
        listenForMoodUpdates();
    }

    /**
     * Get the mood posts.
     * @return
     *      returns the mood posts
     */
    public LiveData<List<Mood>> getMoodPosts() {
        return moodPosts;
    }

    /**
     * Listen for real time updates from firestore.
     */
    private void listenForMoodUpdates() {
        moodsCollection.orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreError", "Error listening for mood updates", error);
                            return;
                        }

                        List<Mood> moodList = new ArrayList<>();
                        if (value != null) {
                            for (DocumentSnapshot document : value.getDocuments()) {
                                Log.d("FirestoreDebug", "Raw Firestore Document: " + document.getData());

                                Mood mood = document.toObject(Mood.class);

                                if (mood != null && mood.isPublic()) {
                                    mood.setMoodId(document.getId());

                                    // Check if timestamp is actually present
                                    if (document.contains("timestamp") && document.getTimestamp("timestamp") != null) {
                                        mood.setTimestamp(document.getTimestamp("timestamp").toDate());
                                    } else {
                                        Log.e("FirestoreError", "ERROR: Mood document missing timestamp! ID: " + document.getId());

                                        // Backup Plan: Assign a default timestamp to prevent crashing
                                        mood.setTimestamp(new Date());
                                    }

                                    moodList.add(mood);
                                } else {
                                    Log.e("FirestoreError", "Mood object is null for document: " + document.getId());
                                }
                            }
                        }
                        //Update UI in real-time
                        moodPosts.postValue(moodList);
                    }
                });
    }
}