package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Util;
import com.tepav.reader.model.File;
import com.tepav.reader.model.News;
import org.json.JSONException;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 19:24
 */
public class NewsDetails extends Activity implements View.OnClickListener {

    Context context;
    DBHandler dbHandler;

    News news;

    WebView webView;
    TextView titleOfNews, timeOfNews;

    LinearLayout llHeaderBack, llFooterLike, llFooterAlreadyLiked, llFooterShare, llFooterAddToList, llFooterAddedToList, filesLayout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        this.context = this;

        news = (News) getIntent().getSerializableExtra("class");
        dbHandler = DBHandler.getInstance(context);

        llFooterLike = (LinearLayout) findViewById(R.id.llFooterLike);
        llFooterAlreadyLiked = (LinearLayout) findViewById(R.id.llFooterAlreadyLiked);
        llFooterShare = (LinearLayout) findViewById(R.id.llFooterShare);
        llFooterAddToList = (LinearLayout) findViewById(R.id.llFooterAddToList);
        llFooterAddedToList = (LinearLayout) findViewById(R.id.llFooterAddedToList);
        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        filesLayout = (LinearLayout) findViewById(R.id.filesLayout);

        llFooterLike.setOnClickListener(this);
        llFooterAlreadyLiked.setOnClickListener(this);
        llFooterShare.setOnClickListener(this);
        llFooterAddToList.setOnClickListener(this);
        llFooterAddedToList.setOnClickListener(this);
        llHeaderBack.setOnClickListener(this);

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, news.getId(), llFooterLike, llFooterAlreadyLiked);
        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, news.getId(), llFooterAddToList, llFooterAddedToList);

        webView = (WebView) findViewById(R.id.wvNewsDetailContentOfNews);
        webView.loadData(news.getHcontent(), "text/html; charset=UTF-8", null);

        titleOfNews = (TextView) findViewById(R.id.tvNewsDetailTitleOfNews);
        titleOfNews.setText(news.getHtitle());

        timeOfNews = (TextView) findViewById(R.id.tvNewsDetailTimeInformationOfNews);
        timeOfNews.setText(news.getHdate());

        for (File file : news.getFiles()) {
            filesLayout.addView(createTextView(file));
        }

    }

    TextView createTextView(final File file) {

        TextView textView = new TextView(this);
        textView.setId(R.id.fileLink);
        textView.setTextColor(Color.BLUE);
        textView.setTextSize(20);
        textView.setText(file.getName());
        textView.setTag(file);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openPDFIntent = new Intent(context, PDFDownloadActivity.class);
                openPDFIntent.putExtra("file_name", file.getName());
                openPDFIntent.putExtra("file_url", file.getUrl());
                startActivity(openPDFIntent);
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 5, 0, 5);
        textView.setLayoutParams(lp);

        return textView;

    }

    @Override
    public void onClick(View view) {

        if (view == llFooterLike) {

            try {
                dbHandler.insert(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (view == llFooterAlreadyLiked) {
            try {
                dbHandler.delete(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (view == llFooterShare) {
            String url = Constant.SHARE_NEWS + news.getHaber_id();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getHtitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, news.getHtitle() + " " + url);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        } else if (view == llFooterAddToList) {
            try {
                dbHandler.insert(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                Util.changeVisibility(llFooterAddToList);
                Util.changeVisibility(llFooterAddedToList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (view == llFooterAddedToList) {

            try {
                dbHandler.delete(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                Util.changeVisibility(llFooterAddToList);
                Util.changeVisibility(llFooterAddedToList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (view == llHeaderBack) {
            onBackPressed();
        }
    }
}