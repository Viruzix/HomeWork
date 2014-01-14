package com.example.ProRssReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ViruZ on 13.01.14.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "feed.db";
    private static final int VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DataBase.init(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        DataBase.drop(db);
        onCreate(db);
    }
}
