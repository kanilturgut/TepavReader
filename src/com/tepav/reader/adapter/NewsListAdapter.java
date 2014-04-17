package com.tepav.reader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.tepav.reader.R;
import com.tepav.reader.cache.DiskBitmapCache;
import com.tepav.reader.helpers.RoundedImageView;
import com.tepav.reader.model.News;

import java.util.List;

/**
 * Author : kanilturgut
 * Date : 16.04.2014
 * Time : 15:29
 */
public class NewsListAdapter extends ArrayAdapter<News> {

    Context context;
    List<News> newsList;

    public NewsListAdapter(Context ctx, List<News> data) {
        super(ctx, R.layout.custom_news_row, data);

        this.context = ctx;
        this.newsList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        NewsHolder holder;
        ImageLoader imageLoader = null;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_news_row, parent, false);

            holder = new NewsHolder();

            holder.imageOfNews = (RoundedImageView) convertView.findViewById(R.id.ivImageOfNews);
            holder.titleOfNews = (TextView) convertView.findViewById(R.id.tvTitleOfNews);
            holder.dateOfNews = (TextView) convertView.findViewById(R.id.tvDateOfNews);


            imageLoader = new ImageLoader(requestQueue, new DiskBitmapCache(context.getExternalCacheDir()));

            imageLoader.get(newsList.get(position).getHimage(), ImageLoader.getImageListener(holder.imageOfNews, R.drawable.ic_launcher, R.drawable.ic_launcher));

            holder.titleOfNews.setText(newsList.get(position).getHtitle());
            holder.dateOfNews.setText(newsList.get(position).getDate());

            convertView.setTag(holder);

        } else {
            holder = (NewsHolder) convertView.getTag();
        }

        return convertView;
    }

    class NewsHolder {

        RoundedImageView imageOfNews;
        TextView titleOfNews;
        TextView dateOfNews;
    }
}
