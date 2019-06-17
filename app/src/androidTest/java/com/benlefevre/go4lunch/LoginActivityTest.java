package com.benlefevre.go4lunch;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.benlefevre.go4lunch.controllers.activities.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.schibsted.spain.barista.assertion.BaristaBackgroundAssertions.assertHasBackground;
import static com.schibsted.spain.barista.assertion.BaristaImageViewAssertions.assertHasDrawable;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mLoginActivityActivityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void loginActivityUITest(){
        assertHasBackground(R.id.login_container,R.drawable.backgo4lunch);
        assertHasDrawable(R.id.logo,R.drawable.logo);
        assertDisplayed(R.id.login_mail_btn);
        assertDisplayed(R.id.login_google_btn);
        assertDisplayed(R.id.login_facebook_btn);
        assertDisplayed(R.id.login_twitter_btn);
    }
}
