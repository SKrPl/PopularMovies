package com.example.siddhant.popularmovies;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.siddhant.popularmovies.api.ApiClient;
import com.example.siddhant.popularmovies.api.MovieApiRequests;
import com.example.siddhant.popularmovies.models.Movie;
import com.example.siddhant.popularmovies.models.MoviesApiResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by siddhant on 8/20/16.
 */
public class PosterFragment extends Fragment implements MovieAdapter.OnItemClickListener {

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
            MovieApiRequests movieApiRequests = ApiClient.getRequest(MovieApiRequests.class);
            String sortingCriteria = Utility.getSortingCriteria(getActivity());

            Call<MoviesApiResponse> call = movieApiRequests.getMovies(
                    sortingCriteria,
                    BuildConfig.API_KEY);
            call.enqueue(new Callback<MoviesApiResponse>() {
                @Override
                public void onResponse(Call<MoviesApiResponse> call, Response<MoviesApiResponse> response) {
                    mMovieAdapter.setMovieList(response.body().getResults());
                    Log.d(LOG_TAG, "Number of movies received: " + mMovieAdapter.getItemCount());
                }

                @Override
                public void onFailure(Call<MoviesApiResponse> call, Throwable t) {
                    Utility.replaceFragment(
                            getActivity(),
                            R.id.fragment_container,
                            new NoNetworkFragment(),
                            null
                    );
                    Log.e(LOG_TAG, t.toString());
                }
            });
        } else {
            mMovieList = savedInstanceState.getParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY);
            Log.d(LOG_TAG, "Number of movies restored from Parcel: " + mMovieList.size());
        }

        mMovieAdapter = new MovieAdapter(getActivity(), mMovieList, this);
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.poster_container);

        rootView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int width = recyclerView.getWidth();

                        Resources resources = getResources();
                        int imageViewWidth = (int) TypedValue
                                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, resources.getDisplayMetrics());

                        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), width/imageViewWidth));

                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });

        recyclerView.setAdapter(mMovieAdapter);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(MOVIE_LIST_PARCELABLE_KEY, mMovieAdapter.getMovieList());
    }

    @Override
    public void setOnClickListener(int position, Object movie) {
        Movie receivedMovie = (Movie) movie;
        Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
        intent.putExtra(PosterFragment.MOVIE_PARCELABLE_KEY, receivedMovie);
        startActivity(intent);
    }

}
