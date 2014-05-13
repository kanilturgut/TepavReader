package com.tepav.reader.helpers;

import android.content.Context;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.Cookie;

import java.util.LinkedList;

/**
 * Author   : kanilturgut
 * Date     : 12/05/14
 * Time     : 18:03
 */
public class Aquery {

    public static AQuery aQuery = null;
    public static LinkedList<Cookie> cookies = new LinkedList<Cookie>();

    public static AQuery getInstance(Context context) {

        if (aQuery == null)
            aQuery = new AQuery(context);

        return aQuery;
    }

    public static void addCookieList(AjaxStatus status) {

        if (status.getCode() == HttpStatus.SC_OK) {
            for (Cookie cookie: status.getCookies())
                cookies.add(cookie);
        }
    }
}
