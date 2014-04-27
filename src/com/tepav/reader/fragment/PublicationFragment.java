package com.tepav.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tepav.reader.R;
import com.tepav.reader.adapter.PublicationListAdapter;
import com.tepav.reader.helpers.Constant;
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

        TextView tvTypeOfPublication = (TextView) view.findViewById(R.id.tvTypeOfPublication);
        tvTypeOfPublication.setText(publicationType);

        //swipe list view of publication
        swipeListViewOfPublication = (SwipeListView) view.findViewById(R.id.swipeListViewOfPublication);
        swipeListViewOfPublication.setAdapter(new PublicationListAdapter(context, publicationType, 1));

        rlLoading.setVisibility(View.GONE);

        return view;
    }


}