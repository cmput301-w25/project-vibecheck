package com.example.vibecheck;

import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;

import android.os.Build;

import com.example.vibecheck.ui.moodevents.AddMoodEventActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddMoodEventActivityTest {
    private AddMoodEventActivity activity;
}

/*
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.R)
public class AddMoodEventActivityTest {

    @Mock
    private FirebaseAuth mockAuth;
    @Mock
    private FirebaseUser mockUser;
    @Mock
    private FirebaseFirestore mockFirestore;

    private AddMoodEventActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        activity = Robolectric.buildActivity(AddMoodEventActivity.class)
                .create()
                .resume()
                .get();

        activity.setAuth(mockAuth);
        activity.setDb(mockFirestore);
    }
}

 */