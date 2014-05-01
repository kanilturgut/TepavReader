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
import com.tepav.reader.adapter.ReadListListAdapter;
import com.tepav.reader.helpers.swipelistview.SwipeListView;

/**
 * Author : kanilturgut
 * Date : 29.04.2014
 * Time : 16:39
 */
public class ReadListFragment extends Fragment {

    Context context;

    SwipeListView swipeListViewOfReadList;
    RelativeLayout rlLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_list, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of news
        swipeListViewOfReadList = (SwipeListView) view.findViewById(R.id.swipeListViewOfReadList);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        swipeListViewOfReadList.setAdapter(new ReadListListAdapter(context));
        rlLoading.setVisibility(View.GONE);
    }
}
