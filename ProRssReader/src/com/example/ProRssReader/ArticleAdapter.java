package com.example.ProRssReader;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ViruZ on 13.01.14.
 */
public class ArticleAdapter extends BaseAdapter {
    DataBase dataBase;
    private Context context;
    long id;
    private ArrayList<RssOneItem> currentState;

    public ArticleAdapter(Context context, DataBase dataBase, long id) {
        this.context = context;
        this.dataBase = dataBase;
        this.id = id;
        currentState = dataBase.getArticles(id);
    }

    public void refresh() {
        currentState = dataBase.getArticles(id);
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
        view = inflater.inflate(R.layout.list_item, parent, false);

        TextView titleText = (TextView) view.findViewById(R.id.titleView);
        titleText.setText(currentState.get(position).getTitle());

        TextView dateText = (TextView) view.findViewById(R.id.dateView);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy  HH:mm");
        dateText.setText(sdf.format(currentState.get(position).getPubDate()));

        if (currentState.get(position).isRead() == 1) {
            titleText.setTypeface(Typeface.DEFAULT);
            titleText.setTextColor(Color.GRAY);
            dateText.setTextColor(Color.GRAY);
        }

        return view;
    }

}
