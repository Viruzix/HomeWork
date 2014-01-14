package com.example.ProRssReader;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Hedgehog on 13.01.14.
 */
public class UpdateContent extends IntentService {
    public static final String TAG = UpdateContent.class.toString();
    public static final String UPDATE_DONE = TAG.concat(":UPDATE_DONE");
    public static final String ALL_UPDATE_DONE = TAG.concat(":ALL_UPDATE_DONE");
    private static final String FORCED_ACTION = TAG.concat(":FORCED");
    private static final String PLANNED_ACTION = TAG.concat(":PLANNED");
    public static final String NO_INTERNET_CONNECTION = TAG.concat(":FAIL");

    private static final long DELAY = 300000;
    private static final int HOUR = 3600000;
    ArrayList<RssOneItem> feedList = new ArrayList<RssOneItem>();
    private DBHelper dbHelper;
    private DataBase dataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(this);
        dataBase = new DataBase(dbHelper.getWritableDatabase());
    }

    public UpdateContent() {
        super("UpdateContent");
    }

    public static void ensureUpdating(Context context, boolean now) {
        AlarmManager manager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        manager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                now ? 0 : DELAY,
                DELAY,
                PendingIntent.getService(
                        context,
                        0,
                        new Intent(context, UpdateContent.class)
                                .setAction(PLANNED_ACTION),
                        PendingIntent.FLAG_UPDATE_CURRENT
                )
        );
    }

    public static void requestUpdate(Context context, boolean force) {
        context.startService(
                new Intent(context, UpdateContent.class).setAction(force ? FORCED_ACTION : PLANNED_ACTION).putExtra("feed_id", 0)
        );
    }

    public static void requestFeedUpdate(Context context, boolean force, long id) {
        context.startService(
                new Intent(context, UpdateContent.class).setAction(force ? FORCED_ACTION : PLANNED_ACTION).putExtra("feed_id", id)
        );
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        long feed_id = intent.getExtras().getLong("feed_id");
        Log.w(TAG, "Start Intent");
        if (isNetworkConnected()) {
            if (FORCED_ACTION.equals(action)) {
                if (feed_id == 0)
                    update(true);
                else
                    updateFeed(true, feed_id);
            } else if (PLANNED_ACTION.equals(action)) {
                update(false);
            }
        } else {
            sendBroadcast(new Intent(NO_INTERNET_CONNECTION));
        }
    }

    public void update(boolean force) {
        long now = System.currentTimeMillis();
        SaxParser saxParser1;
        int i = 0;
        for (RssClass feed : dataBase.getAll()) {
            if (force || now - feed.getLastUpdate() >= HOUR) {
                try {
                    URL url = new URL(feed.getFeedUrl());
                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    SAXParser saxParser = factory.newSAXParser();
                    saxParser1 = new SaxParser();
                    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setConnectTimeout(15000);
                    if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new Exception("Some problem with HttpConnecion");
                    }
                    InputStream inputStream = httpConnection.getInputStream();
                    BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream));
                    String tmp = buffReader.readLine().toString();
                    String encoding = tmp.substring(tmp.indexOf("encoding") + 10, tmp.indexOf("?>") - 1);

                    url = new URL(feed.getFeedUrl());
                    httpConnection = (HttpURLConnection) url.openConnection();
                    httpConnection.setConnectTimeout(15000);
                    inputStream = httpConnection.getInputStream();

                    Reader reader = new InputStreamReader(inputStream, encoding);
                    InputSource is = new InputSource(reader);
                    is.setEncoding(encoding);
                    saxParser.parse(is, saxParser1);
                    feedList = saxParser1.getFeedList();
                    dataBase.updateFeed(feed.getId(), feedList, now);
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                sendBroadcast(new Intent(ALL_UPDATE_DONE).putExtra("count", i));
            }
        }
    }

    public void updateFeed(boolean force, long id) {
        long now = System.currentTimeMillis();
        SaxParser saxParser1;
        RssClass rssClass = dataBase.getRss(id);
        if (force || now - rssClass.getLastUpdate() >= HOUR) {
            try {
                URL url = new URL(rssClass.getFeedUrl());
                SAXParserFactory factory = SAXParserFactory.newInstance();
                SAXParser saxParser = factory.newSAXParser();
                saxParser1 = new SaxParser();
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setConnectTimeout(15000);
                if (httpConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new Exception("Some problem with HttpConnecion");
                }
                InputStream inputStream = httpConnection.getInputStream();
                BufferedReader buffReader = new BufferedReader(new InputStreamReader(inputStream));
                String tmp = buffReader.readLine().toString();
                String encoding = tmp.substring(tmp.indexOf("encoding") + 10, tmp.indexOf("?>") - 1);

                url = new URL(rssClass.getFeedUrl());
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setConnectTimeout(15000);
                inputStream = httpConnection.getInputStream();

                Reader reader = new InputStreamReader(inputStream, encoding);
                InputSource is = new InputSource(reader);
                is.setEncoding(encoding);
                saxParser.parse(is, saxParser1);
                feedList = saxParser1.getFeedList();
                dataBase.updateFeed(rssClass.getId(), feedList, now);
                sendBroadcast(new Intent(UPDATE_DONE));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
