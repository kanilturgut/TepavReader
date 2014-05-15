package com.tepav.reader.adapter.online_list;

import android.app.ProgressDialog;
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
import com.tepav.reader.activity.BlogDetails;
import com.tepav.reader.activity.Splash;
import com.tepav.reader.db.DBHandler;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.Constant;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.helpers.roundedimageview.RoundedImageView;
import com.tepav.reader.model.Blog;
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
 * Date : 18.04.2014
 * Time : 12:45
 */
public class BlogListAdapter extends ArrayAdapter<Blog> {

    String TAG = "BlogListAdapter";
    Context context;
    LinkedList<Blog> blogList = new LinkedList<Blog>();
    int pageNumber;
    AQuery aq;
    DBHandler dbHandler;
    OfflineList offlineList;
    ProgressDialog progressDialog = null;

    public BlogListAdapter(Context c, int number) {
        super(c, R.layout.custom_blog_row);

        this.context = c;
        this.pageNumber = number;

        dbHandler = DBHandler.getInstance(context);
        offlineList = OfflineList.getInstance(context);
        aq = Aquery.getInstance(context);
        progressDialog = ProgressDialog.show(context, context.getString(R.string.please_wait),
                context.getString(R.string.loading), false, false);
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
            holder.ibLike = (ImageButton) convertView.findViewById(R.id.ibLike);
            holder.ibFavorite = (ImageButton) convertView.findViewById(R.id.ibFavorite);
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);

            convertView.setTag(holder);

        } else {
            holder = (BlogHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = R.drawable.tepav_t;

        Bitmap cachedBitmap = aq.getCachedImage(blog.getPimage());
        if (cachedBitmap == null) {
            aq.id(holder.imageOfBlog).image(blog.getPimage(), options);
            Logs.i(TAG, "image received from server");
        } else {
            holder.imageOfBlog.setImageBitmap(cachedBitmap);
            Logs.i(TAG, "image received from cache");
        }

        holder.titleOfBlog.setText(blog.getBtitle());
        holder.dateOfBlog.setText(blog.getBdate());

        // swipe list icons
        if (offlineList != null) {
            if (checkDB(blog, DBHandler.TABLE_FAVORITE))
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites_dolu);
            else
                holder.ibFavorite.setImageResource(R.drawable.swipe_favorites);

            if (checkDB(blog, DBHandler.TABLE_READ_LIST))
                holder.ibReadList.setImageResource(R.drawable.okudum_icon_dolu);
            else
                holder.ibReadList.setImageResource(R.drawable.okudum_icon);

            if (checkDB(blog, DBHandler.TABLE_LIKE))
                holder.ibLike.setImageResource(R.drawable.swipe_like_dolu);
            else
                holder.ibLike.setImageResource(R.drawable.swipe_like);
        }
        
        holder.ibShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Splash.isUserLoggedIn) {

                    try {
                        ShareOperation.doShare(context, Blog.toDBData(blog));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = Constant.SHARE_BLOG + blog.getGunluk_id();

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, blog.getBtitle());
                    shareIntent.putExtra(Intent.EXTRA_TEXT, blog.getBtitle() + " " + url);
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

                    if (!checkDB(blog, DBHandler.TABLE_LIKE)) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_LIKE);
                            LikeOperation.doLike(Blog.toDBData(blog));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_LIKE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (!checkDB(blog, DBHandler.TABLE_LIKE))
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

                    if (!checkDB(blog, DBHandler.TABLE_FAVORITE)) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_FAVORITE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(blog, DBHandler.TABLE_FAVORITE))
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

                    if (!checkDB(blog, DBHandler.TABLE_READ_LIST)) {
                        try {
                            dbHandler.insert(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            dbHandler.delete(Blog.toDBData(blog), DBHandler.TABLE_READ_LIST);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    ImageButton imageButton = (ImageButton) view;
                    if (checkDB(blog, DBHandler.TABLE_READ_LIST))
                        imageButton.setImageResource(R.drawable.okudum_icon_dolu);
                    else
                        imageButton.setImageResource(R.drawable.okudum_icon);
                } else {
                    AlertDialogManager alertDialogManager = new AlertDialogManager();
                    alertDialogManager.showLoginDialog(context, context.getString(R.string.warning), context.getString(R.string.must_log_in), false);
                }

            }
        });
        holder.frontOfBlogClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BlogDetails.class);
                try {
                    intent.putExtra("class", Blog.toDBData(blog));
                    intent.putExtra("fromWhere", Constant.DETAILS_FROM_POST);
                    context.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

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

                LinkedList<Blog> temp = new LinkedList<Blog>();

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
                if (progressDialog != null)
                    progressDialog.dismiss();
            }
        });
    }

    boolean checkDB(Blog blog, String table) {
        return offlineList.checkIfContains(table, blog.getId());
    }
}
