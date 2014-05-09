package com.tepav.reader.adapter;

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
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
import com.tepav.reader.service.TepavService;
import com.tepav.reader.util.AlertDialogManager;
import org.json.JSONException;

import java.util.List;

/**
 * Author : kanilturgut
 * Date : 30.04.2014
 * Time : 09:52
 */
public class FavoriteListAdapter extends ArrayAdapter<DBData> {

    String TAG = "FavoriteListAdapter";
    Context context;
    DBHandler dbHandler;
    List<DBData> dbDataList;
    AQuery aq;
    SwipeListView swipeListView;
    TepavService tepavService = null;

    News news = null;
    Blog blog = null;
    Publication publication = null;

    public FavoriteListAdapter(Context context, SwipeListView swipeListView, List<DBData> dbDataList) {
        super(context, R.layout.custom_favorite_list_row, dbDataList);

        this.context = context;
        this.swipeListView = swipeListView;
        this.dbDataList = dbDataList;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        tepavService = TepavService.getInstance();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        FavoriteHolder holder;
        final DBData dbData = dbDataList.get(position);

        if (dbData.getType() == DBData.TYPE_NEWS) {
            try {
                news = News.fromDBData(dbData);
                blog = null;
                publication = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (dbData.getType() == DBData.TYPE_BLOG) {
            try {
                blog = Blog.fromDBData(dbData);
                news = null;
                publication = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
            try {
                publication = Publication.fromDBData(dbData);
                news = null;
                blog = null;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_favorite_list_row, parent, false);

            holder = new FavoriteHolder();

            //front view
            holder.frontOfFavoriteClick = (RelativeLayout) convertView.findViewById(R.id.frontOfFavoriteClick);
            holder.imageOfFavorite = (RoundedImageView) convertView.findViewById(R.id.ivImageOfFavorite);
            holder.titleOfFavorite = (TextView) convertView.findViewById(R.id.tvTitleOfFavorite);
            holder.dateOfFavorite = (TextView) convertView.findViewById(R.id.tvDateOfFavorite);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            holder.ibArchive = (ImageButton) convertView.findViewById(R.id.ibArchive);
            holder.ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);

            convertView.setTag(holder);

        } else {
            holder = (FavoriteHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.no_image;

        if (news != null) {

            holder.titleOfFavorite.setText(news.getHtitle());
            holder.dateOfFavorite.setText(news.getHdate());

            Bitmap bmp = aq.getCachedImage(news.getHimage());
            if (bmp == null) {
                aq.id(holder.imageOfFavorite).image(news.getHimage(), options);
                Logs.i(TAG, "image received from server");
            } else {
                holder.imageOfFavorite.setImageBitmap(bmp);
                Logs.i(TAG, "image received from cache");
            }

        } else if (blog != null) {
            holder.titleOfFavorite.setText(blog.getBtitle());
            holder.dateOfFavorite.setText(blog.getBtitle());

            Bitmap bmp = aq.getCachedImage(blog.getPimage());
            if (bmp == null) {
                aq.id(holder.imageOfFavorite).image(blog.getPimage(), options);
                Logs.i(TAG, "image received from server");
            } else {
                holder.imageOfFavorite.setImageBitmap(bmp);
                Logs.i(TAG, "image received from cache");
            }

        } else if (publication != null) {
            holder.titleOfFavorite.setText(publication.getYtitle());
            holder.dateOfFavorite.setText(publication.getYdate() + ", " + publication.getYtype());
            holder.imageOfFavorite.setImageResource(R.drawable.no_image);
        }

        if (tepavService != null) {

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

                    if (news != null) {
                        String url = Constant.SHARE_NEWS + news.getHaber_id();

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, news.getHtitle());
                        shareIntent.putExtra(Intent.EXTRA_TEXT, news.getHtitle() + " " + url);
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));

                    } else if (blog != null) {
                        String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                        shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                    } else if (publication != null) {
                        String url = Constant.SHARE_PUBLICATION + publication.getYayin_id();

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, publication.getYtitle());
                        shareIntent.putExtra(Intent.EXTRA_TEXT, publication.getYtitle() + " " + url);
                        context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                    }
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
                        Logs.i(TAG, "ibLike if");
                        if (news != null) {
                            Logs.i(TAG, "ibLike news");
                            dbHandler.insert(dbData, DBHandler.TABLE_LIKE);
                            tepavService.addItemToLikeListOfTepavService(dbData);
                        } else if (blog != null) {
                            dbHandler.insert(dbData, DBHandler.TABLE_LIKE);
                            tepavService.addItemToLikeListOfTepavService(dbData);
                        } else if (publication != null) {
                            dbHandler.insert(dbData, DBHandler.TABLE_LIKE);
                            tepavService.addItemToLikeListOfTepavService(dbData);
                        }
                    } else {
                        Logs.i(TAG, "ibLike else");
                        if (news != null) {
                            Logs.i(TAG, "ibLike news");
                            dbHandler.delete(dbData, DBHandler.TABLE_LIKE);
                            tepavService.removeItemFromLikeListOfTepavService(dbData);
                        } else if (blog != null) {
                            dbHandler.delete(dbData, DBHandler.TABLE_LIKE);
                            tepavService.removeItemFromLikeListOfTepavService(dbData);
                        } else if (publication != null) {
                            dbHandler.delete(dbData, DBHandler.TABLE_LIKE);
                            tepavService.removeItemFromLikeListOfTepavService(dbData);
                        }
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

        holder.ibArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    if (!checkDB(dbData, DBHandler.TABLE_ARCHIVE)) {
                        if (news != null) {
                            dbHandler.insert(dbData, DBHandler.TABLE_ARCHIVE);
                            tepavService.addItemToArchiveListOfTepavService(dbData);
                        } else if (blog != null) {
                            dbHandler.insert(dbData, DBHandler.TABLE_ARCHIVE);
                            tepavService.addItemToArchiveListOfTepavService(dbData);
                        } else if (publication != null) {
                            dbHandler.insert(dbData, DBHandler.TABLE_ARCHIVE);
                            tepavService.addItemToArchiveListOfTepavService(dbData);
                        }
                    } else {
                        if (news != null) {
                            dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                            tepavService.removeItemFromArchiveListOfTepavService(dbData);
                        } else if (blog != null) {
                            dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                            tepavService.removeItemFromArchiveListOfTepavService(dbData);
                        } else if (publication != null) {
                            dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                            tepavService.removeItemFromArchiveListOfTepavService(dbData);
                        }
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

                    if (news != null) {
                        dbHandler.delete(dbData, DBHandler.TABLE_FAVORITE);
                        tepavService.removeItemFromReadingListOfTepavService(dbData);
                        dbDataList.remove(dbData);
                    } else if (blog != null) {
                        dbHandler.delete(dbData, DBHandler.TABLE_FAVORITE);
                        tepavService.removeItemFromReadingListOfTepavService(dbData);
                        dbDataList.remove(dbData);
                    } else if (publication != null) {
                        dbHandler.delete(dbData, DBHandler.TABLE_FAVORITE);
                        tepavService.removeItemFromReadingListOfTepavService(dbData);
                        dbDataList.remove(dbData);
                    }

                    remove(dbData);
                    notifyDataSetChanged();

                    swipeListView.closeAnimate(position);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }

            }
        });


        holder.frontOfFavoriteClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = null;

                if (news != null) {
                    intent = new Intent(context, NewsDetails.class);
                    intent.putExtra("class", news);
                } else if (blog != null) {
                    intent = new Intent(context, BlogDetails.class);
                    intent.putExtra("class", blog);
                } else if (publication != null) {
                    intent = new Intent(context, PublicationDetails.class);
                    intent.putExtra("class", publication);
                }

                context.startActivity(intent);
            }
        });

        return convertView;
    }

    class FavoriteHolder {

        RoundedImageView imageOfFavorite;
        TextView titleOfFavorite;
        TextView dateOfFavorite;
        ImageButton ibShare;
        ImageButton ibLike;
        ImageButton ibArchive;
        ImageButton ibDelete;
        RelativeLayout frontOfFavoriteClick;
    }

    boolean checkDB(DBData dbData, String table) {
        return tepavService.checkIfContains(table, dbData.getId());
    }
}