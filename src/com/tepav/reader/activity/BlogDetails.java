package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.popup.CommentWindows;
import com.tepav.reader.helpers.popup.PopupAdjustFontSize;
import com.tepav.reader.helpers.popup.QuickActionForList;
import com.tepav.reader.helpers.popup.QuickActionForPost;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.DBData;
import com.tepav.reader.operation.LikeOperation;
import com.tepav.reader.operation.ShareOperation;
import com.tepav.reader.util.AlertDialogManager;
import com.tepav.reader.util.Util;
import org.json.JSONException;

/**
 * Author : kanilturgut
 * Date : 21.04.2014
 * Time : 10:47
 */
public class BlogDetails extends Activity implements View.OnClickListener {

    final String TAG = "BlogDetails";
    Context context;
    DBHandler dbHandler;
    QuickActionForPost quickAction;
    QuickActionForList quickActionForList;
    CommentWindows commentWindows;
    int fromWhere, listType;

    Blog blog;

    WebView webView;
    TextView titleOfBlog, timeOfBlog, tvComment;
    LinearLayout llHeaderBack, llFooterLike, llFooterAlreadyLiked, llFooterShare, llFooterAddToList;
    ImageView ivAdjustFontSize;

    RelativeLayout rlFooter;
    View viewTransparent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_details);
        this.context = this;

        try {
            blog = Blog.fromDBData((DBData) getIntent().getSerializableExtra("class"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fromWhere = getIntent().getIntExtra("fromWhere", -1);
        listType = getIntent().getIntExtra("listType", -1);
        dbHandler = DBHandler.getInstance(context);

        viewTransparent = findViewById(R.id.viewTransparent);

        if (fromWhere == Constant.DETAILS_FROM_LIST) {
            quickActionForList = new QuickActionForList(context, dbHandler, blog, listType);
            quickActionForList.setOnDismissListener(new QuickActionForList.OnDismissListener() {
                @Override
                public void onDismiss() {
                    viewTransparent.setVisibility(View.INVISIBLE);
                }
            });
        } else if (fromWhere == Constant.DETAILS_FROM_POST) {
            quickAction = new QuickActionForPost(context, dbHandler, blog);
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
        rlFooter = (RelativeLayout) findViewById(R.id.rlFooter);
        tvComment = (TextView) findViewById(R.id.tvComment);
        ivAdjustFontSize = (ImageView)findViewById(R.id.ivAdjustFontSize);

        llFooterLike.setOnClickListener(this);
        llFooterAlreadyLiked.setOnClickListener(this);
        llFooterShare.setOnClickListener(this);
        llFooterAddToList.setOnClickListener(this);
        llHeaderBack.setOnClickListener(this);
        tvComment.setOnClickListener(this);
        ivAdjustFontSize.setOnClickListener(this);

        webView = (WebView) findViewById(R.id.wvBlogDetailContentOfBlog);
        webView.loadData(blog.getBcontent(), "text/html; charset=UTF-8", null);

        titleOfBlog = (TextView) findViewById(R.id.tvBlogDetailTitleOfBlog);
        titleOfBlog.setText(blog.getBtitle());

        timeOfBlog = (TextView) findViewById(R.id.tvBlogDetailTimeInformationOfBlog);
        timeOfBlog.setText(blog.getBdate());

        //Util.checkIfIsContain(dbHandler, DBHandler.TABLE_LIKE, blog.getId(), llFooterLike, llFooterAlreadyLiked);
    }

    @Override
    public void onClick(View view) {

        if (Splash.isUserLoggedIn) {

            if (view == llFooterLike) {

                try {
                    dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_LIKE);
                    LikeOperation.doLike(Blog.toDBData(blog));
                } catch (JSONException e) {
                    Logs.e(TAG, "ERROR on like", e);
                }

                //Util.changeVisibility(llFooterLike);
                //Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterAlreadyLiked) {
                Util.changeVisibility(llFooterLike);
                Util.changeVisibility(llFooterAlreadyLiked);

            } else if (view == llFooterShare) {

                try {
                    ShareOperation.doShare(context, Blog.toDBData(blog));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
            } else if (view == llFooterAddToList) {

                viewTransparent.setVisibility(View.VISIBLE);

                if (quickActionForList != null) {
                    quickActionForList.show(rlFooter);
                    quickActionForList.setAnimStyle(QuickActionForPost.ANIM_GROW_FROM_CENTER);
                } else if (quickAction != null) {
                    quickAction.show(rlFooter);
                    quickAction.setAnimStyle(QuickActionForPost.ANIM_GROW_FROM_CENTER);
                }

            } else if (view == llHeaderBack) {
                onBackPressed();
            }else if (view == tvComment) {

                try {
                    commentWindows = new CommentWindows(context, Blog.toDBData(blog));
                    commentWindows.setOnDismissListener(new CommentWindows.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            viewTransparent.setVisibility(View.INVISIBLE);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                viewTransparent.setVisibility(View.VISIBLE);
                commentWindows.setAnimStyle(CommentWindows.ANIM_GROW_FROM_CENTER);
                commentWindows.show(view);
            } else if (view == ivAdjustFontSize) {
                Logs.i(TAG, "ivAdjustFontSize");
                PopupAdjustFontSize popupAdjustFontSize = new PopupAdjustFontSize(context, webView);
                popupAdjustFontSize.setAnimStyle(PopupAdjustFontSize.ANIM_GROW_FROM_CENTER);
                popupAdjustFontSize.show(rlFooter);
            }

        } else {
            if (view == llHeaderBack) {
                onBackPressed();
            } else if (view == ivAdjustFontSize) {
                Logs.i(TAG, "ivAdjustFontSize");
                PopupAdjustFontSize popupAdjustFontSize = new PopupAdjustFontSize(context, webView);
                popupAdjustFontSize.setAnimStyle(PopupAdjustFontSize.ANIM_GROW_FROM_CENTER);
                popupAdjustFontSize.show(rlFooter);
            } else {
                AlertDialogManager alertDialogManager = new AlertDialogManager();
                alertDialogManager.showLoginDialog(context, getString(R.string.warning), getString(R.string.must_log_in), false);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Tracker tracker = GoogleAnalytics.getInstance(context).getTracker("UA-51720342-1");
        tracker.set(Fields.SCREEN_NAME, "Günlük Detay Ekranı");
        tracker.send(MapBuilder.createAppView().build());

    }
}