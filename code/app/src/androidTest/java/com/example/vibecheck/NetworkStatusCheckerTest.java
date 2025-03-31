package com.example.vibecheck;

import android.content.Context;
import android.os.SystemClock;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class NetworkStatusCheckerTest {

    private TestNetworkStatusChecker testChecker;
    private Context context;

    /**
     * Test subclass to simulate network connectivity and capture toast messages.
     */
    public static class TestNetworkStatusChecker extends NetworkStatusChecker {
        private boolean fakeState;
        public String lastToastMessage;

        public TestNetworkStatusChecker(Context context, boolean initialState) {
            super(context);
            fakeState = initialState;
        }

        public void setFakeNetworkState(boolean state) {
            fakeState = state;
        }

        @Override
        protected boolean checkNetworkAvailability() {
            return fakeState;
        }

        @Override
        protected void showNetworkToast(boolean isConnected) {
            // Instead of showing an actual toast, record the message.
            lastToastMessage = isConnected ? "Connected to the Internet" : "No Internet Connection";
        }
    }

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // Start with a "connected" fake state.
        testChecker = new TestNetworkStatusChecker(context, true);
    }

    @After
    public void tearDown() {
        testChecker.stopChecking();
    }

    /**
     * Test that when the network state changes, the appropriate toast message is recorded.
     * We use reflection to retrieve the private runnable so we can invoke it manually.
     */
    @Test
    public void testNetworkStatusChangeDisplaysToast() throws Exception {
        // Start checking network status.
        testChecker.startChecking();

        // Use reflection to obtain the networkCheckTask runnable.
        Field field = NetworkStatusChecker.class.getDeclaredField("networkCheckTask");
        field.setAccessible(true);
        Runnable networkTask = (Runnable) field.get(testChecker);

        // First run: initial fake state is true; since lastConnectionState is null,
        // it should record a toast for connectivity.
        networkTask.run();
        assertEquals("Connected to the Internet", testChecker.lastToastMessage);

        // Simulate a state change: now set network state to false.
        testChecker.setFakeNetworkState(false);
        networkTask.run();
        assertEquals("No Internet Connection", testChecker.lastToastMessage);

        // Run again without any state change; no new toast should be recorded.
        testChecker.lastToastMessage = null; // Reset for clarity.
        networkTask.run();
        // Since state did not change, lastToastMessage remains null.
        assertEquals(null, testChecker.lastToastMessage);
    }
}
