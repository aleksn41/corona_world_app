package de.dhbw.corona_world_app.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import de.dhbw.corona_world_app.R;

/**
 * This Fragment is used to show the Settings
 * @author Aleksandr Stankoski
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
    }
}