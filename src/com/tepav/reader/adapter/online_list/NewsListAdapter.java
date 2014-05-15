package com.tepav.reader.adapter.online_list;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.tepav.reader.activity.Splash;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.model.News;
import com.tepav.reader.operation.LikeOperation;
import com.tepav.reader.operation.OfflineList;
import com.tepav.reader.operation.ShareOperation;
import com.tepav.reader.util.AlertDialogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Author : kanilturgut
 * Date : 16.04.2014
 * Time : 15:29
 */
public class NewsListAdapter extends ArrayAdapter<News> {

    String TAG = "NewsListAdapter";
    Context context;
    LinkedList<News> newsList = new LinkedList<News>();
    int pageNumber;
    AQuery aq;
    DBHandler dbHandler;
    OfflineList offlineList;

    public NewsListAdapter(Context ctx, int number) {
        super(ctx, R.layout.custom_news_row);

        this.context = ctx;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        offlineList = OfflineList.getInstance(context);
        aq = Aquery.getInstance(context);
        loadMore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsHolder holder;
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
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
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
        options.fallback = R.drawable.tepav_t;

        Bitmap bmp = aq.getCachedImage(news.getHimage());
        if (bmp == null) {
            aq.id(holder.imageOfNews).image(news.getHimage(), options);
            Logs.i(TAG, "image received from server");
        } else {
            holder.imageOfNews.setImageBitmap(bmp);
            Logs.i(TAG, "image received from cache");
        }

        holder.titleOfNews.setText(news.getHtitle());
        holder.dateOfNews.setText(news.getHdate());

        // swipe list icons
        if (offlineList != null) {
            if (checkDB(news, DBHandler.TABLE_FAVORITE))
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
            else
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

            if (checkDB(news, DBHandler.TABLE_READ_LIST))
                holder.ibReadList.setImageResource(R.drawable.okudum_icon_dolu);
            else
                holder.ibReadList.setImageResource(R.drawable.okudum_icon);

            if (checkDB(news, DBHandler.TABLE_LIKE))
                holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
            else
                holder.ibLike.setImageResource(R.drawable.swipe_like);
        }

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    try {
                        ShareOperation.doShare(context, News.toDBData(news));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = Constant.SHARE_NEWS + news.getHaber_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getHtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, news.getHtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));

                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }
            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(news, DBHandler.TABLE_LIKE)) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_LIKE);
                            LikeOperation.doLike(News.toDBData(news));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_LIKE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (!checkDB(news, DBHandler.TABLE_LIKE))
                        imageButton.setImageResource(R.drawable.swipe_like);
                    else
                        imageButton.setImageResource(R.drawable.swipe_like_dolu);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }
            }
        });

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(news, DBHandler.TABLE_FAVORITE)) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(news, DBHandler.TABLE_FAVORITE))
                        imageButton.setImageResource(R.drawable.swipe_favorites_dolu);
                    else
                        imageButton.setImageResource(R.drawable.swipe_favorites);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }
            }
        });

        holder.ibReadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    ImageButton imageButton = (ImageButton) view;

                    if (!checkDB(news, DBHandler.TABLE_READ_LIST)) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                            imageButton.setImageResource(R.drawable.okudum_icon_dolu);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                            imageButton.setImageResource(R.drawable.okudum_icon);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }
            }
        });
        holder.frontOfNewsClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, NewsDetails.class);
                try {
                    intent.putExtra("class", News.toDBData(news));
                    intent.putExtra("fromWhere", Constant.DETAILS_FROM_POST);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return convertView;
    }

    class NewsHolder {

        RoundedImageView imageOfNews;
        TextView titleOfNews;
        TextView dateOfNews;
        ImageButton ibShare;
        ImageButton ibLike;
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

                LinkedList<News> temp = new LinkedList<News>();

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

    boolean checkDB(News news, String table) {
        return offlineList.checkIfContains(table, news.getId());
    }
}
