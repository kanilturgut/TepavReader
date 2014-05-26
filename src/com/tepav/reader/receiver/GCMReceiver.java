package com.tepav.reader.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.tepav.reader.R;
import com.tepav.reader.activity.Splash;

/**
 * Author   : kanilturgut
 * Date     : 26/05/14
 * Time     : 13:31
 */
public class GCMReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("Yeni bir içerik mevcut");

        Intent resultIntent = new Intent(context, Splash.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Splash.class);
        stackBuilder.addNextIntent(resultIntent);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = { 0, 100, 600, 100, 700};
        vibrator.vibrate(pattern, -1);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
