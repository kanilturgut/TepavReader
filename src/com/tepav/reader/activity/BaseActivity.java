package com.tepav.reader.activity;

import android.app.Activity;
import android.os.Bundle;
import com.tepav.reader.R;

/**
 * Created by kanilturgut on 14/04/14, 18:04.
 */
public class BaseActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }
}