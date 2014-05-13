package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.popup.QuickActionForList;
import com.tepav.reader.helpers.popup.QuickActionForPost;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.File;
import com.tepav.reader.model.News;
import com.tepav.reader.operation.LikeOperation;
import com.tepav.reader.util.AlertDialogManager;
import com.tepav.reader.util.Util;
import org.json.JSONException;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 19:24
 */
public class NewsDetails extends Activity implements View.OnClickListener {

    final String TAG = "NewsDetails";
    Context context;
    DBHandler dbHandler;
    QuickActionForPost quickAction;
    QuickActionForList quickActionForList;
    int fromWhere, listType;

    News news;

    WebView webView;
    TextView titleOfNews, timeOfNews;

    LinearLayout llHeaderBack, llFooterLike, llFooterAlreadyLiked, llFooterShare, llFooterAddToList, filesLayout;
    RelativeLayout rlFooter;
    View viewTransparent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        this.context = this;

        try {
            news = News.fromDBData((DBData) getIntent().getSerializableExtra("class"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fromWhere = getIntent().getIntExtra("fromWhere", -1);
        listType = getIntent().getIntExtra("listType", -1);
        dbHandler = DBHandler.getInstance(context);

        viewTransparent = findViewById(R.id.viewTransparent);

        if (fromWhere == Constant.DETAILS_FROM_LIST) {
            quickActionForList = new QuickActionForList(context, dbHandler, news, listType);
            quickActionForList.setOnDismissListener(new QuickActionForList.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewTransparent.setVisibility(View.INVISIBLE);
                }
            });
        }
        else if (fromWhere == Constant.DETAILS_FROM_POST) {
            quickAction = new QuickActionForPost(context, dbHandler, news);
            quickAction.setOnDismissListener(new QuickActionForPost.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewTransparent.setVisibility(View.INVISIBLE);
                }
            });
        }

        llFooterLike = (LinearLayout) findViewById(R.id.llFooterLike);
        llFooterAlreadyLiked = (LinearLayout) findViewById(R.id.llFooterAlreadyLiked);
        llFooterShare = (LinearLayout) findViewById(R.id.llFooterShare);
        llFooterAddToList = (LinearLayout) findViewById(R.id.llFooterAddToList);
        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        filesLayout = (LinearLayout) findViewById(R.id.filesLayout);
        rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);

        llFooterLike.setOnClickListener(this);
        llFooterAlreadyLiked.setOnClickListener(this);
        llFooterShare.setOnClickListener(this);
        llFooterAddToList.setOnClickListener(this);
        llHeaderBack.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.wvNewsDetailContentOfNews);
        webView.loadData(news.getHcontent(), "text/html; charset=UTF-8", null);

        titleOfNews = (TextView) findViewById(R.id.tvNewsDetailTitleOfNews);
        titleOfNews.setText(news.getHtitle());

        timeOfNews = (TextView) findViewById(R.id.tvNewsDetailTimeInformationOfNews);
        timeOfNews.setText(news.getHdate());

        for (File file : news.getFiles()) {
            filesLayout.addView(createTextView(file));
        }

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_LIKE, news.getId(), llFooterLike, llFooterAlreadyLiked);

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

        if (Splash.isUserLoggedIn) {
            if (view == llFooterLike) {

                try {
                    dbHandler.insert(News.toDBData(news), DBHandler.TABLE_LIKE);
                    LikeOperation.doLike(News.toDBData(news));
                } catch (JSONException e) {
                    Logs.e(TAG, "ERROR on like", e);
                }

                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterAlreadyLiked) {
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterShare) {
                String url = Constant.SHARE_NEWS + news.getHaber_id();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getHtitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, news.getHtitle() + " " + url);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } else if (view == llFooterAddToList) {

                viewTransparent.setVisibility(View.VISIBLE);

                if (quickActionForList != null) {
                    quickActionForList.show(rlFooter);
                    quickActionForList.setAnimStyle(QuickActionForList.ANIM_GROW_FROM_CENTER);
                } else if (quickAction != null) {
                    quickAction.show(rlFooter);
                    quickAction.setAnimStyle(QuickActionForPost.ANIM_GROW_FROM_CENTER);
                }
            } else if (view == llHeaderBack) {
                onBackPressed();
            }
        } else {
            if (view == llHeaderBack) {
                onBackPressed();
            } else {
                AlertDialogManager alertDialogManager = new AlertDialogManager();
                alertDialogManager.showLoginDialog(context, getString(R.string.warning), getString(R.string.must_log_in), false);
            }
        }
    }
}