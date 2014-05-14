package com.tepav.reader.adapter.offline_list;

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
import com.androidquery.callback.ImageOptions;
import com.tepav.reader.R;
import com.tepav.reader.activity.BlogDetails;
import com.tepav.reader.activity.NewsDetails;
import com.tepav.reader.activity.PublicationDetails;
import com.tepav.reader.activity.Splash;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.popup.QuickActionForList;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
import com.tepav.reader.operation.LikeOperation;
import com.tepav.reader.operation.OfflineList;
import com.tepav.reader.util.AlertDialogManager;
import org.json.JSONException;

import java.util.LinkedList;

/**
 * Author : kanilturgut
 * Date : 30.04.2014
 * Time : 10:42
 */
public class ArchiveListAdapter extends ArrayAdapter<DBData> {

    String TAG = "FavoriteListAdapter";
    Context context;
    DBHandler dbHandler;
    LinkedList<DBData> dbDataList;
    AQuery aq;
    SwipeListView swipeListView;
    OfflineList offlineList;

    public ArchiveListAdapter(Context context, SwipeListView swipeListView, LinkedList<DBData> dbDataList) {
        super(context, R.layout.custom_archive_list_row, dbDataList);

        this.context = context;
        this.swipeListView = swipeListView;
        this.dbDataList = dbDataList;

        dbHandler = DBHandler.getInstance(context);
        offlineList = OfflineList.getInstance(context);
        aq = Aquery.getInstance(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ArchiveHolder holder;
        final DBData dbData = dbDataList.get(position);

        String title = "";
        String date = "";
        String imageUrl = "";

        if (dbData.getType() == DBData.TYPE_NEWS) {

            try {
                News news = News.fromDBData(dbData);
                title = news.getHtitle();
                date = news.getDate();
                imageUrl = news.getHimage();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (dbData.getType() == DBData.TYPE_BLOG) {
            try {
                Blog blog = Blog.fromDBData(dbData);
                title = blog.getBtitle();
                date = blog.getDate();
                imageUrl = blog.getPimage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
            try {
                Publication publication = Publication.fromDBData(dbData);
                title = publication.getYtitle();
                date = publication.getDate() + " " + publication.getYtype();
                imageUrl = "";
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_archive_list_row, parent, false);

            holder = new ArchiveHolder();

            //front view
            holder.frontOfArchiveClick = (RelativeLayout) convertView.findViewById(R.id.frontOfArchiveClick);
            holder.imageOfArchive = (RoundedImageView) convertView.findViewById(R.id.ivImageOfArchive);
            holder.titleOfArchive = (TextView) convertView.findViewById(R.id.tvTitleOfArchive);
            holder.dateOfArchive = (TextView) convertView.findViewById(R.id.tvDateOfArchive);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            holder.ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

            convertView.setTag(holder);

        } else {
            holder = (ArchiveHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.no_image;


        if (title != null && !title.isEmpty() && !title.equals(""))
            holder.titleOfArchive.setText(title);

        if (date != null && !date.isEmpty() && !date.equals(""))
            holder.dateOfArchive.setText(date);

        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("")) {
            Bitmap bmp = aq.getCachedImage(imageUrl);
            if (bmp == null) {
                aq.id(holder.imageOfArchive).image(imageUrl, options);
                Logs.i(TAG, "image received from server");
            } else {
                holder.imageOfArchive.setImageBitmap(bmp);
                Logs.i(TAG, "image received from cache");
            }
        } else {
            holder.imageOfArchive.setImageResource(R.drawable.no_image);
        }

        // swipe list icons
        if (offlineList != null) {

            if (checkDB(dbData, DBHandler.TABLE_LIKE))
                holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
            else
                holder.ibLike.setImageResource(R.drawable.swipe_like);
        }

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    String title = "";
                    String url = "";

                    if (dbData.getType() == DBData.TYPE_NEWS) {
                        try {
                            title = News.fromDBData(dbData).getHtitle();
                            url = Constant.SHARE_NEWS + dbData.getId();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (dbData.getType() == DBData.TYPE_BLOG) {
                        try {
                            title = Blog.fromDBData(dbData).getBtitle();
                            url = Constant.SHARE_BLOG + dbData.getId();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
                        try {
                            title = Publication.fromDBData(dbData).getYtitle();
                            url = Constant.SHARE_PUBLICATION + dbData.getId();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
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

                    if (checkDB(dbData, DBHandler.TABLE_LIKE)) {
                        dbHandler.insert(dbData, DBHandler.TABLE_LIKE);
                        LikeOperation.doLike(dbData);
                    } else {
                        dbHandler.delete(dbData, DBHandler.TABLE_LIKE);
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (!checkDB(dbData, DBHandler.TABLE_LIKE))
                        imageButton.setImageResource(R.drawable.swipe_like);
                    else
                        imageButton.setImageResource(R.drawable.swipe_like_dolu);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }

            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                    dbDataList.remove(dbData);

                    remove(dbData);
                    notifyDataSetChanged();

                    swipeListView.closeAnimate(position);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }

            }
        });

        holder.frontOfArchiveClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;

                if (dbData.getType() == DBData.TYPE_NEWS) {
                    intent = new Intent(context, NewsDetails.class);
                    intent.putExtra("class", dbData);
                } else if (dbData.getType() == DBData.TYPE_BLOG) {
                    intent = new Intent(context, BlogDetails.class);
                    intent.putExtra("class", dbData);
                } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
                    intent = new Intent(context, PublicationDetails.class);
                    intent.putExtra("class", dbData);
                }

                if (intent != null) {

                    intent.putExtra("fromWhere", Constant.DETAILS_FROM_LIST);
                    intent.putExtra("listType", QuickActionForList.LIST_TYPE_ARCHIVE);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    class ArchiveHolder {

        RoundedImageView imageOfArchive;
        TextView titleOfArchive;
        TextView dateOfArchive;
        ImageButton ibShare;
        ImageButton ibLike;
        ImageButton ibDelete;
        RelativeLayout frontOfArchiveClick;
    }

    boolean checkDB(DBData dbData, String table) {
        return offlineList.checkIfContains(table, dbData.getId());
    }
}
