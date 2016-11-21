package com.example.siddhant.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.siddhant.popularmovies.api.ApiClient;
import com.example.siddhant.popularmovies.api.MovieApiRequests;
import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.models.MoviesApiResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by siddhant on 8/20/16.
 */
public class PosterFragment extends Fragment implements AdapterView.OnItemClickListener{

    private final String LOG_TAG = PosterFragment.class.getSimpleName();
    private final String MOVIE_LIST_PARCELABLE_KEY = "movie_parcelable_list";

    public static final String MOVIE_PARCELABLE_KEY = "movie_parcelable";

    private MovieAdapter mMovieAdapter;
    private ArrayList<Movie> mMovieList; // movie list, parcelable use for orientation change

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_poster, container, false);

        if (savedInstanceState == null) {
            mMovieList = new ArrayList<Movie>();
            MovieApiRequests movieApiRequests = ApiClient.getApiClient().
                    create(MovieApiRequests.class);
            String sortingCriteria = Utility.getSortingCriteria(getActivity());

            Call<MoviesApiResponse> call = movieApiRequests.getMovies(
                    sortingCriteria,
                    BuildConfig.API_KEY);
            call.enqueue(new Callback<MoviesApiResponse>() {
                @Override
                public void onResponse(Call<MoviesApiResponse> call, Response<MoviesApiResponse> response) {
                    mMovieList = response.body().getResults();
                    Log.d(LOG_TAG, "Number of movies received: " + mMovieList.size());
                }

                @Override
                public void onFailure(Call<MoviesApiResponse> call, Throwable t) {
                    Log.e(LOG_TAG, t.toString());
                }
            });
        }
        else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY);
        }

        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);
        GridView gridView = (GridView) rootView.findViewById(R.id.poster_container);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(this);


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY, mMovieList);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Movie movie = (Movie) adapterView.getItemAtPosition(i);

        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra(MOVIE_PARCELABLE_KEY, movie);

        startActivity(intent);
    }
}
