package com.tepav.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.tepav.reader.R;
import com.tepav.reader.adapter.online_list.PublicationListAdapter;
import com.tepav.reader.helpers.swipelistview.SwipeListView;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 20:17
 */
public class PublicationFragment extends Fragment {

    Activity activity;
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
        this.activity = activity;
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publication, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of publication
        swipeListViewOfPublication = (SwipeListView) view.findViewById(R.id.swipeListViewOfPublication);
        swipeListViewOfPublication.setAdapter(new PublicationListAdapter(context, publicationType, 1));

        // Creating a textview
        TextView textView = new TextView(context);
        textView.setText(context.getString(R.string.visit_web_page));
        textView.setBackgroundColor(getResources().getColor(R.color.beyaz));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(20, 20, 20, 20);
        swipeListViewOfPublication.addFooterView(textView);

        if (rlLoading != null)
            rlLoading.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(context).activityStart(activity);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(context).activityStop(activity);
    }

}