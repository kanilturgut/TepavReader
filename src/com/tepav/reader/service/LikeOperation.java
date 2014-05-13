package com.tepav.reader.service;

import android.content.Context;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.tepav.reader.helpers.Aquery;
import com.tepav.reader.helpers.HttpURL;
import com.tepav.reader.helpers.Logs;
import com.tepav.reader.model.DBData;
import org.apache.http.HttpStatus;
import org.apache.http.cookie.Cookie;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Author   : kanilturgut
 * Date     : 11/05/14
 * Time     : 01:24
 */
public class LikeOperation {

    final static String TAG = "LikeOperation";

    public static void doLike(Context context, DBData dbData) {

        AQuery aQuery = Aquery.getInstance(context);
        String url = "";
        Map<String, String> map = new HashMap<String, String>();

        AjaxCallback<JSONObject> ajaxCallback = new AjaxCallback<JSONObject>() {

            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                super.callback(url, object, status);

                if (status.getCode() == HttpStatus.SC_OK) {
                    Logs.i(TAG, "SUCCESS");
                } else {
                    Logs.e(TAG, "FAILED");
                }
            }
        };

        if (dbData.getType() == DBData.TYPE_NEWS) {
            url = HttpURL.createURL(HttpURL.likeNews);
            map.put("newsId", dbData.getId());
        } else if (dbData.getType() == DBData.TYPE_BLOG) {
            url = HttpURL.createURL(HttpURL.likeBLog);
            map.put("blogId", dbData.getId());
        } else if (dbData.getType() == DBData.TYPE_PUBLICATION) {
            url = HttpURL.createURL(HttpURL.likePublication);
            map.put("publicationId", dbData.getId());
        }

        ajaxCallback.params(map);

        for (Cookie cookie: Aquery.cookies)
            ajaxCallback.cookie(cookie.getName(), cookie.getValue());

        aQuery.ajax(url, JSONObject.class, ajaxCallback);
    }

}
