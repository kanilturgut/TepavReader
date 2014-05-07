package com.tepav.reader.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tepav.reader.R;

/**
 * Author   : kanilturgut
 * Date     : 07/05/14
 * Time     : 19:46
 */
public class NoInternetConnectionFragment extends Fragment{

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_no_internet, null);
    }
}
