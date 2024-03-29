package com.timotheemato.rssfeedaggregator.network.models;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by tmato on 1/21/17.
 */

public class Subscription {
    @Expose private Integer id;
    @Expose private String url;
    @Expose private String title;
    @Expose private String description;
    @Expose private String author;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Subscription(Integer id, String title, String description) {
        this.title = title;
        this.description = description;
        this.id = id;
    }
}
