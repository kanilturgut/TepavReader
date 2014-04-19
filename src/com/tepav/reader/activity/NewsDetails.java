package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.tepav.reader.R;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 19:24
 */
public class NewsDetails extends Activity {

    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_news_details);
    }
}