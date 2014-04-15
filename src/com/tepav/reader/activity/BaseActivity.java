package com.tepav.reader.activity;

import android.app.Activity;
import android.os.Bundle;
import com.tepav.reader.R;

/**
 *
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 13:47
 */
public class BaseActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
}