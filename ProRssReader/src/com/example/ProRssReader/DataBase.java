package com.example.ProRssReader;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ViruZ on 13.01.14.
 */
public class DataBase {
    public static final String TABLE_FEEDS_NAME = "feeds";
    public static final String TABLE_ARTICLES_NAME = "articles";
    public static final String FEED_NAME = "name";
    public static final String FEED_URL = "url";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String PUBDATE = "pubdate";
    public static final String LINK = "link";
    public static final String READED = "readed";
    public static final String LAST_UPDATE = "last_update";
    public static final String FEED_ID = "feed_id";

    private static final String CREATE_FEEDS_TABLE = String.format(
            "create table %s (" +
                    "_id integer not null primary key autoincrement," +
                    "%s text not null," +
                    "%s text default null," +
                    "%s integer default 0" +
                    ")",
            TABLE_FEEDS_NAME,
            FEED_NAME,
            FEED_URL,
            LAST_UPDATE
    );

    private static final String CREATE_ARTICLES_TABLE = String.format(
            "create table %s (" +
                    "_id integer not null primary key autoincrement," +
                    "%s integer not null," +
                    "%s text not null," +
                    "%s text not null," +
                    "%s integer not null," +
                    "%s text not null," +
                    "%s boolean default false" +
                    ")",
            TABLE_ARTICLES_NAME,
            FEED_ID,
            TITLE,
            DESCRIPTION,
            PUBDATE,
            LINK,
            READED
    );

    private static final String DROP_TABLE_QUERY = "drop table if exists " + TABLE_FEEDS_NAME;
    private static final String SELECT_ALL_FEEDS_QUERY = "select * from " + TABLE_FEEDS_NAME;
    private static final String SELECT_FEED_QUERY = "select * from " + TABLE_FEEDS_NAME + " todo where _id = ?";
    private static final String SELECT_UNREAD_ARTICLE_QUERY = "select * from " + TABLE_ARTICLES_NAME + " todo where " + FEED_ID + " = ? and " + READED + " = 0";
    private static final String SELECT_ARTICLE_TO_UPDATE_QUERY = "select * from " + TABLE_ARTICLES_NAME + " todo where " + TITLE + " = ?";

    public static void init(SQLiteDatabase db) {
        db.execSQL(CREATE_FEEDS_TABLE);
        db.execSQL(CREATE_ARTICLES_TABLE);
    }

    public static void drop(SQLiteDatabase db) {
        db.execSQL(DROP_TABLE_QUERY);
    }

    private final SQLiteDatabase db;

    public DataBase(SQLiteDatabase db) {
        this.db = db;
    }

    public long insertNewFeed(RssClass rssClass) {
        ContentValues values = new ContentValues(2);
        values.put(FEED_NAME, rssClass.getFeedName());
        values.put(FEED_URL, rssClass.getFeedUrl());
        return db.insertOrThrow(TABLE_FEEDS_NAME, null, values);
    }

    public void deleteFeed(long id) {
        db.delete(TABLE_FEEDS_NAME, "_id = ?", new String[]{Long.toString(id)});
        db.delete(TABLE_ARTICLES_NAME, FEED_ID + " = ?", new String[]{Long.toString(id)});
    }

    public void deleteReaded(long id) {
        db.delete(TABLE_ARTICLES_NAME, FEED_ID + " = ? and " + READED + " = 1", new String[]{Long.toString(id)});
    }

    public void updateFeed(long id, ArrayList<RssOneItem> articles, long updateTime) {
        ContentValues values = new ContentValues(1);
        values.put(LAST_UPDATE, updateTime);
        db.update(TABLE_FEEDS_NAME, values, "_id = ?", new String[]{Long.toString(id)});
        values = new ContentValues(6);
        for (RssOneItem article : articles) {
            long article_id = searchByTitle(article.getTitle());
            if (article_id == 0) {
                values.put(FEED_ID, id);
                values.put(TITLE, article.getTitle());
                values.put(DESCRIPTION, article.getDescription());
                values.put(PUBDATE, article.getPubDate().getTime());
                values.put(LINK, article.getLink());
                values.put(READED, article.isRead());
                db.insertOrThrow(TABLE_ARTICLES_NAME, null, values);
            }
        }
    }

    private long searchByTitle(String title) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_ARTICLE_TO_UPDATE_QUERY, new String[] {title});
            long result = 0;
            while (cursor.moveToNext()) {
                result = cursor.getLong(0);
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateFeed(long id, String feedName, String feedUrl) {
        ContentValues values = new ContentValues(2);
        values.put(FEED_NAME, feedName);
        values.put(FEED_URL, feedUrl);
        db.update(TABLE_FEEDS_NAME, values, "_id = ?", new String[]{Long.toString(id)});
    }

    public List<RssClass> getAll() {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_ALL_FEEDS_QUERY, null);
            List<RssClass> result = new ArrayList<RssClass>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                String feedName = cursor.getString(1);
                String feedUrl = cursor.getString(2);
                long updated = cursor.getLong(3);


                result.add(new RssClass(
                        id,
                        feedName,
                        feedUrl,
                        getArticles(id),
                        updated
                ));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public RssClass getRss(long rowId) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_FEED_QUERY, new String[]{Long.toString(rowId)});
            RssClass result = null;
            if (cursor != null) {
                cursor.moveToFirst();
                long id = cursor.getLong(0);
                String feedName = cursor.getString(1);
                String feedUrl = cursor.getString(2);
                long updated = cursor.getLong(3);

                result = new RssClass(
                        id,
                        feedName,
                        feedUrl,
                        getArticles(id),
                        updated
                );
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public int getUnreadArticles(long feed_id) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(SELECT_UNREAD_ARTICLE_QUERY, new String[]{Long.toString(feed_id)});
            int result = 0;
            while (cursor.moveToNext()) {
                result++;
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ArrayList<RssOneItem> getArticles(long feed_id) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_ARTICLES_NAME, new String[]{"_id", FEED_ID, TITLE, DESCRIPTION, PUBDATE, LINK, READED},
                    FEED_ID + " = ?", new String[]{Long.toString(feed_id)}, null, null, READED + ", " + PUBDATE + " DESC", null);
            ArrayList<RssOneItem> result = new ArrayList<RssOneItem>();
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                long _id = cursor.getLong(1);
                String title = cursor.getString(2);
                String description = cursor.getString(3);
                long pubDate = cursor.getLong(4);
                String link = cursor.getString(5);
                int readed = cursor.getInt(6);
                result.add(new RssOneItem(title, description, link, pubDate, readed, _id, id));
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void setRead(long id) {
        ContentValues values = new ContentValues(1);
        values.put(READED, 1);
        db.update(TABLE_ARTICLES_NAME, values, "_id = ?", new String[]{Long.toString(id)});
    }
}
