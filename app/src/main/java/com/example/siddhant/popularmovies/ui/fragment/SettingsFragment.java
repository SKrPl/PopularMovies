package com.example.siddhant.popularmovies.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.example.siddhant.popularmovies.R;

/**
 * Created by siddhant on 8/30/16.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    private SharedPreferences mSharedPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPref.registerOnSharedPreferenceChangeListener(this);

        setSummaryAtStart(findPreference(getString(R.string.order_key)));
    }

    private void setSummaryAtStart (Preference preference) {
        /**
         * preference: preference for which we want to set the summary
         * Returns: void, makes the summary of the preference visible when settings fragment
         * is on the screen, without this only when preference is changed then only its summary would
         * be visisble
         */
        onSharedPreferenceChanged(mSharedPref, preference.getKey());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(sharedPreferences.getString(key, ""));
            preference.setSummary(listPreference.getEntries()[prefIndex]);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mSharedPref.unregisterOnSharedPreferenceChangeListener(this);
    }
}
