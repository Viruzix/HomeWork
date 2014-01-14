package com.example.ProRssReader;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by ViruZ on 13.01.14.
 */
public class CreateNew extends Activity {
    private static final String HTTP = "http://";

    private EditText url;
    private EditText name;
    private DBHelper dbHelper;
    private DataBase dataBase;
    private long id = 1;
    private Context context;
    private int edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_new);
        context = this;

        Bundle extras = getIntent().getExtras();

        url = (EditText) findViewById(R.id.editAddress);
        name = (EditText) findViewById(R.id.editName);
        Button addButton = (Button) findViewById(R.id.addButton);
        dbHelper = new DBHelper(this);
        dataBase = new DataBase(dbHelper.getWritableDatabase());
        edit = extras.getInt("edit");
        try {
            id = extras.getLong("_id");
            url.setText(dataBase.getRss(id).getFeedUrl());
            name.setText(dataBase.getRss(id).getFeedName());
        } catch (Exception e) {
            e.printStackTrace();
        }


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url.getText() != null) {
                    String _url = url.getText().toString();
                    if (!_url.substring(0, 7).equals(HTTP))
                        _url = HTTP + _url;
                    String _name = name.getText().toString();
                    if (_name.equals("")) {
                        _name = _url;
                    }
                    RssClass rssClass = new RssClass(id, _name, _url, null, 1);
                    if (edit == 0)
                        UpdateContent.requestFeedUpdate(context, true, dataBase.insertNewFeed(rssClass));
                    else {
                        dataBase.updateFeed(id, _name, _url);
                        UpdateContent.requestFeedUpdate(context, true, id);
                    }
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
}
