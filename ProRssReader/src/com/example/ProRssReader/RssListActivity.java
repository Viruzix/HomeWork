package com.example.ProRssReader;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Hedgehog on 13.01.14.
 */
public class RssListActivity extends ListActivity {
    private DBHelper dbHelper;
    private DataBase dataBase;
    private Context context;
    private ArticleAdapter adapter;
    long id;

    private static final int DELETE_READ = Menu.FIRST;
    private static final int UPDATE_FEED = Menu.FIRST + 1;

    private static final IntentFilter UPDATE_FILTER = new IntentFilter(UpdateContent.UPDATE_DONE);
    private static final IntentFilter NO_INTERNET_FILTER = new IntentFilter(UpdateContent.NO_INTERNET_CONNECTION);

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
        }
    };

    private final BroadcastReceiver no_internet_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
            Toast.makeText(context, "No internet connetcion!", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.rss_list);
        context = this;

        dbHelper = new DBHelper(this);
        dataBase = new DataBase(dbHelper.getWritableDatabase());
        refreshList();
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        RssOneItem selectedFeedItem = (RssOneItem) getListAdapter().getItem(position);
        dataBase.setRead(id);
        final Intent intent = new Intent(context, Description.class);
        intent.putExtra("selectedTitle", selectedFeedItem.getTitle());
        intent.putExtra("selectedDescription", selectedFeedItem.getDescription());
        intent.putExtra("selectedLink", selectedFeedItem.getLink());
        intent.putExtra("selectedPubDate", selectedFeedItem.getPubDate().toString());
        startActivity(intent);
        adapter.refresh();
    }

    private void refreshList() {
        id = getIntent().getExtras().getLong("id");
        RssClass rssClass = dataBase.getRss(id);
        setTitle(rssClass.getFeedName());
        adapter = new ArticleAdapter(context, dataBase, id);
        setListAdapter(adapter);
        adapter.refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, DELETE_READ, 0, "Remove read");
        menu.add(0, UPDATE_FEED, 0, "Update");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_READ:
                dataBase.deleteReaded(id);
                refreshList();
                return true;
            case UPDATE_FEED:
                setProgressBarIndeterminateVisibility(true);
                UpdateContent.requestFeedUpdate(context, true, id);
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, UPDATE_FILTER);
        registerReceiver(no_internet_receiver, NO_INTERNET_FILTER);
        adapter.refresh();
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        unregisterReceiver(no_internet_receiver);
        super.onPause();
    }

}
