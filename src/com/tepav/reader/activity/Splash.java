package com.tepav.reader.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.tepav.reader.R;
import com.tepav.reader.helpers.Constant;

public class Splash extends Activity {

    Context context = null;
    Handler startHandler = null;
    Runnable startRunnable = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        context = this;

        //Implementation of handler and its runnable
        startHandler = new Handler();
        startRunnable = new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(context, MainScreen.class));
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        //call runnable
        startHandler.postDelayed(startRunnable, Constant.SPLASH_TRANSITION_TIME);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (startHandler != null)
            startHandler.removeCallbacks(startRunnable);
    }
}
