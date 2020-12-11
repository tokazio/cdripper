package com.adamdonegan.Discogs4J.models;

import java.util.List;

public class Pagination {

    private int page;
    private int pages;
    private int per_page;
    private int items;
    private List<String> urls;

    public int getPage() {
        return page;
    }

    public int getPages() {
        return pages;
    }

    public int getPer_page() {
        return per_page;
    }

    public int getItems() {
        return items;
    }

    public List<String> getUrls() {
        return urls;
    }
}
