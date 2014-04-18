package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.tepav.reader.R;
import com.tepav.reader.helpers.WrapContentHeightViewPager;
import com.tepav.reader.helpers.pagerindicator.CirclePageIndicator;
import com.tepav.reader.helpers.swipelistview.SwipeListView;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 12:37
 */
public class BlogActivity extends Activity {

    Context context;

    SwipeListView swipeListViewOfNews;
    WrapContentHeightViewPager viewPagerOfNews;
    CirclePageIndicator circlePageIndicator;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);
    }
}