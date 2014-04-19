package com.tepav.reader.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.tepav.reader.R;
import com.tepav.reader.adapter.BlogListAdapter;
import com.tepav.reader.adapter.BlogPagerAdapter;
import com.tepav.reader.helpers.Constant;
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
public class BlogActivity extends Fragment {

    Context context;

    SwipeListView swipeListViewOfBlog;
    WrapContentHeightViewPager viewPagerOfBlog;
    BlogPagerAdapter blogPagerAdapter;
    CirclePageIndicator circlePageIndicator;

    String[] urls = new String[Constant.DRAWERS_PAGE_NUMBER];

    ProgressDialog progressDialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_blog, null);

        progressDialog =ProgressDialog.show(context, "Lütfen Bekleyiniz", "Günlükler getiriliyor", false, false);

        viewPagerOfBlog = (WrapContentHeightViewPager) view.findViewById(R.id.blogPager);
        circlePageIndicator = (CirclePageIndicator) view.findViewById(R.id.circleIndicatorOfBlogPager);

        //swipe list view of blog
        swipeListViewOfBlog = (SwipeListView) view.findViewById(R.id.swipeListViewOfBlog);
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

                for (int i = 0 ; i < Constant.DRAWERS_PAGE_NUMBER; i++) {
                    try {
                        urls[i] = Blog.fromJSON(object.getJSONObject(i)).getPimage();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                blogPagerAdapter = new BlogPagerAdapter(getFragmentManager(), context, urls);
                viewPagerOfBlog.setAdapter(blogPagerAdapter);
                circlePageIndicator.setViewPager(viewPagerOfBlog);

                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });

        return view;
    }
}