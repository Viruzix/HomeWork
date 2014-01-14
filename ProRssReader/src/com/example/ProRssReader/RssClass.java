package com.example.ProRssReader;

import java.util.ArrayList;

/**
 * Created by ViruZ on 13.01.14.
 */
public class RssClass {
    private long id;
    private String feedName;
    private String feedUrl;
    private ArrayList<RssOneItem> data;
    private long lastUpdate;

    public RssClass(long id, String feedName, String feedUrl, ArrayList<RssOneItem> data, long lastUpdate) {
        this.id = id;
        this.feedName = feedName;
        this.feedUrl = feedUrl;
        this.data = data;
        this.lastUpdate = lastUpdate;
    }

    public long getId() {
        return id;
    }

    public String getFeedName() {
        return feedName;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public ArrayList<RssOneItem> getData() {
        return data;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
