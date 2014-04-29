package com.tepav.reader.adapter;

import android.content.Context;
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
import com.tepav.reader.db.DBHandler;
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
 * Date : 29.04.2014
 * Time : 16:46
 */
public class ReadListListAdapter extends ArrayAdapter<DBData> {

    String TAG = "ReadListListAdapter";
    Context context;
    DBHandler dbHandler;
    List<DBData> dbDataList;
    AQuery aq;

    News news = null;
    Blog blog = null;
    Publication publication = null;

    public ReadListListAdapter(Context context) {
        super(context, R.layout.custom_read_list_row);

        this.context = context;
        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);

        new AsyncTask<Void, Void, List<DBData>>() {

            @Override
            protected List<DBData> doInBackground(Void... voids) {
                return dbHandler.read(DBHandler.TABLE_READ_LIST);
            }

            @Override
            protected void onPostExecute(List<DBData> dbDatas) {
                dbDataList = dbDatas;
                addAll(dbDataList);
                notifyDataSetChanged();
            }
        }.execute();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ReadListHolder holder;
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
            convertView = inflater.inflate(R.layout.custom_read_list_row, parent, false);

            holder = new ReadListHolder();

            //front view
            holder.frontOfReadListClick = (RelativeLayout) convertView.findViewById(R.id.frontOfReadListClick);
            holder.imageOfReadList = (RoundedImageView) convertView.findViewById(R.id.ivImageOfReadList);
            holder.titleOfReadList = (TextView) convertView.findViewById(R.id.tvTitleOfReadList);
            holder.dateOfReadList = (TextView) convertView.findViewById(R.id.tvDateOfReadList);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibFavorited = (ImageButton) convertView.findViewById(R.id.ibFavorited);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);
            holder.ibReadListed = (ImageButton) convertView.findViewById(R.id.ibReadListed);

            convertView.setTag(holder);

        } else {
            holder = (ReadListHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.no_image;

        if (news != null) {

            holder.titleOfReadList.setText("N " + news.getHtitle());
            holder.dateOfReadList.setText(news.getHdate());


            Bitmap bmp = aq.getCachedImage(news.getHimage());
            if (bmp == null) {
                aq.id(holder.imageOfReadList).image(news.getHimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfReadList.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (blog != null) {
            holder.titleOfReadList.setText("B " + blog.getBtitle());
            holder.dateOfReadList.setText(blog.getBtitle());

            Bitmap bmp = aq.getCachedImage(blog.getPimage());
            if (bmp == null) {
                aq.id(holder.imageOfReadList).image(blog.getPimage(), options);
                Log.i(TAG, "image received from server");
            } else {
                holder.imageOfReadList.setImageBitmap(bmp);
                Log.i(TAG, "image received from cache");
            }

        } else if (publication != null) {
            holder.titleOfReadList.setText("P " +publication.getYtitle());
            holder.dateOfReadList.setText(publication.getYdate() + ", " + publication.getYtype());
            holder.imageOfReadList.setImageResource(R.drawable.no_image);
        }


        MyOnClickListener myOnClickListener = new MyOnClickListener(position);
        holder.ibShare.setOnClickListener(myOnClickListener);
        holder.ibFavorite.setOnClickListener(myOnClickListener);
        holder.ibReadList.setOnClickListener(myOnClickListener);
        holder.ibFavorited.setOnClickListener(myOnClickListener);
        holder.ibReadListed.setOnClickListener(myOnClickListener);
        holder.frontOfReadListClick.setOnClickListener(myOnClickListener);

        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, dbData.getId(), holder.ibFavorite, holder.ibFavorited);
        Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, dbData.getId(), holder.ibReadList, holder.ibReadListed);

        return convertView;
    }

    class MyOnClickListener implements View.OnClickListener {

        int position;

        public MyOnClickListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View view) {

            //ReadList readList = readListList.get(position);
/*
            switch (view.getId()) {
                case R.id.ibShare:

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, readList.getYtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT,  readList.getYtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                    break;
                case R.id.ibFavorite:

                    try {
                        dbHandler.insert(ReadList.toDBData(readList), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.ibFavorited:

                    try {
                        dbHandler.delete(ReadList.toDBData(readList), DBHandler.TABLE_FAVORITE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;
                case R.id.ibReadList:

                    try {
                        dbHandler.insert(ReadList.toDBData(readList), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.ibReadListed:
                    try {
                        dbHandler.delete(ReadList.toDBData(readList), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.frontOfReadListClick:

                    Intent intent = new Intent(context, ReadListDetails.class);
                    intent.putExtra("class", readList);
                    context.startActivity(intent);
                    break;
            }
        } */
        }
    }

    class ReadListHolder {

        RoundedImageView imageOfReadList;
        TextView titleOfReadList;
        TextView dateOfReadList;
        ImageButton ibShare;
        ImageButton ibFavorite;
        ImageButton ibFavorited;
        ImageButton ibReadList;
        ImageButton ibReadListed;
        RelativeLayout frontOfReadListClick;
    }


}
