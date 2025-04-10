/**
 * MapActivityFragment displays a Google Map with markers representing mood events.
 * <p>
 * It initializes the map via a SupportMapFragment and, when the map is ready, adds markers
 * for sample mood events from two MoodHistory objects (one for the user and one for friends).
 * A toggle button lets the user switch between viewing their own events and their friends' events.
 * </p>
 */


package com.example.vibecheck.ui.map;

import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.vibecheck.R;
import com.example.vibecheck.ui.history.MoodFilterFragment;
import com.example.vibecheck.ui.history.MoodHistory;
import com.example.vibecheck.ui.history.MoodHistoryEntry;
import com.example.vibecheck.ui.history.Singleton;
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
import java.util.List;

import java.util.ArrayList;

/**
 * This class manages the mood map activity
 */
public class MapActivity extends FragmentActivity implements OnMapReadyCallback, MoodFilterFragment.MoodFilterDialogListener {

    private GoogleMap mMap;
    private MapFragmentBinding binding;

    private ImageButton backButton;
    private  ImageButton filterButton;
    private TextView header;
    private TextView label;
    private AppCompatToggleButton toggle;
    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private MoodHistory userHistory;
    private MoodHistory friendsHistory;
    private ArrayList<Mood.MoodState> states = new ArrayList<>();
    private Singleton singleton;

    /**
     * Method that is run where activity is created
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
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

        // Obtains the navController from the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_home);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            Log.d("HomeActivity", "NavController found successfully.");
        }

        /*
        // Initialize BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView == null) {
            Log.e("HomeActivity", "ERROR: BottomNavigationView is NULL! Check activity_home.xml.");
        } else {
            Log.d("HomeActivity", "BottomNavigationView found successfully.");
        }

        // Set up the navigation listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Log.d("HomeActivity", "Bottom Navigation Clicked: " + item.getItemId());

            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                if (navController.getCurrentDestination().getId() == R.id.navigation_home) {
                    Toast.makeText(this, "You are already on this page.", Toast.LENGTH_SHORT).show();
                } else {
                    navController.navigate(R.id.navigation_home);
                }
                return true;

            } else if (itemId == R.id.navigation_history) {
                Log.d("HomeActivity", "Opening MoodHistoryActivity...");
                Intent intent = new Intent(MapActivity.this, MoodHistoryActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_post) {
                Log.d("HomeActivity", "Opening AddMoodEventActivity...");
                Intent intent = new Intent(MapActivity.this, AddMoodEventActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_map) {
                Log.d("HomeActivity", "Opening MapActivity...");
                Intent intent = new Intent(MapActivity.this, MapActivity.class);
                startActivity(intent);
                return true;

            } else if (itemId == R.id.navigation_profile) {
                Log.d("HomeActivity", "Opening ProfileActivity...");
                Intent intent = new Intent(MapActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            } else {
                return false;
            }
        });

         */

        filterButton.setOnClickListener(v -> {
            MoodFilterFragment fragment = MoodFilterFragment.newInstance(states);
            fragment.show(getSupportFragmentManager(), "");
        });

        backButton.setOnClickListener(v -> {
            finish();
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
     * @param googleMap Map to manipulate
     *                  once ready
     *
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

                        // Singleton for passing "states" between activities
                        singleton = Singleton.getINSTANCE();
                        this.states = singleton.getStates();
                        filter(states);

                        Log.d("MapActivityFragment", "User's Mood History obtained succesfully");

                    } else {
                        // Handle error
                        Log.e("MapActivityFragment", "Failed to obtain User's Mood History obtained succesfully");
                    }
                });

        DocumentReference followersRef = db.collection("users").document(currentUser.getUid());

        followersRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        List <String> followers = (List<String>) documentSnapshot.get("followers");
                        ArrayList<MoodHistoryEntry> friends = new ArrayList<>();
                        friendsHistory = new MoodHistory("Friends", friends);

                        // Iterate over the documents in the snapshot
                        if (followers != null) {
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
                        }
                        Log.d("MapActivityFragment", "User's Mood History obtained succesfully");

                    } else {
                        // Handle error
                        Log.e("MapActivityFragment", "Failed to obtain User's Mood History obtained succesfully");
                    }
                });

        //Toggles between personal map and friends map
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

        // Close mood history activity and return to home screen
        backButton.setOnClickListener(v -> {
            finish();
        });

    }

    /**
     * Filters Mood history based on selected Mood states
     * @param states
     *      Mood states to filter on.
     */
    @Override
    public void filter(ArrayList<Mood.MoodState> states) {
        this.states = states;
        singleton.setStates(states);
        userHistory.filterByMood(states);
        mMap.clear();
        //Populating Map
        for(MoodHistoryEntry entry: userHistory.getFilteredMoodList()){
            if(entry.getMood().getLatitude() != null) {
                LatLng marker = new LatLng(entry.getMood().getLatitude(), entry.getMood().getLongitude());
                mMap.addMarker(new MarkerOptions().position(marker));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker));
            }
        }
    }
}