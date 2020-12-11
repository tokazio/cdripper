package com.adamdonegan.Discogs4J.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class PaginatedResult {

    @JsonIgnore
    private Pagination pagination;
    private List<Result> results;

    public Pagination getPagination() {
        return pagination;
    }

    public List<Result> getResults() {
        return results;
    }
}
