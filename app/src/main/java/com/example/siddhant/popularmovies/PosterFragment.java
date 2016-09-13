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

/**
 * Created by siddhant on 8/20/16.
 */
public class PosterFragment extends Fragment implements AdapterView.OnItemClickListener{

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
            mMovieList = new ArrayList<>();
        }
        else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY);
        }

        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList);
        GridView gridView = (GridView) rootView.findViewById(R.id.poster_container);
        gridView.setAdapter(mMovieAdapter);
        gridView.setOnItemClickListener(this);

        if (savedInstanceState == null) {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute(
                    PreferenceManager.getDefaultSharedPreferences(getActivity())
                            .getString(getString(R.string.order_key),
                                    getString(R.string.order_value_default))
            );
        }

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

    class FetchMovieTask extends AsyncTask<String, Void, ArrayList<Movie>  > {

        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
        private final String BASE_URL = "http://api.themoviedb.org/3/movie";

        @Override
        protected ArrayList<Movie>  doInBackground(String... params) {
            if (params.length == 0)
                return null;

            String jsonString = getJSONRequest(params[0]);
            ArrayList<Movie> movieList=getMovieList(jsonString);
            return movieList;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie>  movieList) {
            if (movieList != null) {
                mMovieAdapter.clear();
                mMovieAdapter.addAll(movieList);
                mMovieList = movieList; // movie list assignment, parcelable use for orientation change
            }
        }

        private String getJSONRequest(String sortingPreference) {
            /**
             * themoviedb API request is sent and if successful the received json request is returned
             * sortingPreference: the way movie would be ordered in grid view, either by popularity
             or by rating
             * Returns: the json request as a string or null
             */
            final String API_KEY = "api_key";

            BufferedReader bufferedReader = null;
            HttpURLConnection urlConnection = null;

            try {
                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(sortingPreference)
                        .appendQueryParameter(API_KEY, BuildConfig.API_KEY)
                        .build();

                URL url = new URL(uri.toString());
                urlConnection= (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream == null) {
                    return null;
                }
                InputStreamReader streamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(streamReader);

                String reply;
                StringBuilder jsonRequest = new StringBuilder();
                while((reply = bufferedReader.readLine()) != null) {
                    jsonRequest.append(reply);
                }

                if(jsonRequest.length() == 0)
                    return null;

                return jsonRequest.toString();
            } catch(IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {

                if(urlConnection != null)
                    urlConnection.disconnect();

                if(bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error", e);
                    }
                }
            }
        }

        private ArrayList<Movie> getMovieList(String jsonString) {
            /**
             * JSON request is parsed to create Movie objects
             * jsonString: the returned json api request as string
             * Returns: array list of movie objects so that it can be passed to adapter
             */
            ArrayList<Movie> movieList = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray results = jsonObject.getJSONArray("results");
                int len = results.length();

                for (int i = 0; i < len; i++) {
                    JSONObject movieDetail = results.getJSONObject(i);
                    Movie movie = new Movie(movieDetail);
                    movieList.add(movie);
                }
            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Error", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error", e);
            }
            return movieList;
        }

    }


}
