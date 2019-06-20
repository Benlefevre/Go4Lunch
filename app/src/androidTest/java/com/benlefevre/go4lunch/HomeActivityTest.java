package com.benlefevre.go4lunch;

import android.view.Gravity;
import android.view.View;

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

import leakcanary.LeakSentry;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.benlefevre.go4lunch.utils.Constants.*;
import static com.schibsted.spain.barista.assertion.BaristaDrawerAssertions.assertDrawerIsClosedWithGravity;
import static com.schibsted.spain.barista.assertion.BaristaDrawerAssertions.assertDrawerIsOpenWithGravity;
import static com.schibsted.spain.barista.assertion.BaristaHintAssertions.assertHint;
import static com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertContains;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed;
import static com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed;
import static com.schibsted.spain.barista.interaction.BaristaAutoCompleteTextViewInteractions.writeToAutoComplete;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickBack;
import static com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn;
import static com.schibsted.spain.barista.interaction.BaristaDrawerInteractions.openDrawer;
import static com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem;
import static com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep;
import static com.schibsted.spain.barista.interaction.BaristaSwipeRefreshInteractions.refresh;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class HomeActivityTest {

    private UiDevice mUiDevice;
    private View mDecorView;

    @Rule
    public ActivityScenarioRule<HomeActivity> mHomeActivityActivityScenarioRule = new ActivityScenarioRule<>(HomeActivity.class);

    @Before
    public void setUp() {
        mHomeActivityActivityScenarioRule.getScenario().onActivity(activity -> mDecorView = activity.getWindow().getDecorView());
            Intents.init();
    }

    @After
    public void releaseData() {
        Intents.release();
        LeakSentry.INSTANCE.getRefWatcher().clearWatchedReferences();
    }

    @Test
    public void toolbarTest() {
        assertDisplayed(R.id.home_activity_toolbar);
        assertDisplayed(R.id.toolbar_search);
        assertContains(R.string.hungry);
        clickOn(R.id.toolbar_search);
        assertDisplayed(R.id.home_activity_auto_complete_Txt);
        assertDisplayed(R.id.home_activity_cleartext_btn);
        clickOn(R.id.home_activity_cleartext_btn);
        assertNotDisplayed(R.id.home_activity_autocomplete_layout);
        clickOn(R.id.toolbar_search);
        assertDisplayed(R.id.home_activity_autocomplete_layout);
        clickBack();
        assertNotDisplayed(R.id.home_activity_autocomplete_layout);
    }

    @Test
    public void bottomBarAndDrawerTest() {
        openDrawer();
        assertDrawerIsOpenWithGravity(Gravity.START);
        assertDisplayed(R.string.your_lunch);
        assertDisplayed(R.string.settings);
        assertDisplayed(R.string.logout);
        clickBack();
        assertDrawerIsClosedWithGravity(Gravity.START);

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

    }

    @Test
    public void autocompleteUiTest() {
        assertDisplayed(R.id.home_activity_toolbar);
        assertDisplayed(R.id.toolbar_search);
        assertContains(R.string.hungry);
        clickOn(R.id.toolbar_search);
        clickOn(R.id.home_activity_auto_complete_Txt);
        assertHint(R.id.home_activity_auto_complete_Txt, R.string.search_a_restaurant);
        writeToAutoComplete(R.id.home_activity_auto_complete_Txt, "fac");
        onView(withText("factory & co")).inRoot(withDecorView(not(mDecorView))).check(matches(isDisplayed()));
        clickOn(R.id.home_activity_cleartext_btn);
        assertHint(R.id.home_activity_auto_complete_Txt, R.string.search_a_restaurant);
        clickOn(R.id.home_activity_cleartext_btn);
        assertNotDisplayed(R.id.home_activity_auto_complete_Txt);
    }

    @Test
    public void autocompleteBehaviorTestMapFragment() throws UiObjectNotFoundException {
        mUiDevice = UiDevice.getInstance(getInstrumentation());
        assertDisplayed(R.id.home_activity_toolbar);
        assertDisplayed(R.id.toolbar_search);
        assertContains(R.string.hungry);
        clickOn(R.id.toolbar_search);
        clickOn(R.id.home_activity_auto_complete_Txt);
        assertHint(R.id.home_activity_auto_complete_Txt, R.string.search_a_restaurant);
        writeToAutoComplete(R.id.home_activity_auto_complete_Txt, "eric");
        onView(withText("Eric Kayser - Bercy Village")).inRoot(withDecorView(not(mDecorView))).perform(click());
        UiObject marker = mUiDevice.findObject(new UiSelector().descriptionContains("Eric Kayser - Bercy Village"));
        marker.click();
        marker.click();
        intended(hasExtra(RESTAURANT_NAME, "Eric Kayser - Bercy Village"));
        intended(hasExtra(RESTAURANT_ID,"ChIJ92xQhT9y5kcRncxLxZXgMgY"));
    }

    @Test
    public void autocompleteBehaviorTestRecyclerViewFragment(){
        assertDisplayed(R.id.home_activity_toolbar);
        assertDisplayed(R.id.toolbar_search);
        assertContains(R.string.hungry);
        sleep(1000);
        clickOn(R.id.bottom_restaurant);
        clickOn(R.id.toolbar_search);
        clickOn(R.id.home_activity_auto_complete_Txt);
        writeToAutoComplete(R.id.home_activity_auto_complete_Txt,"prad");
        onView(withText("Maison Pradier")).inRoot(withDecorView(not(mDecorView))).perform(click());
        assertListItemCount(R.id.recycler_fragment_recyclerview,1);
        clickListItem(R.id.recycler_fragment_recyclerview,0);
        intended(hasExtra(RESTAURANT_NAME,"Maison Pradier"));
        intended(hasExtra(RESTAURANT_ID,"ChIJoTOBnD9y5kcRkHqdBW-Wa5k"));
        clickBack();
        refresh();
    }
}
