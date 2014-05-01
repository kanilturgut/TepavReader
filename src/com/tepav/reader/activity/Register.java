package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.tepav.reader.R;

/**
 * Author : kanilturgut
 * Date : 01.05.2014
 * Time : 19:21
 */
public class Register extends Activity {

    Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.context = this;

        TextView tv = (TextView) findViewById(R.id.tvLogin);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, Login.class));
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}