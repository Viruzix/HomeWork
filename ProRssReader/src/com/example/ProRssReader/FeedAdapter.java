package com.example.ProRssReader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by ViruZ on 13.01.14.
 */
public class FeedAdapter extends BaseAdapter {
    DataBase dataBase;
    private Context context;
    private List<RssClass> currentState;

    public FeedAdapter(Context context, DataBase dataBase) {
        this.context = context;
        this.dataBase = dataBase;
        this.currentState = dataBase.getAll();
    }

    public void refresh() {
        currentState = dataBase.getAll();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return currentState.size();
    }

    @Override
    public Object getItem(int position) {
        return currentState.get(position);
    }

    @Override
    public long getItemId(int position) {
        return currentState.get(position).getId();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.rss_item, parent, false);
        RssClass rssClass = currentState.get(position);

        TextView feedName = (TextView) view.findViewById(R.id.feedName);
        feedName.setTextSize(20);
        feedName.setText(rssClass.getFeedName());

        TextView dateText = (TextView) view.findViewById(R.id.lastUpdate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy  HH:mm");
        dateText.setText("Last update: " + " " + sdf.format(rssClass.getLastUpdate()) + ", " +
                "New" + " " + dataBase.getUnreadArticles(rssClass.getId()) + "/" + rssClass.getData().size());

        return view;
    }


}
