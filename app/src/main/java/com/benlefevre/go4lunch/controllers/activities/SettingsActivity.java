package com.benlefevre.go4lunch.controllers.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.benlefevre.go4lunch.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        configureToolbar();
        showPreferenceScreen();
    }

    private void showPreferenceScreen() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.settings_frame_layout, new MySettingsFragment()).commit();
    }

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
            preference.setOnPreferenceClickListener(preference1 -> {
                new AlertDialog.Builder(settingsActivity)
                        .setTitle(settingsActivity.getString(R.string.delete_account))
                        .setMessage(settingsActivity.getString(R.string.are_you_sure_delete))
                        .setPositiveButton(settingsActivity.getString(R.string.yes_sure), (dialog, which) -> settingsActivity.deleteUserAccountInFirebase())
                        .setNegativeButton(settingsActivity.getString(R.string.cancel), (dialog, which) -> dialog.cancel())
                        .show();
                return true;
            });
        }
    }
}
