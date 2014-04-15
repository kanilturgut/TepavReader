package com.tepav.reader.cache;

import android.content.Context;
import com.tepav.reader.helpers.Constant;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;

/**
 * Author : kanilturgut
 * Date : 15.04.2014
 * Time : 16:47
 */
public class DiskCache {

    public static boolean saveToCache(Context context, int cacheType, JSONArray response) {

        String cacheName = "";

        if (cacheType == Constant.CACHE_TYPE_NEWS)
            cacheName = Constant.DISK_CACHE_FOR_NEWS;
        else if (cacheType == Constant.CACHE_TYPE_BLOG)
            cacheName = Constant.DISK_CACHE_FOR_BLOG;
        else if (cacheType == Constant.CACHE_TYPE_PUBLICATION)
            cacheName = Constant.DISK_CACHE_FOR_PUBLICATION;
        else
            cacheName = "";

        if (!cacheName.equals("")) {

            ObjectOutput output = null;
            try {
                output = new ObjectOutputStream(new FileOutputStream(new File(context.getCacheDir(), "") + cacheName));
                output.writeObject(response.toString());
                output.close();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            //veri tipi bilinmiyor
        }

        return false;
    }

    public static JSONArray retrieveFromDiskCache(Context context, int cacheType) {

        String cacheName = "";
        JSONArray jsonArray = null;

        if (cacheType == Constant.CACHE_TYPE_NEWS)
            cacheName = Constant.DISK_CACHE_FOR_NEWS;
        else if (cacheType == Constant.CACHE_TYPE_BLOG)
            cacheName = Constant.DISK_CACHE_FOR_BLOG;
        else if (cacheType == Constant.CACHE_TYPE_PUBLICATION)
            cacheName = Constant.DISK_CACHE_FOR_PUBLICATION;
        else
            cacheName = "";

        if (!cacheName.equals("")) {
            try {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(context.getCacheDir(), "") + cacheName));
                jsonArray = new JSONArray((String) inputStream.readObject());
                inputStream.close();
                return jsonArray;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            //veri tipi bilinmiyor
        }
        return null;
    }
}
