package com.tepav.reader.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.tepav.reader.activity.NewsDetails;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.RoundedImageView;
import com.tepav.reader.helpers.Util;
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
public class NewsListAdapter extends ArrayAdapter<News> {

    Context context;
    List<News> newsList = new LinkedList<News>();
    int pageNumber;
    AQuery aq;
    DBHandler dbHandler;

    public NewsListAdapter(Context ctx, int number) {
        super(ctx, R.layout.custom_news_row);

        this.context = ctx;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        loadMore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final NewsHolder holder;
        final News news = newsList.get(position);

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
            holder.ibFavorited = (ImageButton) convertView.findViewById(R.id.ibFavorited);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);
            holder.ibReadListed = (ImageButton) convertView.findViewById(R.id.ibReadListed);

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

        aq.id(holder.imageOfNews).image(news.getHimage(), options);
        holder.titleOfNews.setText(news.getHtitle());
        holder.dateOfNews.setText(news.getHdate());

        MyOnClickListener myOnClickListener = new MyOnClickListener(position);
        holder.ibShare.setOnClickListener(myOnClickListener);
        holder.ibFavorite.setOnClickListener(myOnClickListener);
        holder.ibReadList.setOnClickListener(myOnClickListener);
        holder.ibFavorited.setOnClickListener(myOnClickListener);
        holder.ibReadListed.setOnClickListener(myOnClickListener);
        holder.frontOfNewsClick.setOnClickListener(myOnClickListener);

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, news.getId(), holder.ibFavorite, holder.ibFavorited);
        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, news.getId(), holder.ibReadList, holder.ibReadListed);

        return convertView;
    }


    class MyOnClickListener implements View.OnClickListener {

        int position;

        public MyOnClickListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View view) {

            News news = newsList.get(position);

            switch (view.getId()) {
                case R.id.ibShare:
                    String url = Constant.SHARE_NEWS + news.getHaber_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getHtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT,  news.getHtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                    break;
                case R.id.ibFavorite:

                    try {
                        dbHandler.insert(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.ibReadList:

                    try {
                        dbHandler.insert(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.frontOfNewsClick:

                    Intent intent = new Intent(context, NewsDetails.class);
                    intent.putExtra("class", news);
                    context.startActivity(intent);
                    break;
            }
        }
    }

    class NewsHolder {

        RoundedImageView imageOfNews;
        TextView titleOfNews;
        TextView dateOfNews;
        ImageButton ibShare;
        ImageButton ibFavorite;
        ImageButton ibFavorited;
        ImageButton ibReadList;
        ImageButton ibReadListed;
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
