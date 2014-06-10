package com.tepav.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.google.analytics.tracking.android.EasyTracker;
import com.tepav.reader.R;
import com.tepav.reader.adapter.offline_list.ArchiveListAdapter;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.DBData;

import java.util.LinkedList;


/**
 * Author : kanilturgut
 * Date : 30.04.2014
 * Time : 10:39
 */
public class ArchiveFragment extends Fragment {

    Context context;
    Activity activity;

    SwipeListView swipeListViewOfArchive;
    RelativeLayout rlLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_archive, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of news
        swipeListViewOfArchive = (SwipeListView) view.findViewById(R.id.swipeListViewOfArchive);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(context).activityStart(activity);

        new GetArchiveListTask().execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(context).activityStop(activity);
    }

    class GetArchiveListTask extends AsyncTask<Void, Void, LinkedList<DBData>> {

        DBHandler dbHandler = DBHandler.getInstance(context);

        @Override
        protected LinkedList<DBData> doInBackground(Void... voids) {
            return dbHandler.read(DBHandler.TABLE_ARCHIVE);
        }

        @Override
        protected void onPostExecute(LinkedList<DBData> dbDatas) {

            if (dbDatas != null)
                swipeListViewOfArchive.setAdapter(new ArchiveListAdapter(context, swipeListViewOfArchive, dbDatas));
            else {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                ListEmptyInformationFragment noInternetConnectionFragment = new ListEmptyInformationFragment(ListEmptyInformationFragment.LIST_TYPE_ARCHIVE);
                fragmentTransaction.replace(R.id.activity_main_content_fragment, noInternetConnectionFragment);
                fragmentTransaction.commit();
            }

            rlLoading.setVisibility(View.GONE);
        }
    }
}
