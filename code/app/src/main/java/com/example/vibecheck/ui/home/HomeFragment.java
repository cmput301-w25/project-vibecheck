package com.example.vibecheck.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

import com.example.vibecheck.R;
import com.example.vibecheck.databinding.HomeScreenBinding;

/**
 * Fragment for the home screen.
 */
public class HomeFragment extends Fragment {

    private HomeScreenBinding binding;
    private HomeScreenViewModel homeScreenViewModel;
    private HomeScreenPostAdapter homeScreenPostAdapter;

    // Declare UI elements
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView iconSearch;
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
        toolbarTitle = view.findViewById(R.id.title);
        iconSearch = view.findViewById(R.id.icon_search);
        iconFilter = view.findViewById(R.id.icon_filter);
        iconNotifications = view.findViewById(R.id.icon_notifications);

        // Set click listeners for toolbar icons
        iconSearch.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Search clicked", Toast.LENGTH_SHORT).show();
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
            public void onMoodClick(String moodEventId, boolean isLoggedUserPost) {
                navigateToViewMoodEvents(moodEventId, isLoggedUserPost);
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
     * Navigate to the view mood events fragment.
     * @param moodEventId
     * @param isLoggedUserPost
     */
    private void navigateToViewMoodEvents(String moodEventId, boolean isLoggedUserPost) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_home);
        Bundle bundle = new Bundle();
        bundle.putString("moodEventId", moodEventId);

        //Navigate to view mood events fragment, to my mood or user mood if the username matches the logged in username
        if (isLoggedUserPost) {
            navController.navigate(R.id.action_home_to_myMoodDisplay, bundle);
        } else {
            navController.navigate(R.id.action_home_to_userMoodDisplay, bundle);
        }
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