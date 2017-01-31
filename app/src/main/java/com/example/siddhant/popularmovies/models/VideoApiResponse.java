package com.example.siddhant.popularmovies.models;

import java.util.List;

/**
 * Created by siddhant on 1/28/17.
 */

public class VideoApiResponse {

    private int id;
    private List<Video> results;

    public int getId() {

        return id;
    }

    public List<Video> getResults() {
        return results;
    }
}
