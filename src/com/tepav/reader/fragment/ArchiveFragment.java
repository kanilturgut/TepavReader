package com.tepav.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.tepav.reader.R;
import com.tepav.reader.adapter.ArchiveListAdapter;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.DBData;

import java.util.List;

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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        new GetArchiveListTask().execute();
    }

    class GetArchiveListTask extends AsyncTask<Void, Void, List<DBData>> {

        DBHandler dbHandler = DBHandler.getInstance(context);

        @Override
        protected List<DBData> doInBackground(Void... voids) {
            return dbHandler.read(DBHandler.TABLE_ARCHIVE);
        }

        @Override
        protected void onPostExecute(List<DBData> dbDatas) {

            if (dbDatas != null)
                swipeListViewOfArchive.setAdapter(new ArchiveListAdapter(context, swipeListViewOfArchive, dbDatas));
            rlLoading.setVisibility(View.GONE);
        }
    }
}
