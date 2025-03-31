/**
 * This is the main activity of the application, although it is only used when no user is logged in.
 * It is the first activity that is launched when the app is opened, it launches the login activity which is the first
 * screen the user sees upon opening the app if no user is logged in. It is responsible for handling navigation to the signup activity.
 *
 * This class has no outstanding issues
 */

package com.example.vibecheck;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vibecheck.ui.home.HomeActivity;
import com.example.vibecheck.ui.login.LoginActivity;
import com.example.vibecheck.ui.signup.SignUpActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private NetworkStatusChecker networkChecker;
    private Button loginButton;
    private EditText editUsername;
    private EditText editPassword;
    private FirebaseAuth mAuth;

    /**
     * Called when the activity is first created, sets the content view and network checker.
     * Handles sign-up navigation and launches the login activity.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        networkChecker = new NetworkStatusChecker(this);
        networkChecker.startChecking();

        // Navigate to SignupActivity when signup link is clicked
        TextView goToSignup = findViewById(R.id.link_to_signup);
        goToSignup.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        // Start home activity if current user is logged in, login activity if not
        //Oliver forgot about this when he added it during branch merging in part 3, not fully implemented
        //but leaving it as-is in case there's time to do the app restructuring necessary for it.
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        finish();
    }

    /**
     * Stops the network checker when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkChecker.stopChecking();

        //Prevent memory leaks
        networkChecker = null;
    }
}