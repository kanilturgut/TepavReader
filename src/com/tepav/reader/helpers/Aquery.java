package com.tepav.reader.helpers;

import android.content.Context;
import com.androidquery.AQuery;

/**
 * Author   : kanilturgut
 * Date     : 12/05/14
 * Time     : 18:03
 */
public class Aquery {

    public static AQuery aQuery = null;

    public static AQuery getInstance(Context context) {

        if (aQuery == null)
            aQuery = new AQuery(context);

        return aQuery;
    }
}
