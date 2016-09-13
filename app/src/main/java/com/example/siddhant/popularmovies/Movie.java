package com.example.siddhant.popularmovies;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by siddhant on 8/26/16.
 */
public class Movie {

    /*
    Keys to retrive data from JSONObject,
    and intent received in MovieDetailFragment
     */
    public static final String ID = "id";
    public static final String TITLE = "original_title";
    public static final String RELEASE_DATE = "release_date";
    public static final String PLOT = "overview";
    public static final String POSTER_URL = "poster_path";
    public static final String RATING = "vote_average";

    private final String LOG_TAG = Movie.class.getSimpleName();

    private int mId;
    private String mTitle;
    private String mReleaseDate;
    private String mPosterUrl;
    private String mPlot;
    private String mRating;

    public Movie(JSONObject jsonMovieObject) throws JSONException{

        try {
            mId = jsonMovieObject.getInt(ID);
            mTitle = jsonMovieObject.getString(TITLE);
            mReleaseDate = jsonMovieObject.getString(RELEASE_DATE);
            String posterPath = jsonMovieObject.getString(POSTER_URL);
            mPosterUrl = buildAbsolutePosterPath(posterPath);
            mPlot = jsonMovieObject.getString(PLOT);
            mRating = jsonMovieObject.getString(RATING);
        }
        catch(MalformedURLException e) {
            Log.e(LOG_TAG, "Error", e);
        }
    }

    private String buildAbsolutePosterPath(String relativePath) throws MalformedURLException {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w185";

        Uri uri = Uri.parse(baseUrl).buildUpon()
                .appendPath(posterSize)
                .appendPath(relativePath.substring(1))
                .build();

        return uri.toString();
    }

    public int getID() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public String getPosterUrl() {
        return mPosterUrl;
    }

    public String getPlot() {
        return mPlot;
    }

    public String getRating() {
        return mRating;
    }
}
