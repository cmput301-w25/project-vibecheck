package com.example.vibecheck.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vibecheck.MoodUtils;
import com.example.vibecheck.R;
import com.example.vibecheck.ui.home.HomeActivity;
import com.example.vibecheck.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private EditText personalInfoEditText;
    private TextView tvDisplayName, tvUsername;
    private ImageView backArrow;
    private Button logoutButton;

    
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

       
        View rootView = findViewById(android.R.id.content);
        
        ScrollView scrollView = (ScrollView) ((ViewGroup) rootView).getChildAt(0);
     
        ViewGroup mainLinearLayout = (ViewGroup) scrollView.getChildAt(0);

        
        ViewGroup profileHeader = (ViewGroup) mainLinearLayout.getChildAt(0);
        backArrow = (ImageView) profileHeader.getChildAt(0);
        tvDisplayName = (TextView) profileHeader.getChildAt(1);
        tvUsername = (TextView) profileHeader.getChildAt(2);

      
        personalInfoEditText = (EditText) mainLinearLayout.getChildAt(3);

        logoutButton = (Button) mainLinearLayout.getChildAt(6);

        loadProfileData();

        backArrow.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
            finish();
        });

        logoutButton.setOnClickListener(view -> {
            MoodUtils.setCurrentUsername(null);
            MoodUtils.clearUserMoodHistory();
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

   
    private void loadProfileData() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            
                            String displayName = documentSnapshot.getString("displayName");
                            if (displayName != null && !displayName.isEmpty()) {
                                tvDisplayName.setText(displayName);
                                tvUsername.setText("@" + MoodUtils.getCurrentUsername());
                            } else {
                                tvDisplayName.setText("No Display Name");
                                tvUsername.setText("@NoDisplayName");
                            }
                            
                            String personalInfo = documentSnapshot.getString("personalInfo");
                            if (personalInfo != null) {
                                personalInfoEditText.setText(personalInfo);
                            }
                        } else {
                            tvDisplayName.setText("No Document");
                            tvUsername.setText("@NoDocument");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ProfileActivity.this,
                                "Failed to load profile data: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            tvDisplayName.setText("Not Logged In");
            tvUsername.setText("@NotLoggedIn");
        }
    }

    
    private void savePersonalInfo() {
        if (currentUser != null) {
            String uid = currentUser.getUid();
            String personalInfo = personalInfoEditText.getText().toString().trim();
            db.collection("users")
                    .document(uid)
                    .update("personalInfo", personalInfo)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(ProfileActivity.this, "Personal info saved", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(ProfileActivity.this, "Failed to save personal info: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }

  
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
           
            int[] location = new int[2];
            backArrow.getLocationOnScreen(location);
            float x = ev.getRawX();
            float y = ev.getRawY();
            
            if (!(x >= location[0] && x <= location[0] + backArrow.getWidth() &&
                    y >= location[1] && y <= location[1] + backArrow.getHeight())) {
                savePersonalInfo();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}
