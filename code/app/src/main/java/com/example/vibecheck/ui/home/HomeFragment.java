/*
This class is the fragment for the home screen, which displays the top tool bar and the home feed for mood event posts.

Outstanding issues: Top tool bar functionality not implemented and may need layout adjustment, need to distinguish
between user posts and logged in user posts.
 */

package com.example.vibecheck.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.appcompat.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.vibecheck.ui.moodevents.Mood;
import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.databinding.HomeScreenBinding;
import com.example.vibecheck.ui.search_for_users.SearchActivity;

/**
 * Fragment for the home screen.
 */
public class HomeFragment extends Fragment {

    private HomeScreenBinding binding;
    private HomeScreenViewModel homeScreenViewModel;
    private HomeScreenPostAdapter homeScreenPostAdapter;

    // Declare UI elements
    private Toolbar toolbar;
    private SearchView iconSearch;
    //private ImageView iconSearch;
    private ImageView iconFilter;
    private ImageView iconNotifications;

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     *      returns the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = HomeScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Toolbar
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Initialize Toolbar components
        TextView title = toolbar.findViewById(R.id.title);
        iconSearch = view.findViewById(R.id.searchview);
        //iconSearch = view.findViewById(R.id.icon_search);
        iconFilter = view.findViewById(R.id.icon_filter);
        iconNotifications = view.findViewById(R.id.icon_notifications);

        // Initialize NavController
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);

        // Set click listeners for toolbar icons
        /*
        iconSearch.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchActivity.class);
            startActivity(intent);

        });
         */
        iconSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeScreenPostAdapter.filter(newText);
                return false;
            }


        });
        ImageView searchViewXBtn = iconSearch.findViewById(androidx.appcompat.R.id.search_close_btn);
        TextView searchViewText = iconSearch.findViewById(androidx.appcompat.R.id.search_src_text);
        searchViewXBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchViewText.setText("");
            }
        });


        iconFilter.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Filter clicked", Toast.LENGTH_SHORT).show();
        });

        iconNotifications.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Notifications clicked", Toast.LENGTH_SHORT).show();
        });


        //Initialize ViewModel
        homeScreenViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);

        //Set up RecyclerView
        RecyclerView recyclerView = binding.recyclerViewFeed;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        //Set up adapter, navigation to view mood events
        homeScreenPostAdapter = new HomeScreenPostAdapter(new HomeScreenPostAdapter.OnMoodClickListener() {
            @Override
            public void onMoodClick(Mood mood) {
                MoodUtils.navigateToViewMoodEvent(navController, mood);
            }
        });

        //Set adapter
        recyclerView.setAdapter(homeScreenPostAdapter);

        //Observe ViewModel changes
        homeScreenViewModel.getMoodPosts().observe(getViewLifecycleOwner(), moodPosts -> {
            if (isAdded()) { //Ensure fragment is still attached before updating UI
                homeScreenPostAdapter.setMoodPosts(moodPosts);
            }
        });
    }

    /**
     * Called when the fragment is no longer in use.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}