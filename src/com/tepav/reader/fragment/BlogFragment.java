package com.tepav.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.tepav.reader.R;
import com.tepav.reader.adapter.online_list.BlogListAdapter;
import com.tepav.reader.helpers.swipelistview.SwipeListView;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 12:37
 */
public class BlogFragment extends Fragment {

    Context context;
    SwipeListView swipeListViewOfBlog;
    RelativeLayout rlLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_blog, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of blog
        swipeListViewOfBlog = (SwipeListView) view.findViewById(R.id.swipeListViewOfBlog);
        swipeListViewOfBlog.setAdapter(new BlogListAdapter(context, 1));

        if (rlLoading != null)
            rlLoading.setVisibility(RelativeLayout.GONE);

        return view;
    }
}