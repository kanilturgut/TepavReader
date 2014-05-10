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
import com.tepav.reader.adapter.online_list.PublicationListAdapter;
import com.tepav.reader.helpers.swipelistview.SwipeListView;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 20:17
 */
public class PublicationFragment extends Fragment {

    Context context;
    SwipeListView swipeListViewOfPublication;
    RelativeLayout rlLoading;
    String publicationType;

    public PublicationFragment(String type) {
        this.publicationType = type;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_publication, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of publication
        swipeListViewOfPublication = (SwipeListView) view.findViewById(R.id.swipeListViewOfPublication);
        swipeListViewOfPublication.setAdapter(new PublicationListAdapter(context, publicationType, 1));

        if (rlLoading != null)
            rlLoading.setVisibility(View.GONE);

        return view;
    }


}