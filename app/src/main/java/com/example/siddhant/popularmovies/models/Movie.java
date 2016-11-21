package com.example.siddhant.popularmovies.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by siddhant on 8/26/16.
 */
public class Movie implements Parcelable{

    private String posterPath;
    private boolean adult;
    private String overview;
    private String releaseDate;
    private List<Integer> genreIds;
    private int id;
    private String originalTitle;
    private String origianlLangugae;
    private String title;
    private String backdropPath;
    private double popularity;
    private int voteCount;
    private boolean video;
    private double voteAverage;

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
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

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOrigianlLangugae() {
        return origianlLangugae;
    }

    public void setOrigianlLangugae(String origianlLangugae) {
        this.origianlLangugae = origianlLangugae;
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

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
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
        return baseUrl + posterSize + posterPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(posterPath);
        dest.writeInt(adult ? 1 : 0);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeList(genreIds);
        dest.writeInt(id);
        dest.writeString(originalTitle);
        dest.writeString(origianlLangugae);
        dest.writeString(title);
        dest.writeString(backdropPath);
        dest.writeDouble(popularity);
        dest.writeInt(voteCount);
        dest.writeInt(video ? 1: 0);
        dest.writeDouble(voteAverage);
    }

    private Movie(Parcel source) {
        posterPath = source.readString();
        adult = source.readInt() == 1 ? true : false;
        overview = source.readString();
        releaseDate = source.readString();
        source.readList(genreIds, List.class.getClassLoader());
        id = source.readInt();
        originalTitle = source.readString();
        origianlLangugae = source.readString();
        title = source.readString();
        backdropPath = source.readString();
        popularity = source.readDouble();
        voteCount = source.readInt();
        video = source.readInt() == 1 ? true : false;
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
