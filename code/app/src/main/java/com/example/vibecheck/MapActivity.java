/**
 * MapActivityFragment displays a Google Map with markers representing mood events.
 * <p>
 * It initializes the map via a SupportMapFragment and, when the map is ready, adds markers
 * for sample mood events from two MoodHistory objects (one for the user and one for friends).
 * A toggle button lets the user switch between viewing their own events and their friends' events.
 * </p>
 */


package com.example.vibecheck;

import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.vibecheck.ui.history.MoodFilterFragment;
import com.example.vibecheck.ui.history.MoodHistory;
import com.example.vibecheck.ui.history.MoodHistoryEntry;
import com.example.vibecheck.ui.moodevents.Mood;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vibecheck.databinding.MapFragmentBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, MoodFilterFragment.MoodFilterDialogListener {

    private GoogleMap mMap;
    private MapFragmentBinding binding;

    private ImageButton backButton;
    private  ImageButton filterButton;
    private TextView header;
    private TextView label;
    private AppCompatToggleButton toggle;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private MoodHistory userHistory;
    private MoodHistory friendsHistory;
    private ArrayList<Mood.MoodState> states = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        binding = MapFragmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        backButton = findViewById(R.id.back_button);
        filterButton = findViewById(R.id.mood_history_filter_button);
        header = findViewById(R.id.mood_history_header);
        label = findViewById(R.id.mood_date_label);
        toggle = findViewById(R.id.toggle);

        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) {
            Log.e("HomeActivity", "ERROR: BottomNavigationView is NULL! Check activity_home.xml.");
        } else {
            Log.d("HomeActivity", "BottomNavigationView found successfully.");
        }

        // Handle filter button press
        filterButton.setOnClickListener(v -> {
            MoodFilterFragment fragment = MoodFilterFragment.newInstance(states);
            fragment.show(getSupportFragmentManager(), "");
        });

        // Handle back button press///////////////////////////////////////////////////////////THIS WILL HAVE TO CHANGE IF I TURN THIS INTO A FRAGMENT
        backButton.setOnClickListener(view -> finish());

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        CollectionReference moodHistoryRef = db.collection("users").document(currentUser.getUid()).collection("MoodHistory");

        moodHistoryRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        ArrayList<MoodHistoryEntry> moodHistoryList = new ArrayList<>();
                        userHistory = new MoodHistory(currentUser.getDisplayName(), moodHistoryList);

                        // Iterate over the documents in the snapshot
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            // Convert each document to an Item object
                            Mood mood = document.toObject(Mood.class);
                            userHistory.addMoodEvent(mood);  // Add the Item to the list
                        }
                        for(MoodHistoryEntry entry: userHistory.getFilteredMoodList()){
                            if(entry.getMood().getLatitude() != null) {
                                LatLng marker = new LatLng(entry.getMood().getLatitude(), entry.getMood().getLongitude());
                                mMap.addMarker(new MarkerOptions().position(marker));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                            }
                        }
                        Log.d("MapActivityFragment", "User's Mood History obtained succesfully");

                    } else {
                        // Handle error
                        Log.e("MapActivityFragment", "Failed to obtain User's Mood History obtained succesfully");
                    }
                });

        DocumentReference followersRef = db.collection("users").document(currentUser.getUid());
/*
        followersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        List <String> followers = (List<String>) documentSnapshot.get("followers");
                        ArrayList<MoodHistoryEntry> friends = new ArrayList<>();
                        friendsHistory = new MoodHistory("Friends", friends);

                        // Iterate over the documents in the snapshot
                        for (String follower : followers) {
                            // Convert each document to an Item object
                            CollectionReference friendHistoryRef = db.collection("users").document(follower).collection("MoodHistory");
                            friendHistoryRef.get()
                                    .addOnCompleteListener(innerTask -> {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot querySnapshot = innerTask.getResult();

                                            // Iterate over the documents in the snapshot
                                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                // Convert each document to an Item object
                                                Mood mood = document.toObject(Mood.class);
                                                friendsHistory.addMoodEvent(mood);  // Add the Item to the list
                                            }

                                            Log.d("MapActivityFragment", "User's Mood History obtained succesfully");

                                        } else {
                                            // Handle error
                                            Log.e("MapActivityFragment", "Failed to obtain User's Mood History obtained succesfully");
                                        }
                                    });
                        }
                        Log.d("MapActivityFragment", "User's Mood History obtained succesfully");

                    } else {
                        // Handle error
                        Log.e("MapActivityFragment", "Failed to obtain User's Mood History obtained succesfully");
                    }
                });





//      mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        toggle.setOnClickListener(v -> {
            if(toggle.isChecked()){
                mMap.clear();
                for(MoodHistoryEntry entry: friendsHistory.getFilteredMoodList()){
                    if(entry.getMood().getLatitude() != null) {
                        LatLng marker = new LatLng(entry.getMood().getLatitude(), entry.getMood().getLongitude());
                        mMap.addMarker(new MarkerOptions().position(marker));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                    }
                }
            }else{
                mMap.clear();
                for(MoodHistoryEntry entry: userHistory.getFilteredMoodList()){
                    if(entry.getMood().getLatitude() != null) {
                        LatLng marker = new LatLng(entry.getMood().getLatitude(), entry.getMood().getLongitude());
                        mMap.addMarker(new MarkerOptions().position(marker));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
                    }
                }
            }


        });

 */



    }

    @Override
    public void filter(ArrayList<Mood.MoodState> states) {
        this.states = states;
        userHistory.filterByMood(states);
        mMap.clear();
        for(MoodHistoryEntry entry: userHistory.getFilteredMoodList()){
            if(entry.getMood().getLatitude() != null) {
                LatLng marker = new LatLng(entry.getMood().getLatitude(), entry.getMood().getLongitude());
                mMap.addMarker(new MarkerOptions().position(marker));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
            }
        }

    }
}