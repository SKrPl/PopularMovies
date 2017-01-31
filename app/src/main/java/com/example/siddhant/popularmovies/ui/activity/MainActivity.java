package com.example.siddhant.popularmovies.ui.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.siddhant.popularmovies.ui.fragment.PosterFragment;
import com.example.siddhant.popularmovies.R;

public class MainActivity extends AppCompatActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private PosterFragment mFragment;

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFragment = new PosterFragment();

        if (findViewById(R.id.movie_detail_container) == null) {
            mTwoPane = false;
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, mFragment)
                        .commit();
            }
        } else {
            mTwoPane = true;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
