package com.example.vibecheck.ui.search_for_users;

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

import com.example.vibecheck.R;

import java.util.ArrayList;

/*
 * Adapter for SearchUserResult.
 */
public class SearchUserResultAdapter extends ArrayAdapter<SearchUserResult> {
    private ImageView userProfileImage;
    private TextView username;
    private TextView numberOfFollowers;
    private Button visitProfile;

    /**
     * @param context
     *      Interface to global information about an application environment.
     * @param searchUserResults
     *      An ArrayList representing search results.
     */
    public SearchUserResultAdapter(Context context, ArrayList<SearchUserResult> searchUserResults) {
        super(context, 0, searchUserResults);
    }

    /**
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return
     */
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
            //userProfileImage.setImageResource(searchResult.getUserProfileImageID());
            username.setText(searchResult.getUsername());
            numberOfFollowers.setText(String.format("%d", searchResult.getNumberOfFollowers()));
            visitProfile.setText("Visit Profile");
        }

        visitProfile.setOnClickListener(v -> {
            //todo: add visit profile functionality
        });

        return view;
    }
}