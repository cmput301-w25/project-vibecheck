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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MapActivityFragment extends FragmentActivity implements OnMapReadyCallback, MoodFilterFragment.MoodFilterDialogListener {

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
    private ArrayList<Mood.MoodState> states = new ArrayList<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword("jo@gmail.com", "joel02");
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

        filterButton.setOnClickListener(v -> {
            MoodFilterFragment fragment = MoodFilterFragment.newInstance(states);
            fragment.show(getSupportFragmentManager(), "");
        });


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
                        Log.d("MapActivityFragment", "Success1");

                    } else {
                        // Handle error
                        Log.e("MapActivityFragment", "Failure1");
                    }
                });

        /*ArrayList<MoodHistoryEntry> testList = new ArrayList<>();
        Mood one = new Mood(Mood.MoodState.BOREDOM);
        one.setLocation(53.522778, -113.623055);
        Mood two = new Mood(Mood.MoodState.BOREDOM);
        two.setLocation(51.522778, -114.623055);
        testList.add(new MoodHistoryEntry(one));
        testList.add(new MoodHistoryEntry(two));
        testList.add(new MoodHistoryEntry(new Mood(Mood.MoodState.ANGER)));
        MoodHistory userHistory = new MoodHistory("Joel", testList);*/

        Mood three= new Mood(Mood.MoodState.BOREDOM);
        three.setLatAndLong(50.522778, -110.623055);

        ArrayList<MoodHistoryEntry> friends = new ArrayList<>();
        friends.add(new MoodHistoryEntry(three));
        MoodHistory friendsHistory = new MoodHistory("Friends", friends);


        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        /*for(MoodHistoryEntry entry: userHistory.getFilteredMoodList()){
            if(entry.getMood().getLatitude() != null) {
                LatLng marker = new LatLng(entry.getMood().getLatitude(), entry.getMood().getLongitude());
                mMap.addMarker(new MarkerOptions().position(marker));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
            }
        }*/

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