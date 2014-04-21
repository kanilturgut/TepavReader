package com.tepav.reader.helpers;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import com.tepav.reader.R;
import com.tepav.reader.db.DBHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 22:59
 */
public class Util {

    //thanks to Mustafa Simav - (https://github.com/msimav)
    public static String getDateAsString(Context ctx, String date) {

        long now = System.currentTimeMillis();
        long unixtime = serverDateToDate(date).getTime();

        if (now - unixtime < 0) {
            return "Just Now";
        } else if (now - 60 * 1000 < unixtime) { // in fucking min
            return String.format(ctx.getString(R.string.comp_sec),
                    ((now - unixtime) / 1000));
        } else if (now - 60 * 60 * 1000 < unixtime) { // fucking hour
            return String.format(ctx.getString(R.string.comp_min),
                    ((now - unixtime) / 60 / 1000));
        } else if (now - 24 * 60 * 60 * 1000 < unixtime) { // fucking day
            return String.format(ctx.getString(R.string.comp_hour),
                    ((now - unixtime) / 60 / 60 / 1000));
        } else if (now - 7 * 24 * 60 * 60 * 1000 < unixtime) { // fucking week
            return String.format(ctx.getString(R.string.comp_day),
                    ((now - unixtime) / 24 / 60 / 60 / 1000));
        } else if (now - 30 * 7 * 24 * 60 * 60 * 1000 < unixtime) {
            // fucking month
            return String.format(ctx.getString(R.string.comp_week),
                    ((now - unixtime) / 7 / 24 / 60 / 60 / 1000));
        } else if (now - 12 * 30 * 7 * 24 * 60 * 60 * 1000 < unixtime) {
            // fucking month
            return String.format(ctx.getString(R.string.comp_month),
                    ((now - unixtime) / 30 / 7 / 24 / 60 / 60 / 1000));
        } else {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            return df.format(serverDateToDate(date));
        }
    }

    public static Date serverDateToDate(String str) {

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

        try {
            return df.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static void changeVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.GONE);
        else
            view.setVisibility(View.VISIBLE);
    }

    public static void checkIfIsContain(final DBHandler dbHandler, final String table, final String id, final View firstView, final View secondView) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return dbHandler.isContain(table, id);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if (aBoolean) {
                    Util.changeVisibility(firstView);
                    Util.changeVisibility(secondView);
                }

            }
        }.execute();
    }

}
