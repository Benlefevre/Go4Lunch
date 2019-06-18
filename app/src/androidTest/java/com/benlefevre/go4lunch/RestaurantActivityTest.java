package com.benlefevre.go4lunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;

import com.benlefevre.go4lunch.controllers.activities.RestaurantActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasData;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_ADDRESS;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_ID;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;
import static com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;


@RunWith(AndroidJUnit4.class)
public class RestaurantActivityTest {

    private SharedPreferences mSharedPreferences;
    private View mDecorView;
    private Context mContext;
    private UiDevice mUiDevice;

    @Before
    public void setUp(){
        mUiDevice = UiDevice.getInstance(getInstrumentation());
        mContext = getInstrumentation().getTargetContext();
        Intent intent = new Intent(mContext,RestaurantActivity.class);
        intent.putExtra(RESTAURANT_NAME,"Partie de Campagne");
        ActivityScenario<RestaurantActivity> activityScenario = ActivityScenario.launch(intent);
        activityScenario.onActivity(activity -> {
            mSharedPreferences = mContext.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
            mDecorView = activity.getWindow().getDecorView();
            mSharedPreferences.edit().remove("Partie de Campagne").apply();
        });
    }

    @Test
    public void RestaurantActivityUiTest(){
        assertDisplayed(R.id.activity_restaurant_img);
        assertDisplayed(R.id.activity_restaurant_name_txt);
        assertDisplayed(R.id.activity_restaurant_address_txt);
        assertDisplayed(R.id.activity_restaurant_floating_button);
        assertDisplayed(R.id.activity_restaurant_call_img);
        assertDisplayed(R.id.activity_restaurant_like_img);
        assertDisplayed(R.id.activity_restaurant_web_img);
        sleep(2000);
        assertContains(R.id.activity_restaurant_name_txt,"Partie de Campagne");
        assertContains(R.id.activity_restaurant_address_txt,"36 Cour Saint-Emilion");

        clickOn(R.id.activity_restaurant_floating_button);
        assertEquals("Partie de Campagne",mSharedPreferences.getString(CHOSEN_RESTAURANT_NAME,""));
        assertEquals("ChIJgQKYmj9y5kcRaWWyHyK298A",mSharedPreferences.getString(CHOSEN_RESTAURANT_ID,""));
        assertEquals("36 Cour Saint-Emilion",mSharedPreferences.getString(CHOSEN_RESTAURANT_ADDRESS,""));

        assertListItemCount(R.id.recycler_fragment_recyclerview,1);

        clickOn(R.id.activity_restaurant_floating_button);
        assertEquals("",mSharedPreferences.getString(CHOSEN_RESTAURANT_NAME,""));
        assertEquals("",mSharedPreferences.getString(CHOSEN_RESTAURANT_ID,""));
        assertEquals("",mSharedPreferences.getString(CHOSEN_RESTAURANT_ADDRESS,""));

        assertListItemCount(R.id.recycler_fragment_recyclerview,0);

        clickOn(R.id.activity_restaurant_like_img);
        onView(withText(mContext.getString(R.string.you_like,"Partie de Campagne"))).inRoot(withDecorView(is(not(mDecorView)))).check(matches(isDisplayed()));

        clickOn(R.id.activity_restaurant_like_img);
        onView(withText(mContext.getString(R.string.already_like,"Partie de Campagne"))).inRoot(withDecorView(not(mDecorView))).check(matches(isDisplayed()));

        Intents.init();
        clickOn(R.id.activity_restaurant_call_img);
        intended(hasAction(Intent.ACTION_DIAL));
        intended(hasData("tel:+33 1 43 40 44 11"));
        mUiDevice.pressBack();
        mUiDevice.pressBack();
        mUiDevice.pressBack();

        clickOn(R.id.activity_restaurant_web_img);
        intended(hasAction(Intent.ACTION_VIEW));
        intended(hasData("http://www.partiedecampagne.com/"));
        mUiDevice.pressBack();
    }
}
