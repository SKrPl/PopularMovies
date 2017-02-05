package com.example.siddhant.popularmovies.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.ui.fragment.MovieDetailFragment;
import com.example.siddhant.popularmovies.ui.fragment.PosterFragment;
import com.example.siddhant.popularmovies.R;

public class MainActivity extends AppCompatActivity implements
        PosterFragment.RecyclerViewClickCallback,
        MovieDetailFragment.DbMovieUiUpdateListener {

    public static final String SHARED_PREF_IS_TWO_PANE = "is_two_pane";

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) == null) {
            mTwoPane = false;
        } else {
            mTwoPane = true;
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().putBoolean(SHARED_PREF_IS_TWO_PANE, mTwoPane).apply();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onRecyclerViewItemSelected(Movie movie) {
        if (mTwoPane) {
            FragmentManager fm = getSupportFragmentManager();
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(movie, mTwoPane);
            fm.beginTransaction().replace(R.id.movie_detail_container, fragment).commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class);
            intent.putExtra(PosterFragment.MOVIE_PARCELABLE_KEY, movie);
            startActivity(intent);
        }
    }

    /**
     * Used to update poster fragment in tablet layout, after a movie is removed by clicking on
     * star icon in MovieDetailFragment.
     */
    @Override
    public void updatePosterFragmentUi() {
        PosterFragment fragment = (PosterFragment) getSupportFragmentManager()
                .findFragmentById(R.id.poster_fragment);
        boolean isOnClickedFavouritePrefValue = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PosterFragment.SHARED_PREF_ON_CLICKED_FAVOURITE, false);
        if (mTwoPane && isOnClickedFavouritePrefValue) {
            fragment.initLoader();
        }
    }
}
