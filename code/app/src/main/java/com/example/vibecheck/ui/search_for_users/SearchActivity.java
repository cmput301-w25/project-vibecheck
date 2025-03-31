package com.example.vibecheck.ui.search_for_users;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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

// Implementation of search references:
// https://www.geeksforgeeks.org/how-to-implement-android-searchview-with-example/

/*
 * Search Activity
 */
public class SearchActivity extends AppCompatActivity {
    private ImageButton searchBackArrow;
    private SearchView searchUsersSearchView;
    private ListView searchUsersListView;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list;

    private FirebaseFirestore db;

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

        // initialize views
        searchBackArrow = findViewById(R.id.search_back_arrow);
        searchUsersSearchView = findViewById(R.id.search_for_users_searchview);
        searchUsersListView = findViewById(R.id.search_users_listview);

        searchBackArrow.setOnClickListener(view -> finish());

        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.fragment_user_search, list);
        searchUsersListView.setAdapter(arrayAdapter);

        // Listener for search bar
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
                    list.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        list.add(documentSnapshot.getString("username"));
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
