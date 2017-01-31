package com.example.siddhant.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by siddhant on 1/28/17.
 */

public class PopMoviesProvider extends ContentProvider {

    private PopMoviesDbHelper mDbHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_ID = 101;

    public static final int REVIEW = 200;
    public static final int REVIEW_WITH_MOVIE = 201;

    public static final int VIDEO = 300;
    public static final int VIDEO_WITH_MOVIE = 301;

    @Override
    public boolean onCreate() {
        mDbHelper = new PopMoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {

        Cursor retCursor;
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = db.query(
                        PopMoviesContract.Movie.TABLE_NAME,
                        null, null, null, null, null, null);
                break;
            case MOVIE_WITH_ID:
                retCursor = db.query(
                        PopMoviesContract.Movie.TABLE_NAME,
                        projection,
                        PopMoviesContract.Movie._ID + "=?",
                        new String[]{uri.getLastPathSegment()},
                        null, null, null);
                break;
            case REVIEW:
                retCursor = db.query(
                        PopMoviesContract.Review.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, null);
                break;
            case VIDEO:
                retCursor = db.query(
                        PopMoviesContract.Video.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknow URI");
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id;
        Uri retUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                id = mDbHelper.getWritableDatabase().insert(
                        PopMoviesContract.Movie.TABLE_NAME, null, values);
                retUri = PopMoviesContract.Movie.CONTENT_URI
                        .buildUpon().appendPath(String.valueOf(id)).build();
                break;
            case REVIEW:
                id = mDbHelper.getWritableDatabase().insert(
                        PopMoviesContract.Review.TABLE_NAME, null, values);
                retUri = PopMoviesContract.Review.CONTENT_URI
                        .buildUpon().appendPath(String.valueOf(id)).build();
                break;
            case VIDEO:
                id = mDbHelper.getWritableDatabase().insert(
                        PopMoviesContract.Video.TABLE_NAME, null, values);
                retUri = PopMoviesContract.Video.CONTENT_URI
                        .buildUpon().appendPath(String.valueOf(id)).build();
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numDeleted;
        switch (sUriMatcher.match(uri)) {
            case MOVIE_WITH_ID:
                numDeleted = mDbHelper.getWritableDatabase().delete(
                        PopMoviesContract.Movie.TABLE_NAME,
                        PopMoviesContract.Movie._ID + "=?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))}
                );
                break;
            case REVIEW:
                numDeleted = mDbHelper.getWritableDatabase().delete(
                        PopMoviesContract.Review.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case VIDEO:
                numDeleted = mDbHelper.getWritableDatabase().delete(
                        PopMoviesContract.Video.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String AUTHORITY = PopMoviesContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(AUTHORITY, PopMoviesContract.Movie.TABLE_NAME, MOVIE);
        uriMatcher.addURI(AUTHORITY, PopMoviesContract.Movie.TABLE_NAME + "/#", MOVIE_WITH_ID);

        uriMatcher.addURI(AUTHORITY, PopMoviesContract.Review.TABLE_NAME, REVIEW);
        uriMatcher.addURI(
                AUTHORITY,
                PopMoviesContract.Review.TABLE_NAME + "/"
                        + PopMoviesContract.Review.COLUMN_MOVIE_ID,
                REVIEW_WITH_MOVIE);

        uriMatcher.addURI(AUTHORITY, PopMoviesContract.Video.TABLE_NAME, VIDEO);
        uriMatcher.addURI(
                AUTHORITY,
                PopMoviesContract.Video.TABLE_NAME + "/" + PopMoviesContract.Video.COLUMN_MOVIE_ID,
                VIDEO_WITH_MOVIE);

        return uriMatcher;
    }
}
