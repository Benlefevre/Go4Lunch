package com.benlefevre.go4lunch.controllers.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
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

import com.benlefevre.go4lunch.R;
import com.benlefevre.go4lunch.api.UserHelper;
import com.benlefevre.go4lunch.controllers.fragments.MapViewFragment;
import com.benlefevre.go4lunch.controllers.fragments.RecyclerViewFragment;
import com.benlefevre.go4lunch.models.User;
import com.benlefevre.go4lunch.utils.UtilsUser;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.benlefevre.go4lunch.utils.Constants.AUTOCOMPLETE_REQUEST;
import static com.benlefevre.go4lunch.utils.Constants.CHOSEN_RESTAURANT_NAME;
import static com.benlefevre.go4lunch.utils.Constants.LAT_NORTH;
import static com.benlefevre.go4lunch.utils.Constants.LAT_SOUTH;
import static com.benlefevre.go4lunch.utils.Constants.LONG_NORTH;
import static com.benlefevre.go4lunch.utils.Constants.LONG_SOUTH;
import static com.benlefevre.go4lunch.utils.Constants.MAP;
import static com.benlefevre.go4lunch.utils.Constants.MAPVIEW;
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

    private FragmentManager mFragmentManager;
    private SharedPreferences mSharedPreferences;

    private boolean mLocationPermissionGranted = false;
    private List<String> mIdList;
    private int displayedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mSharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        mFragmentManager = getSupportFragmentManager();
        getLocationPermission();
        initUi();
        resetUserRestaurantChoice();
    }

    /**
     * Sets an Autocomplete.IntentBuilder with constrains to fetch an Autocomplete widget.
     *
     * @param item The clicked item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        LatLng north = new LatLng(mSharedPreferences.getFloat(LAT_NORTH, (float) 48.834164806748554),
                mSharedPreferences.getFloat(LONG_NORTH, (float) 2.387833558022976));
        LatLng south = new LatLng(mSharedPreferences.getFloat(LAT_SOUTH, (float) 48.8322317248286),
                mSharedPreferences.getFloat(LONG_SOUTH, (float) 2.3856264352798457));
        if (item.getItemId() == R.id.toolbar_search) {
            List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ID);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .setTypeFilter(TypeFilter.ESTABLISHMENT)
                    .setCountry("fr")
                    .setLocationRestriction(RectangularBounds.newInstance(south, north))
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Overrides onActivityResult to handle UI with the user's Autocomplete Prediction choice.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST && resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            for (String restaurantId : mIdList) {
                if (restaurantId.equals(place.getId())) {
                    switch (displayedFragment) {
                        case 1:
                            MapViewFragment mapViewFragment = (MapViewFragment) mFragmentManager.findFragmentByTag(MAPVIEW);
                            if (mapViewFragment != null)
                                mapViewFragment.moveCameraToSelectedRestaurant(place.getLatLng());
                            break;
                        case 2:
                            RecyclerViewFragment recyclerViewFragment = (RecyclerViewFragment) mFragmentManager.findFragmentByTag(RESTAURANT_FRAGMENT);
                            if (recyclerViewFragment != null)
                                recyclerViewFragment.showSelectedRestaurant(place.getId());
                            break;
                    }
                }
            }
        } else if (requestCode == AUTOCOMPLETE_REQUEST && resultCode == AutocompleteActivity.RESULT_ERROR)
            Log.e("error", Autocomplete.getStatusFromIntent(data).getStatusMessage());
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
     * Calls all methods needed to update the ui.
     */
    private void initUi() {
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNav();
        updateUiNavHeader();
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.open_drawer, R.string.close_drawer);
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
                MapViewFragment mapViewFragment;
                if (mFragmentManager.findFragmentByTag(MAPVIEW) != null)
                    mapViewFragment = (MapViewFragment) mFragmentManager.findFragmentByTag(MAPVIEW);
                else
                    mapViewFragment = (MapViewFragment) MapViewFragment.newInstance(mLocationPermissionGranted);

                if (mapViewFragment != null)
                    mFragmentManager.beginTransaction().replace(R.id.home_activity_frame_layout, mapViewFragment, MAPVIEW)
                            .commit();
                break;
            case RESTAURANT:
                RecyclerViewFragment restaurantFragment;
                if (mFragmentManager.findFragmentByTag(RESTAURANT_FRAGMENT) != null)
                    restaurantFragment = (RecyclerViewFragment) mFragmentManager.findFragmentByTag(RESTAURANT_FRAGMENT);
                else
                    restaurantFragment = RecyclerViewFragment.newInstance(RESTAURANT, mIdList);

                if (restaurantFragment != null)
                    mFragmentManager.beginTransaction().replace(R.id.home_activity_frame_layout, restaurantFragment, RESTAURANT_FRAGMENT)
                            .commit();
                break;
            case WORKMATES:
                RecyclerViewFragment workmateFragment;
                if (mFragmentManager.findFragmentByTag(WORKMATE_FRAGMENT) != null)
                    workmateFragment = (RecyclerViewFragment) mFragmentManager.findFragmentByTag(WORKMATE_FRAGMENT);
                else
                    workmateFragment = RecyclerViewFragment.newInstance(WORKMATES, mIdList);

                if (workmateFragment != null)
                    mFragmentManager.beginTransaction().replace(R.id.home_activity_frame_layout, workmateFragment, WORKMATE_FRAGMENT)
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
     * Allows to close the navigation drawer with the device's back button.
     */
    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START))
            mDrawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(List<String> idList) {
        mIdList = new ArrayList<>();
        mIdList = idList;
    }
}
