package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import com.tepav.reader.R;

/**
 * Author : kanilturgut
 * Date : 18.04.2014
 * Time : 20:17
 */
public class PublicationActivity extends Activity {

    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);
        this.context = this;


    }
}