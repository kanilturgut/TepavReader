package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.tepav.reader.R;
import com.tepav.reader.adapter.BlogListAdapter;
import com.tepav.reader.adapter.BlogPagerAdapter;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.WrapContentHeightViewPager;
import com.tepav.reader.helpers.pagerindicator.CirclePageIndicator;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.Blog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 12:37
 */
public class BlogActivity extends FragmentActivity {

    Context context;

    SwipeListView swipeListViewOfBlog;
    WrapContentHeightViewPager viewPagerOfBlog;
    BlogPagerAdapter blogPagerAdapter;
    CirclePageIndicator circlePageIndicator;

    String[] urls = new String[3];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
        context = this;

        viewPagerOfBlog = (WrapContentHeightViewPager) findViewById(R.id.blogPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circleIndicatorOfBlogPager);

        //swipe list view of blog
        swipeListViewOfBlog = (SwipeListView) findViewById(R.id.swipeListViewOfBlog);
        swipeListViewOfBlog.setAdapter(new BlogListAdapter(context, 1));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageNumber", 1);
        } catch (JSONException e) {
            //this will return pageNumber:1
            jsonObject = null;
            e.printStackTrace();
        }

        AQuery aQuery = new AQuery(context);
        aQuery.post(HttpURL.createURL(HttpURL.blog), jsonObject, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray object, AjaxStatus status) {

                for (int i = 0 ; i < 3; i++) {
                    try {
                        urls[i] = Blog.fromJSON(object.getJSONObject(i)).getPimage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                blogPagerAdapter = new BlogPagerAdapter(getSupportFragmentManager(), context, urls);
                viewPagerOfBlog.setAdapter(blogPagerAdapter);
                circlePageIndicator.setViewPager(viewPagerOfBlog);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //clean the file cache with advance option
        long triggerSize = 3000000; //starts cleaning when cache size is larger than 3M
        long targetSize = 2000000;      //remove the least recently used files until cache size is less than 2M
        AQUtility.cleanCacheAsync(this, triggerSize, targetSize);
    }
}