package com.tepav.reader.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tepav.reader.helpers.Logs;

/**
 * Author   : kanilturgut
 * Date     : 26/05/14
 * Time     : 13:31
 */
public class GCMReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Logs.d("TAG", "received");
    }
}
