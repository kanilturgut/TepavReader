package com.tepav.reader.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.ImageOptions;
import com.tepav.reader.R;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.RoundedImageView;
import com.tepav.reader.helpers.swipelistview.BaseSwipeListViewListener;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.News;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 16.04.2014
 * Time : 15:29
 */
public class NewsListAdapter extends ArrayAdapter<News> implements View.OnClickListener {

    Context context;
    List<News> newsList = new LinkedList<News>();
    int pageNumber;
    AQuery aq;

    public NewsListAdapter(Context ctx, int number) {
        super(ctx, R.layout.custom_news_row);

        this.context = ctx;
        this.pageNumber = number;

        aq = new AQuery(context);
        loadMore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsHolder holder;

        if (position == (newsList.size() - 1))
            loadMore();

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_news_row, parent, false);

            holder = new NewsHolder();

            //front view
            holder.frontOfNewsClick = (RelativeLayout) convertView.findViewById(R.id.frontOfNewsClick);
            holder.imageOfNews = (RoundedImageView) convertView.findViewById(R.id.ivImageOfNews);
            holder.titleOfNews = (TextView) convertView.findViewById(R.id.tvTitleOfNews);
            holder.dateOfNews = (TextView) convertView.findViewById(R.id.tvDateOfNews);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);

            convertView.setTag(holder);

        } else {
            holder = (NewsHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = 0;
        options.round = 0;

        aq.id(holder.imageOfNews).image(newsList.get(position).getHimage(), options);
        holder.titleOfNews.setText(newsList.get(position).getHtitle());
        holder.dateOfNews.setText(newsList.get(position).getHdate());

        holder.ibShare.setOnClickListener(this);
        holder.ibFavorite.setOnClickListener(this);
        holder.ibReadList.setOnClickListener(this);
        holder.frontOfNewsClick.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ibShare:
                Log.i("Click", "Share");
                break;
            case R.id.ibFavorite:
                Log.i("Click", "Favorite");
                break;
            case R.id.ibReadList:
                Log.i("Click", "Read List");
                break;
            case R.id.frontOfNewsClick:
                Log.i("Click", "News on front");
                break;
        }

    }

    class NewsHolder {

        RoundedImageView imageOfNews;
        TextView titleOfNews;
        TextView dateOfNews;
        ImageButton ibShare;
        ImageButton ibFavorite;
        ImageButton ibReadList;
        RelativeLayout frontOfNewsClick;
    }

    public void loadMore() {

        JSONObject params = new JSONObject();
        try {
            params.put("pageNumber", pageNumber);
        } catch (JSONException e) {
            params = null;
            e.printStackTrace();
        }

        aq.post(HttpURL.createURL(HttpURL.news), params, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray object, AjaxStatus status) {

                List<News> temp = new LinkedList<News>();

                if (object != null && object.length() != 0) {
                    for (int i = 0; i < object.length(); i++) {
                        try {
                            temp.add(News.fromJSON(object.getJSONObject(i)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    newsList.addAll(temp);
                    addAll(temp);
                    notifyDataSetChanged();

                    pageNumber++;
                }
            }
        });
    }
}
