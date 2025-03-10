package com.example.vibecheck;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.vibecheck.NetworkStatusChecker;

public class MainActivity extends AppCompatActivity {

    private NetworkStatusChecker networkChecker;
    private Button signupButton;
    private Button loginButton;
    private String username;
    private String password;
    private String confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        networkChecker = new NetworkStatusChecker(this);
        networkChecker.startChecking();

        TextView goToSignup = findViewById(R.id.link_to_signup);
        goToSignup.setOnClickListener( g -> {
            setContentView(R.layout.signup);

            signupButton = findViewById(R.id.signup_button);
            signupButton.setOnClickListener( s -> {
                if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    throw new IllegalArgumentException("Must fill all of the fields!");
                }
                else if (!(password.equals(confirmPassword))) {
                    throw new IllegalArgumentException("Password and confirm password must match!");
                } else {
                    setContentView(R.layout.activity_main);
                }
            });

            EditText editSignupUsername = findViewById(R.id.edit_text_signup_username);
            EditText editSignupPassword = findViewById(R.id.edit_text_signup_password);
            EditText editConfirmPassword = findViewById(R.id.edit_text_confirm_password);
            username = editSignupUsername.getText().toString();
            password = editSignupPassword.getText().toString();
            confirmPassword = editConfirmPassword.getText().toString();
        });

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener( l -> {
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                throw new IllegalArgumentException("Must fill all of the fields!");
            }
            else if (!(password.equals(confirmPassword))) {
                throw new IllegalArgumentException("Password and confirm password must match!");
            } else {
                setContentView(R.layout.activity_main);
            }
        });

        EditText editUsername = findViewById(R.id.edit_text_login_username);
        EditText editPassword = findViewById(R.id.edit_text_login_password);
        username = editUsername.getText().toString();
        password = editPassword.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkChecker.stopChecking();
    }
}
