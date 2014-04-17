package com.tepav.reader.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.tepav.reader.R;
import com.tepav.reader.adapter.NewsListAdapter;
import com.tepav.reader.adapter.NewsPagerAdapter;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.WrapContentHeightViewPager;
import com.tepav.reader.helpers.pagerindicator.CirclePageIndicator;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.News;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Author : kanilturgut
 * Date : 16.04.2014
 * Time : 15:19
 */
public class NewsActivity extends FragmentActivity {

    Context context;

    SwipeListView swipeListViewOfNews;
    WrapContentHeightViewPager viewPagerOfNews;
    NewsPagerAdapter newsPagerAdapter;
    CirclePageIndicator circlePageIndicator;

    String[] urls = new String[3];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        context = this;

        viewPagerOfNews = (WrapContentHeightViewPager) findViewById(R.id.newsPager);
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.circleIndicatorOfNewsPager);

        //swipe list view of news
        swipeListViewOfNews = (SwipeListView) findViewById(R.id.swipeListViewOfNews);
        swipeListViewOfNews.setAdapter(new NewsListAdapter(context, 1));

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageNumber", 1);
        } catch (JSONException e) {
            //this will return pageNumber:1
            jsonObject = null;
            e.printStackTrace();
        }

        AQuery aQuery = new AQuery(context);
        aQuery.post(HttpURL.createURL(HttpURL.news), jsonObject, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray object, AjaxStatus status) {

                for (int i = 0 ; i < 3; i++) {
                    try {
                        urls[i] = News.fromJSON(object.getJSONObject(i)).getHimage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                newsPagerAdapter = new NewsPagerAdapter(getSupportFragmentManager(), context, urls);
                viewPagerOfNews.setAdapter(newsPagerAdapter);
                circlePageIndicator.setViewPager(viewPagerOfNews);
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