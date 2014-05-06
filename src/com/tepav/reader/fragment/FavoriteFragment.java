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
import com.tepav.reader.adapter.FavoriteListAdapter;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.DBData;

import java.util.List;

/**
 * Author : kanilturgut
 * Date : 30.04.2014
 * Time : 09:49
 */
public class FavoriteFragment extends Fragment {

    Context context;

    SwipeListView swipeListViewOfFavorite;
    RelativeLayout rlLoading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_list, null);

        rlLoading = (RelativeLayout) view.findViewById(R.id.rlLoading);

        //swipe list view of news
        swipeListViewOfFavorite = (SwipeListView) view.findViewById(R.id.swipeListViewOfFavorite);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        new GetFavoriteListTask().execute();
    }

    class GetFavoriteListTask extends AsyncTask<Void, Void, List<DBData>> {

        DBHandler dbHandler = DBHandler.getInstance(context);

        @Override
        protected List<DBData> doInBackground(Void... voids) {
            return dbHandler.read(DBHandler.TABLE_FAVORITE);
        }

        @Override
        protected void onPostExecute(List<DBData> dbDatas) {

            swipeListViewOfFavorite.setAdapter(new FavoriteListAdapter(context, swipeListViewOfFavorite, dbDatas));
            rlLoading.setVisibility(View.GONE);

        }
    }
}
