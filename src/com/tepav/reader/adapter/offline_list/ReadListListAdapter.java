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
import com.tepav.reader.operation.ShareOperation;
import com.tepav.reader.util.AlertDialogManager;
import org.json.JSONException;

import java.util.LinkedList;

/**
 * Author : kanilturgut
 * Date : 29.04.2014
 * Time : 16:46
 */
public class ReadListListAdapter extends ArrayAdapter<DBData> {

    String TAG = "ReadListListAdapter";
    Context context;
    DBHandler dbHandler;
    LinkedList<DBData> dbDataList;
    AQuery aq;
    SwipeListView swipeListView;
    OfflineList offlineList;

    public ReadListListAdapter(Context context, SwipeListView swipeListView, LinkedList<DBData> dbDataList) {
        super(context, R.layout.custom_read_list_row, dbDataList);

        this.context = context;
        this.swipeListView = swipeListView;
        this.dbDataList = dbDataList;

        dbHandler = DBHandler.getInstance(context);
        aq = Aquery.getInstance(context);
        offlineList = OfflineList.getInstance(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ReadListHolder holder;
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
            convertView = inflater.inflate(R.layout.custom_read_list_row, parent, false);

            holder = new ReadListHolder();

            //front view
            holder.frontOfReadListClick = (RelativeLayout) convertView.findViewById(R.id.frontOfReadListClick);
            holder.imageOfReadList = (RoundedImageView) convertView.findViewById(R.id.ivImageOfReadList);
            holder.titleOfReadList = (TextView) convertView.findViewById(R.id.tvTitleOfReadList);
            holder.dateOfReadList = (TextView) convertView.findViewById(R.id.tvDateOfReadList);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibArchive = (ImageButton) convertView.findViewById(R.id.ibArchive);
            holder.ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

            convertView.setTag(holder);

        } else {
            holder = (ReadListHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.tepav_t;

        if (title != null && !title.isEmpty() && !title.equals(""))
            holder.titleOfReadList.setText(title);

        if (date != null && !date.isEmpty() && !date.equals(""))
            holder.dateOfReadList.setText(date);

        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("")) {
            Bitmap bmp = aq.getCachedImage(imageUrl);
            if (bmp == null) {
                aq.id(holder.imageOfReadList).image(imageUrl, options);
                Logs.i(TAG, "image received from server");
            } else {
                holder.imageOfReadList.setImageBitmap(bmp);
                Logs.i(TAG, "image received from cache");
            }
        } else {
            holder.imageOfReadList.setImageResource(R.drawable.tepav_t);
        }

        // swipe list icons
        if (offlineList != null) {
            if (checkDB(dbData, DBHandler.TABLE_FAVORITE))
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
            else
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

            if (checkDB(dbData, DBHandler.TABLE_ARCHIVE))
                holder.ibArchive.setImageResource(R.drawable.okudum_icon_dolu);
            else
                holder.ibArchive.setImageResource(R.drawable.okudum_icon);

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

                    ShareOperation.doShare(context, dbData);

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

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(dbData, DBHandler.TABLE_FAVORITE)) {
                        dbHandler.insert(dbData, DBHandler.TABLE_FAVORITE);
                    } else {
                        dbHandler.delete(dbData, DBHandler.TABLE_FAVORITE);
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(dbData, DBHandler.TABLE_FAVORITE))
                        imageButton.setImageResource(R.drawable.swipe_favorites_dolu);
                    else
                        imageButton.setImageResource(R.drawable.swipe_favorites);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);

                }
            }
        });

        holder.ibArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(dbData, DBHandler.TABLE_ARCHIVE)) {
                        dbHandler.insert(dbData, DBHandler.TABLE_ARCHIVE);
                    } else {
                        dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(dbData, DBHandler.TABLE_ARCHIVE))
                        imageButton.setImageResource(R.drawable.okudum_icon_dolu);
                    else
                        imageButton.setImageResource(R.drawable.okudum_icon);
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
                    dbHandler.delete(dbData, DBHandler.TABLE_READ_LIST);
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

        holder.frontOfReadListClick.setOnClickListener(new View.OnClickListener() {
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
                    intent.putExtra("listType", QuickActionForList.LIST_TYPE_READING_LIST);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    class ReadListHolder {

        RoundedImageView imageOfReadList;
        TextView titleOfReadList;
        TextView dateOfReadList;
        ImageButton ibShare;
        ImageButton ibLike;
        ImageButton ibFavorite;
        ImageButton ibArchive;
        ImageButton ibDelete;
        RelativeLayout frontOfReadListClick;
    }

    boolean checkDB(DBData dbData, String table) {
        return offlineList.checkIfContains(table, dbData.getId());
    }
}