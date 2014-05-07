package com.tepav.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
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
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
import com.tepav.reader.service.TepavService;
import org.json.JSONException;

import java.util.List;

/**
 * Author : kanilturgut
 * Date : 30.04.2014
 * Time : 10:42
 */
public class ArchiveListAdapter extends ArrayAdapter<DBData> {

    String TAG = "FavoriteListAdapter";
    Context context;
    DBHandler dbHandler;
    List<DBData> dbDataList;
    AQuery aq;
    SwipeListView swipeListView;
    TepavService tepavService;

    News news = null;
    Blog blog = null;
    Publication publication = null;

    public ArchiveListAdapter(Context context, SwipeListView swipeListView, List<DBData> dbDataList) {
        super(context, R.layout.custom_archive_list_row, dbDataList);

        this.context = context;
        this.swipeListView = swipeListView;
        this.dbDataList = dbDataList;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        tepavService = TepavService.getInstance();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ArchiveHolder holder;
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

        if (news != null) {

            holder.titleOfArchive.setText(news.getHtitle());
            holder.dateOfArchive.setText(news.getHdate());


            Bitmap bmp = aq.getCachedImage(news.getHimage());
            if (bmp == null) {
                aq.id(holder.imageOfArchive).image(news.getHimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfArchive.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (blog != null) {
            holder.titleOfArchive.setText(blog.getBtitle());
            holder.dateOfArchive.setText(blog.getBtitle());

            Bitmap bmp = aq.getCachedImage(blog.getPimage());
            if (bmp == null) {
                aq.id(holder.imageOfArchive).image(blog.getPimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfArchive.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (publication != null) {
            holder.titleOfArchive.setText(publication.getYtitle());
            holder.dateOfArchive.setText(publication.getYdate() + ", " + publication.getYtype());
            holder.imageOfArchive.setImageResource(R.drawable.no_image);
        }


        if (tepavService != null) {

            if (checkDB(dbData, DBHandler.TABLE_LIKE))
                holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
            else
                holder.ibLike.setImageResource(R.drawable.swipe_like);
        }

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        holder.ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDB(dbData, DBHandler.TABLE_LIKE)) {
                    if (news != null) {
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
                    if (news != null) {
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
            }
        });

        holder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (news != null) {
                    dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                    tepavService.removeItemFromReadingListOfTepavService(dbData);
                    dbDataList.remove(dbData);
                } else if (blog != null) {
                    dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                    tepavService.removeItemFromReadingListOfTepavService(dbData);
                    dbDataList.remove(dbData);
                } else if (publication != null) {
                    dbHandler.delete(dbData, DBHandler.TABLE_ARCHIVE);
                    tepavService.removeItemFromReadingListOfTepavService(dbData);
                    dbDataList.remove(dbData);
                }

                remove(dbData);
                notifyDataSetChanged();

                swipeListView.closeAnimate(position);
            }
        });

        holder.frontOfArchiveClick.setOnClickListener(new View.OnClickListener() {
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
        return tepavService.checkIfContains(table, dbData.getId());
    }
}
