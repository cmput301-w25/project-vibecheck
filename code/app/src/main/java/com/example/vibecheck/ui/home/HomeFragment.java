package com.example.vibecheck.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vibecheck.databinding.HomeScreenBinding;

/**
 * Fragment for the home screen.
 */
public class HomeFragment extends Fragment {

    private HomeScreenBinding binding;
    private HomeScreenViewModel homeScreenViewModel;
    private HomeScreenPostAdapter homeScreenPostAdapter;

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

        //Initialize ViewModel
        homeScreenViewModel = new ViewModelProvider(this).get(HomeScreenViewModel.class);

        // et up RecyclerView
        RecyclerView recyclerView = binding.recyclerViewFeed;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        homeScreenPostAdapter = new HomeScreenPostAdapter();
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