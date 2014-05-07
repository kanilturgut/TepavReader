package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Util;
import com.tepav.reader.helpers.popup.QuickAction;
import com.tepav.reader.model.Blog;
import com.tepav.reader.util.AlertDialogManager;

/**
 * Author : kanilturgut
 * Date : 21.04.2014
 * Time : 10:47
 */
public class BlogDetails extends Activity implements View.OnClickListener {

    Context context;
    DBHandler dbHandler;
    QuickAction quickAction;

    Blog blog;

    WebView webView;
    TextView titleOfBlog, timeOfBlog;

    LinearLayout llHeaderBack, llFooterLike, llFooterAlreadyLiked, llFooterShare, llFooterAddToList;
    RelativeLayout rlFooter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);
        this.context = this;

        blog = (Blog) getIntent().getSerializableExtra("class");
        dbHandler = DBHandler.getInstance(context);
        quickAction = new QuickAction(context, dbHandler, blog);

        llFooterLike = (LinearLayout) findViewById(R.id.llFooterLike);
        llFooterAlreadyLiked = (LinearLayout) findViewById(R.id.llFooterAlreadyLiked);
        llFooterShare = (LinearLayout) findViewById(R.id.llFooterShare);
        llFooterAddToList = (LinearLayout) findViewById(R.id.llFooterAddToList);
        llHeaderBack = (LinearLayout) findViewById(R.id.llHeaderBack);
        rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);

        llFooterLike.setOnClickListener(this);
        llFooterAlreadyLiked.setOnClickListener(this);
        llFooterShare.setOnClickListener(this);
        llFooterAddToList.setOnClickListener(this);
        llHeaderBack.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.wvBlogDetailContentOfBlog);
        webView.loadData(blog.getBcontent(), "text/html; charset=UTF-8", null);

        titleOfBlog = (TextView) findViewById(R.id.tvBlogDetailTitleOfBlog);
        titleOfBlog.setText(blog.getBtitle());

        timeOfBlog = (TextView) findViewById(R.id.tvBlogDetailTimeInformationOfBlog);
        timeOfBlog.setText(blog.getBdate());
    }

    @Override
    public void onClick(View view) {

        if (Splash.isUserLoggedIn) {

            if (view == llFooterLike) {

                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterAlreadyLiked) {
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterShare) {
                String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } else if (view == llFooterAddToList) {

                quickAction.show(rlFooter);
                quickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);
            } else if (view == llHeaderBack) {
                onBackPressed();
            }

        } else {
            if (view == llHeaderBack) {
                onBackPressed();
            }

            AlertDialogManager alertDialogManager = new AlertDialogManager();
            alertDialogManager.showLoginDialog(context, getString(R.string.warning), getString(R.string.must_log_in), false);

        }

    }
}