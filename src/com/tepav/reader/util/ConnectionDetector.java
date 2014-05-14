package com.tepav.reader.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Author   : kanilturgut
 * Date     : 05/05/14
 * Time     : 10:31
 */
public class ConnectionDetector {

    private Context context;
    public static ConnectionDetector connectionDetector = null;

    public static ConnectionDetector getInstance(Context context) {

        if (connectionDetector == null)
            connectionDetector = new ConnectionDetector(context);

        return connectionDetector;
    }

    public static ConnectionDetector getInstance() {
        if (connectionDetector != null)
            return connectionDetector;

        return null;
    }

    private ConnectionDetector(Context context) {
        this.context = context;
    }

    /**
     * Checking for all possible internet providers
     * *
     */
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }
}
