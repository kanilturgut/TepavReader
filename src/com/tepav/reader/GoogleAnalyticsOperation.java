package com.tepav.reader;

import android.content.Context;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.tepav.reader.helpers.Logs;

/**
 * Author   : kanilturgut
 * Date     : 09/06/14
 * Time     : 09:05
 */
public class GoogleAnalyticsOperation {

    static final String TAG = "GoogleAnalyticsOperation";

    private static synchronized Tracker getTracker(Context context) {
        GoogleAnalytics googleAnalytics = GoogleAnalytics.getInstance(context);
        return googleAnalytics.newTracker(R.xml.app_tracker);
    }

    public static void send(Context context, String screenName) {

        try {
            Tracker tracker = getTracker(context);
            tracker.setScreenName(screenName);
            tracker.send(new HitBuilders.AppViewBuilder().build());
        } catch (Exception e) {
            Logs.e(TAG, "ERROR on send method", e);
        }
    }
}
