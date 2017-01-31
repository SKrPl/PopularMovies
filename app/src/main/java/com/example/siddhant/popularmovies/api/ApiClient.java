package com.example.siddhant.popularmovies.api;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by siddhant on 11/21/16.
 */

public class ApiClient {

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static Retrofit retrofit;

    private static Retrofit getApiClient() {
        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(
                            GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }

    public static <T> T getRequest(Class<T> className) {
        return getApiClient().create(className);
    }
}
