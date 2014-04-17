package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.tepav.reader.R;
import com.tepav.reader.adapter.NewsListAdapter;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.swipelistview.SwipeListView;
import com.tepav.reader.model.News;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;

/**
 * Author : kanilturgut
 * Date : 16.04.2014
 * Time : 15:19
 */
public class NewsActivity extends Activity {

    Context context;

    SwipeListView swipeListView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        context = this;

        swipeListView = (SwipeListView) findViewById(R.id.swipeListViewOfNews);

        final List<News> newsList = new LinkedList<News>();

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, HttpURL.createURL(HttpURL.news),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0 ; i < response.length(); i++) {

                            try {
                                newsList.add(News.fromJSON(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        swipeListView.setAdapter(new NewsListAdapter(context, newsList));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("News", "ERROR! on makeRequest", error);
            }
        });

        requestQueue.add(jsonArrayRequest);

    }
}