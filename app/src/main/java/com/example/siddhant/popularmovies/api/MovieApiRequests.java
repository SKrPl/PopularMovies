package com.example.siddhant.popularmovies.api;

import com.example.siddhant.popularmovies.BuildConfig;
import com.example.siddhant.popularmovies.models.MoviesApiResponse;
import com.example.siddhant.popularmovies.models.ReviewApiResponse;
import com.example.siddhant.popularmovies.models.VideoApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by siddhant on 11/21/16.
 */

public interface MovieApiRequests {

    /*
    sorting_criteria can be popular or top_rated
     */
    @GET("movie/{sorting_criteria}/")
    Call<MoviesApiResponse> getMovies (
            @Path("sorting_criteria") String sortingCriteria,
            @Query("api_key") String apiKey);

    @GET("movie/{movie_id}/reviews")
    Call<ReviewApiResponse> getReviews (
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/videos")
    Call<VideoApiResponse> getVideos (
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

}
