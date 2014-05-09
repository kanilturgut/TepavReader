package com.tepav.reader.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tepav.reader.R;

/**
 * Author   : kanilturgut
 * Date     : 09/05/14
 * Time     : 11:21
 */
public class ListEmptyInformationFragment extends Fragment{

    public static int LIST_TYPE_READING_LIST = 0;
    public static int LIST_TYPE_FAVORITES = 1;
    public static int LIST_TYPE_ARCHIVE = 2;

    int listType;

    ListEmptyInformationFragment(int type) {
        this.listType = type;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_empty_information, null);

        TextView tvFragmentListEmptyContent = (TextView) view.findViewById(R.id.tvFragmentListEmptyContent);

        if (listType == LIST_TYPE_READING_LIST)
            tvFragmentListEmptyContent.setText(getString(R.string.no_item_in_reading_list));
        else if (listType == LIST_TYPE_FAVORITES)
            tvFragmentListEmptyContent.setText(getString(R.string.no_item_in_favorites));
        else if (listType == LIST_TYPE_ARCHIVE)
            tvFragmentListEmptyContent.setText(getString(R.string.no_item_in_archive));

        return view;
    }
}
