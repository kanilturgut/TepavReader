package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.helpers.Util;
import com.tepav.reader.model.News;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 19:24
 */
public class NewsDetails extends Activity {

    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        this.context = this;

        News news = (News) getIntent().getSerializableExtra("class");

        WebView view = (WebView) findViewById(R.id.wvNewsDetailContentOfNews);
        view.loadData(news.getHcontent(), "text/html; charset=UTF-8", null);

        TextView baslik = (TextView) findViewById(R.id.tvNewsDetailTitleOfNews);
        baslik.setText(news.getHtitle());


        TextView time = (TextView) findViewById(R.id.tvNewsDetailTimeInformationOfNews);
        time.setText(news.getHdate());
    }
}