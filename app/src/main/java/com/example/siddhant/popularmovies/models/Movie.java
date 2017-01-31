package com.example.siddhant.popularmovies.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siddhant on 8/26/16.
 */
public class Movie implements Parcelable{

    private String posterPath;
    private String overview;
    private String releaseDate;
    private int id;
    private String title;
    private String backdropPath;
    private double voteAverage;

    public Movie(String posterPath, String overview, String releaseDate, int id,
                 String title, String backdropPath, double voteAverage) {
        this.posterPath = posterPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.id = id;
        this.title = title;
        this.backdropPath = backdropPath;
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getPosterUrl() {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String posterSize = "w185/";
        return baseUrl + posterSize + getPosterPath();
    }

    public String getBackdropUrl() {
        String baseUrl = "http://image.tmdb.org/t/p/";
        String backdropSize = "w500/";
        System.out.println(baseUrl + backdropSize + getBackdropPath());
        return baseUrl + backdropSize + getBackdropPath();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeDouble(voteAverage);
    }

    private Movie(Parcel source) {
        posterPath = source.readString();
        overview = source.readString();
        releaseDate = source.readString();
        id = source.readInt();
        title = source.readString();
        backdropPath = source.readString();
        voteAverage = source.readDouble();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
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
