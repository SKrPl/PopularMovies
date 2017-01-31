package com.example.siddhant.popularmovies.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.ui.fragment.SettingsFragment;

/**
 * Created by siddhant on 8/31/16.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.settings_fragment_container, new SettingsFragment())
                    .commit();
        }
    }
}
