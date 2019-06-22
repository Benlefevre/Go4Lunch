package com.benlefevre.go4lunch;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.benlefevre.go4lunch.controllers.activities.LoginActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.benlefevre.go4lunch.utils.Constants.IS_LOGGED;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground;
import static com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Before
    public void setUp() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().remove(IS_LOGGED).apply();
    }


    @Test
    public void loginActivityUITest() {
        ActivityScenario.launch(LoginActivity.class);
        assertHasBackground(R.id.login_container, R.drawable.backgo4lunch);
        assertHasDrawable(R.id.logo, R.drawable.logo);
        assertDisplayed(R.id.login_mail_btn);
        assertDisplayed(R.id.login_google_btn);
        assertDisplayed(R.id.login_facebook_btn);
        assertDisplayed(R.id.login_twitter_btn);
    }
}
