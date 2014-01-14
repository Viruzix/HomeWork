package com.example.ProRssReader;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hedgehog on 13.01.14.
 */
public class RssOneItem implements Serializable {
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy  HH:mm");

    private String title;
    private String description;
    private Date pubDate;
    private String link;
    private int read = 0;
    private long feed_id;
    private long id;


    public RssOneItem(String title, String description, String link, long pubDate, int read, long feed_id, long id) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.pubDate = new Date(pubDate);
        this.read = read;
        this.feed_id = feed_id;
        this.id = id;
    }

    public RssOneItem(String title, String description, String link, String pubDate) {
        this.title = title;
        this.description = description;
        this.link = link;
        try {
            this.pubDate = new Date(pubDate);
        } catch (Exception e) {
            this.pubDate = new Date(System.currentTimeMillis());
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setRead() {
        this.read = 1;
    }

    public int isRead() {
        return read;
    }

    public long getFeed_id() {
        return feed_id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy  hh:mm");
        return getTitle() + "\n  " + sdf.format(this.getPubDate());
    }
}
