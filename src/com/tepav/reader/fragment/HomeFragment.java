package com.tepav.reader.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.tepav.reader.R;
import com.tepav.reader.activity.MainActivity;
import com.tepav.reader.util.ConnectionDetector;

/**
 * Author   : kanilturgut
 * Date     : 02/05/14
 * Time     : 17:27
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    Context context;
    FrameLayout frameNews, frameBlog, framePublication;
    ConnectionDetector connectionDetector;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;

        connectionDetector = new ConnectionDetector(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, null);

        frameNews = (FrameLayout) view.findViewById(R.id.frameNews);
        frameNews.setOnClickListener(this);

        frameBlog = (FrameLayout) view.findViewById(R.id.frameBlog);
        frameBlog.setOnClickListener(this);

        framePublication = (FrameLayout) view.findViewById(R.id.framePublication);
        framePublication.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

        FragmentManager fragmentManager = MainActivity.fm;
        FragmentTransaction ft = fragmentManager.beginTransaction();

        String fragmentTag = "";
        Fragment fragment = null;

        if (connectionDetector.isConnectingToInternet()) {

            if (view == frameNews) {
                fragmentTag = getString(R.string.News);
                fragment = new NewsFragment();
            } else if (view == frameBlog) {
                fragmentTag = getString(R.string.Blogs);
                fragment = new BlogFragment();
            } else if (view == framePublication) {
                fragmentTag = getString(R.string.Research_And_Publications);
                fragment = new PublicationFragment(fragmentTag);
            }
        } else {
            fragmentTag = getString(R.string.No_Internet_Fragment);
            fragment = new NoInternetConnectionFragment();
        }

        if (fragment != null) {

            ft.replace(R.id.activity_main_content_fragment, fragment, fragmentTag);
            ft.commit();
        }
    }
}
