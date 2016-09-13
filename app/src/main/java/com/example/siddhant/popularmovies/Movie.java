package com.example.siddhant.popularmovies;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;

/**
 * Created by siddhant on 8/26/16.
 */
public class Movie implements Parcelable{

    /*
    Keys to retrieve data from JSONObject,
    and intent received in MovieDetailFragment
     */
    public static final String ID = "id";
    public static final String TITLE = "original_title";
    public static final String RELEASE_DATE = "release_date";
    public static final String POSTER_URL = "poster_path";
    public static final String PLOT = "overview";
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

    private Movie(Parcel parcel) {
        mId = parcel.readInt();
        mTitle = parcel.readString();
        mReleaseDate = parcel.readString();
        mPosterUrl = parcel.readString();
        mPlot = parcel.readString();
        mRating = parcel.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mPosterUrl);
        dest.writeString(mPlot);
        dest.writeString(mRating);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
