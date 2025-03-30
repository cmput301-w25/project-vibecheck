package com.example.vibecheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/*
 * Adapter for SearchUserResult.
 */
public class SearchUserResultAdapter extends ArrayAdapter<SearchUserResult> {
    private ImageView userProfileImage;
    private TextView username;
    private TextView numberOfFollowers;
    private Button visitProfile;

    public SearchUserResultAdapter(Context context, ArrayList<SearchUserResult> searchUserResults) {
        super(context, 0, searchUserResults);
    }

    @NonNull
    @Override public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_user_search, parent, false);
        } else {
            view = convertView;
        }

        SearchUserResult searchResult = getItem(position);

        userProfileImage = view.findViewById(R.id.search_user_profile_image);
        username = view.findViewById(R.id.search_username);
        numberOfFollowers = view.findViewById(R.id.search_number_followers);
        visitProfile = view.findViewById(R.id.search_visit_profile);

        if (searchResult != null) {
            userProfileImage.setImageResource(searchResult.getProfileImageResourceId());
            username.setText(searchResult.getUsername());
            numberOfFollowers.setText(String.format("%d", searchResult.getNumberOfFollowers()));
            visitProfile.setText("Visit Profile");
        }

        return view;
    }

}
