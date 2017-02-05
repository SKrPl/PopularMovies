package com.example.siddhant.popularmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.siddhant.popularmovies.ui.fragment.MovieDetailFragment;
import com.example.siddhant.popularmovies.R;

/**
 * Created by siddhant on 8/28/16.
 */
public class MovieDetailActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {
            MovieDetailFragment fragment = MovieDetailFragment.newInstance(null, false);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.detail_fragment_container, fragment)
                    .commit();
        }
    }
}
