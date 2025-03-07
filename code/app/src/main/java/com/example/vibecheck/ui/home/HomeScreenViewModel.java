package com.example.vibecheck.ui.home;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.vibecheck.Mood;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * View model for the home screen.
 */
public class HomeScreenViewModel extends ViewModel {

    private final MutableLiveData<List<Mood>> moodPosts;
    private final FirebaseFirestore db;
    private final CollectionReference moodsCollection;

    /**
     * Constructor for the view model.
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
        moodsCollection.orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("FirestoreError", "Error listening for mood updates", error);
                            return;
                        }

                        List<Mood> moodList = new ArrayList<>();
                        if (value != null) {
                            for (QueryDocumentSnapshot document : value) {
                                Mood mood = document.toObject(Mood.class);
                                moodList.add(mood);
                            }
                        }

                        //Update UI in real-time
                        moodPosts.postValue(moodList);
                    }
                });
    }

}