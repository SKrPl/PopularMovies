package com.example.siddhant.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by siddhant on 1/28/17.
 */

public class PopMoviesContract {

    public static final String CONTENT_AUTHORITY = "com.example.siddhant.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class Movie implements BaseColumns {
        public static final String TABLE_NAME = "movies";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_POSTER_URL = "poster_url";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_BACKDROP_URL = "backdrop_url";
        public static final String COLUMN_RATING = "rating";
    }

    public static final class Review implements BaseColumns {
        public static final String TABLE_NAME = "reviews";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";
    }

    public static final class Video implements BaseColumns {
        public static final String TABLE_NAME = "videos";
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon().appendPath(TABLE_NAME).build();

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
    }
}
