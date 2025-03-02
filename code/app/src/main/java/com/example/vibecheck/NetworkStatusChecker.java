package com.example.vibecheck;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * Monitors network status and only notifies when it changes.
 */
public class NetworkStatusChecker {
    private final Context appContext;
    private final Handler mainHandler;
    private Runnable networkCheckTask;
    private static final long INTERVAL_MS = 10000; // 10 seconds
    private Boolean lastConnectionState = null; // Null at start to force first-time message

    public NetworkStatusChecker(Context context) {
        this.appContext = context.getApplicationContext();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }

    public void startChecking() {
        networkCheckTask = new Runnable() {
            @Override
            public void run() {
                boolean isConnected = checkNetworkAvailability();

                // Show message at least once when app starts
                if (lastConnectionState == null || isConnected != lastConnectionState) {
                    showNetworkToast(isConnected);
                    lastConnectionState = isConnected;
                }

                mainHandler.postDelayed(this, INTERVAL_MS);
            }
        };
        mainHandler.post(networkCheckTask);
    }

    public void stopChecking() {
        if (networkCheckTask != null) {
            mainHandler.removeCallbacks(networkCheckTask);
        }
    }

    private boolean checkNetworkAvailability() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) return false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork == null) return false;
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(activeNetwork);
            return networkCapabilities != null &&
                    (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
        } else {
            android.net.NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
    }

    private void showNetworkToast(boolean isConnected) {
        String statusMessage = isConnected ? "Connected to the Internet" : "No Internet Connection";
        Toast.makeText(appContext, statusMessage, Toast.LENGTH_SHORT).show();
    }
}
