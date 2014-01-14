package com.example.ProRssReader;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by ViruZ on 13.01.14.
 */
public class MyListsActivity extends ListActivity {
    private static final IntentFilter UPDATE_FILTER = new IntentFilter(UpdateContent.UPDATE_DONE);
    private static final IntentFilter ALL_UPDATE_FILTER = new IntentFilter(UpdateContent.ALL_UPDATE_DONE);
    private static final IntentFilter NO_INTERNET_FILTER = new IntentFilter(UpdateContent.NO_INTERNET_CONNECTION);

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int EDIT_ID = Menu.FIRST + 2;
    private static final int UPDATE_ID = Menu.FIRST + 3;
    private static final int UPDATE_ALL = Menu.FIRST + 4;
    FeedAdapter adapter;
    Context context;

    private DBHelper dbHelper;
    private DataBase dataBase;
    int count = 0;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(context, "Update", Toast.LENGTH_SHORT).show();
        }
    };

    private final BroadcastReceiver no_internet_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    };

    private final BroadcastReceiver all_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.refresh();
            if (count == intent.getExtras().getInt("count")) {
                setProgressBarIndeterminateVisibility(false);
                Toast.makeText(context, "All feeds have been updated!", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.start);
        context = this;
        dbHelper = new DBHelper(this);
        dataBase = new DataBase(dbHelper.getWritableDatabase());
        adapter = new FeedAdapter(this, dataBase);
        setListAdapter(adapter);
        count = dataBase.getAll().size();

        registerForContextMenu(getListView());
        UpdateContent.ensureUpdating(this, true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, "Add");
        menu.add(0, UPDATE_ALL, 0, "Update all");
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case INSERT_ID:
                createFeed();
                return true;
            case UPDATE_ALL:
                setProgressBarIndeterminateVisibility(true);
                UpdateContent.requestUpdate(this, true);
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void createFeed() {
        Intent i = new Intent(this, CreateNew.class);
        i.putExtra("edit", 0);
        startActivity(i);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "Delete");
        menu.add(0, EDIT_ID, 0, "Edit");
        menu.add(0, UPDATE_ID, 0, "Update");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                dataBase.deleteFeed(info.id);
                count--;
                adapter.refresh();
                return true;
            case EDIT_ID:
                AdapterView.AdapterContextMenuInfo edit = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                Intent i = new Intent(this, CreateNew.class);
                i.putExtra("_id", edit.id);
                i.putExtra("edit", 1);
                startActivity(i);
                return true;
            case UPDATE_ID:
                setProgressBarIndeterminateVisibility(true);
                AdapterView.AdapterContextMenuInfo update = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                UpdateContent.requestFeedUpdate(context, true, update.id);
                adapter.refresh();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        count++;
        adapter.refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(receiver, UPDATE_FILTER);
        registerReceiver(all_receiver, ALL_UPDATE_FILTER);
        registerReceiver(no_internet_receiver, NO_INTERNET_FILTER);
        adapter.refresh();
    }

    @Override
    public void onPause() {
        unregisterReceiver(receiver);
        unregisterReceiver(no_internet_receiver);
        unregisterReceiver(all_receiver);
        super.onPause();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, RssListActivity.class);
        i.putExtra("id", id);
        startActivity(i);
    }
}
