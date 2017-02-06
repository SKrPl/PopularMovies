package com.example.siddhant.popularmovies.ui.fragment;


import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.example.siddhant.popularmovies.BuildConfig;
import com.example.siddhant.popularmovies.MovieAdapter;
import com.example.siddhant.popularmovies.R;
import com.example.siddhant.popularmovies.api.ApiClient;
import com.example.siddhant.popularmovies.api.MovieApiRequests;
import com.example.siddhant.popularmovies.data.PopMoviesContract;
import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.models.MoviesApiResponse;
import com.example.siddhant.popularmovies.ui.activity.MainActivity;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by siddhant on 8/20/16.
 */
public class PosterFragment extends Fragment implements
        MovieAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String MOVIE_PARCELABLE_KEY = "movie_parcelable";
    public static final String SHARED_PREF_ON_CLICKED_FAVOURITE = "on_clicked_favourite";

    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private final String SAVED_SCROLL_STATE = "scroll_state";
    private static final int POSTERS_LOADER_ID = 100;
    private final float MOVIE_POSTER_WIDTH = 160;
    private final String MOVIE_LIST_PARCELABLE_KEY = "movie_parcelable_list";
    private final String SORTING_CRITERIA = "sorting_criteria";

    private SharedPreferences mSharedPref;
    private Toast mToast;
    private RecyclerView mRecyclerView;
    private GridLayoutManager layoutManager;
    private Parcelable recyclerViewSavedScrollState;

    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList = new ArrayList<>();
    private RecyclerViewClickCallback mRecyclerViewClickCallback;

    private Call<MoviesApiResponse> mMoviesApiResponseCall;

    private String mSortingCriteria = "popular";
    private int savedScrollState = 0;

    public interface RecyclerViewClickCallback {
        void onRecyclerViewItemSelected(Movie movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) rootView
                .findViewById(R.id.poster_container);


        if (savedInstanceState == null && !getOnClickFavouritePrefValue()) {
            setOnClickFavouritePrefValue(false);
            fetchMovies();
        } else if (savedInstanceState != null){
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY);
            Log.d(LOG_TAG, "Number of movies restored from Parcel: " + mMovieList.size());
            recyclerViewSavedScrollState = savedInstanceState.getParcelable(SAVED_SCROLL_STATE);

        }

        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList, this);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int width = mRecyclerView.getWidth();
                        Resources resources = getResources();
                        int imageViewWidth = (int) TypedValue
                                .applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        MOVIE_POSTER_WIDTH,
                                        resources.getDisplayMetrics()
                                );

                        layoutManager = new GridLayoutManager(getActivity(), width/imageViewWidth);
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.setAdapter(mMovieAdapter);
                        if (recyclerViewSavedScrollState != null) {
                            layoutManager.onRestoreInstanceState(recyclerViewSavedScrollState);
                        }
                        mRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.main_popular:
                mSortingCriteria = "popular";
                fetchMovies();
                break;
            case R.id.main_top_rated:
                mSortingCriteria = "top_rated";
                fetchMovies();
                break;
            case R.id.main_favourite:
                mSortingCriteria = "favourites";
                getLoaderManager().restartLoader(POSTERS_LOADER_ID, null, this);
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY, mMovieAdapter.getMovieList());
        outState.putString(SORTING_CRITERIA, mSortingCriteria);
        outState.putParcelable(SAVED_SCROLL_STATE, layoutManager.onSaveInstanceState());
    }

    @Override
    public void setOnClickListener(Movie movie) {
        mRecyclerViewClickCallback.onRecyclerViewItemSelected(movie);
    }

    @Override
    public void onResume() {
        if (getOnClickFavouritePrefValue()) {
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
                return getActivity().getContentResolver().query(
                        PopMoviesContract.Movie.CONTENT_URI,
                        null, null, null, null);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setOnClickFavouritePrefValue(true);
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

        if (mMovieList.size() == 0) {
            showToastMessage(getResources().getString(R.string.no_favourite_movie));
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.setMovieList(null);
    }

    public void initLoader() {
        getLoaderManager().restartLoader(POSTERS_LOADER_ID, null, this);
    }

    private void fetchMovies() {
        setOnClickFavouritePrefValue(false);
        final boolean twoPane = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(MainActivity.SHARED_PREF_IS_TWO_PANE, false);

        MovieApiRequests movieApiRequests = ApiClient.getRequest(MovieApiRequests.class);

        if (mMoviesApiResponseCall != null) {
            mMoviesApiResponseCall.cancel();
        }

        mMoviesApiResponseCall = movieApiRequests.getMovies(
                mSortingCriteria,
                BuildConfig.API_KEY);

        mMoviesApiResponseCall.enqueue(new Callback<MoviesApiResponse>() {
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
                Log.e(LOG_TAG, t.toString());
                showToastMessage(getResources().getString(R.string.no_network_message));
            }
        });
    }

    private void setOnClickFavouritePrefValue(boolean prefValue) {
        mSharedPref.edit().putBoolean(SHARED_PREF_ON_CLICKED_FAVOURITE, prefValue).apply();
    }

    private boolean getOnClickFavouritePrefValue() {
        return mSharedPref.getBoolean(SHARED_PREF_ON_CLICKED_FAVOURITE, false);
    }

    private void showToastMessage(String toastMessage) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getActivity(), toastMessage, Toast.LENGTH_SHORT);
        mToast.show();
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
}
