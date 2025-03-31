package com.example.vibecheck;

import android.content.Intent;
import android.os.SystemClock;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Espresso tests for EditMoodEventActivity.
 */
@RunWith(AndroidJUnit4.class)
public class EditMoodEventActivityTest {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String testDocId;

    /**
     * Helper method to create a test mood event document in Firestore.
     * The document will contain known values so we can assert on them.
     *
     * @return the created document's ID.
     */
    private String createTestMoodEvent() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] docId = new String[1];

        Map<String, Object> data = new HashMap<>();
        data.put("moodState", "HAPPINESS");
        data.put("trigger", "Test trigger");
        data.put("description", "Test description");
        data.put("timestamp", new Date());
        data.put("socialSituation", "ALONE");

        db.collection("moods").add(data)
                .addOnSuccessListener(documentReference -> {
                    docId[0] = documentReference.getId();
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await();
        return docId[0];
    }

    /**
     * Helper method to delete a test mood event document.
     */
    private void deleteTestMoodEvent(String docId) throws InterruptedException {
        if (docId == null) return;
        final CountDownLatch latch = new CountDownLatch(1);
        db.collection("moods").document(docId).delete()
                .addOnSuccessListener(aVoid -> latch.countDown())
                .addOnFailureListener(e -> latch.countDown());
        latch.await();
    }

    @After
    public void tearDown() throws InterruptedException {
        // Clean up the test document if it still exists.
        if (testDocId != null) {
            deleteTestMoodEvent(testDocId);
        }
    }

    /**
     * Test that the mood event data is loaded correctly into the UI.
     */
    @Test
    public void testLoadMoodEventData() throws InterruptedException {
        testDocId = createTestMoodEvent();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditMoodEventActivity.class);
        intent.putExtra("moodEventId", testDocId);

        ActivityScenario<EditMoodEventActivity> scenario = ActivityScenario.launch(intent);

        // Wait for the asynchronous Firestore load.
        SystemClock.sleep(2000);

        // Verify that the trigger and description fields show the expected text.
        onView(withId(R.id.mood_trigger_input)).check(matches(withText("Test trigger")));
        onView(withId(R.id.mood_description_input)).check(matches(withText("Test description")));

        scenario.close();
    }

    /**
     * Test that clicking the cancel button finishes the activity.
     */
    @Test
    public void testCancelButtonFinishesActivity() throws InterruptedException {
        testDocId = createTestMoodEvent();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditMoodEventActivity.class);
        intent.putExtra("moodEventId", testDocId);

        ActivityScenario<EditMoodEventActivity> scenario = ActivityScenario.launch(intent);
        SystemClock.sleep(2000);

        onView(withId(R.id.cancel_button)).perform(click());

        // Allow the activity to finish.
        SystemClock.sleep(500);
        assertTrue("Activity should be destroyed after cancel button click",
                scenario.getState() == Lifecycle.State.DESTROYED);
    }

    /**
     * Test that updating a mood event (via the save button) updates Firestore and finishes the activity.
     */
    @Test
    public void testSaveMoodEvent() throws InterruptedException {
        testDocId = createTestMoodEvent();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditMoodEventActivity.class);
        intent.putExtra("moodEventId", testDocId);

        ActivityScenario<EditMoodEventActivity> scenario = ActivityScenario.launch(intent);
        SystemClock.sleep(2000);

        // Update the trigger and description fields.
        onView(withId(R.id.mood_trigger_input))
                .perform(clearText(), typeText("Updated trigger"), closeSoftKeyboard());
        onView(withId(R.id.mood_description_input))
                .perform(clearText(), typeText("Updated description"), closeSoftKeyboard());

        // Click the save button.
        onView(withId(R.id.save_button)).perform(click());

        // Wait for the update to complete and for the activity to finish.
        SystemClock.sleep(3000);
        assertTrue("Activity should be destroyed after saving mood event",
                scenario.getState() == Lifecycle.State.DESTROYED);

        // Verify the Firestore document was updated.
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] updatedTrigger = new String[1];
        db.collection("moods").document(testDocId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        updatedTrigger[0] = documentSnapshot.getString("trigger");
                    }
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await();
        assertEquals("Updated trigger", updatedTrigger[0]);
    }

    /**
     * Test that deleting a mood event removes it from Firestore and finishes the activity.
     */
    @Test
    public void testDeleteMoodEvent() throws InterruptedException {
        testDocId = createTestMoodEvent();
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EditMoodEventActivity.class);
        intent.putExtra("moodEventId", testDocId);

        ActivityScenario<EditMoodEventActivity> scenario = ActivityScenario.launch(intent);
        SystemClock.sleep(2000);

        // Click the delete button to show the confirmation dialog.
        onView(withId(R.id.delete_button)).perform(click());
        SystemClock.sleep(500);

        // Confirm deletion by clicking "Delete" in the AlertDialog.
        onView(withText("Delete")).perform(click());

        // Wait for the deletion to complete and for the activity to finish.
        SystemClock.sleep(3000);
        assertTrue("Activity should be destroyed after deleting mood event",
                scenario.getState() == Lifecycle.State.DESTROYED);

        // Verify that the document has been deleted.
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] exists = new boolean[1];
        db.collection("moods").document(testDocId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    exists[0] = documentSnapshot.exists();
                    latch.countDown();
                })
                .addOnFailureListener(e -> latch.countDown());
        latch.await();
        assertTrue("Document should be deleted", !exists[0]);
    }
}
