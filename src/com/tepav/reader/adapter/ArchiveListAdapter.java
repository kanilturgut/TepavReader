package com.tepav.reader.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.tepav.reader.helpers.Util;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.DBData;
import com.tepav.reader.model.News;
import com.tepav.reader.model.Publication;
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

    News news = null;
    Blog blog = null;
    Publication publication = null;

    public ArchiveListAdapter(Context context) {
        super(context, R.layout.custom_archive_list_row);

        this.context = context;
        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);

        new AsyncTask<Void, Void, List<DBData>>() {

            @Override
            protected List<DBData> doInBackground(Void... voids) {
                return dbHandler.read(DBHandler.TABLE_ARCHIVE);
            }

            @Override
            protected void onPostExecute(List<DBData> dbDatas) {

                if (dbDatas != null ) {
                    dbDataList = dbDatas;
                    addAll(dbDataList);
                    notifyDataSetChanged();
                }
            }
        }.execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        FavoriteHolder holder;
        DBData dbData = dbDataList.get(position);

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

            holder = new FavoriteHolder();

            //front view
            holder.frontOfFavoriteClick = (RelativeLayout) convertView.findViewById(R.id.frontOfFavoriteClick);
            holder.imageOfFavorite = (RoundedImageView) convertView.findViewById(R.id.ivImageOfFavorite);
            holder.titleOfFavorite = (TextView) convertView.findViewById(R.id.tvTitleOfFavorite);
            holder.dateOfFavorite = (TextView) convertView.findViewById(R.id.tvDateOfFavorite);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibFavorited = (ImageButton) convertView.findViewById(R.id.ibFavorited);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);
            holder.ibReadListed = (ImageButton) convertView.findViewById(R.id.ibReadListed);

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
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfFavorite.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (blog != null) {
            holder.titleOfFavorite.setText(blog.getBtitle());
            holder.dateOfFavorite.setText(blog.getBtitle());

            Bitmap bmp = aq.getCachedImage(blog.getPimage());
            if (bmp == null) {
                aq.id(holder.imageOfFavorite).image(blog.getPimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfFavorite.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (publication != null) {
            holder.titleOfFavorite.setText(publication.getYtitle());
            holder.dateOfFavorite.setText(publication.getYdate() + ", " + publication.getYtype());
            holder.imageOfFavorite.setImageResource(R.drawable.no_image);
        }


        MyOnClickListener myOnClickListener = new MyOnClickListener(news, blog, publication, position);
        holder.ibShare.setOnClickListener(myOnClickListener);
        holder.ibFavorite.setOnClickListener(myOnClickListener);
        holder.ibFavorite.setOnClickListener(myOnClickListener);
        holder.ibFavorited.setOnClickListener(myOnClickListener);
        holder.ibFavorited.setOnClickListener(myOnClickListener);
        holder.frontOfFavoriteClick.setOnClickListener(myOnClickListener);

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, dbData.getId(), holder.ibFavorite, holder.ibFavorited);
        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, dbData.getId(), holder.ibFavorite, holder.ibFavorited);

        return convertView;
    }

    class MyOnClickListener implements View.OnClickListener {

        News news;
        Blog blog;
        Publication publication;
        int position;

        public MyOnClickListener(News n, Blog b, Publication p, int pos) {
            this.position = pos;
            this.news = n;
            this.blog = b;
            this.publication = p;
        }

        @Override
        public void onClick(View view) {

            DBData dbData = dbDataList.get(position);

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


            switch (view.getId()) {
                case R.id.ibShare:

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

                    break;
                case R.id.ibFavorite:

                    if (news != null) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case R.id.ibFavorited:

                    if (news != null) {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case R.id.ibReadList:

                    if (news != null) {
                        try {
                            dbHandler.insert(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.insert(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case R.id.ibReadListed:

                    if (news != null) {
                        try {
                            dbHandler.delete(News.toDBData(news), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (blog != null) {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (publication != null) {
                        try {
                            dbHandler.delete(Publication.toDBData(publication), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case R.id.frontOfFavoriteClick:

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
                    break;
            }
        }
    }

    class FavoriteHolder {

        RoundedImageView imageOfFavorite;
        TextView titleOfFavorite;
        TextView dateOfFavorite;
        ImageButton ibShare;
        ImageButton ibReadList;
        ImageButton ibReadListed;
        ImageButton ibFavorite;
        ImageButton ibFavorited;
        RelativeLayout frontOfFavoriteClick;


    }
}
