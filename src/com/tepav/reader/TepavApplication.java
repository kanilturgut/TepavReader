package com.tepav.reader;

import android.app.Application;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Author   : kanilturgut
 * Date     : 06/06/14
 * Time     : 22:41
 */
public class TepavApplication extends Application {

    public static TepavApplication tepavApplication = null;

    public TepavApplication() {
        super();

        if (tepavApplication == null)
            tepavApplication = new TepavApplication();
    }

    public static TepavApplication getInstance() {
        if (tepavApplication == null)
            tepavApplication = new TepavApplication();

        return tepavApplication;
    }


    synchronized public Tracker getTracker() {
        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(this);
        return googleAnalytics.newTracker(R.xml.app_tracker);
    }

    public void send(String screenName) {
        Tracker tracker = getInstance().getTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }
}
