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

public class HomeFragment extends Fragment {

    private HomeScreenBinding binding;
    private HomeScreenViewModel homeScreenViewModel;
    private HomeScreenPostAdapter homeScreenPostAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = HomeScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}