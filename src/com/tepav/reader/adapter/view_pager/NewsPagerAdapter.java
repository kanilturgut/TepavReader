package com.tepav.reader.adapter.view_pager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.ImageOptions;
import com.tepav.reader.R;
import com.tepav.reader.activity.NewsDetails;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.model.News;
import org.json.JSONException;

import java.util.LinkedList;


/**
 * Author : kanilturgut
 * Date : 17.04.2014
 * Time : 16:09
 */
public class NewsPagerAdapter extends FragmentStatePagerAdapter {

    static Context context = null;
    public static final String ARG_PAGE = "page";
    static LinkedList<News> newsList;

    public NewsPagerAdapter(FragmentManager fm, Context c, LinkedList<News> list) {
        super(fm);

        context = c;
        newsList = list;
    }

    @Override
    public Fragment getItem(int i) {
        return PageFragment.create(i);
    }

    @Override
    public int getCount() {
        return Constant.DRAWERS_PAGE_NUMBER;
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

            aq = Aquery.getInstance(context);

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.custom_news_pager, container, false);

            TextView tvNewsPagerNewsTitle = (TextView) view.findViewById(R.id.tvNewsPagerNewsTitle);
            tvNewsPagerNewsTitle.setText(newsList.get(myPageNumber).getHtitle());

            ImageView imageView = (ImageView) view.findViewById(R.id.newsPagerImageOfNews);

            ImageOptions imageOptions = new ImageOptions();
            imageOptions.fileCache = true;
            imageOptions.memCache = true;
            imageOptions.targetWidth = 0;
            imageOptions.fallback = R.drawable.no_image;
            imageOptions.ratio = 9f/16f;
            imageOptions.round = 0;

            aq.id(imageView).image(newsList.get(myPageNumber).getHimage(), imageOptions);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, NewsDetails.class);
                    try {
                        intent.putExtra("class", News.toDBData(newsList.get(myPageNumber)));
                        intent.putExtra("fromWhere", Constant.DETAILS_FROM_POST);
                        context.startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            return view;
        }
    }
}
