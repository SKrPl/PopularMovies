package com.example.siddhant.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siddhant on 1/28/17.
 */

public class Review implements Parcelable{

    private String author;
    private String content;
    private String url;

    private Review(Parcel source) {
        author = source.readString();
        content = source.readString();
        url = source.readString();
    }

    public Review(String author, String content, String url) {
        this.author = author;
        this.content = content;
        this.url = url;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel source) {
            return new Review(source);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };
}
