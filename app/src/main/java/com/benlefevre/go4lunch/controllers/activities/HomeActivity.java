package com.benlefevre.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import com.benlefevre.go4lunch.BuildConfig;
import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.UserHelper;
import com.benlefevre.go4lunch.controllers.fragments.MapViewFragment;
import com.benlefevre.go4lunch.controllers.fragments.RecyclerViewFragment;
import com.benlefevre.go4lunch.models.User;
import com.benlefevre.go4lunch.utils.UtilsUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.IS_LOGGED;
import static com.benlefevre.go4lunch.utils.Constants.LAT_NORTH;
import static com.benlefevre.go4lunch.utils.Constants.LAT_SOUTH;
import static com.benlefevre.go4lunch.utils.Constants.LONG_NORTH;
import static com.benlefevre.go4lunch.utils.Constants.LONG_SOUTH;
import static com.benlefevre.go4lunch.utils.Constants.MAP;
import static com.benlefevre.go4lunch.utils.Constants.MAPVIEW;
import static com.benlefevre.go4lunch.utils.Constants.MESS_TOKEN;
import static com.benlefevre.go4lunch.utils.Constants.MESS_TOKEN_CHANGED;
import static com.benlefevre.go4lunch.utils.Constants.PERMISSIONS_REQUEST_ACCESS_LOCATION;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_FRAGMENT;
import static com.benlefevre.go4lunch.utils.Constants.RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.USER_NAME;
import static com.benlefevre.go4lunch.utils.Constants.WORKMATES;
import static com.benlefevre.go4lunch.utils.Constants.WORKMATE_FRAGMENT;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener, MapViewFragment.OnFragmentInteractionListener {

    @BindView(R.id.home_activity_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.home_activity_frame_layout)
    FrameLayout mFrameLayout;
    @BindView(R.id.home_activity_bottombar)
    BottomNavigationView mBottomNav;
    @BindView(R.id.home_activity_nav_view)
    NavigationView mNavView;
    @BindView(R.id.home_activity_drawer)
    DrawerLayout mDrawer;
    @BindView(R.id.home_activity_auto_complete_Txt)
    AutoCompleteTextView mAutoCompleteTextView;
    @BindView(R.id.home_activity_cleartext_btn)
    ImageButton mCleartextBtn;
    @BindView(R.id.home_activity_autocomplete_layout)
    FrameLayout mAutocompleteLayout;

    private FragmentManager mFragmentManager;
    private SharedPreferences mSharedPreferences;

    private boolean mLocationPermissionGranted = false;
    private List<String> mIdList;
    private int displayedFragment;
    private PlacesClient mClient;
    private ArrayAdapter<String> mArrayAdapter;
    private RectangularBounds mRectangularBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initFields();
        updateMessagingTokenIfItChanges();
        getLocationPermission();
        initUi();
        resetUserRestaurantChoice();
    }

    /**
     * Verifies if the user's messenging token has changed and if it is sends the new token in Firestore.
     */
    private void updateMessagingTokenIfItChanges() {
        if (mSharedPreferences.getBoolean(MESS_TOKEN_CHANGED, false)) {
            String token = mSharedPreferences.getString(MESS_TOKEN, "");
            UserHelper.updateUserMessagingToken(getCurrentUser().getUid(), token);
            mSharedPreferences.edit().putBoolean(MESS_TOKEN_CHANGED, false).apply();
        }
    }

    /**
     * Sets an Autocomplete.IntentBuilder with constrains to fetch an Autocomplete widget.
     *
     * @param item The clicked item
     */
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        if (item.getItemId() == R.id.toolbar_search && displayedFragment != 3) {
            mAutocompleteLayout.setVisibility(View.VISIBLE);
            configureRestrictionsBounds();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Defines the geographical boundaries of the prediction search
     */
    private void configureRestrictionsBounds() {
        LatLng south = new LatLng(mSharedPreferences.getFloat(LAT_SOUTH, (float) 48.8322317248286),
                mSharedPreferences.getFloat(LONG_SOUTH, (float) 2.3856264352798457));
        LatLng north = new LatLng(mSharedPreferences.getFloat(LAT_NORTH, (float) 48.834164806748554),
                mSharedPreferences.getFloat(LONG_NORTH, (float) 2.387833558022976));
        mRectangularBounds = RectangularBounds.newInstance(south, north);
    }


    /**
     * Resets users fields if their restaurant's choice's date is before yesterday at 12:00 PM
     */
    private void resetUserRestaurantChoice() {
        UserHelper.getUsersCollection().get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    User user = document.toObject(User.class);
                    if (user != null && user.getChoiceDate() != null &&
                            !UtilsUser.compareDate(user.getChoiceDate()))
                        UserHelper.updateUserChosenRestaurant(user.getUid(), null,
                                null, null);
                }
            }
        });
    }

    /**
     * Init all needed HomeActivity's fields
     */
    private void initFields() {
        mSharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        mFragmentManager = getSupportFragmentManager();
        Places.initialize(this, BuildConfig.google_maps_key);
        mClient = Places.createClient(this);
    }

    /**
     * Calls all methods needed to update the ui.
     */
    private void initUi() {
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNav();
        updateUiNavHeader();
        configureAutoCompleteUI();
    }

    /**
     * Configures AutoComplete UI and behaviors
     */
    private void configureAutoCompleteUI() {
        mArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        mAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bindAutoCompletePrediction(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mAutoCompleteTextView.setAdapter(mArrayAdapter);
        mAutoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) ->
                updateUiAccordingToTheSelectedItem(adapterView.getItemAtPosition(i).toString()));

    }

    /**
     * Defines the expected behavior when the user clicks the clear text button.
     * Either to close the autocomplete or delete the text
     */
    @OnClick(R.id.home_activity_cleartext_btn)
    public void onViewClicked() {
        if (mAutoCompleteTextView.getText().length() > 0)
            mAutoCompleteTextView.setText("");
        else {
            mAutocompleteLayout.setVisibility(View.INVISIBLE);
            UIUtil.hideKeyboard(this);
        }
    }

    /**
     * Defines the UI behaviors according to the displayed fragment and the user's choice
     *
     * @param selectedItem the restaurant's name of the user's choice
     */
    private void updateUiAccordingToTheSelectedItem(String selectedItem) {
        switch (displayedFragment) {
            case 1:
                MapViewFragment mapViewFragment = (MapViewFragment) mFragmentManager.findFragmentByTag(MAPVIEW);
                if (mapViewFragment != null) {
                    mAutoCompleteTextView.setText("");
                    mAutocompleteLayout.setVisibility(View.INVISIBLE);
                    UIUtil.hideKeyboard(this);
                    mapViewFragment.moveCameraToSelectedRestaurant(selectedItem);
                }
                break;
            case 2:
                RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) mFragmentManager.findFragmentByTag(RESTAURANT_FRAGMENT);
                if (recyclerViewFragment != null) {
                    mAutoCompleteTextView.setText("");
                    mAutocompleteLayout.setVisibility(View.INVISIBLE);
                    UIUtil.hideKeyboard(this);
                    recyclerViewFragment.showSelectedRestaurant(selectedItem);
                }
        }
    }

    /**
     * Fetches the Google predictions from the user's input and adds them into the adapter list.
     *
     * @param userInput the user input
     */
    private void bindAutoCompletePrediction(CharSequence userInput) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setCountry("fr")
                .setLocationRestriction(mRectangularBounds)
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setQuery(userInput.toString().toLowerCase())
                .build();

        mClient.findAutocompletePredictions(request).addOnSuccessListener(predictionsResponse -> {
            if (!predictionsResponse.getAutocompletePredictions().isEmpty()) {
                for (AutocompletePrediction prediction : predictionsResponse.getAutocompletePredictions()) {
                    if ( mIdList.contains(prediction.getPlaceId())) {
                        mArrayAdapter.clear();
                        mArrayAdapter.add(prediction.getPrimaryText(null).toString());
                        mArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_activity_toolbar_menu, menu);
        return true;
    }

    /**
     * Loads current user's information into the header's views in the navigation drawer
     */
    private void updateUiNavHeader() {
        View headerView = mNavView.inflateHeaderView(R.layout.home_activity_nav_header);
        AppCompatImageView userPhoto = headerView.findViewById(R.id.nav_user_img);
        TextView userName = headerView.findViewById(R.id.nav_user_name);
        TextView userMail = headerView.findViewById(R.id.nav_user_mail);
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser.getPhotoUrl() != null)
            Glide.with(this).load(currentUser.getPhotoUrl()).apply(RequestOptions.circleCropTransform()).into(userPhoto);
        else
            userPhoto.setImageResource(R.drawable.ic_person_24dp);
        if (currentUser.getDisplayName() != null) {
            userName.setText(currentUser.getDisplayName());
            mSharedPreferences.edit().putString(USER_NAME, currentUser.getDisplayName()).apply();
        }
        if (currentUser.getEmail() != null)
            userMail.setText(currentUser.getEmail());
    }

    /**
     * Sets the listener to define actions when the user click on an item in the bottom navigation view.
     */
    private void configureBottomNav() {
        mBottomNav.setOnNavigationItemSelectedListener(this);
    }

    /**
     * Sets the listener to define actions when the user click on an item in the navigation view.
     */
    private void configureNavigationView() {
        mNavView.setNavigationItemSelectedListener(this);
    }

    /**
     * Configures the toggle button into mToolbar to open or close the navigation drawer.
     */
    private void configureDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                R.string.open_drawer, R.string.close_drawer);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Defines our toolbar as the default ActionBar
     */
    private void configureToolbar() {
        mToolbar.setTitle(getString(R.string.hungry));
        setSupportActionBar(mToolbar);
    }

    /**
     * Defines an action according to the selected item in navigation drawer or bottom navigation.
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.drawer_logout:
                signOutFromFirebase();
                mSharedPreferences.edit().remove(IS_LOGGED).apply();
                break;
            case R.id.drawer_your_lunch:
                getUserChosenRestaurant();
                break;
            case R.id.drawer_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.bottom_map:
                mToolbar.setTitle(R.string.hungry);
                displayFragmentAccordingToItemSelected(MAP);
                displayedFragment = 1;
                break;
            case R.id.bottom_restaurant:
                mToolbar.setTitle(R.string.hungry);
                displayFragmentAccordingToItemSelected(RESTAURANT);
                displayedFragment = 2;
                break;
            case R.id.bottom_workmates:
                mToolbar.setTitle(R.string.available_workmates);
                displayFragmentAccordingToItemSelected(WORKMATES);
                displayedFragment = 3;
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Verifies if the user has choose a restaurant and open the RestaurantActivity if he has.
     */
    private void getUserChosenRestaurant() {
        String chosenRestaurant = mSharedPreferences.getString(CHOSEN_RESTAURANT_NAME, null);
        if (chosenRestaurant != null) {
            Intent intent = new Intent(this, RestaurantActivity.class);
            intent.putExtra(RESTAURANT_NAME, chosenRestaurant);
            startActivity(intent);
        } else
            Toast.makeText(this, getString(R.string.no_chosen_resto), Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a fragment according to the selected item in bottom navigation.
     *
     * @param origin A constant String that represented the selected item
     */
    private void displayFragmentAccordingToItemSelected(String origin) {
        switch (origin) {
            case MAP:
                    mFragmentManager.beginTransaction().replace(R.id.home_activity_frame_layout, MapViewFragment.newInstance(mLocationPermissionGranted), MAPVIEW)
                            .commit();
                break;
            case RESTAURANT:
                    mFragmentManager.beginTransaction().replace(R.id.home_activity_frame_layout, RecyclerViewFragment.newInstance(RESTAURANT, mIdList), RESTAURANT_FRAGMENT)
                            .commit();
                break;
            case WORKMATES:
                    mFragmentManager.beginTransaction().replace(R.id.home_activity_frame_layout, RecyclerViewFragment.newInstance(WORKMATES, mIdList), WORKMATE_FRAGMENT)
                            .commit();
                break;
        }
    }

    /**
     * Checks if the application has the user's permissions to locate him and requests them if it's haven't.
     */
    private void getLocationPermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            mLocationPermissionGranted = true;
            showFirstFragment();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.why_location))
                        .setMessage(getString(R.string.without_location))
                        .setPositiveButton(getString(R.string.allow), (dialog, which) -> {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(HomeActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                    PERMISSIONS_REQUEST_ACCESS_LOCATION);
                        })
                        .setNegativeButton(getString(R.string.deny), (dialog, which) -> dialog.cancel())
                        .show();
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_LOCATION);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                showFirstFragment();
            } else {
                getLocationPermission();
            }
        }
    }

    /**
     * Load a MapViewFragment as first screen when the user arrives in HomeActivity.
     */
    private void showFirstFragment() {
        mFragmentManager.beginTransaction()
                .replace(R.id.home_activity_frame_layout, MapViewFragment.newInstance(mLocationPermissionGranted), MAPVIEW)
                .commit();
        displayedFragment = 1;
    }

    /**
     * Allows to close the navigation drawer or the autocomplete with the device's back button.
     */
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawer(GravityCompat.START);
        else if (mAutocompleteLayout.getVisibility() == View.VISIBLE) {
            mAutoCompleteTextView.setText("");
            mAutocompleteLayout.setVisibility(View.INVISIBLE);
        } else
            super.onBackPressed();
    }

    /**
     * Overrides the MapViewFragment's listener method to fetch the list of restaurant's ID
     * @param idList the list send from the MapViewFragment
     */
    @Override
    public void onFragmentInteraction(List<String> idList) {
        mIdList = new ArrayList<>();
        mIdList = idList;
    }

}
