package com.adamdonegan.Discogs4J.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class Result {

    private String country;
    private String year;
    @JsonIgnore
    private List<String> format;
    @JsonIgnore
    private List<String> label;
    private String type;
    @JsonIgnore
    private List<String> genre;
    @JsonIgnore
    private List<String> style;
    private int id;
    @JsonIgnore
    private List<String> barcode;
    //user_data
    private int master_id;
    private String master_url;
    private String uri;
    private String catno;
    private String title;
    private String thumb;
    private String cover_image;
    private String resource_url;
    private Community community;
    private int format_quantity;
    //formats

    public String getCountry() {
        return country;
    }

    public String getYear() {
        return year;
    }

    public List<String> getFormat() {
        return format;
    }

    public List<String> getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public List<String> getGenre() {
        return genre;
    }

    public List<String> getStyle() {
        return style;
    }

    public int getId() {
        return id;
    }

    public List<String> getBarcode() {
        return barcode;
    }

    public int getMaster_id() {
        return master_id;
    }

    public String getMaster_url() {
        return master_url;
    }

    public String getUri() {
        return uri;
    }

    public String getCatno() {
        return catno;
    }

    public String getTitle() {
        return title;
    }

    public String getThumb() {
        return thumb;
    }

    public String getCover_image() {
        return cover_image;
    }

    public String getResource_url() {
        return resource_url;
    }

    public Community getCommunity() {
        return community;
    }

    public int getFormat_quantity() {
        return format_quantity;
    }

    @Override
    public String toString() {
        return "Result{" +
                "country='" + country + '\'' +
                ", year='" + year + '\'' +
                ", format=" + format +
                ", label=" + label +
                ", type='" + type + '\'' +
                ", genre=" + genre +
                ", style=" + style +
                ", id=" + id +
                ", barcode=" + barcode +
                ", master_id=" + master_id +
                ", master_url='" + master_url + '\'' +
                ", uri='" + uri + '\'' +
                ", catno='" + catno + '\'' +
                ", title='" + title + '\'' +
                ", thumb='" + thumb + '\'' +
                ", cover_image='" + cover_image + '\'' +
                ", resource_url='" + resource_url + '\'' +
                ", community=" + community +
                ", format_quantity=" + format_quantity +
                '}';
    }
}
