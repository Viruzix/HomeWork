package com.example.ProRssReader;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by ViruZ on 13.01.14.
 */
public class Description extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rss_item_detail);

        String title = getIntent().getExtras().getCharSequence("selectedTitle").toString();
        String description = getIntent().getExtras().getCharSequence("selectedDescription").toString();
        String link = getIntent().getExtras().getCharSequence("selectedLink").toString();
        String pubDate = getIntent().getExtras().getCharSequence("selectedPubDate").toString();
        setTitle(title);

        RssOneItem rssOneItem = new RssOneItem(title, description, link, pubDate);

        showDetail(rssOneItem);

    }

    private void showDetail(RssOneItem rssOneItem) {
        String title = rssOneItem.toString();
        String content = "<b>" + title + "</b>" + "<br>" + "<br>" + rssOneItem.getDescription()
                + "<br>" + "<a href=\"" + rssOneItem.getLink() + "\">" + "Go to the website</a>";

        WebView wb = (WebView) findViewById(R.id.webView);

        wb.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
    }
}
