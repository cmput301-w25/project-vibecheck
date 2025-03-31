package com.example.vibecheck;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

        // Initialize the needed views
        searchBackArrow = findViewById(R.id.search_back_arrow);
        searchUsersSearchView = findViewById(R.id.search_for_users_searchview);
        searchUsersListView = findViewById(R.id.search_users_listview);

        searchBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: go back to the home screen
            }
        });

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
                        int numberOfFollowers = 0; // TODO: make this the actual number of followers
                        listOfResults.add(new SearchUserResult(username, numberOfFollowers));
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
