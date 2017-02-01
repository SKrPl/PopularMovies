package com.example.siddhant.popularmovies.ui.fragment;


import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.siddhant.popularmovies.BuildConfig;
import com.example.siddhant.popularmovies.MovieAdapter;
import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.Utility;
import com.example.siddhant.popularmovies.api.ApiClient;
import com.example.siddhant.popularmovies.api.MovieApiRequests;
import com.example.siddhant.popularmovies.data.PopMoviesContract;
import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.models.MoviesApiResponse;
import com.example.siddhant.popularmovies.ui.activity.MainActivity;
import com.example.siddhant.popularmovies.ui.activity.MovieDetailActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by siddhant on 8/20/16.
 */
public class PosterFragment extends Fragment implements
        MovieAdapter.OnItemClickListener,
        Toolbar.OnMenuItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private final String MOVIE_LIST_PARCELABLE_KEY = "movie_parcelable_list";
    private final String SORTING_CRITERIA = "sorting_criteria";

    public static final String MOVIE_PARCELABLE_KEY = "movie_parcelable";
    public static final String SHARED_PREF_DB_KEY = "favourites_db_key";
    public static final int POSTERS_LOADER_ID = 100;

    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList = new ArrayList<Movie>();
    private String mSortingCriteria = "popular";
    private RecyclerViewClickCallback mRecyclerViewClickCallback;

    private Toolbar mToolbar;
    private SharedPreferences mSharedPref;

    public interface RecyclerViewClickCallback {
        public void onRecyclerViewItemSelected(Movie movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setDbValue(false);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerViewClickCallback = (RecyclerViewClickCallback) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);
        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar_poster);
        mToolbar.setTitle("Popular Movies");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(this);


        if (savedInstanceState == null) {
            makeRequest();
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY);
            Log.d(LOG_TAG, "Number of movies restored from Parcel: " + mMovieList.size());
        }

        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList, this);
        final RecyclerView recyclerView = (RecyclerView) rootView
                .findViewById(R.id.poster_container);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int width = recyclerView.getWidth();

                        Resources resources = getResources();
                        int imageViewWidth = (int) TypedValue
                                .applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        180,
                                        resources.getDisplayMetrics()
                                );

                        recyclerView.setLayoutManager(
                                new GridLayoutManager(getActivity(),
                                        width/imageViewWidth)
                        );

                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        recyclerView.setAdapter(mMovieAdapter);

        return rootView;
    }

    private void makeRequest() {
        setDbValue(false);
        final boolean twoPane = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(MainActivity.SHARED_PREF_IS_TWO_PANE, false);

        MovieApiRequests movieApiRequests = ApiClient.getRequest(MovieApiRequests.class);

        String sortingCriteria = Utility.getSortingCriteria(getActivity());
        Call<MoviesApiResponse> call = movieApiRequests.getMovies(
                mSortingCriteria,
                BuildConfig.API_KEY);

        call.enqueue(new Callback<MoviesApiResponse>() {
            @Override
            public void onResponse(
                    Call<MoviesApiResponse> call,
                    Response<MoviesApiResponse> response) {
                mMovieList = response.body().getResults();
                mMovieAdapter.setMovieList(mMovieList);
                if (twoPane) {
                    setOnClickListener(mMovieList.get(0));
                }
                Log.d(LOG_TAG, "Number of movies received: " + mMovieAdapter.getItemCount());
            }

            @Override
            public void onFailure(Call<MoviesApiResponse> call, Throwable t) {
                /*FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, new NoNetworkFragment())
                        .commit();*/

                Log.e(LOG_TAG, t.toString());
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY, mMovieAdapter.getMovieList());
        outState.putString(SORTING_CRITERIA,mSortingCriteria);
    }

    @Override
    public void setOnClickListener(Movie movie) {
        mRecyclerViewClickCallback.onRecyclerViewItemSelected(movie);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_popular:
                mSortingCriteria = "popular";
                makeRequest();
                break;
            case R.id.main_top_rated:
                mSortingCriteria = "top_rated";
                makeRequest();
                break;
            case R.id.main_favourite:
                mSortingCriteria = "favourites";
                getLoaderManager().restartLoader(POSTERS_LOADER_ID, null, this);
                break;
        }
        return true;
    }

    private void setDbValue(boolean dbValue) {
        mSharedPref.edit().putBoolean(SHARED_PREF_DB_KEY, dbValue).apply();
    }

    @Override
    public void onResume() {
        boolean val = mSharedPref.getBoolean(SHARED_PREF_DB_KEY, false);
        if (val) {
            getLoaderManager().restartLoader(POSTERS_LOADER_ID, null, this);
        }
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity()){
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
            }

            @Override
            public Cursor loadInBackground() {
                Cursor cursor = getActivity().getContentResolver().query(
                        PopMoviesContract.Movie.CONTENT_URI,
                        null, null, null, null);
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setDbValue(true);
        final boolean twoPane = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(MainActivity.SHARED_PREF_IS_TWO_PANE, false);
        mMovieList = cursorToMovieList(data);
        mMovieAdapter.setMovieList(mMovieList);

        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (twoPane && mMovieList.size() != 0) {
                    setOnClickListener(mMovieList.get(0));
                }
                super.handleMessage(msg);
            }
        };
        handler.sendEmptyMessage(0);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ArrayList<Movie> cursorToMovieList(Cursor cursor) {
        ArrayList<Movie> movies = new ArrayList<>();

        int idColIndex = cursor.getColumnIndex(PopMoviesContract.Movie._ID);
        int posterUrlColIndex = cursor.getColumnIndex(PopMoviesContract.Movie.COLUMN_POSTER_URL);
        int overviewColIndex = cursor.getColumnIndex(PopMoviesContract.Movie.COLUMN_OVERVIEW);
        int releaseDateColIndex = cursor.getColumnIndex(PopMoviesContract.Movie.COLUMN_RELEASE_DATE);
        int titleColIndex = cursor.getColumnIndex(PopMoviesContract.Movie.COLUMN_TITLE);
        int backdropUrlColIndex = cursor.getColumnIndex(PopMoviesContract.Movie.COLUMN_BACKDROP_URL);
        int ratingColIndex = cursor.getColumnIndex(PopMoviesContract.Movie.COLUMN_RATING);

        while (cursor.moveToNext()) {
            String[] parts = cursor.getString(posterUrlColIndex).split("/");
            String posterPath = parts[parts.length-1];
            parts = cursor.getString(backdropUrlColIndex).split("/");
            String backdropPath = parts[parts.length-1];

            Movie movie = new
                    Movie(
                    posterPath,
                    cursor.getString(overviewColIndex),
                    cursor.getString(releaseDateColIndex),
                    cursor.getInt(idColIndex),
                    cursor.getString(titleColIndex),
                    backdropPath,
                    cursor.getDouble(ratingColIndex));

            movies.add(movie);
        }
        return movies;
    }

    public void initLoader() {
        getLoaderManager().restartLoader(POSTERS_LOADER_ID, null, this);
    }
}
