package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Util;
import com.tepav.reader.model.Blog;
import org.json.JSONException;

/**
 * Author : kanilturgut
 * Date : 21.04.2014
 * Time : 10:47
 */
public class BlogDetails extends Activity implements View.OnClickListener{

    Context context;
    DBHandler dbHandler;

    Blog blog;

    WebView webView;
    TextView titleOfBlog, timeOfBlog;

    LinearLayout llFooterLike, llFooterAlreadyLiked, llFooterShare, llFooterAddToList, llFooterAddedToList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);
        this.context = this;

        blog = (Blog) getIntent().getSerializableExtra("class");
        dbHandler = DBHandler.getInstance(context);

        llFooterLike = (LinearLayout) findViewById(R.id.llFooterLike);
        llFooterAlreadyLiked = (LinearLayout) findViewById(R.id.llFooterAlreadyLiked);
        llFooterShare = (LinearLayout) findViewById(R.id.llFooterShare);
        llFooterAddToList = (LinearLayout) findViewById(R.id.llFooterAddToList);
        llFooterAddedToList = (LinearLayout) findViewById(R.id.llFooterAddedToList);

        llFooterLike.setOnClickListener(this);
        llFooterAlreadyLiked.setOnClickListener(this);
        llFooterShare.setOnClickListener(this);
        llFooterAddToList.setOnClickListener(this);
        llFooterAddedToList.setOnClickListener(this);

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, blog.getId(), llFooterLike, llFooterAlreadyLiked);
        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, blog.getId(), llFooterAddToList, llFooterAddedToList);

        webView = (WebView) findViewById(R.id.wvBlogDetailContentOfBlog);
        webView.loadData(blog.getBcontent(), "text/html; charset=UTF-8", null);

        titleOfBlog = (TextView) findViewById(R.id.tvBlogDetailTitleOfBlog);
        titleOfBlog.setText(blog.getBtitle());

        timeOfBlog = (TextView) findViewById(R.id.tvBlogDetailTimeInformationOfBlog);
        timeOfBlog.setText(blog.getBdate());
    }

    @Override
    public void onClick(View view) {

        if (view == llFooterLike) {

            try {
                dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (view == llFooterAlreadyLiked) {
            try {
                dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (view == llFooterShare) {
            String url = Constant.SHARE_BLOG + blog.getGunluk_id();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT,  blog.getBtitle() + " " + url);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        } else if (view == llFooterAddToList) {
            try {
                dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                Util.changeVisibility(llFooterAddToList);
                Util.changeVisibility(llFooterAddedToList);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (view == llFooterAddedToList) {

            try {
                dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                Util.changeVisibility(llFooterAddToList);
                Util.changeVisibility(llFooterAddedToList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}