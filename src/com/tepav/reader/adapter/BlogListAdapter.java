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
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.ImageOptions;
import com.tepav.reader.R;
import com.tepav.reader.activity.BlogDetails;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.service.TepavService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 12:45
 */
public class BlogListAdapter extends ArrayAdapter<Blog> {

    String TAG = "BlogListAdapter";
    Context context;
    List<Blog> blogList = new LinkedList<Blog>();
    int pageNumber;
    AQuery aq;
    DBHandler dbHandler;
    TepavService tepavService = null;

    boolean isPressedLike = false;
    boolean isPressedFavorite = false;
    boolean isPressedReadList = false;

    public BlogListAdapter(Context c, int number) {
        super(c, R.layout.custom_blog_row);

        this.context = c;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        aq = new AQuery(context);
        tepavService = TepavService.getInstance();

        loadMore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BlogHolder holder;
        final Blog blog = blogList.get(position);

        if (position == (blogList.size() - 1))
            loadMore();

        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_blog_row, parent, false);

            holder = new BlogHolder();

            //front view
            holder.frontOfBlogClick = (RelativeLayout) convertView.findViewById(R.id.frontOfBlogClick);
            holder.imageOfBlog = (RoundedImageView) convertView.findViewById(R.id.ivImageOfBlog);
            holder.titleOfBlog = (TextView) convertView.findViewById(R.id.tvTitleOfBlog);
            holder.dateOfBlog = (TextView) convertView.findViewById(R.id.tvDateOfBlog);

            //back view
            holder.ibShare = (ImageButton) convertView.findViewById(R.id.ibShare);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            //holder.ibFavorited = (ImageButton) convertView.findViewById(R.id.ibFavorited);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);
            //holder.ibReadListed = (ImageButton) convertView.findViewById(R.id.ibReadListed);

            convertView.setTag(holder);

        } else {
            holder = (BlogHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.no_image;

        Bitmap cachedBitmap = aq.getCachedImage(blog.getPimage());
        if (cachedBitmap == null) {
            aq.id(holder.imageOfBlog).image(blog.getPimage(), options);
            Log.i(TAG, "image received from server");
        } else {
            holder.imageOfBlog.setImageBitmap(cachedBitmap);
            Log.i(TAG, "image received from cache");
        }


        holder.titleOfBlog.setText(blog.getBtitle());
        holder.dateOfBlog.setText(blog.getBdate());

        //MyOnClickListener myOnClickListener = new MyOnClickListener(position);

        if (tepavService != null) {
            isPressedFavorite = tepavService.checkIfContains(DBHandler.TABLE_FAVORITE, blog.getId());
            isPressedReadList = tepavService.checkIfContains(DBHandler.TABLE_READ_LIST, blog.getId());
            //isPressedLike = tepavService.checkIfContains(DBHandler.TABLE_FAVORITE, blog.getId());
        }

        if (isPressedFavorite)
            holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
        else
            holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

        if (isPressedReadList)
            holder.ibReadList.setImageResource(R.drawable.okudum_icon_dolu);
        else
            holder.ibReadList.setImageResource(R.drawable.okudum_icon);

        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
                context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
            }
        });

        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isPressedFavorite) {
                    try {
                        dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                        tepavService.addItemToFavoriteListOfTepavService(Blog.toDBData(blog));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                        tepavService.removeItemFromFavoriteListOfTepavService(Blog.toDBData(blog));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                isPressedFavorite = !isPressedFavorite;
                ImageButton imageButton = (ImageButton) view;
                if (isPressedFavorite)
                    imageButton.setImageResource(R.drawable.swipe_favorites_dolu);
                else
                    imageButton.setImageResource(R.drawable.swipe_favorites);

            }
        });
        holder.ibReadList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.frontOfBlogClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BlogDetails.class);
                intent.putExtra("class", blog);
                context.startActivity(intent);
            }
        });


        //Util.checkIfIsContain(dbHandler, DBHandler.TABLE_FAVORITE, blog.getId(), holder.ibFavorite, holder.ibFavorited);
        //Util.checkIfIsContain(dbHandler, DBHandler.TABLE_READ_LIST, blog.getId(), holder.ibReadList, holder.ibReadListed);

        return convertView;
    }

    /*
    class MyOnClickListener implements View.OnClickListener {

        int position;

        public MyOnClickListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View view) {

            Blog blog = blogList.get(position);

            switch (view.getId()) {
                case R.id.ibShare:
                    String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
                    break;
                case R.id.ibLike:
                    //like operation need login first

                    break;
                case R.id.ibFavorite:
/*
                    try {
                        dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (view.getBackground() == context.getResources().getDrawable(R.drawable.swipe_favorites))
                        view.setImageResource(R.drawable.swipe_favorites);
                    else
                        view.setImageResource(R.drawable.swipe_favorites_dolu);

                    break;
                case R.id.ibFavorited:



                    break;
                case R.id.ibReadList:

                    try {
                        dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.ibReadListed:

                    try {
                        dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.frontOfBlogClick:

                    Intent intent = new Intent(context, BlogDetails.class);
                    intent.putExtra("class", blog);
                    context.startActivity(intent);
                    break;
            }
        }

    } */

    class BlogHolder {

        RoundedImageView imageOfBlog;
        TextView titleOfBlog;
        TextView dateOfBlog;
        ImageButton ibShare;
        ImageButton ibLike;
        ImageButton ibFavorite;
        ImageButton ibReadList;
        RelativeLayout frontOfBlogClick;
    }

    public void loadMore() {

        JSONObject params = new JSONObject();
        try {
            params.put("pageNumber", pageNumber);
        } catch (JSONException e) {
            params = null;
            e.printStackTrace();
        }

        aq.post(HttpURL.createURL(HttpURL.blog), params, JSONArray.class, new AjaxCallback<JSONArray>() {

            @Override
            public void callback(String url, JSONArray object, AjaxStatus status) {

                List<Blog> temp = new LinkedList<Blog>();

                if (object != null && object.length() != 0) {
                    for (int i = 0; i < object.length(); i++) {
                        try {
                            temp.add(Blog.fromJSON(object.getJSONObject(i)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    blogList.addAll(temp);
                    addAll(temp);
                    notifyDataSetChanged();

                    pageNumber++;
                }
            }
        });
    }
}
