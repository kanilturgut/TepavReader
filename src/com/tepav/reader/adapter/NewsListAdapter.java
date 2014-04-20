package com.tepav.reader.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.tepav.reader.activity.NewsDetails;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.RoundedImageView;
import com.tepav.reader.model.DBData;
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

        MyOnClickListener myOnClickListener = new MyOnClickListener(position);

        holder.ibShare.setOnClickListener(myOnClickListener);
        holder.ibFavorite.setOnClickListener(myOnClickListener);
        holder.ibReadList.setOnClickListener(myOnClickListener);
        holder.frontOfNewsClick.setOnClickListener(myOnClickListener);

        return convertView;
    }


    class MyOnClickListener implements View.OnClickListener {

        int position;

        public MyOnClickListener(int pos) {
            this.position = pos;
        }

        News news = newsList.get(position);

        @Override
        public void onClick(View view) {
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

                    DBData dbData = new DBData();
                    dbData.setId(news.getId());
                    dbData.setContent(news.getContent());
                    dbData.setType(DBData.TYPE_NEWS);
                    dbData.setReadList(DBData.READ_LIST_FALSE);
                    dbData.setFavoriteList(DBData.FAVORITE_LIST_TRUE);
                    dbData.setArchive(DBData.ARCHIVE_FALSE);

                    dbHandler.insert(dbData);

                    break;
                case R.id.ibReadList:
                    DBData dbData1 = new DBData();
                    dbData1.setId(news.getId());
                    dbData1.setContent(news.getContent());
                    dbData1.setType(DBData.TYPE_NEWS);
                    dbData1.setReadList(DBData.READ_LIST_TRUE);
                    dbData1.setFavoriteList(DBData.FAVORITE_LIST_FALSE);
                    dbData1.setArchive(DBData.ARCHIVE_FALSE);

                    dbHandler.insert(dbData1);
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
