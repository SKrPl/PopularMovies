package com.example.siddhant.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by siddhant on 8/28/16.
 */
public class MovieDetailFragment extends Fragment{

    private static final String MOVIE_PARCELABLE_SAVED = "saved_instance_state";

    private Movie mMovie;
    private TextView mMovieTitle;
    private ImageView mMoviePoster;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mPlot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();

        if (savedInstanceState == null) {
            mMovie = intent.getParcelableExtra(PosterFragment.MOVIE_PARCELABLE_KEY);
        }
        else {
            mMovie = savedInstanceState.getParcelable(MOVIE_PARCELABLE_SAVED);
        }

        mMovieTitle = (TextView) rootView.findViewById(R.id.text_icon);
        String mTitle = mMovie.getTitle();
        mMovieTitle.setText(mTitle);

        mMoviePoster = (ImageView) rootView.findViewById(R.id.movie_poster);
        String mPosterUrl = mMovie.getPosterUrl();
        Picasso.with(getActivity()).load(mPosterUrl).fit().into(mMoviePoster);

        mReleaseDate = (TextView) rootView.findViewById(R.id.release_date);
        String mReleaseDate = mMovie.getReleaseDate();
        this.mReleaseDate.setText(mReleaseDate);

        mRating = (TextView) rootView.findViewById(R.id.rating);
        String mRating = mMovie.getRating() + "/10";
        this.mRating.setText(mRating);

        mPlot = (TextView) rootView.findViewById(R.id.plot);
        String mPlot = mMovie.getPlot();
        this.mPlot.setText(mPlot);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MOVIE_PARCELABLE_SAVED, mMovie);
    }
}
