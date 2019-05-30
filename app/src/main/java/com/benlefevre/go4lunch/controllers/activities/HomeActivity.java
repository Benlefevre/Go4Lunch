package com.benlefevre.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.benlefevre.go4lunch.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, BottomNavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        configureToolbar();
        configureDrawerLayout();
        configureNavigationView();
        configureBottomNav();
        updateUiNavHeader();
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
        if (currentUser.getDisplayName() != null)
            userName.setText(currentUser.getDisplayName());
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
        switch(item.getItemId()){
            case R.id.drawer_logout:
                signOutFromFirebase();
                break;
            case R.id.drawer_your_lunch:
                break;
            case R.id.drawer_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
            case R.id.bottom_map:
                break;
            case R.id.bottom_restaurant:
                break;
            case R.id.bottom_workmates:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
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

}
