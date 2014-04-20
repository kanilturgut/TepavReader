package com.tepav.reader.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.R;
import com.tepav.reader.adapter.NewsListAdapter;
import com.tepav.reader.adapter.NewsPagerAdapter;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.WrapContentHeightViewPager;
import com.tepav.reader.helpers.pagerindicator.CirclePageIndicator;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.News;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 16.04.2014
 * Time : 15:19
 */
public class NewsFragment extends Fragment {

    Context context;

    SwipeListView swipeListViewOfNews;
    WrapContentHeightViewPager viewPagerOfNews;
    NewsPagerAdapter newsPagerAdapter;
    CirclePageIndicator circlePageIndicator;

    List<News> newsListForPager = new LinkedList<News>();

    RelativeLayout rlLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_news, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        viewPagerOfNews = (WrapContentHeightViewPager) view.findViewById(R.id.newsPager);
        circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.circleIndicatorOfNewsPager);

        //swipe list view of news
        swipeListViewOfNews = (SwipeListView) view.findViewById(R.id.swipeListViewOfNews);
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

                for (int i = 0; i < Constant.DRAWERS_PAGE_NUMBER; i++) {
                    try {
                        newsListForPager.add(News.fromJSON(object.getJSONObject(i)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                newsPagerAdapter = new NewsPagerAdapter(getFragmentManager(), context, newsListForPager);
                viewPagerOfNews.setAdapter(newsPagerAdapter);
                circlePageIndicator.setViewPager(viewPagerOfNews);

                if (rlLoading != null)
                    rlLoading.setVisibility(RelativeLayout.GONE);
            }
        });

        return view;
    }


}