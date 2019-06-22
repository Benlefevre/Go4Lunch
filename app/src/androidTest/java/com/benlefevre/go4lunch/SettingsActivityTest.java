package com.benlefevre.go4lunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.preference.PreferenceManager;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.benlefevre.go4lunch.controllers.activities.SettingsActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDialogInteractions.clickDialogNegativeButton;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SettingsActivityTest {

    private Context mContext;
    private View mDecorView;
    private SharedPreferences mSharedPreferences;

    @Before
    public void setUp(){
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mSharedPreferences.edit().putBoolean(mContext.getString(R.string.notif_pref),true).apply();
    }

    @Test
    public void settingsActivityUiTest(){
        ActivityScenario.launch(SettingsActivity.class).onActivity(activity -> mDecorView = activity.getWindow().getDecorView());

        assertDisplayed(R.string.notifications);
        assertDisplayed(R.string.enable_notif);
        assertDisplayed(R.string.notif_summary);
        assertDisplayed(R.string.user_account);
        assertDisplayed(R.string.delete_logout);
        assertDisplayed(R.string.here_to_delete);

        clickOn(R.string.enable_notif);
        assertFalse(mSharedPreferences.getBoolean(mContext.getString(R.string.notif_pref),true));
        clickOn(R.string.enable_notif);
        assertTrue(mSharedPreferences.getBoolean(mContext.getString(R.string.notif_pref),false));
        clickOn(R.string.here_to_delete);
        onView(withText(R.string.are_you_sure_delete)).inRoot(withDecorView(not(mDecorView))).check(matches(isDisplayed()));
        clickDialogNegativeButton();

    }
}
