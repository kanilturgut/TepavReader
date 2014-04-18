package com.tepav.reader.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.tepav.reader.R;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 13:15
 */
public class BlogPagerAdapter extends FragmentPagerAdapter{

    static Context context = null;
    public static final String ARG_PAGE = "page";
    static String[] urls;

    public BlogPagerAdapter(FragmentManager fm, Context c, String[] urlList) {
        super(fm);

        context = c;
        urls = urlList;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.create(i);
    }

    @Override
    public int getCount() {
        return 3;
    }


    static class PageFragment extends Fragment {

        int myPageNumber;
        AQuery aq;


        public PageFragment() {}

        public static PageFragment create(int pageNumber) {
            PageFragment pageFragment = new PageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(ARG_PAGE, pageNumber);
            pageFragment.setArguments(bundle);

            return pageFragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            myPageNumber = getArguments().getInt(ARG_PAGE);

            aq = new AQuery(context);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.custom_blog_pager, container, false);

            ImageView imageView = (ImageView) view.findViewById(R.id.blogPagerImageOfNews);

            ImageOptions imageOptions = new ImageOptions();
            imageOptions.fileCache = false;
            imageOptions.memCache = true;
            imageOptions.targetWidth = 0;
            imageOptions.fallback = 0;
            imageOptions.ratio = 9f/16f;
            imageOptions.round = 0;

            aq.id(imageView).image(urls[myPageNumber], imageOptions);

            return view;
        }
    }
}

