package com.benlefevre.go4lunch.controllers.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.benlefevre.go4lunch.R;

import static com.benlefevre.go4lunch.utils.Constants.IS_LOGGED;
import static com.benlefevre.go4lunch.utils.Constants.PREFERENCES;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configureToolbar();
        showPreferenceScreen();
    }

    /**
     * Bind a MySettingsFragment in the SettingsActivity's FrameLayout with the FragmentManager.
     */
    private void showPreferenceScreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settings_frame_layout, new MySettingsFragment()).commit();
    }

    /**
     * Defines our toolbar as the default ActionBar
     */
    private void configureToolbar() {
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        toolbar.setTitle(getString(R.string.setting));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public static class MySettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            SettingsActivity settingsActivity = (SettingsActivity) getActivity();
            Preference preference = findPreference("account");
            if (settingsActivity != null && preference != null) {
                preference.setOnPreferenceClickListener(preference1 -> {
                    new AlertDialog.Builder(settingsActivity)
                            .setTitle(settingsActivity.getString(R.string.delete_account))
                            .setMessage(settingsActivity.getString(R.string.are_you_sure_delete))
                            .setPositiveButton(settingsActivity.getString(R.string.yes_sure), (dialog, which) -> {
                                settingsActivity.deleteUserAccountInFirebase();
                                settingsActivity.getSharedPreferences(PREFERENCES,MODE_PRIVATE).edit().remove(IS_LOGGED).apply();
                            })
                            .setNegativeButton(settingsActivity.getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                            .show();
                    return true;
                });
            }
        }
    }
}
