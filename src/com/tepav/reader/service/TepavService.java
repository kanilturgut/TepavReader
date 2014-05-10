package com.tepav.reader.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Author   : kanilturgut
 * Date     : 06/05/14
 * Time     : 09:57
 */
public class TepavService extends Service {

    private OfflineList offlineList;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        offlineList = OfflineList.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        offlineList.startReadingFromDatabase();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

        offlineList.destroyList();
    }

}
