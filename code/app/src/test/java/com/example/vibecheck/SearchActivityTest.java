package com.example.vibecheck;

import static org.junit.Assert.*;

import android.widget.ListView;
import android.widget.SearchView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

/*
 * Tests for SearchActivity.
 */
@RunWith(RobolectricTestRunner.class)
public class SearchActivityTest {

    private SearchActivity searchActivity;

    // Initialize SearchActivity before tests.
    @Before
    public void setUp() {
        searchActivity = Robolectric.buildActivity(SearchActivity.class).create().start().resume().get();
    }

    // Check if SearchActivity is initialized properly.
    @Test
    public void testInitializeSearchActivity() {
        assertNotNull(searchActivity.findViewById(R.id.search_back_arrow));
        assertNotNull(searchActivity.findViewById(R.id.search_for_users_searchview));
        assertNotNull(searchActivity.findViewById(R.id.search_users_listview));
    }

    // Ensure that if the user is in the database, they show up in the results.
    public void testUserIsInDatabase() {
        // Test query
        SearchView searchForUsers = searchActivity.findViewById(R.id.search_for_users_searchview);
        searchForUsers.setQuery("friend1@gmail.com", true);

        // Check that results from query are as expected
        ListView searchResults = searchActivity.findViewById(R.id.search_users_listview);
        SearchUserResultAdapter searchAdapter = (SearchUserResultAdapter) searchResults.getAdapter();

        assertEquals(1, searchAdapter.getCount());
        SearchUserResult searchResult = searchAdapter.getItem(0);
        assertEquals("friend1@gmail.com", searchResult.getUsername());
    }

    // Ensure that if the user is not in the database, there won't be anything in the results.
    public void testUserIsInNotDatabase() {
        // Test query
        SearchView searchForUsers = searchActivity.findViewById(R.id.search_for_users_searchview);
        searchForUsers.setQuery("johndoe@gmail.com", true);

        // Check that results from query are as expected
        ListView searchResults = searchActivity.findViewById(R.id.search_users_listview);
        SearchUserResultAdapter searchAdapter = (SearchUserResultAdapter) searchResults.getAdapter();

        assertEquals(0, searchAdapter.getCount());
    }
}