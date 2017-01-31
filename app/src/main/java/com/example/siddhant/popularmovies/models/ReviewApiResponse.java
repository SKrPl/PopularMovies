package com.example.siddhant.popularmovies.models;

import java.util.List;

/**
 * Created by siddhant on 1/28/17.
 */

public class ReviewApiResponse {

    private int id;
    private int page;
    private List<Review> results;
    private int totalPages;
    private int totalResults;

    public int getId() {
        return id;
    }

    public int getPage() {
        return page;
    }

    public List<Review> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
