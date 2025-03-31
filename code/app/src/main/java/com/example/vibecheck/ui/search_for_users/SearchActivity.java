/**
 * SearchActivity.java
 * This activity is responsible for searching for users.
 *
 * Outstanding issues: App crashes when attempting to visit a profile.
 */

package com.example.vibecheck.ui.search_for_users;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vibecheck.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

// This implementation of search references:
// https://www.geeksforgeeks.org/how-to-implement-android-searchview-with-example/

/*
 * Search Activity.
 */
public class SearchActivity extends AppCompatActivity {
    private ImageButton searchBackArrow;
    private SearchView searchUsersSearchView;
    private ListView searchUsersListView;

    private SearchUserResultAdapter arrayAdapter;
    private ArrayList<SearchUserResult> listOfResults;

    protected FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Top padding
        View root = findViewById(R.id.search_users_layout);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Initialize the needed views
        searchBackArrow = findViewById(R.id.search_back_arrow);
        searchUsersSearchView = findViewById(R.id.search_for_users_searchview);
        searchUsersListView = findViewById(R.id.search_users_listview);

        searchBackArrow.setOnClickListener(view -> finish());

        // Get instance of database and set up an ArrayAdapter
        db = FirebaseFirestore.getInstance();
        listOfResults = new ArrayList<>();
        arrayAdapter = new SearchUserResultAdapter(this, listOfResults);
        searchUsersListView.setAdapter(arrayAdapter);

        // Listener for the search bar
        searchUsersSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    // Searches the database for the user from the query
    private void searchUsers(String query) {
        db.collection("users").whereEqualTo("username", query)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if ((task.isSuccessful()) && (task.getResult() != null)) {
                    listOfResults.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String username = documentSnapshot.getString("username");
                        if (username == null || username.trim().isEmpty()) {
                            continue;
                        }
                        int numberOfFollowers = 0; // Placeholder
                        listOfResults.add(new SearchUserResult(username, numberOfFollowers));
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
