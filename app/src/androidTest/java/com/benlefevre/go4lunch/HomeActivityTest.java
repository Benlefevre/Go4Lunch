package com.benlefevre.go4lunch;

import android.content.Intent;
import android.view.Gravity;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import com.benlefevre.go4lunch.controllers.activities.HomeActivity;
import com.benlefevre.go4lunch.utils.Constants;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.schibsted.spain.barista.assertion.BaristaAssertions.assertThatBackButtonClosesTheApp;
import static com.schibsted.spain.barista.assertion.BaristaDrawerAssertions.assertDrawerIsClosedWithGravity;
import static com.schibsted.spain.barista.assertion.BaristaDrawerAssertions.assertDrawerIsOpenWithGravity;
import static com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickBack;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDrawerInteractions.openDrawer;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    private UiDevice mUiDevice;

    @Rule
    public ActivityScenarioRule<HomeActivity> mHomeActivityActivityScenarioRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setUp(){
        mUiDevice = UiDevice.getInstance(getInstrumentation());
        Intents.init();
    }

    @After
    public void releaseDatas(){
        Intents.release();
    }

    @Test
    public void ToolbarTest(){
        assertDisplayed(R.id.home_activity_toolbar);
        assertDisplayed(R.id.toolbar_search);
        assertContains(R.string.hungry);
        clickOn(R.id.toolbar_search);
        assertDisplayed(R.id.autoCompleteTextView);
        assertDisplayed(R.id.home_activity_cleartext_btn);
    }

    @Test
    public void BottomBarAndDrawerTest(){
        assertDisplayed(R.id.home_activity_bottombar);
        assertDisplayed(R.string.map_view);
        onView(withId(R.id.bottom_map)).check(matches(isSelected()));
        assertDisplayed(R.string.restaurant_list);
        assertDisplayed(R.string.workmates);

        clickOn(R.id.bottom_restaurant);
        onView(withId(R.id.bottom_restaurant)).check(matches(isSelected()));
        assertContains(R.string.hungry);

        clickOn(R.id.bottom_workmates);
        onView(withId(R.id.bottom_workmates)).check(matches(isSelected()));
        assertContains(R.string.available_workmates);

        openDrawer();
        assertDrawerIsOpenWithGravity(Gravity.START);
        assertDisplayed(R.string.your_lunch);
        assertDisplayed(R.string.settings);
        assertDisplayed(R.string.logout);
        clickBack();
        assertDrawerIsClosedWithGravity(Gravity.START);
    }

    @Test
    public void AutocompleteTest() throws UiObjectNotFoundException {
//        clickOn(R.id.toolbar_search);
//        onView(withHint("Search")).perform(typeText("Partie de Campagne"));
//        sleep(500);
//        assertContains("Cour Saint-Emilion, Paris, France");
//        clickOn("Cour Saint-Emilion, Paris, France");
//
//        UiObject marker = mUiDevice.findObject(new UiSelector().descriptionContains("Partie de Campagne"));
//        marker.click();
//        marker.click();
//        intended(hasExtra(Constants.RESTAURANT_NAME,"Partie de Campagne"));
//        Intents.release();
//        Intents.init();
//        clickBack();
//        assertContains(R.string.hungry);
//
//        clickOn(R.id.bottom_restaurant);
//        clickOn(R.id.toolbar_search);
//        onView(withHint("Search")).perform(typeText("Partie de Campagne"));
//        sleep(500);
//        clickOn("Cour Saint-Emilion, Paris, France");
//
//        assertListItemCount(R.id.recycler_fragment_recyclerview,1);
//        clickListItem(R.id.recycler_fragment_recyclerview,0);
//        intended(hasExtra(Constants.RESTAURANT_NAME,"Partie de Campagne"));
//        clickBack();
//        assertContains(R.string.hungry);
    }
}
