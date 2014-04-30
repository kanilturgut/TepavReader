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
import com.tepav.reader.adapter.ArchiveListAdapter;
import com.tepav.reader.helpers.swipelistview.SwipeListView;

/**
 * Author : kanilturgut
 * Date : 30.04.2014
 * Time : 10:39
 */
public class ArchiveFragment extends Fragment {

    Context context;

    SwipeListView swipeListViewOfArchive;
    RelativeLayout rlLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of news
        swipeListViewOfArchive = (SwipeListView) view.findViewById(R.id.swipeListViewOfArchive);
        swipeListViewOfArchive.setAdapter(new ArchiveListAdapter(context));

        rlLoading.setVisibility(View.GONE);

        return view;
    }


}
