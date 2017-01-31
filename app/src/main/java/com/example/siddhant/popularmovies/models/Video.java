package com.example.siddhant.popularmovies.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by siddhant on 1/28/17.
 */

public class Video implements Parcelable{

    private String key;
    private String name;

    private Video(Parcel source) {
        key = source.readString();
        name = source.readString();
    }

    public Video(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(name);
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel source) {
            return new Video(source);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };
}
