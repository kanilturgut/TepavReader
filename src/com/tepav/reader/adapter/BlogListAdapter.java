package com.tepav.reader.adapter;

import android.content.Context;
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
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.RoundedImageView;
import com.tepav.reader.model.Blog;
import com.tepav.reader.model.Blog;
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
public class BlogListAdapter extends ArrayAdapter<Blog> implements View.OnClickListener {

    Context context;
    List<Blog> blogList = new LinkedList<Blog>();
    int pageNumber;
    AQuery aq;

    public BlogListAdapter(Context c, int number) {
        super(c, R.layout.custom_blog_row);

        this.context = c;
        this.pageNumber = number;

        aq = new AQuery(context);
        loadMore();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BlogHolder holder;

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
            holder.ibReadList = (ImageButton) convertView.findViewById(R.id.ibReadList);

            convertView.setTag(holder);

        } else {
            holder = (BlogHolder) convertView.getTag();
        }

        ImageOptions options = new ImageOptions();
        options.fileCache = true;
        options.memCache = true;
        options.targetWidth = 0;
        options.fallback = 0;
        options.round = 0;

        aq.id(holder.imageOfBlog).image(blogList.get(position).getPimage(), options);
        holder.titleOfBlog.setText(blogList.get(position).getBtitle());
        holder.dateOfBlog.setText(blogList.get(position).getBdate());

        holder.ibShare.setOnClickListener(this);
        holder.ibFavorite.setOnClickListener(this);
        holder.ibReadList.setOnClickListener(this);
        holder.frontOfBlogClick.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibShare:
                Log.i("Click", "Share");
                break;
            case R.id.ibFavorite:
                Log.i("Click", "Favorite");
                break;
            case R.id.ibReadList:
                Log.i("Click", "Read List");
                break;
            case R.id.frontOfBlogClick:
                Log.i("Click", "Blog on front");
                break;
        }

    }

    class BlogHolder {

        RoundedImageView imageOfBlog;
        TextView titleOfBlog;
        TextView dateOfBlog;
        ImageButton ibShare;
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
