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
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkChecker.stopChecking();

        //Prevent memory leaks
        networkChecker = null;
    }
}
