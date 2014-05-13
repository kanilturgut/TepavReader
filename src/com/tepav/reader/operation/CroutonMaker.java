package com.tepav.reader.operation;

import android.app.Activity;
import com.tepav.reader.R;
import com.tepav.reader.helpers.crouton.Crouton;
import com.tepav.reader.helpers.crouton.Style;

/**
 * Author   : kanilturgut
 * Date     : 13/05/14
 * Time     : 12:33
 */
public class CroutonMaker {

    public static void confirm(Activity activity, String message) {
        Style style = new Style.Builder()
                .setHeight(100)
                .setBackgroundColor(activity.getResources().getColor(R.color.crouton_confirm))
                .build();
        Crouton.showText(activity, message, style);
    }

    public static void alert(Activity activity, String message) {
        Style style = new Style.Builder()
                .setHeight(100)
                .setBackgroundColor(activity.getResources().getColor(R.color.crouton_alert))
                .build();
        Crouton.showText(activity, message, style);
    }

    public static void info(Activity activity, String message) {
        Style style = new Style.Builder()
                .setHeight(100)
                .setBackgroundColor(activity.getResources().getColor(R.color.crouton_info))
                .build();
        Crouton.showText(activity, message, style);
    }

}
