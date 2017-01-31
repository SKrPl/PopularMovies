package com.example.siddhant.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by siddhant on 1/28/17.
 */

public class PopMoviesDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "popular_movies.db";
    private static final int DATABASE_VERSION = 1;

    public PopMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String MOVIE_CREATE_TABLE = "CREATE TABLE "
                + PopMoviesContract.Movie.TABLE_NAME
                + "( " + PopMoviesContract.Movie._ID + " INTEGER PRIMARY KEY, "
                + PopMoviesContract.Movie.COLUMN_POSTER_URL + " TEXT NOT NULL, "
                + PopMoviesContract.Movie.COLUMN_OVERVIEW + " TEXT NOT NULL, "
                + PopMoviesContract.Movie.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + PopMoviesContract.Movie.COLUMN_TITLE + " TEXT NOT NULL, "
                + PopMoviesContract.Movie.COLUMN_BACKDROP_URL + " TEXT NOT NULL, "
                + PopMoviesContract.Movie.COLUMN_RATING + " REAL NOT NULL"
                + " );";

        final String REVIEW_CREATE_TABLE = "CREATE TABLE "
                + PopMoviesContract.Review.TABLE_NAME + " ( "
                + PopMoviesContract.Review.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + PopMoviesContract.Review.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + PopMoviesContract.Review.COLUMN_CONTENT + " TEXT NOT NULL, "
                + PopMoviesContract.Review.COLUMN_URL + " TEXT NOT NULL, "
                + "FOREIGN KEY " + "(" + PopMoviesContract.Review.COLUMN_MOVIE_ID + ") "
                + "REFERENCES " + PopMoviesContract.Movie.TABLE_NAME + "("
                + PopMoviesContract.Movie._ID + ")"
                + " );";

        final String VIDEO_CREATE_TABLE ="CREATE TABLE "
                + PopMoviesContract.Video.TABLE_NAME + "( "
                + PopMoviesContract.Video.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + PopMoviesContract.Video.COLUMN_KEY + " TEXT NOT NULL, "
                + PopMoviesContract.Video.COLUMN_NAME + " TEXT NOT NULL, "
                + "FOREIGN KEY " + "(" + PopMoviesContract.Video.COLUMN_MOVIE_ID + ") "
                + "REFERENCES " + PopMoviesContract.Movie.TABLE_NAME + "("
                + PopMoviesContract.Movie._ID + ")"
                + " );";

        db.execSQL(MOVIE_CREATE_TABLE);
        db.execSQL(REVIEW_CREATE_TABLE);
        db.execSQL(VIDEO_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.Review.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.Video.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + PopMoviesContract.Movie.TABLE_NAME);

        onCreate(db);
    }
}
