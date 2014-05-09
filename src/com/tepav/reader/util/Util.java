package com.tepav.reader.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import com.tepav.reader.activity.Splash;
import com.tepav.reader.db.DBHandler;

/**
 * Author : kanilturgut
 * Date : 19.04.2014
 * Time : 22:59
 */
public class Util {

    public static void changeVisibility(View view) {
        Log.i("TAG", "changeVisibility");

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

    public static boolean canDisplayPdf(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkIfUserLoggedIn() {
        return Splash.isUserLoggedIn;
    }

}
